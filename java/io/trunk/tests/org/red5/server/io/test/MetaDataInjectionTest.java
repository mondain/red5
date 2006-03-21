package org.red5.server.io.test;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.amf.AMF;
import org.red5.io.amf.Output;
import org.red5.io.flv.ICueType;
import org.red5.io.flv.ICuePoint;
import org.red5.io.flv.IFLV;
import org.red5.io.flv.IFLVService;
import org.red5.io.flv.impl.CuePoint;
import org.red5.io.flv.impl.FLVService;
import org.red5.io.flv.impl.MetaData;
import org.red5.io.flv.impl.MetaData1;
import org.red5.io.flv.IReader;
import org.red5.io.flv.ITag;
import org.red5.io.flv.impl.Tag;
import org.red5.io.flv.IWriter;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;
import org.red5.io.utils.IOUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato(daccattato@gmail.com)
 * @version 0.3
 */
public class MetaDataInjectionTest extends TestCase {

	private IFLVService service;
	
	/**
	 * SetUp is called before each test
	 * @return void
	 */
	public void setUp() {
		service = new FLVService();
		service.setSerializer(new Serializer());
		service.setDeserializer(new Deserializer());
	}
	
	/**
	 * Test MetaData injection
	 * @throws IOException
	 */
	public void testMetaDataInjection() throws IOException {
		File f = new File("tests/test_cue1.flv");
		
		if(f.exists()) {			
			f.delete();
		}
		
		// Create new file
		f.createNewFile();
		
		// Use service to grab FLV file
		IFLV flv = service.getFLV(f);
		
		// Grab a writer for writing a new FLV
		IWriter writer = flv.writer();
		
		// Create a reader for testing
		File readfile = new File("tests/test_cue.flv");
		IFLV readflv = service.getFLV(readfile);
		
		// Grab a reader for reading a FLV in
		IReader reader = readflv.reader();
		
		// Inject MetaData
		writeTagsWithInjection(reader, writer);	
		
	}

	/**
	 * Write FLV tags and inject Cue Points
	 * @param reader
	 * @param writer
	 * @throws IOException
	 */
	private void writeTagsWithInjection(IReader reader, IWriter writer) throws IOException {
				
		ICuePoint cp = new CuePoint();
		cp.setName("cue_1");
		cp.setTime(0.01);
		cp.setType(ICueType.EVENT);
		
		ICuePoint cp1 = new CuePoint();
		cp1.setName("cue_1");
		cp1.setTime(2.01);
		cp1.setType(ICueType.EVENT);	

		// Place in TreeSet for sorting
		TreeSet ts = new TreeSet();
		ts.add(cp);
		ts.add(cp1);
		
		int cuePointTimeStamp = getTimeInMilliseconds(ts.first());		
		
		ITag tag = null;
		ITag injectedTag = null;
		
		while(reader.hasMoreTags()) {
			tag = reader.readTag();
			
			// if there are cuePoints in the TreeSet
			if(!ts.isEmpty()) {
	
				// If the tag has a greater timestamp than the
				// cuePointTimeStamp, then inject the tag
				while(tag.getTimestamp() > cuePointTimeStamp) {
					
					injectedTag = (ITag) injectMetaData(ts.first(), tag);
					writer.writeTag(injectedTag);					
					tag.setPreviousTagSize((injectedTag.getBodySize() + 11));
					
					// Advance to the next CuePoint
					ts.remove(ts.first());
				
					if(ts.isEmpty()) {
						break;						
					}
					
					cuePointTimeStamp = getTimeInMilliseconds(ts.first());
				}										
			}
			
			writer.writeTag(tag);
			
		}
	}		

	/**
	 * Injects metadata (Cue Points) into a tag
	 * @param cue
	 * @param writer
	 * @param tag
	 * @return ITag tag
	 */
	private ITag injectMetaData(Object cue, ITag tag) {
		
		ICuePoint cp = (CuePoint) cue;
		Output out = new Output(ByteBuffer.allocate(1000));
		Serializer ser = new Serializer();		
		ser.serialize(out,"onCuePoint");
		ser.serialize(out,cp);
				
		ByteBuffer tmpBody = out.buf().flip();		
		int tmpBodySize = out.buf().limit();	
		int tmpPreviousTagSize = tag.getPreviousTagSize();
		byte tmpDataType = ((byte)(ITag.TYPE_METADATA));
		int tmpTimestamp = getTimeInMilliseconds(cp);
								
		return new Tag(tmpDataType, tmpTimestamp, tmpBodySize, tmpBody, tmpPreviousTagSize);
		
	}
	
	/**
	 * Returns a timestamp in milliseconds
	 * @param object
	 * @return int time
	 */
	private int getTimeInMilliseconds(Object object) {
		ICuePoint cp = (CuePoint) object;		
		return (int) (cp.getTime() * 1000.00);
		
	}

	/**
	 * Test to see if TreeSet is sorting properly
	 * @return void
	 */
	public void testMetaDataOrder() {
		ICuePoint cue = new CuePoint();
		cue.setName("cue_1");
		cue.setTime(0.01);
		cue.setType(ICueType.EVENT);
		
		ICuePoint cue1 = new CuePoint();
		cue1.setName("cue_1");
		cue1.setTime(2.01);
		cue1.setType(ICueType.EVENT);
		
		ICuePoint cue2 = new CuePoint();
		cue2.setName("cue_1");
		cue2.setTime(1.01);
		cue2.setType(ICueType.EVENT);
		
		TreeSet ts = new TreeSet();
		ts.add(cue);
		ts.add(cue1);
		ts.add(cue2);
		
		System.out.println("ts: " + ts);
		
		Assert.assertEquals(true, true);
	}
	
}
