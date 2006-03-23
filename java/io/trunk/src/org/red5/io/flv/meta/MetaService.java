package org.red5.io.flv.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.amf.Input;
import org.red5.io.amf.Output;
import org.red5.io.flv.ITag;
import org.red5.io.flv.impl.FLVService;
import org.red5.io.flv.impl.Reader;
import org.red5.io.flv.impl.Tag;
import org.red5.io.flv.impl.Writer;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
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
	private FileOutputStream fos;
	private Serializer serializer;
	private Deserializer deserializer;
	private Resolver resolver;
	
	/**
	 * @return Returns the resolver.
	 */
	public Resolver getResolver() {
		return resolver;
	}

	/**
	 * @param resolver The resolver to set.
	 */
	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * @return Returns the deserializer.
	 */
	public Deserializer getDeserializer() {
		return deserializer;
	}

	/**
	 * @param deserializer The deserializer to set.
	 */
	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * @return Returns the serializer.
	 */
	public Serializer getSerializer() {
		return serializer;
	}

	/**
	 * @param serializer The serializer to set.
	 */
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

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
	public void write(IMetaData meta) throws IOException {
	
//		MetaData md = new MetaData();
//		md.setMetaCue(meta.getMetaCue());

		//this will happen here
		IMetaCue[] metaArr = meta.getMetaCue();
		Reader reader = new Reader(fis);
		Writer writer = new Writer(fos);
		writer.writeHeader();
		
		IMetaData metaData = null;
		ITag tag = null;
		// Read first tag
		if(reader.hasMoreTags()) {
			 tag = reader.readTag();
			if(tag.getDataType() == ITag.TYPE_METADATA) {
				metaData = this.readMetaData(tag.getBody());
			}			
		}
		
		IMetaData mergedMeta = (IMetaData) mergeMeta(metaData, meta);
		ITag injectedTag = injectMetaData(mergedMeta, tag);
		System.out.println("tag: \n--------\n" + injectedTag);
		writer.writeTag(injectedTag);
		
		int cuePointTimeStamp = getTimeInMilliseconds(metaArr[0]);
		int counter = 0;
		while(reader.hasMoreTags()) {
			tag = reader.readTag();
	
			// if there are cuePoints in the TreeSet
			if(counter < metaArr.length) {
	
				// If the tag has a greater timestamp than the
				// cuePointTimeStamp, then inject the tag
				while(tag.getTimestamp() > cuePointTimeStamp) {
					
					injectedTag = (ITag) injectMetaCue(metaArr[0], tag);
					System.out.println("In tag: \n--------\n" + injectedTag);
					writer.writeTag(injectedTag);	
					
					tag.setPreviousTagSize((injectedTag.getBodySize() + 11));
					
					// Advance to the next CuePoint
					counter++;
				
//					if(ts.isEmpty()) {
//						break;						
//					}
					
					cuePointTimeStamp = getTimeInMilliseconds(metaArr[counter]);
				}										
			}
			
			System.out.println("tag: \n--------\n" + tag);
			writer.writeTag(tag);
			
		}
		
		/*
		// Write out MetaData
		if(metaData != null) {
			writeMetaData(metaData);
		}
		
		// Write out CuePoints
		// might not be needed.. this can all be called
		// from method above
		if(true) {
			writeMetaCue();
		}
		*/
	}
	

	/**
	 * Merges the two Meta objects according to user
	 * @param metaData
	 * @param md
	 * @return
	 */
	private IMeta mergeMeta(IMetaData metaData, IMetaData md) {
		return resolver.resolve(metaData, md);
	}

	private ITag injectMetaData(IMetaData meta, ITag tag) {
		
		Output out = new Output(ByteBuffer.allocate(1000));
		Serializer ser = new Serializer();		
		ser.serialize(out,"onMetaData");
		ser.serialize(out,meta);
		
		ByteBuffer tmpBody = out.buf().flip();		
		int tmpBodySize = out.buf().limit();	
		int tmpPreviousTagSize = tag.getPreviousTagSize();
		byte tmpDataType = ((byte)(ITag.TYPE_METADATA));
		int tmpTimestamp = 0;
		
		return new Tag(tmpDataType, tmpTimestamp, tmpBodySize, tmpBody, tmpPreviousTagSize);
	}

	
	/**
	 * Injects metadata (Cue Points) into a tag
	 * @param cue
	 * @param writer
	 * @param tag
	 * @return ITag tag
	 */
	private ITag injectMetaCue(IMetaCue meta, ITag tag) {
		
//		IMeta meta = (MetaCue) cue;
		Output out = new Output(ByteBuffer.allocate(1000));
		Serializer ser = new Serializer();		
		ser.serialize(out,"onCuePoint");
		ser.serialize(out,meta);
				
		ByteBuffer tmpBody = out.buf().flip();	
		int tmpBodySize = out.buf().limit();	
		int tmpPreviousTagSize = tag.getPreviousTagSize();
		byte tmpDataType = ((byte)(ITag.TYPE_METADATA));
		int tmpTimestamp = getTimeInMilliseconds(meta);
								
		return new Tag(tmpDataType, tmpTimestamp, tmpBodySize, tmpBody, tmpPreviousTagSize);
		
	}
	
	/**
	 * Returns a timestamp in milliseconds
	 * @param object
	 * @return int time
	 */
	private int getTimeInMilliseconds(IMetaCue object) {
		IMetaCue cp = (MetaCue) object;		
		return (int) (cp.getTime() * 1000.00);
		
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IMetaService#writeMetaData()
	 */
	public void writeMetaData(IMetaData metaData) {
		IMetaCue meta = (MetaCue) metaData;
		Output out = new Output(ByteBuffer.allocate(1000));		
		serializer.serialize(out,"onCuePoint");
		serializer.serialize(out,meta);

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IMetaService#writeMetaCue()
	 */
	public void writeMetaCue() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MetaService service = new MetaService();
		try {
			service.write(new MetaData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void setOutStream(FileOutputStream fos) {
		this.fos = fos;		
	}

	public MetaData readMetaData(ByteBuffer buffer) {
		MetaData retData;
		Input input = new Input(buffer);	
		String metaData =  (String) deserializer.deserialize(input);
		retData = new MetaData();
		return retData;
	}

	public IMetaCue[] readMetaCue() {
		// TODO Auto-generated method stub
		return null;
	}

}
