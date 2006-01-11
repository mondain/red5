package org.red5.io.flv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public interface FLV {

	public boolean hasMetaData();
	public void setMetaData(Map metadata);
	public Map getMetaData();
	
	public boolean hasKeyFrameData();
	public void setKeyFrameData(Map keyframedata);
	public Map getKeyFrameData();
	
	public void refreshHeaders() throws IOException;
	public void flushHeaders() throws IOException;
	
	public Reader reader();
	public Reader readerFromNearestKeyFrame(int seekPoint);
	
	public Writer writer();
	public Writer writerFromNearestKeyFrame(int seekPoint);
	public void setFileInputStream(FileInputStream fis);
	
}
