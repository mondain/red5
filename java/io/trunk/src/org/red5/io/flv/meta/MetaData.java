/**
 * 
 */
package org.red5.io.flv.meta;
/*
 * * RED5 Open Source Flash Server - http://www.osflash.org/red5
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
 */

import java.io.Serializable;
import java.util.HashMap;


/**
 * MetaData Implementation
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato (daccattato@gmail.com)
 * @version 0.3
 * 
 * Example:
 * 
 * //	private boolean canSeekToEnd = true;
	//	private int videocodecid = 4;
	//	private int framerate = 15;
	//	private int videodatarate = 600;
	//	private int height;
	//	private int width = 320;
	//	private double duration = 7.347;
 */
public class MetaData extends HashMap implements IMetaData, Serializable {
		
	/**
	 * serialVersionUID = -5681069577717669925L;
	 */
	private static final long serialVersionUID = -5681069577717669925L;
		
	IMetaCue cuePoints[] = null;	/** CuePoint array **/
		
	/**
	 * MetaData constructor
	 */
	public MetaData() {
		
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getCanSeekToEnd()
	 */
	public boolean getCanSeekToEnd() {
		boolean canSeekToEnd = ((Boolean)this.get("canSeekToEnd")).booleanValue();
		return canSeekToEnd;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setCanSeekToEnd(boolean)
	 */
	public void setCanSeekToEnd(boolean b) {
		this.put("canSeekToEnd", new Boolean(b));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getVideoCodecId()
	 */
	public int getVideoCodecId() {
		int videocodecid = ((Integer)this.get("videocodecid")).intValue();
		return videocodecid;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setVideoCodecId(int)
	 */
	public void setVideoCodecId(int id) {
		this.put("videocodecid", new Integer(id));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getframeRate()
	 */
	public int getframeRate() {
		int framerate = ((Integer)this.get("framerate")).intValue();
		return framerate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setframeRate(int)
	 */
	public void setframeRate(int rate) {
		this.put("framerate", new Integer(rate));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getVideoDataRate()
	 */
	public int getVideoDataRate() {
		int videodatarate = ((Integer)this.get("videodatarate")).intValue();
		return videodatarate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setVideoDataRate(int)
	 */
	public void setVideoDataRate(int rate) {
		this.put("videodatarate", new Integer(rate));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getWidth()
	 */
	public int getWidth() {
		int width = ((Integer)this.get("width")).intValue();
		return width;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setWidth(int)
	 */
	public void setWidth(int w) {
		this.put("width", new Integer(w));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getDuration()
	 */
	public double getDuration() {
		double duration = ((Double)this.get("duration")).doubleValue();
		return duration;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setDuration(int)
	 */
	public void setDuration(double d) {
		this.put("duration", new Double(d));
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getHeight()
	 */
	public int getHeight() {
		int height = ((Integer)this.get("height")).intValue();
		return height;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setHeight(int)
	 */
	public void setHeight(int h) {
		this.put("height", new Integer(h));
	}
	
	public String toString() {
		String ret = "";
		return ret;
	}

	/**
	 * @return Returns the cuePoints.
	 */
	public IMetaCue[] getCuePoints() {
		return cuePoints;
	}

	/**
	 * @param cuePoints The cuePoints to set.
	 */
	public void setCuePoints(IMetaCue[] cuePoints) {
		this.cuePoints = cuePoints;
	}

}
