package org.red5.io.flv;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Writer {
	
	public FLV getFLV();
	public long getOffset();
	public long getBytesWritten();
	
	public boolean writeTag(Tag tag) throws IOException;
	public boolean writeTag(byte type, ByteBuffer data) throws IOException;
	
	public void close();
	
}
