package org.red5.stream.utils;

import java.util.HashSet;
import java.util.Set;

import org.red5.stream.AbstractProcessor;
import org.red5.stream.ICaster;
import org.red5.stream.IFrame;
import org.red5.stream.IReceiver;

public class Broadcaster extends AbstractProcessor implements ICaster {

	protected Set<IReceiver> receivers = new HashSet<IReceiver>();

	public void add(IReceiver receiver) {
		receivers.add(receiver);
	}

	public void remove(IReceiver receiver) {
		receivers.remove(receiver);
	}

	@Override
	protected boolean process(IFrame packet) {
		for(IReceiver receiver : receivers)
			receiver.push(packet.clone());
		return true;
	}

	public void close(){
		for(IReceiver receiver : receivers)
			receiver.close();
		super.close();
	}

}