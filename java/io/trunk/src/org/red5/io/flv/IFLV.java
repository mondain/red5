package org.red5.io.flv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public interface IFLV {

	/**
	 * Returns a boolean stating whether the flv has metadata
	 * @return boolean
	 */
	public boolean hasMetaData();
	
	/**
	 * Sets the metadata
	 * @param Map metadata
	 * @return void
	 */
	public void setMetaData(Map metadata);
	
	/**
	 * Returns a map of the metadata
	 * @return Map metadata
	 */
	public Map getMetaData();
	
	/**
	 * Returns a boolean stating whether a flv has keyframedata
	 * @return boolean
	 */
	public boolean hasKeyFrameData();
	
	/**
	 * Sets the keyframe data of a flv file
	 * @param Map keyframedata
	 * @return void
	 */
	public void setKeyFrameData(Map keyframedata);
	
	/**
	 * Gets the keyframe data
	 * @return Map keyframedata
	 */
	public Map getKeyFrameData();
	
	/**
	 * Refreshes the headers.  Usually used after data is
	 * added to the flv file
	 * @throws IOException
	 * @return void
	 */
	public void refreshHeaders() throws IOException;
	
	/**
	 * Flushes Header
	 * @throws IOException
	 * @return void
	 */
	public void flushHeaders() throws IOException;
	
	/**
	 * Returns a Reader to parse and read the flv file
	 * @return Reader reader
	 */
	public Reader reader() throws IOException;
	
	/**
	 * Returns a Reader closest to the nearest keyframe
	 * @param int seekPoint
	 * @return Reader reader
	 */
	public Reader readerFromNearestKeyFrame(int seekPoint);
	
	/**
	 * Returns a Writer
	 * @return Writer writer
	 */
	public Writer writer() throws IOException;

	/**
	 * Returns a Writer which is setup to append to flv
	 * @return Writer writer
	 */
	public Writer append() throws IOException;
	
	/**
	 * Returns a Writer based on the nearest key frame
	 * @param int seekPoint
	 * @return Writer writer
	 */
	public Writer writerFromNearestKeyFrame(int seekPoint);
	
}
