package org.red5.server.rtmp.message;

import org.apache.mina.common.ByteBuffer;

public class Packet implements Constants {
	
	private byte dataType = 0;
	private int refCount = 0;
	private PacketHeader source = null;
	private PacketHeader destination = null;
	private ByteBuffer data;
	
	public Packet(int initialCapacity){
		data = ByteBuffer.allocate(initialCapacity);
		data.setAutoExpand(true);
		data.acquire(); // this stops it being released
		acquire();
	}
	
	public PacketHeader getSource() {
		dataType = source.getDataType();
		return source;
	}

	public void setSource(PacketHeader source) {
		this.source = source;
	}
	
	public PacketHeader getDestination() {
		return destination;
	}

	public void setDestination(PacketHeader destination) {
		this.destination = destination;
	}

	public byte getDataType() {
		return dataType;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public void acquire(){
		refCount++;
	}
	
	public void release(){
		refCount--;
		if(refCount == 0){
			source = null;
			data.release();
			doRelease();
		}
	}
	
	protected void doRelease(){
		// override
	}
	
	public ByteBuffer getData() {
		return data;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}
	
}
