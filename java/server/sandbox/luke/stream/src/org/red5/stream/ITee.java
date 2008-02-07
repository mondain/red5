package org.red5.stream;

public interface ITee extends IForwarder {

	public void setOutput(IReceiver receiver);

}