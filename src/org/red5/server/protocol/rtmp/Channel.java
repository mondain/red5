package org.red5.server.protocol.rtmp;

public class Channel {

	public byte id;
	public int timer = 0;
	public int size = 0;
	public byte dataType = 0;
	public byte[] source = new byte[4];
	public Packet packet = null;

	public Channel(byte channelId){
		this.id = channelId;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Timer: ").append(timer).append(", ");
		sb.append("Size: ").append(size).append(", ");
		sb.append("DateType: ").append(dataType).append(", ");
		sb.append("Source: ").append(source);
		return sb.toString();
	}
	
	public void clearPacket(){
		packet = null;
	}
	
	public Packet getPacket(){
		if(packet == null) 
			packet = new Packet(timer,size,dataType,source);
		return packet;
	}
	
}
