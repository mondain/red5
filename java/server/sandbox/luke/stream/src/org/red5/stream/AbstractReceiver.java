package org.red5.stream;


public abstract class AbstractReceiver implements IReceiver, IDropper {

	public void push(IFrame packet){
		drop(packet);
	}

	public void drop(IFrame packet){
		packet.destroy();
	}

	abstract public void close();

}
