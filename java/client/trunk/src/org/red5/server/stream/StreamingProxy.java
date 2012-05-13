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

package org.red5.server.stream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.client.net.rtmp.RTMPClient;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.IPushableConsumer;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A proxy to publish stream from server to server.
 *
 * TODO: Use timer to monitor the connect/stream creation.
 *
 * @author Steven Gong (steven.gong@gmail.com)
 * @author Andy Shaules (bowljoman@hotmail.com)
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class StreamingProxy implements IPushableConsumer, IPipeConnectionListener, INetStreamEventHandler, IPendingServiceCallback {

	private static Logger log = LoggerFactory.getLogger(StreamingProxy.class);

	private LinkedList<IMessage> frameBuffer = new LinkedList<IMessage>();

	private String host;

	private int port;

	private String app;

	private RTMPClient rtmpClient;

	private StreamState state;

	private String publishName;

	private int streamId;

	private String publishMode;

	private final Semaphore lock = new Semaphore(1, true);
	
	public void init() {
		rtmpClient = new RTMPClient();
		setState(StreamState.STOPPED);
	}

	public void start(String publishName, String publishMode, Object[] params) {
		setState(StreamState.CONNECTING);
		this.publishName = publishName;
		this.publishMode = publishMode;

		Map<String, Object> defParams = rtmpClient.makeDefaultConnectionParams(host, port, app);
		rtmpClient.connect(host, port, defParams, this, params);
	}

	public void stop() {
		if (state != StreamState.STOPPED) {
			rtmpClient.disconnect();
		}
		setState(StreamState.STOPPED);
		frameBuffer.clear();
	}

	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		// nothing to do
	}

	public void pushMessage(IPipe pipe, IMessage message) throws IOException {
		if (state == StreamState.PUBLISHED && message instanceof RTMPMessage) {
			RTMPMessage rtmpMsg = (RTMPMessage) message;
			rtmpClient.publishStreamData(streamId, rtmpMsg);
		} else {
			frameBuffer.add(message);
		}
	}

	public void onOOBControlMessage(IMessageComponent source, IPipe pipe, OOBControlMessage oobCtrlMsg) {
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public void onStreamEvent(Notify notify) {
		log.debug("onStreamEvent: {}", notify);
		ObjectMap<?, ?> map = (ObjectMap<?, ?>) notify.getCall().getArguments()[0];
		String code = (String) map.get("code");
		log.debug("<:{}", code);
		if (StatusCodes.NS_PUBLISH_START.equals(code)) {
			setState(StreamState.PUBLISHED);
			rtmpClient.invoke("FCPublish", new Object[] { publishName }, this);
			IMessage message = null;
			while (!frameBuffer.isEmpty()) {
				message = frameBuffer.removeFirst();
				rtmpClient.publishStreamData(streamId, message);
			}
		} else if (StatusCodes.NS_UNPUBLISHED_SUCCESS.equals(code)) {
			setState(StreamState.UNPUBLISHED);
		}
	}

	public void resultReceived(IPendingServiceCall call) {
		log.debug("resultReceived:> {}", call.getServiceMethodName());
		if ("connect".equals(call.getServiceMethodName())) {
			setState(StreamState.STREAM_CREATING);
			rtmpClient.createStream(this);
		} else if ("createStream".equals(call.getServiceMethodName())) {
			setState(StreamState.PUBLISHING);
			Object result = call.getResult();
			if (result instanceof Integer) {
				Integer streamIdInt = (Integer) result;
				streamId = streamIdInt.intValue();
				log.debug("Publishing: {}", state);
				rtmpClient.publish(streamIdInt.intValue(), publishName, publishMode, this);
			} else {
				rtmpClient.disconnect();
				setState(StreamState.STOPPED);
			}
		}
	}
	
	protected void setState(StreamState state) {
		try {
			lock.acquire();
			this.state = state;
		} catch (InterruptedException e) {
			log.warn("Exception setting state", e);
		} finally {
			lock.release();
		}
	}

	protected StreamState getState() {
		return state;
	}
	
}
