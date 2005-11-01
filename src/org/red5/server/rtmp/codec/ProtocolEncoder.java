package org.red5.server.rtmp.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.protocol.ProtocolEncoderOutput;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.mina.protocol.ProtocolViolationException;
import org.red5.server.io.BufferUtils;
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Output;
import org.red5.server.protocol.rtmp.RTMPUtils;
import org.red5.server.rtmp.Channel;
import org.red5.server.rtmp.Connection;
import org.red5.server.rtmp.message.AudioData;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Invoke;
import org.red5.server.rtmp.message.Message;
import org.red5.server.rtmp.message.Notify;
import org.red5.server.rtmp.message.OutPacket;
import org.red5.server.rtmp.message.PacketHeader;
import org.red5.server.rtmp.message.Ping;
import org.red5.server.rtmp.message.StreamBytesRead;
import org.red5.server.rtmp.message.VideoData;
import org.red5.server.utils.BufferLogUtils;

public class ProtocolEncoder implements org.apache.mina.protocol.ProtocolEncoder, Constants {

	protected static Log log =
        LogFactory.getLog(ProtocolEncoder.class.getName());
	
	private Serializer serializer = null;
	
	public void encode(ProtocolSession session, Object message, ProtocolEncoderOutput out) 
		throws ProtocolViolationException {
		
		log.debug("Encode");
		
		try {
			final Connection conn = (Connection) session.getAttachment();
			final OutPacket packet = (OutPacket) message;
			final PacketHeader header = packet.getDestination();
			final Channel channel = conn.getChannel(header.getChannelId());	
			
			if(conn.getState() == Connection.STATE_HANDSHAKE){
				log.debug("Sending handshake");
				out.write(packet.getMessage().getData());
				return;
			}
				
			final ByteBuffer data = encodeMessage(packet.getMessage());
			header.setSize(data.limit());
			ByteBuffer headers = encodeHeader(header,channel.getLastWriteHeader());

			ByteBuffer buf = null;
			buf = ByteBuffer.allocate(2048); // FIX ME
			buf.setAutoExpand(true);

			headers.flip();	
			buf.put(headers);
	
			int numChunks =  (int) Math.ceil((header.getSize() / (float) header.getChunkSize()));
			
			
			// TODO: threadsafe way of doing this reusing the data here, im thinking a lock
			for(int i=0; i<numChunks; i++){
				int readAmount = (data.remaining()>header.getChunkSize()) 
					? header.getChunkSize() : data.remaining();
				log.debug("putting chunk");
				BufferUtils.put(buf,data,readAmount);
				if(data.remaining()>0){
					log.debug("header byte");
					buf.put(RTMPUtils.encodeHeaderByte(HEADER_CONTINUE, header.getChannelId()));
				}
			}
			
			buf.flip();
			
			if(log.isDebugEnabled()){
				log.debug(" ====== WRITE DATA ===== ");
				BufferLogUtils.debug(log,"Write raw response",buf);
			}
			
			out.write(buf);
			
			// this will destroy the packet if there are no more refs
			packet.getMessage().release();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public ByteBuffer encodeMessage(Message message){
		if(message.isSealed()){
			return message.getData();
		}
		switch(message.getDataType()){
		case TYPE_INVOKE:
			encodeInvoke((Invoke) message);
			break;
		case TYPE_NOTIFY:
			encodeNotify((Notify) message);
			break;
		case TYPE_PING:
			encodePing((Ping) message);
			break;
		case TYPE_STREAM_BYTES_READ:
			encodeStreamBytesRead((StreamBytesRead) message);
			break;
		case TYPE_AUDIO_DATA:
			encodeAudioData((AudioData) message);
			break;
		case TYPE_VIDEO_DATA:
			encodeVideoData((VideoData) message);
			break;
		}
		message.getData().flip();
		message.setSealed(true);
		return message.getData();
	}
	
	public void encodeInvoke(Invoke invoke){
		log.debug("Encode invoke");
		Output output = new Output(invoke.getData());
		serializer.serialize(output, "/result"); // seems right
		// dont know what this number does, so im just sending it back
		serializer.serialize(output, new Integer(invoke.getInvokeId())); 
		serializer.serialize(output, null);
		Object[] param = invoke.getCall().getArguments();
		for(int i=0; i<param.length; i++){
			serializer.serialize(output, param[i]);
		}
		//invoke.getData().flip();
	}
	
	public void encodeNotify(Notify notify){

	}
	
	public void encodePing(Ping ping){

	}
	
	public void encodeStreamBytesRead(StreamBytesRead streamBytesRead){

	}
	
	public void encodeAudioData(AudioData audioData){

	}
	
	public void encodeVideoData(VideoData videoData){

	}
	
	public void enchunkData(ByteBuffer in, int chunkSize){

	}
		
	
}
