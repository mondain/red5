package org.red5.server.rtmp.message;

public class StreamBytesRead extends Packet {
	
	private static final int INITIAL_CAPACITY = 4;
	
	private int bytesRead = 0;

	public StreamBytesRead(){
		super(INITIAL_CAPACITY);
	}
	
	public int getBytesRead(){
		return bytesRead;
	}

	public void setBytesRead(int bytesRead) {
		this.bytesRead = bytesRead;
	}

	protected void doRelease() {
		bytesRead = 0;
	}
	
	public String toString(){
		return "StreamBytesRead: "+bytesRead;
	}
	
}
