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

package org.red5.client;

import java.io.IOException;
import java.util.Map;

import org.red5.client.net.rtmp.ClientExceptionHandler;
import org.red5.client.net.rtmp.RTMPClient;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Ping;
import org.red5.server.stream.StreamingProxy;
import org.red5.server.stream.message.RTMPMessage;

/**
 * Relay a stream from one location to another via RTMP.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class StreamRelay {

	// our consumer
	private static RTMPClient client;

	// our publisher
	private static StreamingProxy proxy;

	/**
	 * Creates a stream client to consume a stream from an end point and a proxy to relay the stream
	 * to another end point.
	 * 
	 * @param args application arguments
	 */
	public static void main(String... args) {
		// handle the args
		if (args == null || args.length < 6) {
			System.out
					.println("Not enough args supplied. Usage: <source uri> <source app> <source stream name> <destination uri> <destination app> <destination stream name> <publish mode>");
		} else {
			// parse the args
			String sourceHost = "localhost", destHost = "localhost";
			String sourceApp = "live", destApp = "live";
			int sourcePort = 1935, destPort = 1935;
			String sourceStreamName = "stream", destStreamName = "stream_clone";
			String publishMode = "live"; //live, record, or append

			// create the consumer
			client = new RTMPClient();
			client.setStreamEventDispatcher(new StreamEventDispatcher());
			client.setConnectionClosedHandler(new Runnable() {
				public void run() {
					System.out.println("Source connection has been closed");
				}
			});
			client.setExceptionHandler(new ClientExceptionHandler() {
				@Override
				public void handleException(Throwable throwable) {
					throwable.printStackTrace();
				}
			});

			// create our publisher
			proxy = new StreamingProxy();
			proxy.setHost(destHost);
			proxy.setPort(destPort);
			proxy.setApp(destApp);
			proxy.init();
			proxy.start(destStreamName, publishMode, null);
			// wait for the publish state
			do {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (!proxy.isPublished());
			System.out.println("Publishing...");

			// connect the consumer
			final CreateStreamCallback createStreamCallback = new CreateStreamCallback(sourceStreamName);
			Map<String, Object> defParams = client.makeDefaultConnectionParams(sourceHost, sourcePort, sourceApp);
			client.connect(sourceHost, sourcePort, defParams, new IPendingServiceCallback() {
				public void resultReceived(IPendingServiceCall call) {
					System.out.println("connectCallback");
					client.createStream(createStreamCallback);
				}
			});
		}

	}

	/**
	 * Dispatches consumer events.
	 */
	private static final class StreamEventDispatcher implements IEventDispatcher {

		public void dispatchEvent(IEvent event) {
			System.out.println("ClientStream.dispachEvent()" + event.toString());
			try {
				proxy.pushMessage(null, RTMPMessage.build((IRTMPEvent) event));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Creates a "stream" via playback, this is the source stream.
	 */
	private static final class CreateStreamCallback implements IPendingServiceCallback {

		private String streamName;

		public CreateStreamCallback(String streamName) {
			System.out.println("createStreamCallback: " + streamName);
			this.streamName = streamName;
		}

		public void resultReceived(IPendingServiceCall call) {
			System.out.println("resultReceived: " + call);
			int streamId = (Integer) call.getResult();
			System.out.println("stream id: " + streamId);
			client.ping(Ping.CLIENT_BUFFER, streamId, 5000);
			client.play(streamId, streamName, 0, -2);
		}
		
	}

}
