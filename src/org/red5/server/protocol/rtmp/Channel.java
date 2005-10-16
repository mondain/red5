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
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Output;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.red5.server.utils.BufferLogUtils;
import org.red5.server.utils.HexDump;

public class Channel {

	protected static Log log =
        LogFactory.getLog(Channel.class.getName());
	
	public static final byte HEADER_NEW = 0x00;
	public static final byte HEADER_SAME_SOURCE = 0x01;
	public static final byte HEADER_TIMER_CHANGE = 0x02;
	public static final byte HEADER_CONTINUE = 0x03;
	
	private byte id;

	private Packet lastWritePacket = null;
	private Packet lastReadPacket = null;
	private Connection connection = null;

	private int timer = 0;
	private int size = 0;
	private byte dataType = 0;
	private int source = 0;
	
	private ByteBuffer headerBuf = ByteBuffer.allocate(12);
	private byte headerSize = 0;
	
	private boolean finishedReadHeaders = true;
	private boolean finishedReadBody = true;
	private boolean finishedReadChunk = true;
	
	private StatusObjectService statusObjectService;
	
	public Channel(Connection session, byte channelId){
		if(log.isDebugEnabled())
			log.debug("New channel: "+id);
		this.connection = session;
		this.id = channelId;
	}
	
	public byte getId() {
		return id;
	}
	
	public boolean isFinishedRead() {
		return (finishedReadHeaders && finishedReadBody && finishedReadChunk);
	}

	
	public boolean isFinishedReadHeaders() {
		return finishedReadHeaders;
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

	public Connection getConnection() {
		return connection;
	}

	protected int getHeaderLength(byte headerSize){
		switch(headerSize){
		case HEADER_NEW:
			return 12;
		case HEADER_SAME_SOURCE:
			return 8;
		case HEADER_TIMER_CHANGE:
			return 4;
		case HEADER_CONTINUE:
			return 1;
		default:
			return -1;
		}
	}
	
	
	
	protected Packet readPacket(ByteBuffer in){
		
		
		log.debug("Position: "+in.position());
		
		// if the chunk did not finish attempt to finish this time
		if(!finishedReadChunk){
			//log.debug("Not finished chunk, continue");
			finishedReadChunk = lastReadPacket.readChunkFrom(in);
			return lastReadPacket;
		} else {
			//log.debug("Chunk read ok last time");
		}
		
		
		ByteBuffer header = null;
		
		if(finishedReadHeaders){
			
			headerSize = (byte) RTMPUtils.decodeHeaderSize(in.get());
			int headerLength = getHeaderLength(headerSize);
			
			boolean bufferHeaders = (headerLength > in.remaining());
			
			if(bufferHeaders){
				//log.debug("Buffering packet header");
				headerBuf.position(0);
				headerBuf.limit(headerLength);
				in.position(in.position()-1);
				headerBuf.put(in);
				finishedReadHeaders = false;
				return null;
			} else {
				header = in;
			}
					
		} else {
		
			boolean continueBuffer = (headerBuf.remaining() > in.remaining()); 
			if(continueBuffer){
				//log.debug("Continuing packet header");
				headerBuf.put(in);
				return null;
			} else {
				//log.debug("Finished buffering header");
				int limit = in.limit();
				in.limit(in.position()+headerBuf.remaining());
				headerBuf.put(in);
				in.limit(limit);
				headerBuf.flip();
				header = headerBuf;
				header.get();
				log.debug(HexDump.formatHexDump(header.getHexDump()));
			}
			
		}
		
		finishedReadHeaders = true;
		
		Packet packet = null;
		boolean newPacket = true;
		
		switch(headerSize){
		
		case HEADER_NEW:
			if(log.isDebugEnabled())
				log.debug("0: Full headers");
			timer = RTMPUtils.readMediumInt(header);
			size = RTMPUtils.readMediumInt(header);
			dataType = header.get();
			source = header.getInt();
			break;
			
		case HEADER_SAME_SOURCE:
			if(log.isDebugEnabled())
				log.debug("1: Same source as last time");
			timer = RTMPUtils.readMediumInt(header);
			size = RTMPUtils.readMediumInt(header);
			dataType = header.get();
			break;
			
		case HEADER_TIMER_CHANGE:
			if(log.isDebugEnabled())
				log.debug("2: Only the timer changed");
			timer = RTMPUtils.readMediumInt(header);
			break;
			
		case HEADER_CONTINUE:
			if(log.isDebugEnabled())
				log.debug("3: Continue, no change");
			newPacket = (lastReadPacket != null && lastReadPacket.isSealed());
			break;
			
		default:
			log.error("Unexpected header size: "+headerSize);
			break;
		
		}
		
		packet = (newPacket) ? 
				new Packet(this,timer,size,dataType,source) :
				lastReadPacket;

		if(packet.getSize()>0){
			finishedReadBody = false;
		}
				
		finishedReadChunk = packet.readChunkFrom(in);
		
		if(packet.isSealed()) finishedReadBody = true;
		
		lastReadPacket = packet;
		connection.setLastReadChannel(this);

		return packet;
	}
	
	public void writePacket(Packet packet){
		writePacket(packet, null);
	}
	
	public void writePacket(Packet packet, Object stream){
		
		ByteBuffer headers = ByteBuffer.allocate(9);
		
		// Note: its probably possible to do this with less bytes
		// by using the different header lengths like in readPacket
		// but for now im just going to send a full header

		// Channel channel = session.getLastWriteChannel(); later
		
		// write header size
		// write channel id
		
		byte headerByte = RTMPUtils.encodeHeaderByte(HEADER_NEW, id);
		
		headers.put(headerByte);
		
		// write timer
		RTMPUtils.writeMediumInt(headers, packet.getTimer());
		
		if(log.isDebugEnabled())
			log.debug("Packet size: "+packet.getSize());
		// write size
		RTMPUtils.writeMediumInt(headers, packet.getSize());
		
		// write datatype
		headers.put(packet.getDataType());
		
		// write source
		headers.putInt(packet.getSource());
		
		// write all the chunks.. oh.. chunky!
		ByteBuffer out = ByteBuffer.allocate(2048);
		out.setAutoExpand(true);
		
		connection.setLastWriteChannel(this);
		//session.getIoSession().write(headers,null);
		headers.flip();
		out.put(headers);
		
		int numChunks = packet.getNumberOfChunks();
		
		if(log.isDebugEnabled())
			log.debug("Num chunks: "+numChunks);
		if(numChunks > 1){
			if(log.isDebugEnabled())
				log.debug("Writing "+numChunks+" chunks");
			ByteBuffer chunk; // = packet.getChunk(0);
			//session.getIoSession().write(chunk,null);
			for(int i=0; i<=numChunks && (chunk = packet.getChunk(i)) != null; i++){
				//log.debug("Continue writing");
				if(i>0) out.put(RTMPUtils.encodeHeaderByte(HEADER_CONTINUE, id));
				out.put(chunk);
				
			}
		} else if (numChunks == 1){
			// no need to chunk it, as its only one piece
			// log.debug("Writing a single chunk");
			ByteBuffer body = packet.getData();
			body.acquire(); // we dont want it released
			//session.getIoSession().write(body,null);
			out.put(body);
		} else {
			if(log.isDebugEnabled())
				log.debug("No chunks to write.");
		}
		
		out.flip();
		
		//log.debug("Position: "+out.position());
		//log.debug("Limit: "+out.limit());
		
		if(log.isDebugEnabled()){
			log.debug(" ====== WRITE DATA ===== ");
			BufferLogUtils.debug(log,"Write raw response",out);
		}
		
		connection.getIoSession().write(out,stream);
		
		// this will destroy the packet if there are no more refs
		packet.release();
		
	}
	
	
	
}
