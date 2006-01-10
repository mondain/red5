/**
 * 
 */
package org.red5.io.flv;
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

/**
 * MetaData Implementation
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato (daccattato@gmail.com)
 * @version 0.3
 */
public class MetaDataImpl implements MetaData {
	private boolean canSeekToEnd = true;
	private int videocodecid = 4;
	private int framerate = 15;
	private int videodatarate = 400;
	private int height = 215;
	private int width = 320;
	private double duration = 7.347;
	
	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getCanSeekToEnd()
	 */
	public boolean getCanSeekToEnd() {
		return canSeekToEnd;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setCanSeekToEnd(boolean)
	 */
	public void setCanSeekToEnd(boolean b) {
		canSeekToEnd = b;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getVideoCodecId()
	 */
	public int getVideoCodecId() {
		return videocodecid;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setVideoCodecId(int)
	 */
	public void setVideoCodecId(int id) {
		videocodecid = id;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getframeRate()
	 */
	public int getframeRate() {
		return framerate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setframeRate(int)
	 */
	public void setframeRate(int rate) {
		framerate = rate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getVideoDataRate()
	 */
	public int getVideoDataRate() {
		return videodatarate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setVideoDataRate(int)
	 */
	public void setVideoDataRate(int rate) {
		videodatarate = rate;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setHeight(int)
	 */
	public void setHeight(int h) {
		height = h;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setWidth(int)
	 */
	public void setWidth(int w) {
		width = w;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#getDuration()
	 */
	public double getDuration() {
		return duration;
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.MetaData#setDuration(int)
	 */
	public void setDuration(double d) {
		duration = d;
	}

}
