package org.red5.server.protocol.rtmp.status;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

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
