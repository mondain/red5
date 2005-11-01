package org.red5.server.context;

import org.red5.server.protocol.rtmp.status2.StatusObjectService;

public class Scope {

	private static ThreadLocal clientLocal = new ThreadLocal();
	private static ThreadLocal statusObjectServiceLocal = new ThreadLocal();
	
	public static Client getClient(){
		return (Client) clientLocal.get();
	}

	public static void setClient(Client client) {
		clientLocal.set(client);
	}
	
	public static StatusObjectService getStatusObjectService(){
		return (StatusObjectService) statusObjectServiceLocal.get();
	}

	public static void setStatusObjectService(StatusObjectService sos) {
		statusObjectServiceLocal.set(sos);
	}
}
