package org.red5.server.stream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.api.stream.ResourceExistException;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.messaging.IFilter;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IMessageOutput;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.IProvider;
import org.red5.server.messaging.IPushableConsumer;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.status.Status;
import org.red5.server.stream.consumer.FileConsumer;
import org.red5.server.stream.message.RTMPMessage;
import org.red5.server.stream.message.StatusMessage;
import org.red5.server.stream.pipe.RefCountPushPushPipe;
import org.springframework.core.io.Resource;

public class ClientBroadcastStream extends AbstractClientStream implements
		IClientBroadcastStream, IFilter, IPushableConsumer,
		IPipeConnectionListener, IEventDispatcher {
	
	private static final Log log = LogFactory.getLog(ClientBroadcastStream.class);
	private String publishedName;
	
	private IMessageOutput connMsgOut;
	private IPipe livePipe;
	private IPipe recordPipe;
	
	private long startTime;

	public void start() {
		IConsumerService consumerManager =
			(IConsumerService) getScope().getContext().getBean(IConsumerService.KEY);
		connMsgOut = consumerManager.getConsumerOutput(this);
		recordPipe = new RefCountPushPushPipe();
		Map<Object, Object> recordParamMap = new HashMap<Object, Object>();
		recordParamMap.put("record", null);
		recordPipe.subscribe((IProvider) this, recordParamMap);
		startTime = System.currentTimeMillis();
		sendStartNotify();
	}

	public void close() {
		if (livePipe != null) {
			livePipe.unsubscribe((IProvider) this);
		}
		recordPipe.unsubscribe((IProvider) this);
	}

	public void saveAs(String name, boolean isAppend)
			throws ResourceNotFoundException, ResourceExistException {
		try {
			IScope scope = getConnection().getScope();
			Resource res = scope.getResource(getStreamFilename(name, ".flv"));
			if (!isAppend && res.exists()) 
				res.getFile().delete();
			
			if (!res.exists()) {
				// Make sure the destination directory exists
				try {
					String path = res.getFile().getAbsolutePath();
					int slashPos = path.lastIndexOf(File.separator);
					if (slashPos != -1)
						path = path.substring(0, slashPos);
					File tmp = new File(path);
					tmp.mkdirs();
				} catch (IOException err) {
					log.error("Could not create destination directory.", err);
				}
				res = scope.getResource(getStreamDirectory()).createRelative(name + ".flv");
			}
			
			if (!res.exists())
				res.getFile().createNewFile();
			FileConsumer fc = new FileConsumer(scope, res.getFile());
			Map<Object, Object> paramMap = new HashMap<Object, Object>();
			if (isAppend) {
				paramMap.put("mode", "append");
			} else {
				paramMap.put("mode", "record");
			}
			recordPipe.subscribe(fc, paramMap);
		} catch (IOException e) {}
	}

	public IProvider getProvider() {
		return this;
	}

	public String getPublishedName() {
		return publishedName;
	}

	public void setPublishedName(String name) {
		this.publishedName = name;
	}

	public void pushMessage(IPipe pipe, IMessage message) {
	}

	public void onOOBControlMessage(IMessageComponent source, IPipe pipe, OOBControlMessage oobCtrlMsg) {
	}

	public void dispatchEvent(IEvent event) {
		if (!(event instanceof IRTMPEvent) && (event.getType() != IEvent.Type.STREAM_CONTROL) && (event.getType() != IEvent.Type.STREAM_DATA))
			return;
		
		long runTime = System.currentTimeMillis() - startTime;
		IRTMPEvent rtmpEvent = (IRTMPEvent) event;
		rtmpEvent.setTimestamp((int) runTime);
		RTMPMessage msg = new RTMPMessage();
		msg.setBody(rtmpEvent);
		if (livePipe != null) {
			// XXX probable race condition here
			livePipe.pushMessage(msg);
		}
		recordPipe.pushMessage(msg);
	}

	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
		case PipeConnectionEvent.PROVIDER_CONNECT_PUSH:
			if (event.getProvider() == this &&
					(event.getParamMap() == null || !event.getParamMap().containsKey("record"))) {
				this.livePipe = (IPipe) event.getSource();
			}
			break;
		case PipeConnectionEvent.PROVIDER_DISCONNECT:
			if (this.livePipe == event.getSource()) {
				sendStopNotify();
				this.livePipe = null;
			}
			break;
		default:
			break;
		}
	}
	
	private void sendStartNotify() {
		Status start = new Status(Status.NS_PUBLISH_START);
		start.setClientid(getStreamId());
		start.setDetails(getName());
		
		StatusMessage startMsg = new StatusMessage();
		startMsg.setBody(start);
		connMsgOut.pushMessage(startMsg);
	}
	
	private void sendStopNotify() {
		Status stop = new Status(Status.NS_UNPUBLISHED_SUCCESS);
		stop.setClientid(getStreamId());
		stop.setDetails(getName());
		
		StatusMessage stopMsg = new StatusMessage();
		stopMsg.setBody(stop);
		connMsgOut.pushMessage(stopMsg);
	}
	
	private String getStreamDirectory() {
		return "streams/";
	}
	
	private String getStreamFilename(String name, String extension) {
		String result = getStreamDirectory() + name;
		if (extension != null && !extension.equals(""))
			result += extension;
		return result;
	}
}
