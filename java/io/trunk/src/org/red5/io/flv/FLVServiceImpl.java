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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

/**
 * A FLVServiceImpl sets up the service and hands out FLV objects to 
 * its callers
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class FLVServiceImpl implements FLVService {
	private Serializer serializer;
	private Deserializer deserializer;
	
	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#setSerializer(org.red5.io.object.Serializer)
	 */
	public void setSerializer(Serializer serializer) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#setDeserializer(org.red5.io.object.Deserializer)
	 */
	public void setDeserializer(Deserializer deserializer) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#getFLV()
	 */
	public FLV getFLV() throws FileNotFoundException, IOException {
		FLV flv = new FLVImpl();
		return flv;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#getFLV(java.lang.String)
	 */
	public FLV getFLV(String filename) throws FileNotFoundException,
			IOException {
		FLV flv = new FLVImpl(new FileInputStream(new File(filename)));		
		return flv;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#getFLV(java.io.FileInputStream)
	 */
	public FLV getFLV(FileInputStream fis) throws IOException {
		FLV flv = new FLVImpl(fis); 
		return flv;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLVService#getFLV(java.io.File)
	 */
	public FLV getFLV(File file) throws IOException {new FileInputStream(file);
		FLV flv = new FLVImpl(new FileInputStream(file)); 
		return flv;
	}

	public FLV getFLV(FileOutputStream fos) {
		FLV flv = new FLVImpl(fos);
		// TODO Auto-generated method stub
		return flv;
	}

	public FLV setFLV(String filename) {
		FLV flv = null;
		try {
			flv = new FLVImpl(new FileInputStream(new File(filename)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flv;
	}
	
}
