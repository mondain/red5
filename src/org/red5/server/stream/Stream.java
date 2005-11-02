package org.red5.server.stream;

import org.red5.server.rtmp.Channel;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Message;

public class Stream implements Constants {
	
	int source = 0;
	
	int bitRate = 0;
	int bytesRead = 0;
	int lastTimeStamp = 0;
	
	private Channel video;
	private Channel audio;
	private Channel data;
	private Channel unknown;
	private Channel ctrl;

	//addOutput(Connection conn);
	//writeData(Message, timer);
	
	public Stream(Channel video, Channel audio, Channel data, Channel unknown, Channel ctrl){
		this.video = video;
		this.audio = audio;
		this.data = data;
		this.unknown = unknown;
		this.ctrl = ctrl;
	}
	
	public void write(Message message){
		// switch on type and send down channel
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
	
	public void byteRead(int bytes){
		/*
		 * lastBytesRead / last
		 * speed. 
		 */
		// schedule a delay, until next write
	}
	
}
