package org.red5.server.api.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;

public class Client extends AttributeStore  
	implements  org.red5.server.api.IClient {

	protected String id;
	protected long creationTime;
	protected HashMap<IConnection,IScope> connToScope = new HashMap<IConnection,IScope>();
	
	public Client(String id){
		this.id = id;
		this.creationTime = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public long getCreationTime() {
		return creationTime;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Client)) return false;
		final Client client = (Client) obj;
		return client.getId() == getId();
 	}

	public String toString() {
		return "Client: "+id;
	}
	
	public Set<IConnection> getConnections() {
		return connToScope.keySet();
	}

	public Collection<IScope> getScopes() {
		return connToScope.values();
	}

	public void disconnect() {
		Iterator<IConnection> conns = getConnections().iterator();
		while(conns.hasNext()){
			conns.next().close();
		}
	}
		
	void register(IConnection conn){
		connToScope.put(conn, conn.getScope());
	}
	
	void unregister(IConnection conn){
		connToScope.remove(conn);
	}

}