package org.red5.client;

import java.util.Map;

import org.red5.client.net.rtmp.ClientExceptionHandler;
import org.red5.client.net.rtmp.RTMPClient;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.Ping;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.net.rtmp.status.StatusCodes;

public class ClientTest extends RTMPClient {

	private String server = "localhost";

	private int port = 1935;

	private String application = "oflaDemo";

	private String filename = "prometheus.flv";
	//private String filename = "NAPNAP.flv";

	private static boolean finished = false;

	public static void main(String[] args) throws InterruptedException {

		final ClientTest player = new ClientTest();
		player.connect();

		synchronized (ClientTest.class) {
			if (!finished)
				ClientTest.class.wait();
		}

		System.out.println("Ended");
	}

	public void connect() {
		setExceptionHandler(new ClientExceptionHandler() {
			@Override
			public void handleException(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		setStreamEventDispatcher(streamEventDispatcher);
		connect(server, port, application, connectCallback);
	}

	private IEventDispatcher streamEventDispatcher = new IEventDispatcher() {
		@Override
		public void dispatchEvent(IEvent event) {
			System.out.println("AudioListenerClientStream.dispachEvent()" + event.toString());
		}
	};

	private IPendingServiceCallback connectCallback = new IPendingServiceCallback() {
		@Override
		public void resultReceived(IPendingServiceCall call) {
			System.out.println("connectCallback");
			createStream(createStreamCallback);
		}
	};

	private IPendingServiceCallback createStreamCallback = new IPendingServiceCallback() {
		@Override
		public void resultReceived(IPendingServiceCall call) {
			int streamId = (Integer) call.getResult();
			conn.ping(new Ping(Ping.CLIENT_BUFFER, streamId, 5000));
			play(streamId, filename, 0, -2);
		}
	};

	@SuppressWarnings("unchecked")
	protected void onInvoke(RTMPConnection conn, Channel channel, Header header, Notify notify, RTMP rtmp) {
		super.onInvoke(conn, channel, header, notify, rtmp);

		System.out.println("onInvoke, header = " + header.toString());
		System.out.println("onInvoke, notify = " + notify.toString());
		System.out.println("onInvoke, rtmp = " + rtmp.toString());

		Object obj = notify.getCall().getArguments()[0];
		if (obj instanceof Map) {
			Map<String, String> map = (Map<String, String>) notify.getCall().getArguments()[0];
			String code = map.get("code");
			if (StatusCodes.NS_PLAY_STOP.equals(code)) {

				synchronized (ClientTest.class) {
					finished = true;
					ClientTest.class.notifyAll();
				}

				disconnect();
				System.out.println("Disconnected");
			}
		}

	};

}
