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
import java.nio.channels.WritableByteChannel;

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
	private WritableByteChannel channel;
	private MappedByteBuffer mappedFile;
	private ByteBuffer out;
	private int limit;
	
	
	private FLV flv = null;
	private long bytesWritten = 0;
	private long offset;
	
	/**
	 * WriterImpl Constructor
	 * @param fos 
	 */
	public WriterImpl(FileOutputStream f) {
		this.fos = f;
				
		channel = this.fos.getChannel();
		out = ByteBuffer.allocate(1024);
		limit  = out.limit();
		
		try {
			writeHeader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Writes the header bytes
	 * @throws IOException 
	 *
	 */
	private void writeHeader() throws IOException {
		// TODO Auto-generated method stub
		out.put((byte)0x46);
		out.put((byte)0x4C);
		out.put((byte)0x56);
		
		// Write version
		out.put((byte)0x01);
		
		// For testing purposes write video only
		// TODO CHANGE
		out.put((byte)0x08);
		
		// Data Offset
		out.putInt(0x09);
		
		out.flip();
		
		channel.write(out.buf());
	
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getFLV()
	 */
	public FLV getFLV() {
		return flv;
	}
	
	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#setFLV()
	 */
	public void setFLV(FLV flv) {
		this.flv = flv;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getOffset()
	 */
	public long getOffset() {
		// TODO Auto-generated method stub
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#getBytesWritten()
	 */
	public long getBytesWritten() {
		return bytesWritten; 
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.Writer#writeTag(org.red5.io.flv.Tag)
	 */
	public boolean writeTag(Tag tag) throws IOException {
		// PreviousTagSize
		//out = out.reset();
		out.clear();
		out.putInt(tag.getPreviousTagSize());
		
		// Data Type
		out.put((byte)(tag.getDataType()));
		
		// Body Size
		IOUtils.writeMediumInt(out, tag.getBodySize());
		
		// Timestamp
		IOUtils.writeMediumInt(out, tag.getTimestamp());
		
		// Reserved
		out.putInt(0x00);

		out.flip();
		bytesWritten += channel.write(out.buf());

		ByteBuffer bodyBuf = tag.getBody();
		bytesWritten += channel.write(bodyBuf.buf());
		
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
		try {
			channel.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean writeStream(byte[] b) {
		// TODO Auto-generated method stub
		return false;
	}

}
