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
 * @author Dominick Accattato (dominick@red5.org)
 */
public class ConnectionStatus {

	private static Map SUCCESS;
	private static Map CALL_FAILED;
	// TODO OTHERS	
	
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
		
		// DO OTHER CODES
	}
	
	public Map getStatus(String status) {
		String s = status;
		if(s.equals("success")) {
			return SUCCESS;
		} else if(s.equals("call_failed")){
			return CALL_FAILED;
		}
		
		return null;
		
	}

	
}
