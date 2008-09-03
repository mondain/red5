/**
 * 
 */
package org.red5.io.impl;
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

import org.red5.io.flv.IMetaData;

/**
 * MetaData Implementation
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato (daccattato@gmail.com)
 * @version 0.3
 */
public class MetaData extends HashMap implements IMetaData, Comparable, Serializable {
	
	private boolean canSeekToEnd = true;
	private int videocodecid = 4;
	private int framerate = 15;
	private int videodatarate = 400;
	private int height;
	private int width = 320;
	private double duration = 7.347;
	
	private int timestamp = 0;
	private String name = null;
	
	public MetaData(String string) {
		this.name  = string;
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
		canSeekToEnd = b;
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
		videocodecid = id;
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
		framerate = rate;
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
		videodatarate = rate;
		this.put("videodatarate", new Integer(rate));
	}



	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setHeight(int)
	 */
	

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
		width = w;
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
		duration = d;
		this.put("duration", new Double(d));
	}

	public int getHeight() {
		int height = ((Integer)this.get("height")).intValue();
		return height;
	}

	public void setHeight(int h) {
		this.height = h;
		this.put("height", new Integer(h));
	}

	public int compareTo(Object arg0) {		
		MetaData mdi = (MetaData) arg0;
		int mdiTime = mdi.getTimestamp();
		int thisTime = this.getTimestamp();
		
		if(mdiTime > thisTime) {
			return -1;
		} else if(mdiTime < thisTime) {
			return 1;
		}
		
		return 0;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		String ret = "";
		ret += name;
		return ret;
	}

}
