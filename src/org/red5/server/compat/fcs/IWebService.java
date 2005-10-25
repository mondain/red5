package org.red5.server.compat.fcs;

public interface IWebService {
	
	public Object onFault(ISoapFault fault);
	public Object onLoad(String wsdlDocument);
	
}
