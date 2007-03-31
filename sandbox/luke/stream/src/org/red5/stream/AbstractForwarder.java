package org.red5.stream;


public abstract class AbstractForwarder extends AbstractReceiver implements IForwarder {

	protected IReceiver receiver = null;

	public void setReceiver(IReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void push(IFrame packet) {
		forward(packet);
	}

	protected boolean canForward(){
		return receiver != null;
	}

	protected void forward(IFrame packet){
		if(canForward()) receiver.push(packet);
		else drop(packet);
	}

	public void close(){
		if(receiver != null)
			receiver.close();
		receiver = null;
	}

}