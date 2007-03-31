package org.red5.stream.utils;

import org.red5.stream.AbstractBuffer;
import org.red5.stream.IFrame;

public class TimeBuffer extends AbstractBuffer {

	public TimeBuffer(int capacity){
		this.capacity = capacity;
	}

	@Override
	protected void add(IFrame packet) {
		position += packet.getTime();
	}

	@Override
	protected void remove(IFrame packet) {
		position -= packet.getTime();
	}

	@Override
	protected boolean full() {
		return position > capacity;
	}

}
