package org.red5.stream;

import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class AbstractBuffer extends AbstractForwarder {

	protected ConcurrentLinkedQueue<IFrame> stack = new ConcurrentLinkedQueue<IFrame>();
	protected int capacity = 0;
	protected int position = 0;

	@Override
	public void push(IFrame packet) {
		stack.offer(packet);
		add(packet);
		while(full()) overflow();
	}

	protected void overflow(){
		final IFrame packet = stack.poll();
		remove(packet);
		forward(packet);
	}

	abstract protected void add(IFrame packet);
	abstract protected void remove(IFrame packet);
	abstract protected boolean full();

}
