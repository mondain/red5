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
public interface ITag {

	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	public static final byte TYPE_METADATA = 0x12;
	
	public static final byte MASK_SOUND_TYPE = 0x01;
	public static final byte FLAG_TYPE_MONO = 0x00;
	public static final byte FLAG_TYPE_STEREO = 0x01;
	
	public static final byte MASK_SOUND_SIZE = 0x02;
	public static final byte FLAG_SIZE_8_BIT = 0x00;
	public static final byte FLAG_SIZE_16_BIT = 0x02;
	
	public static final byte MASK_SOUND_RATE = 0x0C;
	public static final byte FLAG_RATE_5_5_KHZ = 0x00;
	public static final byte FLAG_RATE_11_KHZ = 0x01;
	public static final byte FLAG_RATE_22_KHZ = 0x02;
	public static final byte FLAG_RATE_44_KHZ = 0x03;
	
	public static final byte MASK_SOUND_FORMAT = 0xF0 - 0xFF; // unsigned 
	public static final byte FLAG_FORMAT_RAW = 0x00;
	public static final byte FLAG_FORMAT_ADPCM = 0x01;
	public static final byte FLAG_FORMAT_MP3 = 0x02;
	public static final byte FLAG_FORMAT_NELLYMOSER_8_KHZ = 0x05;
	public static final byte FLAG_FORMAT_NELLYMOSER = 0x06;
	
	public static final byte MASK_VIDEO_CODEC = 0x0F;
	public static final byte FLAG_CODEC_H263 = 0x02;
	public static final byte FLAG_CODEC_SCREEN = 0x03;
	public static final byte FLAG_CODEC_VP6 = 0x04;
	
	public static final byte MASK_VIDEO_FRAMETYPE = 0xF0 - 0xFF; // unsigned 
	public static final byte FLAG_FRAMETYPE_KEYFRAME = 0x01;
	public static final byte FLAG_FRAMETYPE_INTERFRAME = 0x02;
	public static final byte FLAG_FRAMETYPE_DISPOSABLE = 0x03;
	
	
	/**
	 * Return the body ByteBuffer
	 * @return ByteBuffer
	 */
	public ByteBuffer getBody();
	
	/**
	 * Return the size of the body
	 * @return int
	 */
	public int getBodySize();
	
	/**
	 * Get the data type
	 * @return byte
	 */
	public byte getDataType();
	
	/**
	 * Return the timestamp
	 * @return int
	 */
	public int getTimestamp();
	

	/**
	 * Returns the data as a ByteBuffer
	 * @return ByteBuffer buf
	 */
	public ByteBuffer getData();
	
	/**
	 * Returns the data as a ByteBuffer
	 * @return ByteBuffer buf
	 */
	public int getPreviousTagSize();
	
	/**
	 * Return the body ByteBuffer
	 * @return ByteBuffer
	 */
	public void setBody(ByteBuffer body);
	
	/**
	 * Return the size of the body
	 * @return int
	 */
	public void setBodySize(int size);
	
	/**
	 * Get the data type
	 * @return byte
	 */
	public void setDataType(byte datatype);
	
	/**
	 * Return the timestamp
	 * @return int
	 */
	public void setTimestamp(int timestamp);
	

	/**
	 * Returns the data as a ByteBuffer
	 * @return ByteBuffer buf
	 */
	public void setPreviousTagSize(int size);

}
