package org.red5.server.net.rtmp.event;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2009 by respective authors (see below). All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.stream.IStreamData;

public class AudioData extends BaseEvent implements IStreamData<AudioData>, IStreamPacket {

	private static final long serialVersionUID = -4102940670913999407L;

	protected IoBuffer data;

	/**
	 * Data type
	 */
	private byte dataType = TYPE_AUDIO_DATA;

	/** Constructs a new AudioData. */
	public AudioData() {
		this(IoBuffer.allocate(0).flip());
	}

	public AudioData(IoBuffer data) {
		super(Type.STREAM_DATA);
		setData(data);
	}

	/**
	 * Create audio data event with given data buffer
	 * @param data Audio data
	 * @param copy true to use a copy of the data or false to use reference
	 */
	public AudioData(IoBuffer data, boolean copy) {
		super(Type.STREAM_DATA);
		if (copy) {
			byte[] array = new byte[data.limit()];
			data.mark();
			data.get(array);
			data.reset();
			setData(array);
		} else {
			setData(data);
		}
	}

	/** {@inheritDoc} */
	@Override
	public byte getDataType() {
		return dataType;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	/** {@inheritDoc} */
	public IoBuffer getData() {
		return data;
	}

	public void setData(IoBuffer data) {
		this.data = data;
	}

	public void setData(byte[] data) {
		this.data = IoBuffer.allocate(data.length);
		this.data.put(data).flip();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.format("Audio - ts: %s length: %s", getTimestamp(), (data != null ? data.limit() : '0'));
	}

	/** {@inheritDoc} */
	@Override
	protected void releaseInternal() {
		if (data != null) {
			data.free();
			data = null;
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		byte[] byteBuf = (byte[]) in.readObject();
		if (byteBuf != null) {
			data = IoBuffer.allocate(0);
			data.setAutoExpand(true);
			SerializeUtils.ByteArrayToByteBuffer(byteBuf, data);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		if (data != null) {
			out.writeObject(SerializeUtils.ByteBufferToByteArray(data));
		} else {
			out.writeObject(null);
		}
	}
	
	/**
     * Duplicate this message / event.
     * 
     * @return  duplicated event
     */
	public AudioData duplicate() throws IOException, ClassNotFoundException {
		AudioData result = new AudioData();
		// serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);		
		writeExternal(oos);
		oos.close();
		// convert to byte array
		byte[] buf = baos.toByteArray();
		baos.close();
		// create input streams
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		// deserialize
		result.readExternal(ois);
		ois.close();
		bais.close();
		// clone the header if there is one
		if (header != null) {
			result.setHeader(header.clone());
		}
		return result;
	}	
	
}
