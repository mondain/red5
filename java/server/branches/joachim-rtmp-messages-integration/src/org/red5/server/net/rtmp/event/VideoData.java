package org.red5.server.net.rtmp.event;

import org.apache.mina.common.ByteBuffer;
import org.red5.server.stream.IStreamData;
import org.red5.server.net.rtmp.message.Header;

public class VideoData extends BaseEvent implements IStreamData {

	protected ByteBuffer data = null;
	
	public VideoData(ByteBuffer data){
		super(Type.STREAM_DATA);
		this.data = data;
	}

	public byte getDataType() {
		return TYPE_VIDEO_DATA;
	}
	
	public ByteBuffer getData(){
		return data;
	}
	
	public String toString(){
		return "Audio  ts: "+getTimestamp();
	}
	
}