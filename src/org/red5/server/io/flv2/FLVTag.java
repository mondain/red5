package org.red5.server.io.flv2;

import org.apache.mina.common.ByteBuffer;

public class FLVTag {
	
	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	public static final byte TYPE_METADATA = 0x18;

	protected byte dataType;
	protected int timestamp;
	protected int bodySize;
	protected ByteBuffer body;
	
	public FLVTag(byte dataType, int timestamp, int bodySize, ByteBuffer body){
		this.dataType = dataType;
		this.timestamp = timestamp;
		this.bodySize = bodySize;
		this.body = body;
	}
	
	public ByteBuffer getBody() {
		return body;
	}
	
	public int getBodySize() {
		return bodySize;
	}
	
	public byte getDataType() {
		return dataType;
	}
	
	public int getTimestamp() {
		return timestamp;
	}

}
