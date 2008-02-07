package org.red5.stream;

public interface ICaster extends IForwarder {

	public void add(IReceiver receiver);
	public void remove(IReceiver receiver);

}
