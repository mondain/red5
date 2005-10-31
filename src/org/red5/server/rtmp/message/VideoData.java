package org.red5.server.rtmp.message;

import org.red5.server.stream.Stream;

public class VideoData extends Message {
	
	private static final int INITIAL_CAPACITY = 2048;
	
	private Stream stream;

	public VideoData(){
		super(TYPE_VIDEO_DATA, INITIAL_CAPACITY);
	}
	
	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}
	
	protected void doRelease(){
		stream = null;
	}
	
}
