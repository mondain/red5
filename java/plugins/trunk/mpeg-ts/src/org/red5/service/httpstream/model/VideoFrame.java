package org.red5.service.httpstream.model;

import java.nio.ByteBuffer;

public class VideoFrame implements MediaFrame {

	private ByteBuffer data;
	
	private int width;
	
	private int height;
	
	private int fps;
	
	private long timestamp;
	
	public VideoFrame(byte[] videoData) {
		data = ByteBuffer.wrap(videoData);
	}

	public VideoFrame(ByteBuffer videoData) {
		byte[] buf = new byte[videoData.limit()];
		videoData.get(buf);
		data = ByteBuffer.wrap(buf);
		videoData.clear();
	}

	public boolean isAudio() {
		return false;
	}

	public boolean isVideo() {
		return true;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ByteBuffer getData() {
		return data.asReadOnlyBuffer();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}
	
}
