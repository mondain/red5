package org.red5.server.rtmp.message;

import org.red5.server.service.Call;

public class Invoke extends Packet {
	
	private static final int INITIAL_CAPACITY = 1024;
	
	private Call call;
	
	public Invoke(){
		super(INITIAL_CAPACITY);
	}

	public Call getCall() {
		return call;
	}

	public void setCall(Call call) {
		this.call = call;
	}

	protected void doRelease() {
		call = null;
	}
	
}
