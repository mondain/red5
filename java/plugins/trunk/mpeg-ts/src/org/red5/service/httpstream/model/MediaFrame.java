package org.red5.service.httpstream.model;

import java.nio.ByteBuffer;

public interface MediaFrame {
	
	public boolean isAudio();

	public boolean isVideo();
	
	public long getTimestamp();
	
	public ByteBuffer getData();
	
}
