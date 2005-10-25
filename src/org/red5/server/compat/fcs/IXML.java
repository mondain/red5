package org.red5.server.compat.fcs;

public interface IXML {
	
	public void addRequestHeader(String headerName, String headerValue);
	public void addRequestHeader(String[] headerHash);
	
	public void appendChild(/*XmlNode childNode*/);
	public void cloneNode(boolean deep);
}
