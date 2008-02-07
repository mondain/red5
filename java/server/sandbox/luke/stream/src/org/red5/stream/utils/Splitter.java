package org.red5.stream.utils;

import org.red5.stream.AbstractProcessor;
import org.red5.stream.IFrame;
import org.red5.stream.IReceiver;

public class Splitter extends AbstractProcessor {

	protected IReceiver audio = null;
	protected IReceiver video = null;
	protected IReceiver data = null;

	public IReceiver getAudio() {
		return audio;
	}

	public void setAudio(IReceiver audio) {
		this.audio = audio;
	}

	public IReceiver getData() {
		return data;
	}

	public void setData(IReceiver data) {
		this.data = data;
	}


	public IReceiver getVideo() {
		return video;
	}


	public void setVideo(IReceiver video) {
		this.video = video;
	}

	@Override
	protected boolean process(IFrame packet) {
		switch(packet.getType()){
		case AUDIO:
			audio.push(packet);
			break;
		case VIDEO:
			video.push(packet);
			break;
		case DATA:
			data.push(packet);
			break;
		}
		return true;
	}

}
