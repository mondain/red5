package org.red5.server.stream.filter;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.messaging.IFilter;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;

public class StreamBandwidthController implements IFilter,
		IPipeConnectionListener, Runnable {
	private static final Log log = LogFactory
			.getLog(StreamBandwidthController.class);

	public static final String KEY = StreamBandwidthController.class.getName();

	private IPipe providerPipe;

	private IPipe consumerPipe;

	private Thread puller;

	private boolean isStarted;

	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
		case PipeConnectionEvent.PROVIDER_CONNECT_PULL:
			if (event.getProvider() != this && providerPipe == null) {
				providerPipe = (IPipe) event.getSource();
			}
			break;
		case PipeConnectionEvent.PROVIDER_DISCONNECT:
			if (event.getSource() == providerPipe) {
				providerPipe = null;
			}
			break;
		case PipeConnectionEvent.CONSUMER_CONNECT_PUSH:
			if (event.getConsumer() != this && consumerPipe == null) {
				consumerPipe = (IPipe) event.getSource();
			}
			break;
		case PipeConnectionEvent.CONSUMER_DISCONNECT:
			if (event.getSource() == consumerPipe) {
				consumerPipe = null;
			}
			break;
		default:
			break;
		}
	}

	public void onOOBControlMessage(IMessageComponent source, IPipe pipe,
			OOBControlMessage oobCtrlMsg) {
	}

	public void run() {
		while (isStarted && providerPipe != null && consumerPipe != null) {
			try {
				IMessage message = providerPipe.pullMessage();
				log.debug("got message: " + message);
				consumerPipe.pushMessage(message);
			} catch (Exception e) {
				break;
			}
		}
		isStarted = false;
	}

	public void start() {
		startThread();
	}

	public void close() {
		isStarted = false;
	}

	synchronized private void startThread() {
		if (!isStarted && providerPipe != null && consumerPipe != null) {
			puller = new Thread(this);
			puller.setDaemon(true);
			isStarted = true;
			puller.start();
		}
	}
}
