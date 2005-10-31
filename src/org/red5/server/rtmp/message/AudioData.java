package org.red5.server.rtmp.message;

import org.red5.server.stream.Stream;

public class AudioData extends Message {

	private static final int INITIAL_CAPACITY = 2048;
	
	private Stream stream;
	
	public AudioData(){
		super(TYPE_AUDIO_DATA, INITIAL_CAPACITY);
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
