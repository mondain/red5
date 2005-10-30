package org.red5.server.rtmp.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.protocol.ProtocolEncoderOutput;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.mina.protocol.ProtocolViolationException;
import org.red5.server.io.BufferUtils;
import org.red5.server.io.Serializer;
import org.red5.server.protocol.rtmp.RTMPUtils;
import org.red5.server.rtmp.Connection;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Packet;
import org.red5.server.rtmp.message.PacketHeader;
import org.red5.server.utils.BufferLogUtils;

public class ProtocolEncoder implements org.apache.mina.protocol.ProtocolEncoder, Constants {

	protected static Log log =
        LogFactory.getLog(ProtocolEncoder.class.getName());
	
	private Serializer serializer = null;
	
	public void encode(ProtocolSession session, Object message, ProtocolEncoderOutput out) 
		throws ProtocolViolationException {
		
		final Connection conn = (Connection) session.getAttachment();
		final Packet packet = (Packet) message;
		final PacketHeader header = packet.getDestination();
		final ByteBuffer data = packet.getData();
		
		ByteBuffer headers = ByteBuffer.allocate(9);
		byte headerByte = RTMPUtils.encodeHeaderByte(HEADER_NEW, header.getChannelId());
		headers.put(headerByte);
		
		// write timer
		RTMPUtils.writeMediumInt(headers, header.getTimer());
		
		// write size
		RTMPUtils.writeMediumInt(headers, header.getSize());
		
		// write datatype
		headers.put(header.getDataType());
		
		// write source
		headers.putInt(header.getSource());
	
		
		ByteBuffer buf = null;
		// write all the chunks.. oh.. chunky!
		//if(packet.getDataType() == Packet.TYPE_VIDEO) {
			buf = ByteBuffer.allocate(2048);
		//} else {
			//out = ByteBuffer.allocate(1024);
		//}
		buf.setAutoExpand(true);
		
		//conn.setLastWriteChannel(this);
		//session.getIoSession().write(headers,null);
		headers.flip();
		
		buf.put(headers);
		
		int numChunks =  (int) Math.ceil((header.getSize() / (float) 128));
		
		if(log.isDebugEnabled())
			log.debug("Num chunks: "+numChunks);
		if(numChunks > 1){
			if(log.isDebugEnabled())
				log.debug("Writing "+numChunks+" chunks");
			//ByteBuffer chunk; // = packet.getChunk(0);
			/*
			BufferUtils.put(buff, packet.getData() , chunkSize);
			
			//session.getIoSession().write(chunk,null);
			for(int i=0; i<=numChunks && (chunk = packet.getChunk(i)) != null; i++){
				//log.debug("Continue writing");
				if(i>0) buf.put(RTMPUtils.encodeHeaderByte(HEADER_CONTINUE, header.getChannelId()));
				buf.put(chunk);
			}
			*/
		} else if (numChunks == 1){
			// no need to chunk it, as its only one piece
			// log.debug("Writing a single chunk");
			ByteBuffer body = packet.getData();
			body.acquire(); // we dont want it released
			//session.getIoSession().write(body,null);
			buf.put(body);
		} else {
			if(log.isDebugEnabled())
				log.debug("No chunks to write.");
		}
		
		buf.flip();
		
		//log.debug("Position: "+out.position());
		//log.debug("Limit: "+out.limit());
		
		if(log.isDebugEnabled()){
			log.debug(" ====== WRITE DATA ===== ");
			BufferLogUtils.debug(log,"Write raw response",buf);
		}
		
		out.write(buf);
		
		// this will destroy the packet if there are no more refs
		packet.release();
		
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public ByteBuffer encodeHeader(PacketHeader header, PacketHeader lastHeader){
		ByteBuffer buf = ByteBuffer.allocate(9);
		byte headerByte = RTMPUtils.encodeHeaderByte(HEADER_NEW, header.getChannelId());
		buf.put(headerByte);
		
		// write timer
		RTMPUtils.writeMediumInt(buf, header.getTimer());
		
		// write size
		RTMPUtils.writeMediumInt(buf, header.getSize());
		
		// write datatype
		buf.put(header.getDataType());
		
		// write source
		buf.putInt(header.getSource());
		return buf;
	}
	
	public ByteBuffer encodePacket(Packet packet){
		return null;
	}
	
}
