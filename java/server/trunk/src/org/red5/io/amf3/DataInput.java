package org.red5.io.amf3;

/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright (c) 2006-2011 by respective authors (see below). All rights reserved.
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.io.amf.AMF;
import org.red5.io.object.Deserializer;

/**
 * Implementation of the IDataInput interface. Can be used to load an
 * IExternalizable object.
 *  
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 * 
 */
public class DataInput implements IDataInput {

	/** The input stream. */
	private Input input;
	
	/** The deserializer to use. */
	private Deserializer deserializer;
	
	/** Raw data of input source. */
	private IoBuffer buffer;
	
	/**
	 * Create a new DataInput.
	 * 
	 * @param input			input to use
	 * @param deserializer	the deserializer to use
	 */
	protected DataInput(Input input, Deserializer deserializer) {
		this.input = input;
		this.deserializer = deserializer;
		buffer = input.getBuffer();
	}

	/** {@inheritDoc} */
	public ByteOrder getEndian() {
		return buffer.order();
	}

	/** {@inheritDoc} */
	public void setEndian(ByteOrder endian) {
		buffer.order(endian);
	}
	
	/** {@inheritDoc} */
	public boolean readBoolean() {
		return (buffer.get() != 0);
	}

	/** {@inheritDoc} */
	public byte readByte() {
		return buffer.get();
	}

	/** {@inheritDoc} */
	public void readBytes(byte[] bytes) {
		buffer.get(bytes);
	}

	/** {@inheritDoc} */
	public void readBytes(byte[] bytes, int offset) {
		buffer.get(bytes, offset, bytes.length - offset);
	}

	/** {@inheritDoc} */
	public void readBytes(byte[] bytes, int offset, int length) {
		buffer.get(bytes, offset, length);
	}

	/** {@inheritDoc} */
	public double readDouble() {
		return buffer.getDouble();
	}

	/** {@inheritDoc} */
	public float readFloat() {
		return buffer.getFloat();
	}

	/** {@inheritDoc} */
	public int readInt() {
		return buffer.getInt();
	}

	/** {@inheritDoc} */
	public String readMultiByte(int length, String charSet) {
		final Charset cs = Charset.forName(charSet);
		int limit = buffer.limit();
		final ByteBuffer strBuf = buffer.buf();
		strBuf.limit(strBuf.position() + length);
		final String string = cs.decode(strBuf).toString();
		buffer.limit(limit); // Reset the limit
		return string;
	}

	/** {@inheritDoc} */
	public Object readObject() {
		return deserializer.deserialize(input, Object.class);
	}

	/** {@inheritDoc} */
	public short readShort() {
		return buffer.getShort();
	}

	/** {@inheritDoc} */
	public int readUnsignedByte() {
		return buffer.getUnsigned();
	}

	/** {@inheritDoc} */
	public long readUnsignedInt() {
		return buffer.getUnsignedInt();
	}

	/** {@inheritDoc} */
	public int readUnsignedShort() {
		return buffer.getShort() & 0xffff; //buffer.getUnsignedShort();
	}

	/** {@inheritDoc} */
	public String readUTF() {
		int length = buffer.getShort() & 0xffff; //buffer.getUnsignedShort();
		return readUTFBytes(length);
	}

	/** {@inheritDoc} */
	public String readUTFBytes(int length) {
		int limit = buffer.limit();
		final ByteBuffer strBuf = buffer.buf();
		strBuf.limit(strBuf.position() + length);
		final String string = AMF.CHARSET.decode(strBuf).toString();
		buffer.limit(limit); // Reset the limit
		return string;
	}

}
