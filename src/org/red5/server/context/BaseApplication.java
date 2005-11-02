package org.red5.server.context;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.protocol.rtmp.status2.StatusObject;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.red5.server.stream.Stream;
import org.red5.server.stream.StreamManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseApplication implements ApplicationContextAware, Application {

	//private StatusObjectService statusObjectService = null;
	private ApplicationContext appCtx = null;
	private HashSet clients = new HashSet();
	private StreamManager streamManager = null;
	
	protected static Log log =
        LogFactory.getLog(BaseApplication.class.getName());
	
	public void setApplicationContext(ApplicationContext appCtx){
		this.appCtx = appCtx;
	}
	
	public void setStreamManager(StreamManager streamManager){
		this.streamManager = streamManager;
	}
	
	/*
	public void setStatusObjectService(StatusObjectService statusObjectService){
		this.statusObjectService = this.statusObjectService;
	}
	*/
	
	private StatusObject getStatus(String statusCode){
		// TODO: fix this, getting the status service out of the thread scope is a hack
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
	
	public int createStream(){
		// i think this is to say if the user is allowed to create a stream
		// if it returns 0 the play call will not come through
		// any number higher than 0 seems to do the same thing
		return 1; 
	}
	
	public void play(String name){
		final Stream stream = Scope.getStream();
		log.debug("play: "+name);
		log.debug("stream: "+stream);
		//streamManager.play(stream, name);
	}
	
	public void pause(){
		
	}
	
	public void deleteStream(){
		
	}
	
	public void closeStream(){
		
	}
	// publishStream ?
	
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
