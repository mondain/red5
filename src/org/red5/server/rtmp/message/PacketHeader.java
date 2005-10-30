package org.red5.server.rtmp.message;

public class PacketHeader {

	private byte channelId = 0;
	private int timer = 0;
	private int size = 0;
	private byte dataType = 0;
	private int source = 0;
	
	public byte getChannelId() {
		return channelId;
	}
	
	public void setChannelId(byte channelId) {
		this.channelId = channelId;
	}
	
	public byte getDataType() {
		return dataType;
	}
	
	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSource() {
		return source;
	}
	
	public void setSource(int source) {
		this.source = source;
	}
	
	public int getTimer() {
		return timer;
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
	}

}
