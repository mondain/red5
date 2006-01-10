/**
 * 
 */
package org.red5.io.flv;

/**
 * @author daccattato
 *
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
