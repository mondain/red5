package org.red5.server.stream;

import org.red5.server.rtmp.Channel;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Message;

public class DownStreamSink implements IStreamSink, Constants {

	private Channel video;
	private Channel audio;
	private Channel data;
	
	public DownStreamSink(Channel video, Channel audio, Channel data){
		this.video = video;
		this.audio = audio;
		this.data = data;
	}
	
	public boolean canAccept(){
		return true;
	}
	
	public void enqueue(Message message){
		switch(message.getDataType()){
		case TYPE_VIDEO_DATA:
			video.write(message);
			break;
		case TYPE_AUDIO_DATA:
			audio.write(message);
			break;
		default:
			data.write(message);
			break;
		}
	}
	
	public void close(){
		// do something ?
	}
	
}
