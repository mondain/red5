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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import org.apache.mina.common.ByteBuffer;
import org.red5.io.utils.IOUtils;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A Writer is used to write the contents of a FLV file
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class WriterImpl implements Writer {
	
	private FileOutputStream fos = null;
	private FileChannel channel;
	private MappedByteBuffer mappedFile;
	private ByteBuffer in;
	private int limit;
	
	/**
	 * WriterImpl Constructor
	 * @param fos 
	 */
	public WriterImpl(FileOutputStream f) {
		this.fos = f;
		try {
			writeHeader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		channel = fos.getChannel();
		try {
			mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mappedFile.order(ByteOrder.BIG_ENDIAN);
		in = ByteBuffer.wrap(mappedFile);
		limit  = in.limit();
	}

	/**
	 * Writes the header bytes
	 * @throws IOException 
	 *
	 */
	private void writeHeader() throws IOException {
		// TODO Auto-generated method stub
		fos.write((byte)0x46);
		fos.write((byte)0x4C);
		fos.write((byte)0x56);
		
		// Write version
		fos.write((byte)0x01);
		
		// For testing purposes write video only
		// TODO CHANGE
		fos.write((byte)0x08);
		
		fos.write(0x09);
		
		// Write first previous tag size
		fos.write(0x00);
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getFLV()
	 */
	public FLV getFLV() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getOffset()
	 */
	public long getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getBytesWritten()
	 */
	public long getBytesWritten() {
		// TODO Auto-generated method stub
		return 0; 
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#writeTag(org.red5.io.flv.Tag)
	 */
	public boolean writeTag(Tag tag) throws IOException {
		// TODO Auto-generated method stub
		in.put((byte)(tag.getDataType()));
		in.putInt(tag.getBodySize());
		//in.putInt(IOUtils.readUnsignedMediumInt(tag.getTimestamp();
		return false;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#writeTag(byte, java.nio.ByteBuffer)
	 */
	public boolean writeTag(byte type, ByteBuffer data) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc) 
	 * @see org.red5.io.flv.Writer#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

}
