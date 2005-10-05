package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.utils.BufferLogUtils;
import org.red5.server.utils.HexDump;

public class Channel {

	protected static Log log =
        LogFactory.getLog(Channel.class.getName());
	
	public static final byte HEADER_NEW = 0x00;
	public static final byte HEADER_SAME_SOURCE = 0x01;
	public static final byte HEADER_TIMER_CHANGE = 0x02;
	public static final byte HEADER_CONTINUE = 0x03;
	
	private byte id;

	private Packet lastWritePacket = null;
	private Packet lastReadPacket = null;
	private Session session = null;

	private int timer = 0;
	private int size = 0;
	private byte dataType = 0;
	private int source = 0;
	
	public Channel(Session session, byte channelId){
		this.session = session;
		this.id = channelId;
	}
	
	public byte getId() {
		return id;
	}
	
	public Packet getLastReadPacket() {
		return lastReadPacket;
	}

	public void setLastReadPacket(Packet lastReadPacket) {
		this.lastReadPacket = lastReadPacket;
	}

	public Packet getLastWritePacket() {
		return lastWritePacket;
	}

	public void setLastWritePacket(Packet lastWritePacket) {
		this.lastWritePacket = lastWritePacket;
	}

	public Session getSession() {
		return session;
	}

	public Packet readPacket(ByteBuffer in){
		return readPacket(in, false);
	}
	
	protected Packet readPacket(ByteBuffer in, boolean all){
		
		log.debug("Position: "+in.position());
		
		byte headerByte = in.get();
		byte headerSize = (byte) RTMPUtils.decodeHeaderSize(headerByte);
		
		// This is likely to be wrong
		/* and it was :)
		byte kont = 0xC3 - 256;
		
		if(headerByte == kont && !lastReadPacket.isSealed()){
			log.debug("Continue last packet");
			lastReadPacket.readChunkFrom(in);
			return lastReadPacket;
		}
		*/
		
		Packet packet = null;
		
		boolean newPacket = true;

		switch(headerSize){
		
		case HEADER_NEW:
			log.debug("0: Full headers");
			timer = RTMPUtils.readMediumInt(in);
			size = RTMPUtils.readMediumInt(in);
			dataType = in.get();
			source = in.getInt();
			break;
			
		case HEADER_SAME_SOURCE:
			log.debug("1: Same source as last time");
			timer = RTMPUtils.readMediumInt(in);
			size = RTMPUtils.readMediumInt(in);
			dataType = in.get();
			break;
			
		case HEADER_TIMER_CHANGE:
			log.debug("2: Only the timer changed");
			timer = RTMPUtils.readMediumInt(in);
			break;
			
		case HEADER_CONTINUE:
			log.debug("3: Continue, no change");
			newPacket = false;
			break;
			
		default:
			log.error("Unexpected header size: "+headerSize);
			break;
		
		}
		
		packet = (newPacket) ? 
				new Packet(this,timer,size,dataType,source) :
				lastReadPacket;
		
		if(all){
			while(packet.readChunkFrom(in));
		} else {
			packet.readChunkFrom(in);
		}
		
		lastReadPacket = packet;
		session.setLastReadChannel(this);
		
		return packet;
	}
	
	public void writePacket(Packet packet){
		
		ByteBuffer headers = ByteBuffer.allocate(9);
		
		// Note: its probably possible to do this with less bytes
		// by using the different header lengths like in readPacket
		// but for now im just going to send a full header

		// Channel channel = session.getLastWriteChannel(); later
		
		// write header size
		// write channel id
		
		byte headerByte = RTMPUtils.encodeHeaderByte(HEADER_NEW, id);
		
		headers.put(headerByte);
		
		// write timer
		RTMPUtils.writeMediumInt(headers, packet.getTimer());
		
		log.debug("Packet size: "+packet.getSize());
		// write size
		RTMPUtils.writeMediumInt(headers, packet.getSize());
		
		// write datatype
		headers.put(packet.getDataType());
		
		// write source
		headers.putInt(packet.getSource());
		
		// write all the chunks.. oh.. chunky!
		ByteBuffer out = ByteBuffer.allocate(2048);
		out.setAutoExpand(true);
		
		session.setLastWriteChannel(this);
		//session.getIoSession().write(headers,null);
		headers.flip();
		out.put(headers);
		
		int numChunks = packet.getNumberOfChunks();
		log.debug("Num chunks: "+numChunks);
		if(numChunks > 1){
			log.debug("Writing "+numChunks+" chunks");
			ByteBuffer chunk; // = packet.getChunk(0);
			//session.getIoSession().write(chunk,null);
			for(int i=0; i<=numChunks && (chunk = packet.getChunk(i)) != null; i++){
				log.debug("Continue writing");
				if(i>0) out.put(RTMPUtils.encodeHeaderByte(HEADER_CONTINUE, id));
				out.put(chunk);
				
			}
		} else if (numChunks == 1){
			// no need to chunk it, as its only one piece
			log.debug("Writing a single chunk");
			ByteBuffer body = packet.getData();
			body.acquire(); // we dont want it released
			//session.getIoSession().write(body,null);
			out.put(body);
			
			
			
			
		} else {
			log.debug("No chunks to write.");
		}
		
		out.flip();
		log.debug("Position: "+out.position());
		log.debug("Limit: "+out.limit());
		
		if(log.isDebugEnabled()){
			log.debug(" ====== WRITE DATA ===== ");
			BufferLogUtils.debug(log,"Write raw response",out);
		}
		
		session.getIoSession().write(out,null);
		
		// this will destroy the packet if there are no more refs
		packet.release();
		
	}
	

	
}
