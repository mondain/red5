package org.red5.server.service;

public class ServiceNotFoundException extends RuntimeException {

	public ServiceNotFoundException(String serviceName){
		super("Service not found: "+serviceName);
	}

}
