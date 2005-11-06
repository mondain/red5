package org.red5.server.stream;

import org.red5.server.io.flv2.FLVReader;
import org.red5.server.io.flv2.FLVTag;
import org.red5.server.rtmp.message.Message;

public class FileStreamSource implements IStreamSource {

	private FLVReader reader = null;
	
	public void close() {
		
	}

	public Message dequeue() {
		final FLVTag tag = reader.getNextTag();
		return null;
	}

	public boolean hasMore() {
		return reader.hasMoreTags();
	}
	
}
