package org.red5.server.net.mrtmp.codec;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2009 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.red5.server.net.mrtmp.MRTMPPacket;
import org.red5.server.net.rtmp.message.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steven Gong (steven.gong@gmail.com)
 */
public class MRTMPProtocolDecoder implements ProtocolDecoder {
	private static final Logger log = LoggerFactory.getLogger(MRTMPProtocolDecoder.class);

	public void decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		IoBuffer buffer = (IoBuffer) session.getAttribute("buffer");
		if (buffer == null) {
			buffer = IoBuffer.allocate(16 * 1024);
			buffer.setAutoExpand(true);
			session.setAttribute("buffer", buffer);
		}
		buffer.put(in);
		buffer.flip();
		while (true) {
			if (buffer.remaining() < MRTMPPacket.COMMON_HEADER_LENGTH) {
				break;
			}
			int pos = buffer.position();
			MRTMPPacket.Header header = decodeHeader(buffer);
			if (header == null) {
				buffer.position(pos);
				break;
			}
			if (buffer.remaining() < header.getBodyLength()) {
				buffer.position(pos);
				break;
			}
			MRTMPPacket.Body body = decodeBody(buffer, header);
			MRTMPPacket packet = new MRTMPPacket();
			packet.setHeader(header);
			packet.setBody(body);
			if (log.isDebugEnabled()) {
				log.debug(packet.toString());
			}
			out.write(packet);
		}
		buffer.compact();
	}

	public void dispose(IoSession session) throws Exception {
		// nothing to dispose for decoding
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		IoBuffer buffer = (IoBuffer) session.getAttribute("buffer");
		if (buffer != null) {
			buffer.free();
		}
	}
	
	public MRTMPPacket.Header decodeHeader(IoBuffer buffer) {
		short type = buffer.getShort();
		short bodyEncoding = buffer.getShort();
		int preserved = buffer.getInt();
		int clientId = buffer.getInt();
		int headerLength = buffer.getInt();
		int bodyLength = buffer.getInt();
		if (buffer.remaining() < headerLength - MRTMPPacket.COMMON_HEADER_LENGTH) {
			return null;
		}
		MRTMPPacket.Header header = null;
		if (type == MRTMPPacket.RTMP && headerLength != MRTMPPacket.RTMP_HEADER_LENGTH) {
			// XXX errrh, something weird happens
			log.warn("Codec error: wrong RTMP header length " + headerLength);
			header = new MRTMPPacket.Header();
			buffer.skip(headerLength - MRTMPPacket.COMMON_HEADER_LENGTH);
		} else if (type == MRTMPPacket.RTMP) {
			header = new MRTMPPacket.RTMPHeader();
			MRTMPPacket.RTMPHeader rtmpHeader = (MRTMPPacket.RTMPHeader) header;
			rtmpHeader.setRtmpType(buffer.getInt());
		} else {
			header = new MRTMPPacket.Header();
			buffer.skip(headerLength - MRTMPPacket.COMMON_HEADER_LENGTH);
		}
		header.setType(type);
		header.setBodyEncoding(bodyEncoding);
		header.setDynamic((preserved & 0x8000000) != 0);
		header.setClientId(clientId);
		header.setHeaderLength(headerLength);
		header.setBodyLength(bodyLength);
		return header;
	}

	public MRTMPPacket.Body decodeBody(IoBuffer buffer, MRTMPPacket.Header header) {
		MRTMPPacket.Body body = null;
		switch (header.getType()) {
			case MRTMPPacket.CONNECT:
			case MRTMPPacket.CLOSE:
				if (header.getBodyLength() != 0) {
					// XXX something weird happens
					log.warn("Codec error: wrong connect/close body length " + header.getBodyLength());
				}
				return new MRTMPPacket.Body();
			case MRTMPPacket.RTMP:
				byte[] byteArray = new byte[header.getBodyLength()];
				buffer.get(byteArray);
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
					Packet packet = (Packet) ois.readObject();
					body = new MRTMPPacket.RTMPBody();
					MRTMPPacket.RTMPBody rtmpBody = (MRTMPPacket.RTMPBody) body;
					rtmpBody.setRtmpPacket(packet);
				} catch (IOException e) {
					// XXX should not happen
					log.error("", e);
				} catch (ClassNotFoundException e) {
					// XXX should not happen
					log.error("", e);
				}
				break;
			default:
				byteArray = new byte[header.getBodyLength()];
				buffer.get(byteArray);
				body = new MRTMPPacket.Body();
				body.setRawBuf(IoBuffer.wrap(byteArray));
				break;
		}
		return body;
	}
}
