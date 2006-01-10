package org.red5.io.flv;

import java.nio.ByteBuffer;

public interface Tag {

	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	public static final byte TYPE_METADATA = 0x12;
	
	public byte getType();
	public int getSize();
	public ByteBuffer getData();
	
}
