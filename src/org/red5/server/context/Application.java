package org.red5.server.context;

import java.util.List;

public interface Application {

	public boolean onConnect(Client client, List params);
	
	public void onDisconnect(Client client);

}