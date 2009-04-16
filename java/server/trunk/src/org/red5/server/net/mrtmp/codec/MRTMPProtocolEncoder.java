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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.red5.server.net.mrtmp.MRTMPPacket;

/**
 * @author Steven Gong (steven.gong@gmail.com)
 */
public class MRTMPProtocolEncoder implements ProtocolEncoder {

	public void dispose(IoSession session) throws Exception {
	}

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		MRTMPPacket packet = (MRTMPPacket) message;
		MRTMPPacket.Header header = packet.getHeader();
		IoBuffer buf = null;
		switch (header.getType()) {
			case MRTMPPacket.CONNECT:
			case MRTMPPacket.CLOSE:
				buf = IoBuffer.allocate(MRTMPPacket.COMMON_HEADER_LENGTH);
				buf.setAutoExpand(true);
				break;
			case MRTMPPacket.RTMP:
				buf = IoBuffer.allocate(MRTMPPacket.RTMP_HEADER_LENGTH);
				buf.setAutoExpand(true);
				break;
			default:
				break;
		}
		if (buf == null) {
			return;
		}
		buf.putShort(header.getType());
		buf.putShort(MRTMPPacket.JAVA_ENCODING);
		int preserved = header.isDynamic() ? 0x80000000 : 0;
		buf.putInt(preserved);
		buf.putInt(header.getClientId());
		if (header.getType() == MRTMPPacket.CONNECT ||
				header.getType() == MRTMPPacket.CLOSE) {
			buf.putInt(MRTMPPacket.COMMON_HEADER_LENGTH);
			buf.putInt(0);
		} else if (header.getType() == MRTMPPacket.RTMP) {
			buf.putInt(MRTMPPacket.RTMP_HEADER_LENGTH);
			int bodyLengthPos = buf.position();
			buf.putInt(0);
			MRTMPPacket.RTMPHeader rtmpHeader = (MRTMPPacket.RTMPHeader) packet.getHeader();
			buf.putInt(rtmpHeader.getRtmpType());
			MRTMPPacket.RTMPBody body = (MRTMPPacket.RTMPBody) packet.getBody();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(body.getRtmpPacket());
			oos.close();
			buf.put(baos.toByteArray());
			// substract the 8-byte body length field and rtmp type field
			buf.putInt(bodyLengthPos, buf.position() - bodyLengthPos - 8);
		}
		buf.flip();
		out.write(buf);
	}
}
