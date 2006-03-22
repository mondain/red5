package org.red5.io.flv.impl;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.io.amf.Input;
import org.red5.io.flv.IFLV;
import org.red5.io.flv.IReader;
import org.red5.io.flv.ITag;
import org.red5.io.flv.IWriter;
import org.red5.io.flv.meta.IMetaData;
import org.red5.io.flv.meta.IMetaService;

/**
 * A FLVImpl implements the FLV api
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class FLV implements IFLV {

	
	protected static Log log =
        LogFactory.getLog(FLV.class.getName());
	
	private File file;

	private IMetaService metaService;
	
	public FLV(File file){
		this.file = file;
	}
	
	/*
	private FileInputStream fis;
	private FileOutputStream fos;
	public FLVImpl(FileInputStream f) {
		this.fis = f;
	}

	public FLVImpl() {
		// TODO Auto-generated constructor stub
	}

	public FLVImpl(FileOutputStream f) {
		this.fos = f;
	}
	*/

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#hasMetaData()
	 */
	public boolean hasMetaData() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#setMetaData(java.util.Map)
	 */
	public void setMetaData(Map metadata) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#getMetaData()
	 */
	public Map getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#hasKeyFrameData()
	 */
	public boolean hasKeyFrameData() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#setKeyFrameData(java.util.Map)
	 */
	public void setKeyFrameData(Map keyframedata) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#getKeyFrameData()
	 */
	public Map getKeyFrameData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#refreshHeaders()
	 */
	public void refreshHeaders() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#flushHeaders()
	 */
	public void flushHeaders() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#reader()
	 */
	public IReader reader() throws IOException{
		if(!file.exists()) {
			log.info("Creating new file: "+file);
			file.createNewFile();
		}
		log.info("File size: "+file.length());
		Reader reader = new Reader(new FileInputStream(file));
		return reader;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#readerFromNearestKeyFrame(int)
	 */
	public IReader readerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#writer()
	 */
	public IWriter writer() throws IOException {
		if(file.exists()) file.delete();
		file.createNewFile();
		IWriter writer = new Writer(new FileOutputStream(file));
		writer.writeHeader();
		return writer;
	}
	
	public IWriter append() throws IOException {
		// If the file doesnt exist, we cant append to it, so return a writer
		if(!file.exists()){ 
			log.info("File does not exist, calling writer. This will create a new file.");
			return writer();
		}
		IReader reader = reader();
		// Its an empty flv, so no point appending call writer
		if(!reader.hasMoreTags()) {
			reader.close();
			log.info("Reader is empty, calling writer. This will create a new file.");
			return writer();
		}
		ITag lastTag = null;
		while(reader.hasMoreTags()){
			lastTag = reader.readTag();
		}
		reader.close();
		FileOutputStream fos = new FileOutputStream(file, true);
		IWriter writer = new Writer(fos, lastTag);
		return writer;
	}
	

	
	
	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#writerFromNearestKeyFrame(int)
	 */
	public IWriter writerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMetaData(IMetaData meta) throws FileNotFoundException {
		metaService.setInStream(new FileInputStream(file));
		metaService.write(meta);
		
	}

	public void setMetaService(IMetaService service) {
		metaService = service;		
	}
}
