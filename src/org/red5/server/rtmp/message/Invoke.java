package org.red5.server.rtmp.message;

import java.util.Map;

import org.red5.server.service.Call;

public class Invoke extends Message {
	
	private static final int INITIAL_CAPACITY = 1024;
	
	private Call call;
	private int invokeId = 0;
	private Map connectionParams = null;
	
	public Invoke(){
		super(TYPE_INVOKE,INITIAL_CAPACITY);
	}

	public Call getCall() {
		return call;
	}

	public void setCall(Call call) {
		this.call = call;
	}

	public int getInvokeId() {
		return invokeId;
	}

	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}

	protected void doRelease() {
		call = null;
	}

	public Map getConnectionParams() {
		return connectionParams;
	}

	public void setConnectionParams(Map connectionParams) {
		this.connectionParams = connectionParams;
	}
	
}
