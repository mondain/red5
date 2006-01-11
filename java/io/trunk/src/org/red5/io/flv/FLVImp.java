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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * A FLVImpl implements the FLV api
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class FLVImp implements FLV {

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
	public Reader reader() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#readerFromNearestKeyFrame(int)
	 */
	public Reader readerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#writer()
	 */
	public Writer writer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#writerFromNearestKeyFrame(int)
	 */
	public Writer writerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.FLV#setFileInputStream(java.io.FileInputStream)
	 */
	public void setFileInputStream(FileInputStream fis) {
		// TODO Auto-generated method stub

	}
}
