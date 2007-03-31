package org.red5.stream.rtmp;

import org.red5.stream.AbstractReceiver;
import org.red5.stream.IFrame;

public class ConnectionReceiver extends AbstractReceiver {

	@Override
	public void close() {
		// close channels
	}

	@Override
	public void push(IFrame packet) {
		// push packet to channel.
	}

}
