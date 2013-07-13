/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright 2006-2012 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.client.net.rtmp;

import org.red5.server.net.rtmp.RTMPConnManager;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.RTMPMinaConnection;
import org.red5.server.net.rtmpt.RTMPTConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTMPClientConnManager extends RTMPConnManager {

	private static final Logger log = LoggerFactory.getLogger(RTMPClientConnManager.class);

//	private static final RTMPClientConnManager instance = new RTMPClientConnManager();
//
//	private ConcurrentMap<String, RTMPConnection> connMap = new ConcurrentHashMap<String, RTMPConnection>();
//
//	private AtomicInteger conns = new AtomicInteger();

	private RTMPClientConnManager() {
	}

	public static RTMPClientConnManager getInstance() {
		if (instance == null) {
			instance = new RTMPClientConnManager();
		}
		return (RTMPClientConnManager) instance;
	}

	/**
	 * Creates a connection of the type specified.
	 * 
	 * @param connCls
	 */
	@Override
	public RTMPConnection createConnection(Class<?> connCls) {
		RTMPConnection conn = null;
		if (RTMPConnection.class.isAssignableFrom(connCls)) {
			try {
				// create connection
				conn = createConnectionInstance(connCls);
				// add to local map
				connMap.put(conn.getSessionId(), conn);
				log.trace("Connections: {}", conns.incrementAndGet());
				log.trace("Connection created: {}", conn);
			} catch (Exception ex) {
				log.warn("Exception creating connection", ex);
			}
		}
		return conn;
	}

	/**
	 * Creates a connection instance based on the supplied type.
	 * 
	 * @param cls
	 * @return connection
	 * @throws Exception
	 */
	@Override
	public RTMPConnection createConnectionInstance(Class<?> cls) throws Exception {
		RTMPConnection conn = null;
		if (cls == RTMPMinaConnection.class) {
			conn = (RTMPMinaConnection) cls.newInstance();
		} else if (cls == RTMPTConnection.class) {
			conn = (RTMPTConnection) cls.newInstance();
		} else {
			conn = (RTMPConnection) cls.newInstance();
		}
		conn.setMaxHandshakeTimeout(7000);
		conn.setMaxInactivity(60000);
		conn.setPingInterval(0);
		return conn;
	}
//	
//	/**
//	 * Returns a connection for a given session id.
//	 * 
//	 * @param sessionId
//	 * @return connection if found and null otherwise
//	 */
//	@Override
//	public RTMPConnection getConnectionBySessionId(String sessionId) {
//		log.debug("Getting connection by session id: {}", sessionId);
//		if (connMap.containsKey(sessionId)) {
//			return connMap.get(sessionId);
//		}
//		return null;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public RTMPConnection removeConnection(String sessionId) {
//		log.debug("Removing connection with session id: {}", sessionId);
//		if (log.isTraceEnabled()) {
//			log.trace("Connections ({}) at pre-remove: {}", connMap.size(), connMap.values());
//		}
//		// remove from map
//		RTMPConnection conn = connMap.remove(sessionId);
//		if (conn != null) {
//			log.trace("Connections: {}", conns.decrementAndGet());	
//		}
//		return conn;
//	}		

}
