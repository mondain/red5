package org.red5.server.compat.fcs;

public interface IApplication {
		
	public void acceptConnection(Client clientObj);
	public void disconnect(Client clientObj);
}