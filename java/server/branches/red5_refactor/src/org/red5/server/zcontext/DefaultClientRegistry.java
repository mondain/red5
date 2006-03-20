package org.red5.server.zcontext;

import java.util.HashMap;

import org.red5.server.api.IClient;
import org.red5.server.api.IClientRegistry;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.core.Client;
import org.red5.server.net.AccessDeniedException;
import org.red5.server.net.ClientNotFoundException;
import org.red5.server.net.HostNotFoundException;
import org.red5.server.net.ScopeNotFoundException;
import org.springframework.context.ApplicationContext;

public class DefaultClientRegistry implements IClientRegistry {

	private HashMap clients = new HashMap();
	private Integer sessionIdCounter = new Integer(0);
	
	public void registerClient(IClient client) {
		clients.put(client.getId(), client);
	}

	public void unregisterClient(IClient client) {
		clients.remove(client.getId());
	}

	public boolean hasClient(String id) {
		return clients.containsKey(id);
	}

	public IClient newClient() throws HostNotFoundException {
		Integer sid;
		synchronized (sessionIdCounter) {
			sid = sessionIdCounter;
			sessionIdCounter = new Integer(sid.intValue() + 1);
		}
		IClient client = new Client(sid.toString());
		registerClient(client);
		return client;
	}

	public IClient lookupClient(String sessionId) throws ClientNotFoundException {
		if (!clients.containsKey(sessionId))
			throw new ClientNotFoundException();
		return (IClient) clients.get(sessionId);
	}

}
