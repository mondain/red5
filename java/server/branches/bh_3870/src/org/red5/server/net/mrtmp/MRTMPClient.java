package org.red5.server.net.mrtmp;

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

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * @author Steven Gong (steven.gong@gmail.com)
 */
public class MRTMPClient implements Runnable {
	private IoHandler ioHandler;
	private IoHandler ioHandlerWrapper;
	private String server;
	private int port;
	private Thread connectThread;
	private boolean needReconnect;
	
	public String getServer() {
		return server;
	}
	public void setServer(String address) {
		this.server = address;
	}
	public IoHandler getIoHandler() {
		return ioHandler;
	}
	public void setIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public void start() {
		needReconnect = true;
		ioHandlerWrapper = new IoHandlerWrapper(ioHandler);
		connectThread = new Thread(this, "MRTMPClient");
		connectThread.setDaemon(true);
		connectThread.start();
	}
	
	public void run() {
		while (true) {
			synchronized (ioHandlerWrapper) {
				if (needReconnect) {
					doConnect();
					needReconnect = false;
				}
				try {
					ioHandlerWrapper.wait();
				} catch (Exception e) {}
			}
		}
	}
	
	private void doConnect() {
		IoConnector connector = new NioSocketConnector();
		connector.setHandler(ioHandlerWrapper);
		SocketSessionConfig sessionConf = (SocketSessionConfig) connector.getSessionConfig();
		sessionConf.setTcpNoDelay(true);
		while (true) {
			ConnectFuture future = connector.connect(new InetSocketAddress(server, port));
			future.awaitUninterruptibly(500);
			if (future.isConnected()) {
				break;
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
	}
	
	private void reconnect() {
		synchronized (ioHandlerWrapper) {
			needReconnect = true;
			ioHandlerWrapper.notifyAll();
		}
	}
	
	private class IoHandlerWrapper implements IoHandler {
		private IoHandler wrapped;
		
		public IoHandlerWrapper(IoHandler wrapped) {
			this.wrapped = wrapped;
		}

		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			wrapped.exceptionCaught(session, cause);
			MRTMPClient.this.reconnect();
		}

		public void messageReceived(IoSession session, Object message) throws Exception {
			wrapped.messageReceived(session, message);
		}

		public void messageSent(IoSession session, Object message) throws Exception {
			wrapped.messageSent(session, message);
		}

		public void sessionClosed(IoSession session) throws Exception {
			wrapped.sessionClosed(session);
		}

		public void sessionCreated(IoSession session) throws Exception {
			wrapped.sessionCreated(session);
		}

		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			wrapped.sessionIdle(session, status);
		}

		public void sessionOpened(IoSession session) throws Exception {
			wrapped.sessionOpened(session);
		}
	}
}
