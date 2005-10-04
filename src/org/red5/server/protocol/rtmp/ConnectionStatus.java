package org.red5.server.protocol.rtmp;

import java.util.HashMap;
import java.util.Map;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */
public class ConnectionStatus {

	private static Map CALL_FAILED;
	private static Map APP_SHUT_DOWN;
	private static Map BAD_VERSION;
	private static Map CLOSED;
	private static Map CONNECT_FAILED;
	private static Map INVALID_APP;
	private static Map REJECTED;
	private static Map SUCCESS;
	
	public ConnectionStatus() {
		
		// SUCCESS
		SUCCESS = new HashMap();
		SUCCESS.put("description",IConnectionStatusMessage.SUCCESS_DESCRIPTION);
		SUCCESS.put("code",IConnectionStatusMessage.SUCCESS_CODE);
		SUCCESS.put("level",IConnectionStatusMessage.SUCCESS_LEVEL);
		
		// CALL_FAILED
		CALL_FAILED = new HashMap();
		CALL_FAILED.put("description",IConnectionStatusMessage.CALL_FAILED_DESCRIPTION);
		CALL_FAILED.put("code",IConnectionStatusMessage.CALL_FAILED_CODE);
		CALL_FAILED.put("level",IConnectionStatusMessage.CALL_FAILED_LEVEL);
		
		// APP_SHUT_DOWN
		APP_SHUT_DOWN = new HashMap();
		APP_SHUT_DOWN.put("description",IConnectionStatusMessage.APP_SHUT_DOWN_DESCRIPTION);
		APP_SHUT_DOWN.put("code",IConnectionStatusMessage.APP_SHUT_DOWN_CODE);
		APP_SHUT_DOWN.put("level",IConnectionStatusMessage.APP_SHUT_DOWN_LEVEL);
	
		// BAD_VERSION
		BAD_VERSION = new HashMap();
		BAD_VERSION.put("description",IConnectionStatusMessage.BAD_VERSION_DESCRIPTION);
		BAD_VERSION.put("code",IConnectionStatusMessage.BAD_VERSION_CODE);
		BAD_VERSION.put("level",IConnectionStatusMessage.BAD_VERSION_LEVEL);
		
		// CLOSED
		CLOSED = new HashMap();
		CLOSED.put("description",IConnectionStatusMessage.CLOSED_DESCRIPTION);
		CLOSED.put("code",IConnectionStatusMessage.CLOSED_CODE);
		CLOSED.put("level",IConnectionStatusMessage.CLOSED_LEVEL);
		
		// CONNECT_FAILED
		CONNECT_FAILED = new HashMap();
		CONNECT_FAILED.put("description",IConnectionStatusMessage.CONNECT_FAILED_DESCRIPTION);
		CONNECT_FAILED.put("code",IConnectionStatusMessage.CONNECT_FAILED_CODE);
		CONNECT_FAILED.put("level",IConnectionStatusMessage.CONNECT_FAILED_LEVEL);
		
		// INVALID_APP
		INVALID_APP = new HashMap();
		INVALID_APP.put("description",IConnectionStatusMessage.INVALID_APP_DESCRIPTION);
		INVALID_APP.put("code",IConnectionStatusMessage.INVALID_APP_CODE);
		INVALID_APP.put("level",IConnectionStatusMessage.INVALID_APP_LEVEL);
		
		// REJECTED
		REJECTED = new HashMap();
		REJECTED.put("description",IConnectionStatusMessage.REJECTED_DESCRIPTION);
		REJECTED.put("code",IConnectionStatusMessage.REJECTED_CODE);
		REJECTED.put("level",IConnectionStatusMessage.REJECTED_LEVEL);
		// DO OTHER CODES
	}
	
	public Map getStatus(String status) {
		String s = status;
		if(s.equals("SUCCESS")) {
			return SUCCESS;
		} else if(s.equals("CALL_FAILED")){
			return CALL_FAILED;
		} else if(s.equals("APP_SHUT_DOWN")){
			return APP_SHUT_DOWN;
		} else if(s.equals("BAD_VERSION")){
			return BAD_VERSION;
		} else if(s.equals("CLOSED")){
			return CLOSED;
		} else if(s.equals("CONNECT_FAILED")){
			return CONNECT_FAILED;
		} else if(s.equals("INVALID_APP")){
			return INVALID_APP;
		} else if(s.equals("REJECTED")){
			return REJECTED;
		}
		
		return null;
		
	}

	
}
