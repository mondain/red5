package org.red5.stream.utils;

import org.red5.stream.AbstractForwarder;
import org.red5.stream.ISender;

public class Muxer extends AbstractForwarder {

	protected ISender audio;
	protected ISender video;
	protected ISender data;

	public ISender getAudio() {
		return audio;
	}

	public void setAudio(ISender audio) {
		if(this.audio != null)
			this.audio.setReceiver(null);
		this.audio = audio;
		this.audio.setReceiver(this);
	}

	public ISender getData() {
		return data;
	}

	public void setData(ISender data) {
		if(this.data != null)
			this.data.setReceiver(null);
		this.data = data;
		this.data.setReceiver(this);
	}

	public ISender getVideo() {
		return video;
	}

	public void setVideo(ISender video) {
		if(this.video != null)
			this.video.setReceiver(null);
		this.video = video;
		this.video.setReceiver(this);
	}

}
