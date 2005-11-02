package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.rtmp.message.InPacket;
import org.red5.server.rtmp.message.Message;
import org.red5.server.rtmp.message.OutPacket;
import org.red5.server.rtmp.message.PacketHeader;

public class Channel {

	protected static Log log =
        LogFactory.getLog(Channel.class.getName());
	
	private Connection connection = null;
	private byte id = 0;
	//private Stream stream;
	private PacketHeader lastReadHeader = null;
	private PacketHeader lastWriteHeader = null;
	private InPacket inPacket = null;
	private OutPacket outPacket = null;

	public Channel(Connection conn, byte channelId){
		connection = conn;
		id = channelId;
	}
	
	public byte getId(){
		return id;
	}
	
	/*
	public Stream getStream() {
		return stream;
	}
	*/

	public InPacket getInPacket() {
		return inPacket;
	}

	public void setInPacket(InPacket incommingPacket) {
		this.inPacket = incommingPacket;
	}

	public OutPacket getOutPacket() {
		return outPacket;
	}

	public void setOutPacket(OutPacket outgoingPacket) {
		this.outPacket = outgoingPacket;
	}

	public PacketHeader getLastReadHeader() {
		return lastReadHeader;
	}

	public void setLastReadHeader(PacketHeader lastReadHeader) {
		this.lastReadHeader = lastReadHeader;
	}

	public PacketHeader getLastWriteHeader() {
		return lastWriteHeader;
	}

	public void setLastWriteHeader(PacketHeader lastWriteHeader) {
		this.lastWriteHeader = lastWriteHeader;
	}
	
	public void write(Message message){
		write(message, 0, 0);
	}
	
	public void write(Message message, int timer, int source){
		
		final OutPacket packet = new OutPacket();
		final PacketHeader header = new PacketHeader();
		
		header.setChannelId(id);
		header.setTimer(timer);
		header.setSource(source);
		header.setDataType(message.getDataType());
		
		packet.setDestination(header);
		packet.setMessage(message);
		
		connection.write(packet);
		
	}

}
