package org.red5.server.context;

public class Scope {

	private static ThreadLocal clientLocal = new ThreadLocal();

	public static Client getClient(){
		return (Client) clientLocal.get();
	}

	public static void setClient(Client client) {
		clientLocal.set(client);
	}
	
}
