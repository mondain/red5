package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.rtmp.message.PacketHeader;
import org.red5.server.stream.Stream;

public class Channel {

	protected static Log log =
        LogFactory.getLog(Channel.class.getName());
	
	private Stream stream;
	private PacketHeader lastReadHeader;
	private PacketHeader lastWriteHeader;

	public Stream getStream() {
		return stream;
	}

	public PacketHeader getLastReadHeader() {
		return lastReadHeader;
	}

	public void setLastReadHeader(PacketHeader lastReadHeader) {
		this.lastReadHeader = lastReadHeader;
	}

	public PacketHeader getLastWriteHeader() {
		return lastWriteHeader;
	}

	public void setLastWriteHeader(PacketHeader lastWriteHeader) {
		this.lastWriteHeader = lastWriteHeader;
	}

}
