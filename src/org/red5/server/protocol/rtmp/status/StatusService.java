package org.red5.server.protocol.rtmp.status;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.Server;
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Output;

public class StatusService {
	
	protected static Log log =
        LogFactory.getLog(StatusService.class.getName());
	
	protected Serializer serializer;
	protected ConnectionStatus connectionStatus;
	protected ApplicationStatus applicationStatus;
	//TODO other status
	
	// ConnectionStatus
	private ByteBuffer success;
	private ByteBuffer callFailed;
	private ByteBuffer appShutDown;
	private ByteBuffer badVersion;
	private ByteBuffer closed;
	private ByteBuffer connectFailed;
	private ByteBuffer invalidApp;
	private ByteBuffer rejected;
	
	protected ByteBuffer out;
	
	public StatusService() {
		//TODO nada
		//cache();
		// NOTE: No need to set via injection
		connectionStatus = new ConnectionStatus();
		applicationStatus = new ApplicationStatus();
	}
	
	public void cache() {
		// Cache ConnectionStatus
		cacheConnectionStatus();
		
		//TODO cache ApplicationStatus
		cacheApplicationStatus();
	}

	private void cacheApplicationStatus() {
		// TODO Auto-generated method stub
		
	}

	private void cacheConnectionStatus() {
		// TODO Auto-generated method stub
		log.info("test end");
		Output output;
		success = ByteBuffer.allocate(256);
		success.setAutoExpand(true);
		output = new Output(success);
		log.info("success: " + success);
		log.info("output: " + output);
		log.info("serializer: " + serializer);
		serializer.serialize(output, connectionStatus.getStatus("SUCCESS"));
		success.flip();
		success.acquire(); // dont want it reused, need to add to others
		
		callFailed = ByteBuffer.allocate(256);
		callFailed.setAutoExpand(true);
		output = new Output(callFailed);
		serializer.serialize(output, connectionStatus.getStatus("CALL_FAILED"));
		callFailed.flip();
		
		appShutDown = ByteBuffer.allocate(256);
		appShutDown.setAutoExpand(true);
		output = new Output(appShutDown);
		serializer.serialize(output, connectionStatus.getStatus("APP_SHUT_DOWN"));
		appShutDown.flip();
		
		badVersion = ByteBuffer.allocate(256);
		badVersion.setAutoExpand(true);
		output = new Output(badVersion);
		serializer.serialize(output, connectionStatus.getStatus("BAD_VERSION"));
		badVersion.flip();
		
		closed = ByteBuffer.allocate(256);
		closed.setAutoExpand(true);
		output = new Output(closed);
		serializer.serialize(output, connectionStatus.getStatus("CLOSED"));
		closed.flip();
		
		connectFailed = ByteBuffer.allocate(256);
		connectFailed.setAutoExpand(true);
		output = new Output(connectFailed);
		serializer.serialize(output, connectionStatus.getStatus("CONNECT_FAILED"));
		closed.flip();
		
		invalidApp = ByteBuffer.allocate(256);
		invalidApp.setAutoExpand(true);
		output = new Output(invalidApp);
		serializer.serialize(output, connectionStatus.getStatus("INVALID_APP"));
		
		rejected = ByteBuffer.allocate(256);
		rejected.setAutoExpand(true);
		output = new Output(rejected);
		serializer.serialize(output, connectionStatus.getStatus("REJECTED"));
		rejected.flip();
		
	}

	public void setApplicationStatus(ApplicationStatus applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	public void setConnectionStatus(ConnectionStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
	
	public ByteBuffer getStatus(String status) {
		String s = status;
		if(s.equals("SUCCESS")) {
			return success;
		} else if(s.equals("CALL_FAILED")){
			return callFailed;
		} else if(s.equals("APP_SHUT_DOWN")){
			return appShutDown;
		} else if(s.equals("BAD_VERSION")){
			return badVersion;
		} else if(s.equals("CLOSED")){
			return closed;
		} else if(s.equals("CONNECT_FAILED")){
			return connectFailed;
		} else if(s.equals("INVALID_APP")){
			return invalidApp;
		} else if(s.equals("REJECTED")){
			return rejected;
		}
		
		return null;
		
	}
}
