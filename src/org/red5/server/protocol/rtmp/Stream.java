package org.red5.server.protocol.rtmp;

public class Stream {
	
	public static final byte STATE_STOPPED = 0x00;
	public static final byte STATE_PLAYING = 0x02;
	public static final byte STATE_PAUSED = 0x03;
	public static final byte STATE_END = 0x04;
	
	protected Channel channel;
	protected String flvPath;
	protected int currentTag = 0;
	protected int numTags = 0;

	protected byte status = STATE_STOPPED;
	
	public Stream(Channel channel){
		this.channel = channel;
	}
	
	public void setup(String flvPath){
		this.flvPath = flvPath;
		// read the flv header here
		// get a hold on the mapped body
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
		return (currentTag < numTags);
	}
	
	public void writeNextPacket(){
		channel.writePacket(getNextPacket(), this);
	}
	
	public Packet getNextPacket(){
		currentTag++;
		// get the next chunk as nio read only buffer
		// create a new packet with correct source etc
		return null;
	}

}
