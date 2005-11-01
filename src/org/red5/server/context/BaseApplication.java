package org.red5.server.context;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.protocol.rtmp.status2.StatusObject;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseApplication implements ApplicationContextAware, Application {

	//private StatusObjectService statusObjectService = null;
	private ApplicationContext appCtx = null;
	private HashSet clients = new HashSet();
	
	protected static Log log =
        LogFactory.getLog(BaseApplication.class.getName());
	
	public void setApplicationContext(ApplicationContext appCtx){
		this.appCtx = appCtx;
	}
	
	/*
	public void setStatusObjectService(StatusObjectService statusObjectService){
		this.statusObjectService = this.statusObjectService;
	}
	*/
	
	private StatusObject getStatus(String statusCode){
		final StatusObjectService statusObjectService = Scope.getStatusObjectService();
		return statusObjectService.getStatusObject(statusCode);
	}
	
	public final void initialize(){
		log.debug("Calling onAppStart");
		onAppStart();
	}
	
	public final StatusObject connect(List params){
		final Client client = Scope.getClient();
		log.debug("Calling onConnect");
		if(onConnect(client, params)){
			clients.add(client);
			return getStatus(StatusObjectService.NC_CONNECT_SUCCESS);
		} else {
			return getStatus(StatusObjectService.NC_CONNECT_REJECTED);
		}
	}
	
	public final void disconnect(){
		final Client client = Scope.getClient();
		clients.remove(client);
		log.debug("Calling onDisconnect");
		onDisconnect(client);
	}
	
	// -----------------------------------------------------------------------------
	
	public void onAppStart(){
		// called when the app starts
	}
	
	public boolean onConnect(Client conn, List params){
		// always ok, override
		return true;
	}
	
	public void onDisconnect(Client conn){
		// do nothing, override
	}

}
