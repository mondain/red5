package org.red5.server.service;

public class Call {

	public static final byte STATUS_PENDING = 0x01;
	public static final byte STATUS_SUCCESS_RESULT = 0x02;
	public static final byte STATUS_SUCCESS_NULL = 0x03;
	public static final byte STATUS_SERVICE_NOT_FOUND = 0x04;
	public static final byte STATUS_METHOD_NOT_FOUND = 0x05;
	public static final byte STATUS_ACCESS_DENIED = 0x06;
	public static final byte STATUS_INVOCATION_EXCEPTION = 0x07;
	public static final byte STATUS_GENERAL_EXCEPTION = 0x08;
	
    protected String serviceName;
    protected String serviceMethodName;
    protected Object[] arguments;
    protected Object result = null;
    protected byte status = STATUS_PENDING;
    protected Exception exception = null;
    
    public Call(String name, String method, Object[] args){
    		serviceName = name;
    		serviceMethodName = method;
    		arguments = args;
    		status = STATUS_PENDING;
    }
    
    public boolean isSuccess(){
    		return (status == STATUS_SUCCESS_RESULT) || (status == STATUS_SUCCESS_NULL);
    }
    
	public String getServiceMethodName() {
		return serviceMethodName;
	}
	public void setServiceMethodName(String serviceMethodName) {
		this.serviceMethodName = serviceMethodName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Object[] getArguments() {
		return arguments;
	}
	public void setArguments(Object[] args) {
		arguments = args;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
 
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Service: "+serviceName+" Method: "+serviceMethodName);
		if(arguments!=null) sb.append(" Num Params: "+arguments.length);
		else sb.append(" No params");
		return sb.toString();
	}
	
	
}
