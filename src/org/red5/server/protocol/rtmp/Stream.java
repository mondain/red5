package org.red5.server.protocol.rtmp;

import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.flv.FLVBody;
import org.red5.server.io.flv.FLVDecoder;
import org.red5.server.io.flv.FLVHeader;
import org.red5.server.io.flv.FLVTag;

public class Stream {
	
	public static final byte STATE_STOPPED = 0x00;
	public static final byte STATE_PLAYING = 0x02;
	public static final byte STATE_PAUSED = 0x03;
	public static final byte STATE_END = 0x04;
	
	protected Channel channel;
	protected String flvPath;
	protected int currentTag = 0;
	protected int numTags = 0;
	
	private FLVDecoder decoder = null;
	private FLVHeader header = null;
	private FLVBody body = null;
	private FLVTag tag = null;

	protected byte status = STATE_STOPPED;
	
	public Stream(Channel channel){
		this.channel = channel;
	}
	
	public void setup(String flvPath){
		this.flvPath = flvPath;
		System.out.println("flvPath" + flvPath);
		// read the flv header here
		// get a hold on the mapped body
		
		// Grab FLVDecoder
		decoder = new FLVDecoder(this.flvPath);
		// Grab FLVHeader
		header = decoder.decodeHeader();
		// Grab FLVBody
		body = decoder.decodeBody();
		
		writeHeader(header);
	}
	
	public void start(){
		status = STATE_PLAYING;
		writeNextPacket();
	}
	
	public void seek(int pos){
		status = STATE_PLAYING;
		//TODO add seek support
	}
	
	public void end(){
		status = STATE_END;
	}
	
	public boolean hasMorePackets(){		
		//return (currentTag < numTags);
		return body.hasRemaining();
	}
	
	public void writeHeader(FLVHeader header2) {
		
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put(header2.getSignature());
		buf.put(header2.getVersion());
		
		byte type = (byte) 0x00;
		if(header2.getFlagAudio()) {
			type |= (byte) (0x01 << 2);
		}
		
		if(header2.getFlagVideo()) {
			type |= (byte) 0x01;
		}
		
		buf.put(type);
		buf.putInt(header2.getDataOffset());
		
		int timer = 3;
		byte tagType = Packet.TYPE_VIDEO;
		// Still need to figure out how to create packet
		Packet packet = new Packet(buf, timer, tagType, 3);
		channel.writePacket(packet, this);
		
	}
	
	public void writeNextPacket(){
		channel.writePacket(getNextPacket(), this);
	}
	
	public Packet getNextPacket(){
		//currentTag++;
		// get the next chunk as nio read only buffer
		// create a new packet with correct source etc
		
		// After getting body, we can start calling body.getNextTag();
		// Grab FLVTag
		// a FLVBody consists of a int representing the previousSize and
		// a FLVTag
		int previousTagSize = body.getPreviousTagSize();
		tag = body.getNextTag();
		byte tagType = (byte) tag.getTagType();
		int dataSize = tag.getDataSize();
		byte[] timeStamp = (byte[]) tag.getTimeStamp();
		int reserved = tag.getReserved();
		byte[] data = (byte[]) tag.getData();
		
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.putInt(previousTagSize);
		buf.put(tagType);
		buf.putInt(dataSize);
		buf.put(timeStamp);
		buf.putInt(reserved);
		buf.put(data);
		
		int timer = 1;
		
		
		// Still need to figure out how to create packet
		Packet packet = new Packet(buf, timer, tagType, 3);
		return packet;
	}

}
