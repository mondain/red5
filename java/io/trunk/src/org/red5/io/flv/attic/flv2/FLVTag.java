package org.red5.io.flv.attic.flv2;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors. All rights reserved.
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
 */

import org.apache.mina.common.ByteBuffer;

/**
 * A FLVTag represents the contents of a FLV Video file.  The  flv file consists of
 * a HEADER, BODY, and the body consists of 1,.,.,n FLVTags. 
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (Dominick@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */
public class FLVTag {
	
	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	public static final byte TYPE_METADATA = 0x12;

	protected byte dataType;
	protected int timestamp;
	protected int bodySize;
	protected ByteBuffer body;
	
	/**
	 * FLVTag Constructor
	 * 
	 * @param dataType
	 * @param timestamp
	 * @param bodySize
	 * @param body
	 */
	public FLVTag(byte dataType, int timestamp, int bodySize, ByteBuffer body){
		this.dataType = dataType;
		this.timestamp = timestamp;
		this.bodySize = bodySize;
		this.body = body;
	}
	
	/**
	 * Return the body ByteBuffer
	 * @return ByteBuffer
	 */
	public ByteBuffer getBody() {
		return body;
	}
	
	/**
	 * Return the size of the body
	 * @return int
	 */
	public int getBodySize() {
		return bodySize;
	}
	
	/**
	 * Get the data type
	 * @return byte
	 */
	public byte getDataType() {
		return dataType;
	}
	
	/**
	 * Return the timestamp
	 * @return int
	 */
	public int getTimestamp() {
		return timestamp;
	}

}
