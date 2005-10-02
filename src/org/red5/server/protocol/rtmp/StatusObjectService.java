package org.red5.server.protocol.rtmp;

import java.util.Map;

public class StatusObjectService {

	public static final String NC_CALL_FAILED = "";
	public static final String NC_CALL_BADVERSION = "";
	
	public static final String NC_CONNECT_APPSHUTDOWN = "";
	public static final String NC_CONNECT_CLOSED = "";
	public static final String NC_CONNECT_FAILED = "";
	public static final String NC_CONNECT_INVALID_APP = "";
	public static final String NC_CONNECT_REJECTED = "";
	public static final String NC_CONNECT_SUCCESS = "";
	
	public static final String NS_CLEAR_SUCCESS = "";
	public static final String NS_CLEAR_FAILED = "";
	
	public static final String NS_PUBLISH_START = "";
	public static final String NS_PUBLISH_BADNAME = "";
	public static final String NS_FAILED = "";
	public static final String NS_UNPUBLISHED_SUCCESS = "";
	
	public static final String NS_RECORD_START = "";
	public static final String NS_RECOED_NOACCESS = "";
	public static final String NS_RECORD_STOP = "";
	public static final String NS_RECORD_FAILED = "";
	
	public static final String NS_PLAY_INSUFFICIENT_BW = "";
	public static final String NS_PLAY_START = "";
	public static final String NS_PLAY_STREAMNOTFOUND = "";
	
	public static final String NS_PLAY_STOP = "";
	public static final String NS_PLAY_FAILED = "";
	public static final String NS_PLAY_RESET = "";
	public static final String NS_PLAY_PUBLISHNOTIFY = "";
	public static final String NS_PLAY_UNPUBLISHNOTIFY = "";
	
	public static final String APP_SCRIPT_ERROR = "";
	public static final String APP_SCRIPT_WARNING ="";
	public static final String APP_RESOURCE_LOWMEMORY = "";
	public static final String APP_SHUTDOWN = "";
	public static final String APP_GC = "";
	
	protected Map statusObjects;
	
	public void loadStatusObjects(){
		
		
		
	}
	
	public StatusObject getStatusObject(String statusCode){
		return null;
	}
	
	public StatusObject getStatusObject(String statusCode, String message){
		return null;
	}
	
	public StatusObject getStatusObject(String statusCode, Throwable ex){
		return null;
	}
	
	public StatusObject getStatusObject(String statusCode, String message, Throwable ex){
		return null;
	}
	
}
