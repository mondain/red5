package org.red5.io.flv;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.utils.IOUtils;

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


/**
 * A Reader is used to read the contents of a FLV file
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class ReaderImpl implements Reader {
	
	private FileInputStream fis = null;
	private FLVHeader header = null;
	private FileChannel channel = null;
	private MappedByteBuffer mappedFile = null;
	private ByteBuffer in = null;
	private int limit = 0;
	private Tag tag = null;
	
	public ReaderImpl(FileInputStream f) {
		this.fis = f;
		channel = fis.getChannel();
		try {
			mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mappedFile.order(ByteOrder.BIG_ENDIAN);
		in = ByteBuffer.wrap(mappedFile);
		limit  = in.limit();
		decodeHeader();
	}

	public void decodeHeader() {
		// SIGNATURE, lets just skip
		header = new FLVHeader();
		in.skip(3);
		header.setVersion((byte) in.get());
		header.setTypeFlags((byte) in.get());
		header.setDataOffset(in.getInt());		
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#getFLV()
	 */
	public FLV getFLV() {
		// TODO Auto-generated method stub
		// TODO wondering if we need to have a reference
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#getOffset()
	 */
	public int getOffset() {
		//return header.getDataOffset();
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#getBytesRead()
	 */
	public long getBytesRead() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#hasMoreTags()
	 */
	public boolean hasMoreTags() {
		return in.remaining() > 4;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#readTag()
	 */
	public Tag readTag() {
		
		// PREVIOUS TAG SIZE
		int previousTagSize = in.getInt();
		
		// START OF FLV TAG
		byte dataType = in.get();
		
		// The next two lines use a utility method which reads in
		// three consecutive bytes but stores them in a 4 byte int.
		// We are able to write those three bytes back out by using
		// another utility method which strips off the last byte
		// However, we will have to check into this during optimization
		int bodySize = IOUtils.readUnsignedMediumInt(in);
		int timestamp = IOUtils.readUnsignedMediumInt(in);
		int reserved = in.getInt();
		
		ByteBuffer body = ByteBuffer.allocate(bodySize);
		in.limit(in.position()+bodySize);
		body.put(in);
		body.flip();
		in.limit(limit);
		
		tag = new TagImpl(dataType,timestamp, bodySize, body, previousTagSize);
	
		return tag;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Reader#close()
	 */
	public void close() {
		mappedFile.clear();
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}

}
