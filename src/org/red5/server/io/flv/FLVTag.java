package org.red5.server.io.flv;

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
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

import java.nio.ByteBuffer;

public class FLVTag {
	
	private byte tagType = 0x00; //audio=8, video=9
	private byte[] dataSize;
	private byte[] timeStamp;
	private int reserved = 0x00;
	private byte[] data; // audio or video data
	private ByteBuffer buf;

	public FLVTag(ByteBuffer buf) {
		// TODO Auto-generated constructor stub
		this.buf = buf;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		
		this.data = data;
	}

	public byte[] getDataSize() {
		return dataSize;
	}

	public void setDataSize(byte[] dataSize) {
		this.dataSize = (byte[]) dataSize;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	public byte getTagType() {
		return tagType;
	}

	public void setTagType(byte tagType) {
		this.tagType = tagType;
	}

	public byte[] getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(byte[] timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String toString() {
		String ret = "";
		//ret += "SIGNATURE: \t" + getSIGNATURE() + "\n";
		//ret += "previousTagSize: \t\t" + 
		ret += "tagType: \t\t" + this.getTagType() +  "\n";  
		ret += "dataSize: \t\t" +  (unsignedByteToInt(dataSize[0]) + unsignedByteToInt(dataSize[1]) + unsignedByteToInt(dataSize[2])) + "\n";
		ret += "timeStamp: \t" + (unsignedByteToInt(timeStamp[0]) + unsignedByteToInt(timeStamp[1]) + unsignedByteToInt(timeStamp[2])) + "\n";
		ret += "reserved: \t" + reserved + "\n";
		ret += "data: \t\t" + data.length + "\n";
		//byte b = 0x01;
		
		return ret;
	}
	
	public static int unsignedByteToInt(byte b) {
	    return (int) b & 0xFF;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
