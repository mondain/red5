package org.red5.server.protocol.rtmp;

import java.util.HashMap;
import java.util.Map;

public class ApplicationStatus {
	private static Map SCRIPT_ERROR;
	private static Map SCRIPT_WARNING;
	private static Map RESOURCE_LOW_MEMORY;
	private static Map SHUTDOWN;
	private static Map GC;
	
	public ApplicationStatus() {
		
		// SUCCESS
		SCRIPT_ERROR = new HashMap();
		SCRIPT_ERROR.put("level",IApplicationStatusMessage.SCRIPT_ERROR_LEVEL);
		SCRIPT_ERROR.put("code",IApplicationStatusMessage.SCRIPT_ERROR_CODE);
		
		// SCRIPT_WARNING
		SCRIPT_WARNING = new HashMap();
		SCRIPT_WARNING.put("level",IApplicationStatusMessage.SCRIPT_WARNING_LEVEL);
		SCRIPT_WARNING.put("code",IApplicationStatusMessage.SCRIPT_WARNING_CODE);
		
		// RESOURCE_LOW_MEMORY
		RESOURCE_LOW_MEMORY = new HashMap();
		RESOURCE_LOW_MEMORY.put("level",IApplicationStatusMessage.RESOURCE_LOW_MEMORY_LEVEL);
		RESOURCE_LOW_MEMORY.put("code",IApplicationStatusMessage.RESOURCE_LOW_MEMORY_CODE);
		
		// SHUTDOWN
		SHUTDOWN = new HashMap();
		SHUTDOWN.put("level",IApplicationStatusMessage.SHUTDOWN_LEVEL);
		SHUTDOWN.put("code",IApplicationStatusMessage.SHUTDOWN_CODE);
		
		// GC
		GC = new HashMap();
		GC.put("level",IApplicationStatusMessage.GC_LEVEL);
		GC.put("code",IApplicationStatusMessage.GC_CODE);
	}
}
