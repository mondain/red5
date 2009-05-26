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
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.red5.server.jmx.JMXAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steven Gong (steven.gong@gmail.com)
 */
public class MRTMPMinaTransport {

	private static final int DEFAULT_EVENT_THREADS_CORE = 16;

	private static final int DEFAULT_EVENT_THREADS_KEEPALIVE = 60;

	private static final int DEFAULT_EVENT_THREADS_MAX = 32;

	private static final int DEFAULT_EVENT_THREADS_QUEUE = -1;

	private static final int DEFAULT_IO_THREADS = Runtime.getRuntime()
			.availableProcessors();

	private static final int DEFAULT_PORT = 1935;

	private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 256 * 1024;

	private static final int DEFAULT_SEND_BUFFER_SIZE = 64 * 1024;

	private static final boolean DEFAULT_TCP_NO_DELAY = false;

	private static final boolean DEFAULT_USE_HEAP_BUFFERS = true;

	private static final Logger log = LoggerFactory.getLogger(MRTMPMinaTransport.class);

	private SocketAcceptor acceptor;

	private String address = null;

	private int eventThreadsCore = DEFAULT_EVENT_THREADS_CORE;

	private int eventThreadsKeepalive = DEFAULT_EVENT_THREADS_KEEPALIVE;

	private int eventThreadsMax = DEFAULT_EVENT_THREADS_MAX;

	private int eventThreadsQueue = DEFAULT_EVENT_THREADS_QUEUE;

	private IoHandlerAdapter ioHandler;

	private int ioThreads = DEFAULT_IO_THREADS;

	private boolean isLoggingTraffic = false;

	/**
	 * MBean object name used for de/registration purposes.
	 */
	private ObjectName oName;

	private int port = DEFAULT_PORT;

	private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;

	private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;

	private boolean tcpNoDelay = DEFAULT_TCP_NO_DELAY;

	private boolean useHeapBuffers = DEFAULT_USE_HEAP_BUFFERS;

	private void initIOHandler() {
		if (ioHandler == null) {
			log.info("No rtmp IO Handler associated - using defaults");
			ioHandler = new OriginMRTMPHandler();
		}
	}

	public void setAddress(String address) {
		if ("*".equals(address) || "0.0.0.0".equals(address)) {
			address = null;
		}
		this.address = address;
	}

	public void setEventThreadsCore(int eventThreadsCore) {
		this.eventThreadsCore = eventThreadsCore;
	}

	public void setEventThreadsKeepalive(int eventThreadsKeepalive) {
		this.eventThreadsKeepalive = eventThreadsKeepalive;
	}

	public void setEventThreadsMax(int eventThreadsMax) {
		this.eventThreadsMax = eventThreadsMax;
	}

	public void setEventThreadsQueue(int eventThreadsQueue) {
		this.eventThreadsQueue = eventThreadsQueue;
	}

	public void setIoHandler(IoHandlerAdapter rtmpIOHandler) {
		this.ioHandler = rtmpIOHandler;
	}

	public void setIoThreads(int ioThreads) {
		this.ioThreads = ioThreads;
	}

	public void setIsLoggingTraffic(boolean isLoggingTraffic) {
		this.isLoggingTraffic = isLoggingTraffic;
	}

	public void setPort(int port) {
		this.port = port;
		JMXAgent.updateMBeanAttribute(oName, "port", port);
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public void setUseHeapBuffers(boolean useHeapBuffers) {
		this.useHeapBuffers = useHeapBuffers;
	}

	public void start() throws Exception {
		initIOHandler();

		IoBuffer.setUseDirectBuffer(!useHeapBuffers); // this is global, oh well.
		if (useHeapBuffers) {
			IoBuffer.setAllocator(new SimpleBufferAllocator()); // dont pool for heap buffers.
		}
		log.info("MRTMP Mina Transport Settings");
		log.info("IO Threads: {}", ioThreads);
		log.info("Event Threads:" + " core: " + eventThreadsCore + "+1"
				+ " max: " + eventThreadsMax + "+1" + " queue: "
				+ eventThreadsQueue + " keepalive: " + eventThreadsKeepalive);

		// Executors.newCachedThreadPool() is always preferred by IoService
		// See http://mina.apache.org/configuring-thread-model.html for details
		acceptor = new NioSocketAcceptor(ioThreads);
		acceptor.setHandler(ioHandler);
		acceptor.setBacklog(100);

		log.info("TCP No Delay: {}", tcpNoDelay);
		log.info("Receive Buffer Size: {}", receiveBufferSize);
		log.info("Send Buffer Size: {}", sendBufferSize);

		SocketSessionConfig sessionConf = (SocketSessionConfig) acceptor.getSessionConfig();
		sessionConf.setReuseAddress(true);
		sessionConf.setTcpNoDelay(tcpNoDelay);
		// XXX ignore the config of buffer settings
//		sessionConf.setReceiveBufferSize(receiveBufferSize);
//		sessionConf.setSendBufferSize(sendBufferSize);

		if (isLoggingTraffic) {
			log.info("Configuring traffic logging filter");
			IoFilter filter = new LoggingFilter();
			acceptor.getFilterChain().addFirst("LoggingFilter", filter);
		}
				
		Set<SocketAddress> addresses = new HashSet<SocketAddress>();
		
		SocketAddress socketAddress = (address == null) 
				? new InetSocketAddress(port)
				: new InetSocketAddress(address, port);

		addresses.add(socketAddress);	
				
		acceptor.bind(addresses);
		
		log.info("MRTMP Mina Transport bound to {}", socketAddress.toString());
	}

	public void stop() {
		log.info("MRTMP Mina Transport unbind");
		acceptor.unbind();
	}

	public String toString() {
		return "MRTMP Mina Transport [port=" + port + "]";
	}
}
