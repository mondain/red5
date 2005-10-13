package org.red5.server.protocol.rtmp;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.utils.HexDump;

public class Packet {

	public static final byte TYPE_FUNCTION_CALL = 0x14;
	public static final byte TYPE_FUNCTION_CALL_NOREPLY = 0x12;
	
	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	
	public static final byte TYPE_CLIENT_BANDWIDTH = 0x06;
	public static final byte TYPE_SERVER_BANDWIDTH = 0x05;
	public static final byte TYPE_PING = 0x04;
	public static final byte TYPE_MISTERY = 0x03;
	
	// somthing to do with shared object
	public static final byte TYPE_SHARED_OBJECT = 0x13;
	public static final byte TYPE_SHARED_OBJECT_CONNECT = 0x19;
	
		
	protected static Log log =
        LogFactory.getLog(Packet.class.getName());
	
	private int timer = 0;
	private int size = 0;
	private byte dataType = 0;
	private int source = 0;
	private ByteBuffer data = null; 
	private boolean isSealed = false;
	private Channel sourceChannel = null;
	private int refCount = 1;
	private int numChunks = 0;
	private int chunksRead = 0;
	private boolean finishedReadChunk = true;
	private int chunkRemaining = 0;
	
	public Packet(Channel sourceChannel, int timer, int size, byte dataType, int source){
		this.sourceChannel = sourceChannel;
		this.timer = timer;
		this.size = size;
		this.dataType = dataType;
		this.source = source;
		this.data = ByteBuffer.allocate(this.size);
		this.isSealed = false;
		this.numChunks = (int) Math.ceil((this.size / (float) getMaxChunkSize()));
		this.chunksRead = 0;
	}
	
	public Packet(ByteBuffer data, int timer, byte dataType, int source){
		this.sourceChannel = null;
		this.timer = timer;
		this.size = data.limit();
		this.dataType = dataType;
		this.source = source;
		this.data = data;
		this.isSealed = true;
		this.numChunks = (int) Math.ceil((this.size / (float) getMaxChunkSize()));
		this.chunksRead = this.numChunks;
	}
	
	public int getNumberOfChunks(){
		return numChunks;
	}
	
	public int getMaxChunkSize(){
		return (dataType == TYPE_AUDIO) ? 64 : 128;
	}
	
	public boolean readChunkFrom(ByteBuffer in){
		
		if(isSealed) {
			log.error("Calling readChunkFrom on packet which is sealed, returning false");
			return false;
		}
			
		int chunkSize = (finishedReadChunk) ? getMaxChunkSize() : chunkRemaining;
		
		int readAmount = ( (size - data.position()) > chunkSize) 
			? chunkSize : size - data.position();

		if(finishedReadChunk){
			// start a new chunk
			chunkRemaining = readAmount;
		}
		
		boolean fullRead = (readAmount <= in.remaining());		
		if(!fullRead) readAmount = in.remaining();
		chunkRemaining -= readAmount;
		finishedReadChunk = fullRead;
		
		log.debug("Copy bytes: "+readAmount);
		
		int limit = in.limit();
		in.limit( in.position() + readAmount );
		data.put(in);
		in.limit( limit );
		
		log.debug("Packet pos: "+data.position());
		log.debug("Size: "+size);
		
		if(data.position() == size){
			log.debug("Finished all chunks, end of read");
			data.flip();
			data.mark();
			isSealed = true;
		}
		
		log.debug("Finished read chunk: "+finishedReadChunk);
		
		if(finishedReadChunk){
			chunksRead++;
		}
		
		return finishedReadChunk;
	}
	
	public ByteBuffer getChunk(int num){
		
		if(!isSealed || num < 0 || num >= getNumberOfChunks()) 
			return null;
		
		int chunkSize = getMaxChunkSize();
			
		ByteBuffer chunk = ByteBuffer.wrap(data.buf());
		
		int position = num * chunkSize;
		chunk.position(position);

		int writeAmount = ( (size - chunk.position()) > chunkSize) 
			? chunkSize : size - chunk.position();
		
		chunk.limit(position + writeAmount);
		
		return chunk;
	}
	
	public ByteBuffer getData(){
		return data;
	}

	public byte getDataType() {
		return dataType;
	}
	
	public int getSize() {
		return size;
	}

	public int getSource() {
		return source;
	}
	
	public int getTimer() {
		return timer;
	}
	
	public boolean isSealed() {
		return isSealed;
	}

	public Channel getSourceChannel() {
		return sourceChannel;
	}

	public void acquire(){
		refCount++;
	}
	
	public void release(){
		refCount--;
		if(refCount == 0){
			data.release();
			data = null;
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Timer: ").append(timer).append(", ");
		sb.append("Size: ").append(size).append(", ");
		sb.append("DateType: ").append(dataType).append(", ");
		sb.append("Source: ").append(source).append(", ");
		sb.append("Source Channel: ").append(sourceChannel).append("\n");
		sb.append(HexDump.formatHexDump(data.getHexDump()));
		return sb.toString();
	}
	
	
	/*
	public boolean writeChunkTo(ByteBuffer out){
		
		int chunkSize = getMaxChunkSize();
		
		int writeAmount = ( (size - data.position()) >chunkSize) 
			? chunkSize : size - data.position();
				
		int limit = data.limit();
		data.limit( data.position() + writeAmount );
		out.put(data);
		data.limit( limit );
		
		log.debug("Packet pos: "+data.position());
		
		if(data.position() == size){
			data.reset();
			return false; 
		} else return true;
	}
	*/
	
}
