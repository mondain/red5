package org.red5.server.rtmp.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.protocol.ProtocolDecoderOutput;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.mina.protocol.ProtocolViolationException;
import org.apache.mina.protocol.codec.CumulativeProtocolDecoder;
import org.red5.server.io.Deserializer;
import org.red5.server.protocol.rtmp.RTMPUtils;
import org.red5.server.rtmp.Channel;
import org.red5.server.rtmp.Connection;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.HandshakeConnect;
import org.red5.server.rtmp.message.HandshakeOK;
import org.red5.server.rtmp.message.PacketHeader;

public class ProtocolDecoder extends CumulativeProtocolDecoder implements Constants {

	protected static Log log =
        LogFactory.getLog(ProtocolDecoder.class.getName());
	
	private Deserializer deserializer = null;

	public ProtocolDecoder(){
		super(1024); // default capacity, auto expands
	}
	
	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}
	
	protected boolean doDecode(ProtocolSession session, ByteBuffer in,
			 ProtocolDecoderOutput out) throws ProtocolViolationException {
		
		final Connection conn = (Connection) session.getAttachment();
		
		switch (conn.getState()) {
		
			case Connection.STATE_CONNECT:
				
				if(in.remaining() < HANDSHAKE_SIZE) return false;
				
				final HandshakeConnect connect = new HandshakeConnect();
				connect.getData().put(in).flip();
				out.write(connect);
				conn.setState(Connection.STATE_HANDSHAKE);
				
				return true;
				
			case Connection.STATE_HANDSHAKE:
				
				if(in.remaining() < HANDSHAKE_SIZE) return false;
				
				final HandshakeOK ok = new HandshakeOK();
				ok.getData().put(in).flip();
				out.write(ok);
				conn.setState(Connection.STATE_CONNECTED);
				
				return true;
				
			case Connection.STATE_CONNECTED:
				
				if(in.remaining() < 1) return false;
				
				final byte headerByte = in.get();
				final byte channelId = RTMPUtils.decodeChannelId(headerByte);
				final Channel channel = conn.getChannel(channelId);
				final byte headerSize = (byte) RTMPUtils.decodeHeaderSize(headerByte);
				int headerLength = RTMPUtils.getHeaderLength(headerSize);
				
				if(headerLength > in.remaining()) return false;
				
				final PacketHeader header = decodeHeader(in,headerByte,channel.getLastReadHeader());
				
				if(header==null) 
					throw new ProtocolViolationException("Header is null, check for error");
				
				if(in.remaining() < header.getSize()) return false;
				
				channel.setLastReadHeader(header);
				
				out.write(decodePacket(in,header));
				return true;
		}
		
		in.skip(in.remaining());
		return true;
	}
	
	public PacketHeader decodeHeader(ByteBuffer in, byte headerByte, PacketHeader lastHeader){
		
		final byte channelId = RTMPUtils.decodeChannelId(headerByte);
		final byte headerSize = (byte) RTMPUtils.decodeHeaderSize(headerByte);
		PacketHeader header = new PacketHeader();
		header.setChannelId(channelId);
		
		switch(headerSize){
		
		case HEADER_NEW:
			if(log.isDebugEnabled())
				log.debug("0: Full headers");			
			header.setTimer(RTMPUtils.readMediumInt(in));
			header.setSize(RTMPUtils.readMediumInt(in));
			header.setDataType(in.get());
			header.setSource(in.getInt());
			break;
			
		case HEADER_SAME_SOURCE:
			if(log.isDebugEnabled())
				log.debug("1: Same source as last time");
			header.setTimer(RTMPUtils.readMediumInt(in));
			header.setSize(RTMPUtils.readMediumInt(in));
			header.setDataType(in.get());
			header.setSource(lastHeader.getSource());
			break;
			
		case HEADER_TIMER_CHANGE:
			if(log.isDebugEnabled())
				log.debug("2: Only the timer changed");
			header.setTimer(RTMPUtils.readMediumInt(in));
			header.setSize(lastHeader.getSize());
			header.setDataType(lastHeader.getDataType());
			header.setSource(lastHeader.getSource());
			break;
			
		case HEADER_CONTINUE:
			header = lastHeader;
			break;
			
		default:
			log.error("Unexpected header size: "+headerSize);
			return null;
		
		}
		return header;
	}
	
	private Object decodePacket(ByteBuffer in, PacketHeader header) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
