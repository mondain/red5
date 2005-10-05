package org.red5.server.protocol.rtmp.status;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

public interface IApplicationStatusMessage {
	
	// SCRIPT_ERROR
	public static final String SCRIPT_ERROR_LEVEL = "Error";
	public static final String SCRIPT_ERROR_CODE = "Application.Script.Error";
	
	// SCRIPT_WARNING
	public static final String SCRIPT_WARNING_LEVEL = "Warning";
	public static final String SCRIPT_WARNING_CODE = "Application.Script.Warning";
	
	// RESOURCE_LOW_MEMORY
	public static final String RESOURCE_LOW_MEMORY_LEVEL = "Warning";
	public static final String RESOURCE_LOW_MEMORY_CODE = "Application.Resource.LowMemory";
	
	// SHUTDOWN
	public static final String SHUTDOWN_LEVEL = "Status";
	public static final String SHUTDOWN_CODE = "Application.Shutdown";
	
	// GC
	public static final String GC_LEVEL = "Status";
	public static final String GC_CODE = "Application.GC";
}
