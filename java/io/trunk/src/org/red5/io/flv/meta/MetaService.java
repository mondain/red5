package org.red5.io.flv.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.flv.impl.FLVService;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

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
 * MetaService represents a MetaData service in Spring
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato(daccattato@gmail.com)
 * @version 0.3
 */
public class MetaService implements IMetaService {

	FLVService service = null;
	File file = null;
	private FileInputStream fis;
	private MappedByteBuffer mappedFile;
	private ByteBuffer in;
	
	/**
	 * MetaService constructor
	 */
	public MetaService() {
		super();
		
		service = new FLVService();
		service.setSerializer(new Serializer());
		service.setDeserializer(new Deserializer());
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IMetaService#write()
	 */
	public void write(IMetaData meta) {

		FileChannel channel = fis.getChannel();
		try {
			mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		in = ByteBuffer.wrap(mappedFile);

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IMetaService#writeMetaData()
	 */
	public void writeMetaData() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IMetaService#writeMetaCue()
	 */
	public void writeMetaCue() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MetaService service = new MetaService();
		service.write(new MetaData());
	}



	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return Returns the service.
	 */
	public FLVService getService() {
		return service;
	}

	/**
	 * @param service The service to set.
	 */
	public void setService(FLVService service) {
		this.service = service;
	}

	public void setInStream(FileInputStream fis) {
		this.fis = fis;
	}

}
