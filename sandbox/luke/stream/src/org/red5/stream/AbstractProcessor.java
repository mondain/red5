package org.red5.stream;


public abstract class AbstractProcessor extends AbstractForwarder implements IForwarder {

	public void push(IFrame packet) {
		if(process(packet))
			forward(packet);
	}

	abstract protected boolean process(IFrame packet);

}