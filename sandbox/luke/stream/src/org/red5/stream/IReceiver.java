package org.red5.stream;

public interface IReceiver {

	public void push(IFrame packet);

	public void close();

}