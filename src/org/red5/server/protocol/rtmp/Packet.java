package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;

public class Packet {

	public static final byte TYPE_FUNCTION_CALL = 0x14;
	
	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	
	public static final byte TYPE_CLIENT_BANDWIDTH = 0x06;
	public static final byte TYPE_SERVER_BANDWIDTH = 0x05;
	public static final byte TYPE_PING = 0x04;
	public static final byte TYPE_MISTERY = 0x03;
	
	protected static Log log =
        LogFactory.getLog(Packet.class.getName());
	
	public int timer = 0;
	public int size = 0;
	public byte dataType = 0;
	public byte[] source = new byte[4];
	public ByteBuffer data = null; 
	public boolean full = false;
	
	public Packet(int timer, int size, byte dataType, byte[] source){
		this.timer = timer;
		this.size = size;
		this.dataType = dataType;
		this.source = source;
		this.data = ByteBuffer.allocate(this.size);
	}
	
	public void putChunk(ByteBuffer in, int chunkSize){
		
		int readAmount = ( (size - data.position()) >chunkSize) 
			? chunkSize : size - data.position();
		
		if(readAmount > in.remaining()) readAmount = in.remaining();
		
		log.debug("Copy bytes: "+readAmount);
		
		int limit = in.limit();
		in.limit( in.position() + readAmount );
		data.put(in);
		in.limit( limit );
		
		log.debug("Packet pos: "+data.position());
		
		if(data.position() == size){
			full = true;
			data.flip();
		}
	}
	
	public ByteBuffer getData(){
		if(full) return data;
		else return null; // FIXME THROW EXCEPTION
	}
	
	public void destory(){
		data.release();
		data = null;
	}
	
}
