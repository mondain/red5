package org.red5.server;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard (luke@red5.org)
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.net.NetworkManager;
import org.springframework.context.ApplicationContext;

public class Server {
	
	// NOTE: Moved main to Standalone.java
	
	protected static Log log =
        LogFactory.getLog(Server.class.getName());
	
	protected NetworkManager networkManager;
	protected SessionRegistry sessionRegistry;
	protected ApplicationContext serviceContext;
	
	public void startup(){
		if(log.isInfoEnabled()){
			log.info("Startup");
			networkManager.up();
		}
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public ApplicationContext getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(ApplicationContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public SessionRegistry getSessionRegistry() {
		return sessionRegistry;
	}

	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

}
