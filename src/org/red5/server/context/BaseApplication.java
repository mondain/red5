package org.red5.server.context;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.red5.server.protocol.rtmp.status2.StatusObject;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseApplication implements ApplicationContextAware, Application {

	private StatusObjectService statusObjectService = null;
	private ApplicationContext appCtx = null;
	private HashSet clients = new HashSet();
	
	public void setApplicationContext(ApplicationContext appCtx){
		this.appCtx = appCtx;
	}
	
	public void setStatusObjectService(StatusObjectService statusObjectService){
		this.statusObjectService = this.statusObjectService;
	}
	
	public StatusObject getStatus(String statusCode){
		return statusObjectService.getStatusObject(statusCode);
	}
	
	public void initialize(){
		onAppStart();
	}
	
	public StatusObject connect(Map param, List params){
		final Client client = Scope.getClient();
		if(onConnect(client, params)){
			clients.add(client);
			return getStatus(StatusObjectService.NC_CONNECT_SUCCESS);
		} else {
			return getStatus(StatusObjectService.NC_CONNECT_REJECTED);
		}
	}
	
	public void disconnect(){
		final Client client = Scope.getClient();
		clients.remove(client);
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
