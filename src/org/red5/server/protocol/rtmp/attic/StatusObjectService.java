package org.red5.server.protocol.rtmp.attic;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors (see below). All rights reserved.
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
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

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
