package org.red5.service.httpstream.model;

import java.nio.ByteBuffer;

public class AudioFrame implements MediaFrame {

	private ByteBuffer data;

	public AudioFrame(byte[] audioData) {
		data = ByteBuffer.wrap(audioData);
	}

	public AudioFrame(ByteBuffer audioData) {
		byte[] buf = new byte[audioData.limit()];
		audioData.get(buf);
		data = ByteBuffer.wrap(buf);
		audioData.clear();
	}	
	
	public boolean isAudio() {
		return true;
	}

	public boolean isVideo() {
		return false;
	}

	public long getTimestamp() {
		return 0;
	}

	public ByteBuffer getData() {
		return data.asReadOnlyBuffer();
	}	
	
}
