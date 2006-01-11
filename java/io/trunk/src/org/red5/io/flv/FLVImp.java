package org.red5.io.flv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class FLVImp implements FLV {

	public void setFileInputStream(FileInputStream fis) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasMetaData() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMetaData(Map metadata) {
		// TODO Auto-generated method stub

	}

	public Map getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasKeyFrameData() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setKeyFrameData(Map keyframedata) {
		// TODO Auto-generated method stub

	}

	public Map getKeyFrameData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshHeaders() throws IOException {
		// TODO Auto-generated method stub

	}

	public void flushHeaders() throws IOException {
		// TODO Auto-generated method stub

	}

	public Reader reader() {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader readerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}
 
	public Writer writer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Writer writerFromNearestKeyFrame(int seekPoint) {
		// TODO Auto-generated method stub
		return null;
	}

}
