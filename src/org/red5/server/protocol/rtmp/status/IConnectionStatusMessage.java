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

public interface IConnectionStatusMessage {

	// CALL FAILED
	public static final String CALL_FAILED_CODE = "NetConnection.Call.Failed";
	public static final String CALL_FAILED_LEVEL = "Error";
	public static final String CALL_FAILED_MEANING = "The NetConnection.call method was not able to invoke the server-side method or command.";
	public static final String CALL_FAILED_DESCRIPTION = "description";  //TODO come up with valid descriptions why connection failed

	// APP SHUT DOWN
	public static final String APP_SHUT_DOWN_CODE = "NetConnection.Connect.AppShutdown";
	public static final String APP_SHUT_DOWN_LEVEL = "Error";
	public static final String APP_SHUT_DOWN_DESCRIPTION = "description";

	// BAD VERSION
	public static final String BAD_VERSION_CODE = "NetConnection.call.BadVersion";
	public static final String BAD_VERSION_LEVEL = "Error";
	public static final String BAD_VERSION_DESCRIPTION = "description";

	// CLOSED
	public static final String CLOSED_CODE = "NetConnection.Connect.Closed";
	public static final String CLOSED_LEVEL = "Status";
	public static final String CLOSED_DESCRIPTION = "description";

	// CONNECT FAILED
	public static final String CONNECT_FAILED_CODE = "NetConnection.Connect.Failed";
	public static final String CONNECT_FAILED_LEVEL = "Error";
	public static final String CONNECT_FAILED_DESCRIPTION = "description";
	
	// INVALID APP
	public static final String INVALID_APP_CODE = "NetConnection.Connect.Failed";
	public static final String INVALID_APP_LEVEL = "Error";
	public static final String INVALID_APP_DESCRIPTION = "description";
	
	// REJECTED
	public static final String REJECTED_CODE = "NetConnection.Connect.Rejected";
	public static final String REJECTED_LEVEL = "Error";
	public static final String REJECTED_DESCRIPTION = "description";
	
	// SUCCESS
	public static final String SUCCESS_CODE = "NetConnection.Connect.Success";
	public static final String SUCCESS_LEVEL = "Status";
	public static final String SUCCESS_DESCRIPTION = "description";
	
	

}
