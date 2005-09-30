package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.utils.HexDump;

public class Channel {

	protected static Log log =
        LogFactory.getLog(Channel.class.getName());
	
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
		
		byte headerByte = in.get();
		byte headerSize = (byte) RTMPUtils.decodeHeaderSize(headerByte);
		
		// This is likely to be wrong
		byte kont = 0xC3 - 256;
		
		if(headerByte == kont && !lastReadPacket.isSealed()){
			log.debug("Continue last packet");
			lastReadPacket.readChunkFrom(in);
			return lastReadPacket;
		}
		
		Packet packet = null;
		
		log.debug(">> Header size: "+headerSize);

		switch(headerSize){
		case 0:
			log.debug("Full headers");
			timer = RTMPUtils.readMediumInt(in);
			size = RTMPUtils.readMediumInt(in);
			dataType = in.get();
			source = in.getInt();
			break;
			
		case 1:
			log.debug("Source the same");
			timer = RTMPUtils.readMediumInt(in);
			size = RTMPUtils.readMediumInt(in);
			dataType = in.get();
			break;
			
		case 2:
			log.debug("Only timer change");
			timer = RTMPUtils.readMediumInt(in);
			break;
			
		case 3:
			log.debug("Same as last");
			break;
		
		}
		
		
		packet = new Packet(this,timer,size,dataType,source);
		
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
		byte headerSize = 0x00;
		headers.put(RTMPUtils.encodeHeaderByte(headerSize, id));
		
		// write timer
		RTMPUtils.writeMediumInt(headers, packet.getTimer());
	
		// write size
		log.debug("Size: "+packet.getSize());
		RTMPUtils.writeMediumInt(headers, packet.getSize());
		
		// write datatype
		headers.put(packet.getDataType());
		
		// write source
		headers.putInt(packet.getSource());
		
		// write all the chunks.. oh.. chunky!
		ByteBuffer out = ByteBuffer.allocate(2048);
		
		session.setLastWriteChannel(this);
		//session.getIoSession().write(headers,null);
		headers.flip();
		out.put(headers);
		
		int numChunks = packet.getNumberOfChunks();
		log.debug("Num chunks: "+numChunks);
		if(numChunks > 1){
			log.debug("Writing "+numChunks+" chunks");
			ByteBuffer chunk = packet.getChunk(0);
			ByteBuffer kont = ByteBuffer.wrap(new byte[]{0xC3 - 256});
			session.getIoSession().write(chunk,null);
			for(int i=1; i<numChunks && (chunk = packet.getChunk(i)) != null; i++){
				// write the continuation byte
				//session.getIoSession().write(kont,null);
				out.put(kont);
				// and the chunk
				//session.getIoSession().write(chunk,null);
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
		
		log.debug(HexDump.formatHexDump(out.getHexDump()));
		session.getIoSession().write(out,null);
		
		// this will destroy the packet if there are no more refs
		packet.release();
		
	}
	
	
	
}
