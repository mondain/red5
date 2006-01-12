package org.red5.io.flv;

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

import org.apache.mina.common.ByteBuffer;

/**
 * A Tag represents the contents or payload of a FLV file
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class TagImpl implements Tag {

	private byte type;
	private byte dataType;
	private int timestamp;
	private int bodySize;
	private ByteBuffer body;

	/**
	 * TagImpl Constructor
	 * 
	 * @param dataType
	 * @param timestamp
	 * @param bodySize
	 * @param body
	 */
	public TagImpl(byte dataType, int timestamp, int bodySize, ByteBuffer body){
		this.dataType = dataType;
		this.timestamp = timestamp;
		this.bodySize = bodySize;
		this.body = body;
	}
	
	public byte setType(byte b) {
		return (type = b);
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Tag#getType()
	 */
	public byte getType() {
		
		return type;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Tag#getSize()
	 */
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Tag#getData()
	 */
	public ByteBuffer getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSize(int int1) {
		// TODO Auto-generated method stub
		
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
	
	/**
	 * Prints out the contents of the tag
	 * @return tag contents
	 */
	public String toString() {
		String ret 	 = "type=\t ##";
		ret 		+= "size=\t ##";
		ret			+= "time=\t ##";
		ret			+= "res =\t ##";
		ret			+= "data=\t ##";
		
		return ret;
	}

}
