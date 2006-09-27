package org.red5.io.utils;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
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
 * Encoding and decoding methods for UTF-8 that have no overhead
 * in object creation.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 */
public class UTF8Codec {

	/**
	 * Determine the length of a string when encoded to UTF-8.
	 * 
	 * @param in
	 * 			string to calculate length for
	 * @return the length of the UTF-8 encoded string
	 */
	public static final int calculateUTF8Length(String in) {
		int len = 0;
		for (int i=0; i<in.length(); i++) {
			char x = in.charAt(i);
			if (x <= 0x7f)
				len++;
			else if (x <= 0x7ff)
				len += 2;
			else if (x <= 0xffff)
				len += 3;
			else
				len += 4;
		}
		return len;
	}
	
	/**
	 * Encode a string to UTF-8.
	 * 
	 * @param in
	 * 			string to encode
	 * @param out
	 * 			ByteBuffer to write the encoded string to
	 */
	public static final void encodeUTF8(String in, ByteBuffer out) {
		for (int i=0; i<in.length(); i++) {
			char x = in.charAt(i);
			if (x <= 0x7f)
				out.put((byte) x);
			else if (x <= 0x7ff) {
				out.put((byte) (0xc0 | (x >> 6)));
				out.put((byte) (0x80 | (x & 0x3f)));
			} else if (x <= 0xffff) {
				out.put((byte) (0xe0 | (x >> 12)));
				out.put((byte) (0x80 | ((x >> 6) & 0x3f)));
				out.put((byte) (0x80 | (x & 0x3f)));
			} else {
				out.put((byte) (0xf0 | (x >> 18)));
				out.put((byte) (0x80 | ((x >> 12) & 0x3f)));
				out.put((byte) (0x80 | ((x >> 6) & 0x3f)));
				out.put((byte) (0x80 | (x & 0x3f)));
			}
		}
	}

}
