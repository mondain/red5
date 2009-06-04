package org.red5.server.net.rtmp;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 *
 * Copyright (c) 2006-2009 by respective authors (see below). All rights reserved.
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
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;

import javax.management.ObjectName;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.red5.server.api.IScope;
import org.red5.server.jmx.JMXAgent;
import org.red5.server.jmx.JMXFactory;
import org.red5.server.net.rtmp.message.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTMPMinaConnection extends RTMPConnection implements
		RTMPMinaConnectionMBean {
	/**
	 * Logger
	 */
	protected static Logger log = LoggerFactory.getLogger(RTMPMinaConnection.class);

	/**
	 * MINA I/O session, connection between two endpoints
	 */
	private volatile IoSession ioSession;
	
	/**
	 * MBean object name used for de/registration purposes.
	 */
	private volatile ObjectName oName;

	{
		log.info("RTMPMinaConnection created");
	}
	
	/** Constructs a new RTMPMinaConnection. */
	public RTMPMinaConnection() {
		super(PERSISTENT);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		super.close();
		if (ioSession != null) {
			ioSession.close(true);
		}
		// Deregister with JMX
		try {
		    JMXAgent.unregisterMBean(oName);
		} catch (Exception e) {
		    //sometimes the client isnt registered in jmx
		}
	}

	@Override
	public boolean connect(IScope newScope, Object[] params) {
		boolean success = super.connect(newScope, params);
		if (!success) {
			return false;
		}
		String hostStr = host;
		int port = 1935;
		if (host != null && host.indexOf(":") > -1) {
			String[] arr = host.split(":");
			hostStr = arr[0];
			port = Integer.parseInt(arr[1]);
		}
		try {
			//if the client is null for some reason, skip the jmx registration
			if (client != null) {
				String cName = this.getClass().getName();
				if (cName.indexOf('.') != -1) {
					cName = cName.substring(cName.lastIndexOf('.')).replaceFirst(
							"[\\.]", "");
				}				
			    // Create a new mbean for this instance
			    oName = JMXFactory.createObjectName("type", cName,
					"connectionType", type, "host", hostStr, "port", port + "",
					"clientId", client.getId());
			    JMXAgent.registerMBean(this, this.getClass().getName(),	RTMPMinaConnectionMBean.class, oName);		
			} else {
                log.warn("Client was null");			
			}
		} catch (Exception e) {
			log.warn("Exception registering mbean", e);
		}
		return success;
	}

	/**
	 * Return MINA I/O session.
	 *
	 * @return MINA O/I session, connection between two endpoints
	 */
	public IoSession getIoSession() {
		return ioSession;
	}

	/** {@inheritDoc} */
	@Override
	public long getPendingMessages() {
		if (ioSession == null) {
			return 0;
		}
		return ioSession.getScheduledWriteMessages();
	}

	/** {@inheritDoc} */
	@Override
	public long getReadBytes() {
		if (ioSession == null) {
			return 0;
		}
		// TODO: half-measure, writing to readBytes isn't synchronised in mina 
		synchronized (ioSession) {
			return ioSession.getReadBytes();
		}
	}

	/** {@inheritDoc} */
	@Override
	public long getWrittenBytes() {
		if (ioSession == null) {
			return 0;
		}
		// TODO: half-measure, writing to writtenBytes isn't synchronised in mina 
		synchronized (ioSession) {
			return ioSession.getWrittenBytes();
		}
	}

	public void invokeMethod(String method) {
		invoke(method);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConnected() {
		return super.isConnected() && (ioSession != null) && ioSession.isConnected();
	}

	/** {@inheritDoc} */
	@Override
	protected void onInactive() {
		this.close();
	}

	/** {@inheritDoc} */
	@Override
	public void rawWrite(IoBuffer out) {
		if (ioSession != null) {
			ioSession.write(out);
		}
	}

	/**
	 * Setter for MINA I/O session (connection).
	 *
	 * @param protocolSession  Protocol session
	 */
	public void setIoSession(IoSession protocolSession) {
		SocketAddress remote = protocolSession.getRemoteAddress();
		if (remote instanceof InetSocketAddress) {
			remoteAddress = ((InetSocketAddress) remote).getAddress()
					.getHostAddress();
			remotePort = ((InetSocketAddress) remote).getPort();
		} else {
			remoteAddress = remote.toString();
			remotePort = -1;
		}
		remoteAddresses = new ArrayList<String>();
		remoteAddresses.add(remoteAddress);
		remoteAddresses = Collections.unmodifiableList(remoteAddresses);
		this.ioSession = protocolSession;
	}

	/** {@inheritDoc} */
	@Override
	public void write(Packet out) {
		if (ioSession != null) {
			writingMessage(out);
			ioSession.write(out);
		}
	}
}
