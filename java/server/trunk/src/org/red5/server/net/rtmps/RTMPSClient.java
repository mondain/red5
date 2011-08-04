package org.red5.server.net.rtmps;

/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright (c) 2006-2011 by respective authors (see below). All rights reserved.
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
 */

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.red5.server.net.rtmp.RTMPClient;
import org.red5.server.net.rtmp.RTMPClientConnManager;
import org.red5.server.net.rtmp.codec.RTMP;

/**
 * RTMPS client object
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Paul Gregoire (mondain@gmail.com)
 * @author Kevin Green (kevygreen@gmail.com)
 */
public class RTMPSClient extends RTMPClient {

	// I/O handler
	private final RTMPSMinaIoHandler ioHandler;
	
	/** Constructs a new RTMPClient. */
    public RTMPSClient() {
		ioHandler = new RTMPSMinaIoHandler();
		ioHandler.setCodecFactory(getCodecFactory());
		ioHandler.setMode(RTMP.MODE_CLIENT);
		ioHandler.setHandler(this);
		ioHandler.setRtmpConnManager(RTMPClientConnManager.getInstance());
	}

	public Map<String, Object> makeDefaultConnectionParams(String server, int port, String application) {
		Map<String, Object> params = super.makeDefaultConnectionParams(server, port, application);
		if (!params.containsKey("tcUrl")) {
			params.put("tcUrl", String.format("rtmps://%s:%s/%s", server, port, application));
		}
		return params;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected void startConnector(String server, int port) {
		socketConnector = new NioSocketConnector();
		socketConnector.setHandler(ioHandler);
		future = socketConnector.connect(new InetSocketAddress(server, port));
		future.addListener(
				new IoFutureListener() {
					public void operationComplete(IoFuture future) {
						try {
							// will throw RuntimeException after connection error
							future.getSession(); 
						} catch (Throwable e) {
							//if there isn't an ClientExceptionHandler set, a 
							//RuntimeException may be thrown in handleException
							handleException(e);
						}
					}
				}
		);
	    // Do the close requesting that the pending messages are sent before
	    // the session is closed
		future.getSession().close(false);
	    // Now wait for the close to be completed
		future.awaitUninterruptibly(CONNECTOR_WORKER_TIMEOUT);
	    // We can now dispose the connector
		socketConnector.dispose();
	}
	
}
