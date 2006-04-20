package org.red5.io.flv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.red5.io.flv.meta.IMetaData;
import org.red5.io.flv.meta.IMetaService;

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
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public void setMetaData(IMetaData metadata) throws FileNotFoundException, IOException;
	
	/**
	 * Sets the MetaService through Spring
	 * @param IMetaService service
	 */
	public void setMetaService(IMetaService service);
	
	/**
	 * Returns a map of the metadata
	 * @return Map metadata
	 * @throws FileNotFoundException 
	 */
	public IMetaData getMetaData() throws FileNotFoundException;
	
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
	public IReader reader() throws IOException;
	
	/**
	 * Returns a Reader closest to the nearest keyframe
	 * @param int seekPoint
	 * @return Reader reader
	 */
	public IReader readerFromNearestKeyFrame(int seekPoint);
	
	/**
	 * Returns a Writer
	 * @return Writer writer
	 */
	public IWriter writer() throws IOException;

	/**
	 * Returns a Writer which is setup to append to flv
	 * @return Writer writer
	 */
	public IWriter append() throws IOException;
	
	/**
	 * Returns a Writer based on the nearest key frame
	 * @param int seekPoint
	 * @return Writer writer
	 */
	public IWriter writerFromNearestKeyFrame(int seekPoint);
	
}
