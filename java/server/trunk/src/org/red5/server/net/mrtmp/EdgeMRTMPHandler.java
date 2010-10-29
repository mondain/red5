package org.red5.server.net.mrtmp;

/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright (c) 2006-2010 by respective authors (see below). All rights reserved.
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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.red5.server.net.mrtmp.MRTMPPacket.RTMPBody;
import org.red5.server.net.mrtmp.MRTMPPacket.RTMPHeader;
import org.red5.server.net.rtmp.IRTMPConnManager;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Invoke;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.service.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steven Gong (steven.gong@gmail.com)
 */
public class EdgeMRTMPHandler extends IoHandlerAdapter implements Constants {

	private static final Logger log = LoggerFactory.getLogger(EdgeMRTMPHandler.class);

	private IRTMPConnManager rtmpConnManager;
	private IMRTMPEdgeManager mrtmpManager;
	private ProtocolCodecFactory codecFactory;
	
	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	public void setMrtmpManager(IMRTMPEdgeManager mrtmpManager) {
		this.mrtmpManager = mrtmpManager;
	}

	public void setRtmpConnManager(IRTMPConnManager rtmpConnManager) {
		this.rtmpConnManager = rtmpConnManager;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MRTMPPacket mrtmpPacket = (MRTMPPacket) message;
		int clientId = mrtmpPacket.getHeader().getClientId();
		RTMPConnection conn = rtmpConnManager.getConnection(clientId);
		if (conn == null) {
			log.debug("Client " + clientId + " is already closed.");
			return;
		}
		RTMP rtmpState = conn.getState();
		switch (mrtmpPacket.getHeader().getType()) {
			case MRTMPPacket.CLOSE:
				conn.setStateCode(RTMP.STATE_EDGE_DISCONNECTING);
				conn.close();
				break;
			case MRTMPPacket.RTMP:
				RTMPHeader rtmpHeader = (RTMPHeader) mrtmpPacket.getHeader();
				RTMPBody rtmpBody = (RTMPBody) mrtmpPacket.getBody();
				boolean toDisconnect = false;
				conn.getWriteLock().lock();
				try {
					if (rtmpState.getState() == RTMP.STATE_ORIGIN_CONNECT_FORWARDED &&
							rtmpHeader.getRtmpType() == TYPE_INVOKE) {
						// we got the connect invocation result from Origin
						// parse the result
						Invoke invoke = (Invoke) rtmpBody.getRtmpPacket().getMessage();
						if ("connect".equals(invoke.getCall().getServiceMethodName())) {
							if (invoke.getCall().getStatus() == Call.STATUS_SUCCESS_RESULT) {
								rtmpState.setState(RTMP.STATE_CONNECTED);
							} else {
								// TODO set EdgeRTMP state to closing ?
								toDisconnect = true;
							}
						}
					}
				} finally {
					conn.getWriteLock().unlock();
				}
				log.debug("Forward packet to client: {}", rtmpBody.getRtmpPacket().getMessage());
				// send the packet back to client
				conn.write(rtmpBody.getRtmpPacket());
				if (toDisconnect) {
					conn.close();
				}
				conn.getWriteLock().lock();
				try {
					if (rtmpState.getState() == RTMP.STATE_CONNECTED) {
						conn.startRoundTripMeasurement();
					}
				} finally {
					conn.getWriteLock().unlock();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// do nothing
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		MRTMPEdgeConnection conn = (MRTMPEdgeConnection) session.getAttribute(MRTMPEdgeConnection.EDGE_CONNECTION_KEY);
		mrtmpManager.unregisterConnection(conn);
		conn.close();
		log.debug("Closed MRTMP Edge Connection " + conn);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		MRTMPEdgeConnection conn = new MRTMPEdgeConnection();
		conn.setIoSession(session);
		mrtmpManager.registerConnection(conn);
		session.setAttribute(MRTMPEdgeConnection.EDGE_CONNECTION_KEY, conn);
		session.getFilterChain().addFirst("protocolFilter",
				new ProtocolCodecFilter(this.codecFactory));
		if (log.isDebugEnabled()) {
			session.getFilterChain().addLast("logger", new LoggingFilter());
		}
		log.debug("Created MRTMP Edge Connection {}", conn);
	}
}
