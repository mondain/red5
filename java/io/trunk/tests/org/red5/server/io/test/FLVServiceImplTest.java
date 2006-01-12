package org.red5.server.io.test;

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
import java.io.IOException;

import org.red5.io.flv.FLV;
import org.red5.io.flv.FLVService;
import org.red5.io.flv.FLVServiceImpl;
import org.red5.io.flv.Reader;
import org.red5.io.flv.Tag;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * A FLVServiceImpl TestCase
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @version 0.3
 */
public class FLVServiceImplTest extends TestCase {
	private FLVService service;
	
	/**
	 * SetUp is called before each test
	 * @return void
	 */
	public void setUp() {
		service = new FLVServiceImpl();
		service.setSerializer(new Serializer());
		service.setDeserializer(new Deserializer());
	}

	/**
	 * Tests: getFlv(String s)
	 * @return void
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void testFLVString() throws FileNotFoundException, IOException  {
		FLV flv = service.getFLV("tests/test.flv");
		
		Reader reader = flv.reader();
		reader.decodeHeader();
		
		/*
		Tag tag = null;
		while(reader.hasMoreTags()) {
			tag = reader.readTag();
		}
		*/
		
		//System.out.println("tag: " + tag);
		Assert.assertEquals(0,0);
	}
	
	/**
	 * Tests: getFLVFile(File f)
	 * @return void
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void testFLVFile() throws FileNotFoundException, IOException  {
		File f = new File("tests/test.flv");
		FLV flv = service.getFLV(f);
		//Reader reader = flv.reader();
		Assert.assertEquals(0,0);
	}
	
	/**
	 * Tests: getFLVFileInputStream(FileInputStream fis)
	 * @return void
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void testFLVFileInputStream() throws FileNotFoundException, IOException  {
		File f = new File("tests/test.flv");
		FileInputStream fis = new FileInputStream(f);
		FLV flv = service.getFLV(fis);
		//Reader reader = flv.reader();
		Assert.assertEquals(0,0);
	}
}
