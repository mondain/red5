package org.red5.stream.utils;

import org.red5.stream.AbstractProcessor;
import org.red5.stream.IFrame;
import org.red5.stream.IReceiver;
import org.red5.stream.ITee;

public class Recorder extends AbstractProcessor implements ITee {

	protected IReceiver out;
	protected boolean recording = false;

	public void setOutput(IReceiver receiver) {
		out = receiver;
	}

	@Override
	protected boolean process(IFrame packet) {
		if(isRecording()) out.push(packet.clone());
		return true;
	}

	public boolean isRecording(){
		return (out != null && recording);
	}

	public void record(){
		recording = true;
	}

	public void pause(){
		recording = false;
	}

	public void stop(){
		recording = false;
		// close the output
	}

}
