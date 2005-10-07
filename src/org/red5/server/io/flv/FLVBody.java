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
 */

import java.nio.MappedByteBuffer;

public class FLVBody {
	private int previousTagSize = 0;
	private FLVTag tag;
	private MappedByteBuffer mappedFile;
	
	
	public FLVBody(MappedByteBuffer mappedFile) {
		// TODO Auto-generated constructor stub
		this.mappedFile = mappedFile;
	}
	public int getPreviousTagSize() {
		return previousTagSize;
	}
	public void setPreviousTagSize(int previousTagSize) {
		this.previousTagSize = previousTagSize;
	}
	public FLVTag getTag() {
		return tag;
	}
	public void setTag(FLVTag tag) {
		this.tag = tag;
	}
	public void getTags() {
		// TODO Auto-generated method stub
		int packetSize = 0;
		//while(mappedFile.hasRemaining()) {
			
			packetSize++;
		
			System.out.println("packet #: " + packetSize);
		
			// PREVIOUS_TAG_SIZE
			this.setPreviousTagSize(mappedFile.getInt());
			System.out.println("previous tag: " + this.getPreviousTagSize());
			// Create FLVTag
		
			FLVTag tag = new FLVTag(mappedFile);
		
			
			// TAG TYPE
			//System.out.println("ho: " + unsignedByteToInt(mappedFile.get()));
			
			tag.setTagType((byte) mappedFile.get());
			
			//System.out.println("test: " + mappedFile.get());
			// DATA SIZE
			
			tag.setDataSize(this.readDataSize());
			
			// TIME STAMP
			tag.setTimeStamp(this.readTimeStamp());
			
			// RESERVED
			tag.setReserved(mappedFile.getInt());
			
			byte[] tmp = tag.getDataSize();
			
			//System.out.println("ah: " + unsignedByteToInt(tmp[2]));
			tag.setData(this.readData(tmp));
			
			//System.out.println("test: " + tag.getDataSize()[0] +tag.getDataSize()[1] + tag.getDataSize()[2] );
			//byte[] n = this.readData(tag.getDataSize());
			System.out.println("\nFLVTag: \n------------\n" + tag + "\n");
			
			// DATA
			
		//}
		
	}
	private byte[] readData(byte[] dataSize) {
		int tmp = unsignedByteToInt(dataSize[0]);
		tmp += unsignedByteToInt(dataSize[1]);
		tmp += unsignedByteToInt(dataSize[2]);
		//int tmp = dataSize[0] + dataSize[1] + dataSize[2];
		
		//System.out.println("data[0]: " + data[0]);
		byte b[] = new byte[tmp];
		for(int i=0; i<tmp; i++) {
			b[i] = mappedFile.get();
		}
		
		return b;
	}
	private byte[] readTimeStamp() {
		int timeStampBytes = 3;
		byte b[] = new byte[3];
		for(int i=0; i<timeStampBytes; i++) {
			b[i] = (byte) mappedFile.get();
		}	
		
		return b;
	}
	private byte[] readDataSize() {
		int dataSizeBytes = 3;
		byte b[] = new byte[3];
		for(int i=0; i<dataSizeBytes; i++) {
			b[i] = (byte) mappedFile.get();
			//System.out.println("wow: " + b[i]);
		}	
		
		return b;
	}
	
	public static int unsignedByteToInt(byte b) {
	    return (int) b & 0xFF;
	}
}
