package org.red5.server.context;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.SharedObjectPersistence;
import org.red5.server.SharedObjectRamPersistence;
import org.red5.server.net.rtmp.Connection;
import org.red5.server.net.rtmp.message.Ping;
import org.red5.server.net.rtmp.status.StatusObject;
import org.red5.server.net.rtmp.status.StatusObjectService;
import org.red5.server.stream.IStreamSource;
import org.red5.server.stream.Stream;
import org.red5.server.stream.StreamManager;
import org.red5.server.stream.TemporaryStream;
import org.red5.server.stream.VideoCodecFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseApplication 
	implements AppLifecycleAware, ApplicationContextAware, BeanPostProcessor {

	//private StatusObjectService statusObjectService = null;
	private ApplicationContext appCtx = null;
	private HashSet clients = new HashSet();
	private StreamManager streamManager = null;
	// Persistent shared objects are configured through red5.xml
	private SharedObjectPersistence soPersistence = null;
	// Non-persistent shared objects are only stored in memory
	private SharedObjectRamPersistence soTransience = new SharedObjectRamPersistence(); 
	private HashSet listeners = new HashSet();
	private VideoCodecFactory videoCodecs = null;
	
	protected static Log log =
        LogFactory.getLog(BaseApplication.class.getName());
	
	public void setApplicationContext(ApplicationContext appCtx){
		this.appCtx = appCtx;
	}
	
	public void setStreamManager(StreamManager streamManager){
		this.streamManager = streamManager;
	}
	
	public void setSharedObjectPersistence(SharedObjectPersistence soPersistence) {
		this.soPersistence = soPersistence;
	}
	
	public void setVideoCodecFactory(VideoCodecFactory factory) {
		this.videoCodecs = factory;
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
			Connection conn = (Connection) client;
			Ping ping = new Ping();
			ping.setValue1((short)0);
			ping.setValue2(0);
			conn.ping(ping);
			return getStatus(StatusObjectService.NC_CONNECT_SUCCESS);
		} else {
			return getStatus(StatusObjectService.NC_CONNECT_REJECTED);
		}
	}
	
	public final void disconnect(){
		final Client client = Scope.getClient();
		clients.remove(client);
		if (this.soPersistence != null) {
			// Unregister client from shared objects
			Iterator it = this.soPersistence.getSharedObjects();
			while (it.hasNext()) {
				PersistentSharedObject so = (PersistentSharedObject) it.next();
				so.unregisterClient(client);
			}
		}
		log.info("Calling onDisconnect");
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
		 play(name, new Double(-2000.0));
	}
	
	public void play(String name, Double number){
		final Stream stream = Scope.getStream();
		// it seems as if the number is sent multiplied by 1000
		int num = (int)(number.doubleValue() / 1000.0);
		if (num < -2)
			num = -2;
		stream.setName(name);
		log.debug("play: "+name);
		log.debug("stream: "+stream);
		log.debug("number:"+number);
		
		// According the documentation of NetStream.play, the number has the following
		// meanings:
		//
		// -2 (default)
		// try to play live stream <name>, if none exists, play recorded stream,
		// if no rec. stream exists, create live stream and begin playing once
		// someone publishes to it
		//
		// -1
		// play live stream, if it doesn't exist, wait for it
		//
		// 0 or positive number
		// play recorded stream at <number> seconds from the beginning
		//
		// any negative number but -1 and -2
		// use same behaviour as -2
		//
		
		boolean isPublishedStream = streamManager.isPublishedStream(name);
		boolean isFileStream = streamManager.isFileStream(name);
		
		switch (num) {
		case -2:
			if (isPublishedStream) {
				streamManager.connectToPublishedStream(stream);
				stream.start();
			} else if (isFileStream) {
				final IStreamSource source = streamManager.lookupStreamSource(name);
				log.debug(source);
				stream.setSource(source);
				
				//Integer.MAX_VALUE;
				//stream.setNSId();
				stream.start();
			} else {
				// Create temporary live stream and publish it
				streamManager.publishStream(new TemporaryStream(name, Stream.MODE_LIVE));
				streamManager.connectToPublishedStream(stream);
				stream.start();
			}
			break;
		
		case -1:
			if (isPublishedStream) {
				streamManager.connectToPublishedStream(stream);
				stream.start();
			} else {
				// TODO: Wait for stream to be created until timeout, otherwise continue
				// with next item in playlist (see Macromedia documentation)
				// NOTE: For now we create a temporary stream
				streamManager.publishStream(new TemporaryStream(name, Stream.MODE_LIVE));
				streamManager.connectToPublishedStream(stream);
				stream.start();
			}
			break;
			
		default:
			if (isFileStream) {
				final IStreamSource source = streamManager.lookupStreamSource(name);
				log.debug(source);
				stream.setSource(source);
				
				//Integer.MAX_VALUE;
				//stream.setNSId();
				// TODO: Seek to requested start
				stream.start();
			} else {
				// TODO: Wait for it, then continue with next item in playlist (?)
			}
			break;
		}
		//streamManager.play(stream, name);
		//return getStatus(StatusObjectService.NS_PLAY_START);
	}
	
	public StatusObject publish(String name, String mode){
		final Stream stream = Scope.getStream();
		stream.setName(name);
		stream.setMode(mode);
		stream.setVideoCodecFactory(this.videoCodecs);
		streamManager.publishStream(stream);
		stream.publish();		
		log.debug("publish: "+name);
		log.debug("stream: "+stream);
		log.debug("mode:"+mode);
		return getStatus(StatusObjectService.NS_PUBLISH_START);
	}
	
	
	public void pause(boolean pause, int time){
		log.info("Pause called: "+pause+" true:"+time);
		final Stream stream = Scope.getStream();
		if(pause) stream.pause();
		else stream.resume();
	}
	
	public void deleteStream(int number){
		Connection conn = (Connection) Scope.getClient();
		Stream stream = conn.getStreamById(number);
		log.debug("Delete stream: "+stream+" number: "+number);
		streamManager.deleteStream(stream);
	}
	
	public void closeStream(){
		final Stream stream = Scope.getStream();
		stream.stop();
	}
	// publishStream ?
	
	// -----------------------------------------------------------------------------
	
	public void onAppStart(){
		
	}
	
	public void onAppStop(){
		
	}
	
	public boolean onConnect(Client conn, List params){
		// always ok, override
		return true;
	}
	
	public void onDisconnect(Client conn){
		Iterator it = listeners.iterator();
		while(it.hasNext()){
			AppLifecycleAware el = (AppLifecycleAware) it.next();
			el.onDisconnect(conn);
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
		if(bean instanceof AppLifecycleAware){
			listeners.add(bean);
		}
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		// not needed
		return bean;
	}
	
	// -----------------------------------------------------------------------------
	
	public PersistentSharedObject getSharedObject(String name, boolean persistent) {
		SharedObjectPersistence persistence = this.soPersistence;
		if (!persistent) {
			persistence = this.soTransience;
		}
			
		if (persistence == null) {
			// XXX: maybe we should thow an exception here as a non-persistent SO doesn't make any sense...
			return new PersistentSharedObject(name, false, null);
		}
		
		PersistentSharedObject result = persistence.loadSharedObject(name);
		if (result == null) {
			// Create new shared object with given name
			log.info("Creating new shared object " + name);
			result = new PersistentSharedObject(name, persistent, persistence);
			persistence.storeSharedObject(result);
		}
		
		return result;
	}
	
}
