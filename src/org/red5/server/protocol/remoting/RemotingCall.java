package org.red5.server.protocol.remoting;

import org.red5.server.service.Call;

public class RemotingCall extends Call {

	public static final String HANDLER_SUCCESS = "/onResult";
	public static final String HANDLER_ERROR = "/onStatus";
	
	public String clientCallback = null;

	public RemotingCall(String serviceName, String serviceMethod, Object[] args, String callback){
		super(serviceName, serviceMethod, args);
		setClientCallback(callback);
	}
	
	public void setClientCallback(String clientCallback) {
		this.clientCallback = clientCallback;
	}

	public String getClientResponse(){
		if(clientCallback != null)
			return clientCallback + (isSuccess() ? HANDLER_SUCCESS : HANDLER_ERROR);
		else return null; 
	}
	
	public Object getClientResult(){
		return isSuccess() ? getResult() : getException();
	}

}
