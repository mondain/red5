package org.red5.server.stream;

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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.management.ObjectName;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.event.IEventListener;
import org.red5.server.api.statistics.IClientBroadcastStreamStatistics;
import org.red5.server.api.statistics.support.StatisticsCounter;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.api.stream.IStreamAwareScopeHandler;
import org.red5.server.api.stream.IStreamCapableConnection;
import org.red5.server.api.stream.IStreamCodecInfo;
import org.red5.server.api.stream.IStreamFilenameGenerator;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.api.stream.IVideoStreamCodec;
import org.red5.server.api.stream.ResourceExistException;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.api.stream.IStreamFilenameGenerator.GenerationType;
import org.red5.server.jmx.JMXAgent;
import org.red5.server.jmx.JMXFactory;
import org.red5.server.messaging.IConsumer;
import org.red5.server.messaging.IFilter;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IMessageOutput;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.IProvider;
import org.red5.server.messaging.IPushableConsumer;
import org.red5.server.messaging.InMemoryPushPushPipe;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Invoke;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.status.Status;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.stream.codec.StreamCodecInfo;
import org.red5.server.stream.consumer.FileConsumer;
import org.red5.server.stream.message.RTMPMessage;
import org.red5.server.stream.message.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents live stream broadcasted from client. As Flash Media Server, Red5 supports
 * recording mode for live streams, that is, broadcasted stream has broadcast mode. It can be either
 * "live" or "record" and latter causes server-side application to record broadcasted stream.
 *
 * Note that recorded streams are recorded as FLV files. The same is correct for audio, because
 * NellyMoser codec that Flash Player uses prohibits on-the-fly transcoding to audio formats like MP3
 * without paying of licensing fee or buying SDK.
 *
 * This type of stream uses two different pipes for live streaming and recording.
 */
public class ClientBroadcastStream extends AbstractClientStream implements
		IClientBroadcastStream, IFilter, IPushableConsumer,
		IPipeConnectionListener, IEventDispatcher,
		IClientBroadcastStreamStatistics, ClientBroadcastStreamMBean {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ClientBroadcastStream.class);

	/** Stores absolute time for audio stream. */
	private int audioTime = -1;

	/**
	 * Total number of bytes received.
	 */
	private long bytesReceived;

	/**
	 * Is there need to check video codec?
	 */
	private boolean checkVideoCodec = false;

	/**
	 * Data is sent by chunks, each of them has size
	 */
	private int chunkSize;

	/**
	 * Is this stream still active?
	 */
	private boolean closed;

	/**
	 * Output endpoint that providers use
	 */
	private IMessageOutput connMsgOut;

	/** Stores absolute time for data stream. */
	private int dataTime = -1;

	/** Stores timestamp of first packet. */
	private int firstPacketTime = -1;

	/**
	 * Pipe for live streaming
	 */
	private IPipe livePipe;

	/**
	 * MBean object name used for de/registration purposes.
	 */
	private ObjectName oName;

	/**
	 * Stream published name
	 */
	private String publishedName;

	/**
	 * Whether we are recording or not
	 */
	private volatile boolean recording;

	/**
	 * FileConsumer used to output recording to disk
	 */
	private FileConsumer recordingFile;

	/**
	 * The filename we are recording to.
	 */
	private String recordingFilename;

	/**
	 * Pipe for recording
	 */
	private IPipe recordPipe;

	/**
	 * Is there need to send start notification?
	 */
	private boolean sendStartNotification = true;

	/**
	 * Stores statistics about subscribers.
	 */
	private StatisticsCounter subscriberStats = new StatisticsCounter();

	/** Stores absolute time for video stream. */
	private int videoTime = -1;

	private int lastEventTime = -1;
	
	private int minStreamTime;
	
	/**
	 * Stores the streams metadata
	 */
	protected Notify metaData;
	
	/** Listeners to get notified about received packets. */
	private Set<IStreamListener> listeners = new CopyOnWriteArraySet<IStreamListener>();
	
	/**
	 * Sets the minimum stream time.
	 * 
	 * @param minStreamTime
	 */
	public void setMinStreamTime(int minStreamTime) { 
		this.minStreamTime = minStreamTime; 
	}
	
	/**
	 * Check and send notification if necessary
	 * @param event          Event
	 */
	private void checkSendNotifications(IEvent event) {
		IEventListener source = event.getSource();
		sendStartNotifications(source);
	}

	/**
	 * Closes stream, unsubscribes provides, sends stoppage notifications and broadcast close notification.
	 */
	public void close() {
		if (closed) {
			// Already closed
			return;
		}
		closed = true;
		if (livePipe != null) {
			livePipe.unsubscribe((IProvider) this);
		}
		if (recordPipe != null) {
			recordPipe.unsubscribe((IProvider) this);
		}
		if (recording) {
			sendRecordStopNotify();
		}
		sendPublishStopNotify();
		// TODO: can we sent the client something to make sure he stops sending data?
		connMsgOut.unsubscribe(this);
		notifyBroadcastClose();
		// deregister with jmx
		JMXAgent.unregisterMBean(oName);
	}

	/**
	 * Dispatches event
	 * @param event          Event to dispatch
	 */	
	public void dispatchEvent(IEvent event) {
		if (!(event instanceof IRTMPEvent)
				&& (event.getType() != IEvent.Type.STREAM_CONTROL)
				&& (event.getType() != IEvent.Type.STREAM_DATA) || closed) {
			// ignored event
			log.debug("dispatchEvent: {}", event.getType());
			return;
		}

		// Get stream codec
		IStreamCodecInfo codecInfo = getCodecInfo();
		StreamCodecInfo info = null;
		if (codecInfo instanceof StreamCodecInfo) {
			info = (StreamCodecInfo) codecInfo;
		}

		IRTMPEvent rtmpEvent;
		try {
			rtmpEvent = (IRTMPEvent) event;
		} catch (ClassCastException e) {
			log.error("Class cast exception in event dispatch", e);
			return;
		}
		int eventTime = -1;
		if (log.isDebugEnabled()) {
			// If this is first packet save its timestamp; expect it is
			// absolute? no matter: it's never used!
			if (firstPacketTime == -1) {
				firstPacketTime = rtmpEvent.getTimestamp();
				log.debug(String.format("CBS=@%08x: firstPacketTime=%d %s",
						System.identityHashCode(this), firstPacketTime,
						(rtmpEvent.getHeader().isTimerRelative() ? "(rel)"
								: "(abs)")));
			}

			int basetime = ((rtmpEvent instanceof VideoData) ? videoTime
					: ((rtmpEvent instanceof AudioData) ? audioTime : dataTime));
			if (rtmpEvent.getHeader().isTimerRelative()) {
				int abstime = rtmpEvent.getTimestamp() + basetime;
				log
						.debug(String
								.format(
										"CBS=@%08x: rtmpEvent=%s  timestamp=%d (relative)+%d = %d",
										System.identityHashCode(this),
										rtmpEvent.getClass().getSimpleName(),
										rtmpEvent.getTimestamp(), basetime,
										abstime));
			} else {
				int deltime = rtmpEvent.getTimestamp() - basetime;
				log
						.debug(String
								.format(
										"CBS=@%08x: rtmpEvent=%s  timestamp=%d (absolute)-%d = %d",
										System.identityHashCode(this),
										rtmpEvent.getClass().getSimpleName(),
										rtmpEvent.getTimestamp(), basetime,
										deltime));
			}
		}
		//get the buffer only once per call
		IoBuffer buf = null;
		if (rtmpEvent instanceof IStreamData
				&& (buf = ((IStreamData) rtmpEvent).getData()) != null) {
			bytesReceived += buf.limit();
		}
		if (rtmpEvent instanceof AudioData) {
			if (info != null) {
				info.setHasAudio(true);
			}
			if (rtmpEvent.getHeader().isTimerRelative()) {
				if (audioTime == 0) {
					log.warn("First Audio timestamp is relative! {}", rtmpEvent.getTimestamp());
				}
				audioTime  = rtmpEvent.getTimestamp()+lastEventTime;
			} else {
				audioTime = rtmpEvent.getTimestamp();
			}
			eventTime = audioTime;
			log.trace("Audio: {}", eventTime);
		} else if (rtmpEvent instanceof VideoData) {
			
			IVideoStreamCodec videoStreamCodec = null;
			if (checkVideoCodec) {
				videoStreamCodec = VideoCodecFactory.getVideoCodec(buf);
				if (codecInfo instanceof StreamCodecInfo) {
					((StreamCodecInfo) codecInfo)
							.setVideoCodec(videoStreamCodec);
				}
				checkVideoCodec = false;
			} else if (codecInfo != null) {
				videoStreamCodec = codecInfo.getVideoCodec();
			}

			if (videoStreamCodec != null) {
				videoStreamCodec.addData(buf);
			}

			if (info != null) {
				info.setHasVideo(true);
			}
			if (rtmpEvent.getHeader().isTimerRelative()) {
				if (videoTime == 0) {
					log.warn("First Video timestamp is relative! {}", rtmpEvent.getTimestamp());
				}
				
				videoTime = rtmpEvent.getTimestamp() + lastEventTime;
				
			} else {
				videoTime = rtmpEvent.getTimestamp();
				// Flash player may send first VideoData with old-absolute timestamp.
				// This ruins the stream's timebase in FileConsumer.
				// We don't want to discard the packet, as it may be a video keyframe.
				// Generally a Data or Audio packet has set the timebase to a reasonable value,
				// Eventually a new/correct absolute time will come on the video channel.
				// We could put this logic between livePipe and filePipe;
				// This would work for Audio Data as well, but have not seen the need.
				int cts = Math.max(audioTime, dataTime);
				cts = Math.max(cts, minStreamTime);
				int fudge = 20;
				// accept some slightly (20ms) retro timestamps [this may not be needed,
				// the publish Data should strictly precede the video data]
				if (videoTime + fudge < cts) {
					log.info("dispatchEvent: adjust archaic videoTime, from: {} to {}", videoTime, cts);
					videoTime = cts;
				}
			}
			eventTime = videoTime;	
			log.trace("Video: {}", eventTime);
		} else if (rtmpEvent instanceof Invoke) {
			if (rtmpEvent.getHeader().isTimerRelative()) {
				if (dataTime < 0) {
					log.warn("First data [Invoke] timestamp is relative! {}", rtmpEvent.getTimestamp());
				}
				dataTime = rtmpEvent.getTimestamp() + lastEventTime;
			} else {
				dataTime = rtmpEvent.getTimestamp();
			}
			//do we want to return from here?
			//event / stream listeners will not be notified of invokes
			return;
		} else if (rtmpEvent instanceof Notify) {
			//TDJ: store METADATA
			Notify notifyEvent = (Notify) rtmpEvent;
			if (metaData == null && notifyEvent.getHeader().getDataType() == Notify.TYPE_STREAM_METADATA){
				try {
					metaData = notifyEvent.duplicate();
				} catch (Exception e) {
					log.warn("Metadata could not be duplicated for this stream", e);
				}
			}
			
			if (rtmpEvent.getHeader().isTimerRelative()) {
				if (dataTime < 0) {
					log.warn("First data [Notify] timestamp is relative! {}", rtmpEvent.getTimestamp());
				}
				dataTime = rtmpEvent.getTimestamp() + lastEventTime;
			} else {
				dataTime = rtmpEvent.getTimestamp();
			}
			eventTime = dataTime;
		}
		
		lastEventTime = eventTime;
		
		// Notify event listeners
		checkSendNotifications(event);

		// Create new RTMP message, initialize it and push through pipe
		RTMPMessage msg = new RTMPMessage();
		msg.setBody(rtmpEvent);
		msg.getBody().setTimestamp(eventTime); 
		// rtmpEvent.setTimestamp(eventTime); ~ABSOLUTE!
		// note this timestamp is set in event/body but not in the associated header.
		try {
			if (livePipe != null) {
				livePipe.pushMessage(msg);
			} else {
				log.debug("Live pipe was null, message was not pushed");
			}
			if (recordPipe != null) {
				recordPipe.pushMessage(msg);
			} else {
				log.debug("Record pipe was null, message was not pushed");
			}
		} catch (IOException err) {
			sendRecordFailedNotify(err.getMessage());
			stop();
		}
		
		// Notify listeners about received packet
		if (rtmpEvent instanceof IStreamPacket) {
			for (IStreamListener listener: getStreamListeners()) {
				try {
					listener.packetReceived(this, (IStreamPacket) rtmpEvent);
				} catch (Exception e) {
					log.error("Error while notifying listener {}", listener, e);
				}
			}
		}
	}

	/** {@inheritDoc} */
	public int getActiveSubscribers() {
		return subscriberStats.getCurrent();
	}

	/** {@inheritDoc} */
	public long getBytesReceived() {
		return bytesReceived;
	}

	/** {@inheritDoc} */
	public int getCurrentTimestamp() {
		return Math.max(Math.max(videoTime, audioTime), dataTime);
	}

	/** {@inheritDoc} */
	public int getMaxSubscribers() {
		return subscriberStats.getMax();
	}

	/**
	 * Getter for provider
	 * @return            Provider
	 */
	public IProvider getProvider() {
		return this;
	}

	/**
	 * Getter for published name
	 * @return        Stream published name
	 */
	public String getPublishedName() {
		return publishedName;
	}

	/** {@inheritDoc} */
	public String getSaveFilename() {
		return recordingFilename;
	}

	/** {@inheritDoc} */
	public IClientBroadcastStreamStatistics getStatistics() {
		return this;
	}

	/** {@inheritDoc} */
	public int getTotalSubscribers() {
		return subscriberStats.getTotal();
	}

	/**
	 *  Notifies handler on stream broadcast stop
	 */
	private void notifyBroadcastClose() {
		IStreamAwareScopeHandler handler = getStreamAwareHandler();
		if (handler != null) {
			try {
				handler.streamBroadcastClose(this);
			} catch (Throwable t) {
				log.error("Error in notifyBroadcastClose", t);
			}
		}
	}

	/**
	 *  Notifies handler on stream broadcast start
	 */
	private void notifyBroadcastStart() {
		IStreamAwareScopeHandler handler = getStreamAwareHandler();
		if (handler != null) {
			try {
				handler.streamBroadcastStart(this);
			} catch (Throwable t) {
				log.error("Error in notifyBroadcastStart", t);
			}
		}
	}

	/**
	 * Send OOB control message with chunk size
	 */
	private void notifyChunkSize() {
		if (chunkSize > 0 && livePipe != null) {
			OOBControlMessage setChunkSize = new OOBControlMessage();
			setChunkSize.setTarget("ConnectionConsumer");
			setChunkSize.setServiceName("chunkSize");
			if (setChunkSize.getServiceParamMap() == null) {
				setChunkSize.setServiceParamMap(new HashMap<String, Object>());
			}
			setChunkSize.getServiceParamMap().put("chunkSize", chunkSize);
			livePipe.sendOOBControlMessage(getProvider(), setChunkSize);
		}
	}

	/**
	 * Out-of-band control message handler
	 *
	 * @param source           OOB message source
	 * @param pipe             Pipe that used to send OOB message
	 * @param oobCtrlMsg       Out-of-band control message
	 */
	public void onOOBControlMessage(IMessageComponent source, IPipe pipe,
			OOBControlMessage oobCtrlMsg) {
		if ("ClientBroadcastStream".equals(oobCtrlMsg.getTarget())) {
    		if ("chunkSize".equals(oobCtrlMsg.getServiceName())) {
    			chunkSize = (Integer) oobCtrlMsg.getServiceParamMap().get(
    					"chunkSize");
    			notifyChunkSize();
    		}
		}
	}

	/**
	 * Pipe connection event handler
	 * @param event          Pipe connection event
	 */
	@SuppressWarnings("unused")
	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
			case PipeConnectionEvent.PROVIDER_CONNECT_PUSH:
				if (event.getProvider() == this
						&& event.getSource() != connMsgOut
						&& (event.getParamMap() == null || !event.getParamMap()
								.containsKey("record"))) {
					this.livePipe = (IPipe) event.getSource();
					for (IConsumer consumer : this.livePipe.getConsumers()) {
						subscriberStats.increment();
					}
				}
				break;
			case PipeConnectionEvent.PROVIDER_DISCONNECT:
				if (this.livePipe == event.getSource()) {
					this.livePipe = null;
				}
				break;
			case PipeConnectionEvent.CONSUMER_CONNECT_PUSH:
				if (this.livePipe == event.getSource()) {
					notifyChunkSize();
				}
				
				if (metaData != null) {
					RTMPMessage msg = new RTMPMessage();
					msg.setBody(metaData);
					msg.getBody().setTimestamp(0);
					try {
						livePipe.pushMessage(msg);
					} catch (IOException e) {
						log.warn("Error sending metadata", e);
					}
				}
				
				subscriberStats.increment();
				
				break;
			case PipeConnectionEvent.CONSUMER_DISCONNECT:
				subscriberStats.decrement();
				break;
			default:
		}
	}

	/**
	 * Currently not implemented
	 *
	 * @param pipe           Pipe
	 * @param message        Message
	 */
	public void pushMessage(IPipe pipe, IMessage message) {
	}

	/**
	 * Save broadcasted stream.
	 *
	 * @param name                           Stream name
	 * @param isAppend                       Append mode
	 * @throws IOException					 File could not be created/written to.
	 * @throws ResourceNotFoundException     Resource doesn't exist when trying to append.
	 * @throws ResourceExistException        Resource exist when trying to create.
	 */
	public void saveAs(String name, boolean isAppend) throws IOException,
			ResourceNotFoundException, ResourceExistException {
		log.debug("SaveAs - name: {} append: {}", name, isAppend);
		// Get stream scope
		IStreamCapableConnection conn = getConnection();
		if (conn == null) {
			// TODO: throw other exception here?
			throw new IOException("Stream is no longer connected");
		}
		IScope scope = conn.getScope();
		// Get stream filename generator
		IStreamFilenameGenerator generator = (IStreamFilenameGenerator) ScopeUtils
				.getScopeService(scope, IStreamFilenameGenerator.class,
						DefaultStreamFilenameGenerator.class);

		// Generate filename
		recordingFilename = generator.generateFilename(scope, name, ".flv",
				GenerationType.RECORD);
		// Get file for that filename
		File file;
		if (generator.resolvesToAbsolutePath()) {
			file = new File(recordingFilename);
		} else {
			file = scope.getContext().getResource(recordingFilename).getFile();
		}
		//
		log.debug("File exists: {} writable: {}", file.exists(), file.canWrite());
		// If append mode is on...
		if (!isAppend) {
			if (file.exists()) {
				// Per livedoc of FCS/FMS:
				// When "live" or "record" is used,
				// any previously recorded stream with the same stream URI is deleted.
				if (!file.delete()) {
					throw new IOException(String.format("File: %s could not be deleted", file.getName()));
				}
			}
		} else {
			if (!file.exists()) {
				// Per livedoc of FCS/FMS:
				// If a recorded stream at the same URI does not already exist,
				// "append" creates the stream as though "record" was passed.
				isAppend = false;
			}
		}

		if (!file.exists()) {
			// Make sure the destination directory exists
			String path = file.getAbsolutePath();
			int slashPos = path.lastIndexOf(File.separator);
			if (slashPos != -1) {
				path = path.substring(0, slashPos);
			}
			File tmp = new File(path);
			if (!tmp.isDirectory()) {
				tmp.mkdirs();
			}

			file.createNewFile();
		} 
		
		//remove existing meta file
		File meta = new File(file.getCanonicalPath() + ".meta");
		if (meta.exists()) {
   			log.trace("Meta file exists");
    		if (meta.delete()) {
    			log.debug("Meta file deleted - {}", meta.getName());
    		} else {
    			log.warn("Meta file was not deleted - {}", meta.getName());
    			meta.deleteOnExit();
    		}
		} else {
   			log.debug("Meta file does not exist: {}", meta.getCanonicalPath());
		}
		
		log.debug("Recording file: {}", file.getCanonicalPath());
		recordingFile = new FileConsumer(scope, file);
		Map<Object, Object> paramMap = new HashMap<Object, Object>(1);
		if (isAppend) {
			paramMap.put("mode", "append");
		} else {
			paramMap.put("mode", "record");
		}
		//mark as "recording" only if we get subscribed
		recording = recordPipe.subscribe(recordingFile, paramMap);
	}

	/**
	 * Sends publish start notifications
	 */
	private void sendPublishStartNotify() {
		Status publishStatus = new Status(StatusCodes.NS_PUBLISH_START);
		publishStatus.setClientid(getStreamId());
		publishStatus.setDetails(getPublishedName());

		StatusMessage startMsg = new StatusMessage();
		startMsg.setBody(publishStatus);
		try {
			connMsgOut.pushMessage(startMsg);
		} catch (IOException err) {
			log.error("Error while pushing message.", err);
		}
	}

	/**
	 *  Sends publish stop notifications
	 */
	private void sendPublishStopNotify() {
		Status stopStatus = new Status(StatusCodes.NS_UNPUBLISHED_SUCCESS);
		stopStatus.setClientid(getStreamId());
		stopStatus.setDetails(getPublishedName());

		StatusMessage stopMsg = new StatusMessage();
		stopMsg.setBody(stopStatus);
		try {
			connMsgOut.pushMessage(stopMsg);
		} catch (IOException err) {
			log.error("Error while pushing message.", err);
		}
	}

	/**
	 *  Sends record failed notifications
	 */
	private void sendRecordFailedNotify(String reason) {
		Status failedStatus = new Status(StatusCodes.NS_RECORD_FAILED);
		failedStatus.setLevel(Status.ERROR);
		failedStatus.setClientid(getStreamId());
		failedStatus.setDetails(getPublishedName());
		failedStatus.setDesciption(reason);

		StatusMessage failedMsg = new StatusMessage();
		failedMsg.setBody(failedStatus);
		try {
			connMsgOut.pushMessage(failedMsg);
		} catch (IOException err) {
			log.error("Error while pushing message.", err);
		}
	}

	/**
	 *  Sends record start notifications
	 */
	private void sendRecordStartNotify() {
		Status recordStatus = new Status(StatusCodes.NS_RECORD_START);
		recordStatus.setClientid(getStreamId());
		recordStatus.setDetails(getPublishedName());

		StatusMessage startMsg = new StatusMessage();
		startMsg.setBody(recordStatus);
		try {
			connMsgOut.pushMessage(startMsg);
		} catch (IOException err) {
			log.error("Error while pushing message.", err);
		}
	}

	/**
	 *  Sends record stop notifications
	 */
	private void sendRecordStopNotify() {
		Status stopStatus = new Status(StatusCodes.NS_RECORD_STOP);
		stopStatus.setClientid(getStreamId());
		stopStatus.setDetails(getPublishedName());

		StatusMessage startMsg = new StatusMessage();
		startMsg.setBody(stopStatus);
		try {
			connMsgOut.pushMessage(startMsg);
		} catch (IOException err) {
			log.error("Error while pushing message.", err);
		}
	}

	private void sendStartNotifications(IEventListener source) {
		if (sendStartNotification) {
			// Notify handler that stream starts recording/publishing
			sendStartNotification = false;
			if (source instanceof IConnection) {
				IScope scope = ((IConnection) source).getScope();
				if (scope.hasHandler()) {
					Object handler = scope.getHandler();
					if (handler instanceof IStreamAwareScopeHandler) {
						if (recording) {
							((IStreamAwareScopeHandler) handler)
									.streamRecordStart(this);
						} else {
							((IStreamAwareScopeHandler) handler)
									.streamPublishStart(this);
						}
					}
				}
			}
			// Send start notifications
			sendPublishStartNotify();
			if (recording) {
				sendRecordStartNotify();
			}
			notifyBroadcastStart();
		}
	}

	/**
	 * Setter for stream published name
	 * @param name       Name that used for publishing. Set at client side when begin to broadcast with NetStream#publish.
	 */
	public void setPublishedName(String name) {
		log.debug("setPublishedName: {}", name);
		//check to see if we are setting the name to the same string
		if (!name.equals(publishedName)) {
			// update an attribute
			JMXAgent.updateMBeanAttribute(oName, "publishedName", name);
		} else {
			//create a new mbean for this instance with the new name
			oName = JMXFactory.createObjectName("type",
					"ClientBroadcastStream", "publishedName", name);
			JMXAgent.registerMBean(this, this.getClass().getName(),
					ClientBroadcastStreamMBean.class, oName);
		}
		this.publishedName = name;
	}

	/**
	 * Starts stream. Creates pipes, video codec from video codec factory bean,
	 * connects
	 */
	public void start() {
		log.info("Stream start");
		IConsumerService consumerManager = (IConsumerService) getScope()
				.getContext().getBean(IConsumerService.KEY);
		firstPacketTime = -1;
		audioTime = videoTime = dataTime = 0;
		connMsgOut = consumerManager.getConsumerOutput(this);
		connMsgOut.subscribe(this, null);
		recordPipe = new InMemoryPushPushPipe();
		Map<Object, Object> recordParamMap = new HashMap<Object, Object>();
		// Clear record flag
		recordParamMap.put("record", null);
		recordPipe.subscribe((IProvider) this, recordParamMap);
		recording = false;
		recordingFilename = null;
		setCodecInfo(new StreamCodecInfo());
		closed = false;
		bytesReceived = 0;
		creationTime = System.currentTimeMillis();
	}

	/** {@inheritDoc} */
	public void startPublishing() {
		// We send the start messages before the first packet is received.
		// This is required so FME actually starts publishing.
		sendStartNotifications(Red5.getConnectionLocal());
	}

	/** {@inheritDoc} */
	public void stop() {
		stopRecording();
		close();
	}

	/**
	 * Stops any currently active recordings.
	 */
	public void stopRecording() {
		if (recording) {
			recording = false;
			recordingFilename = null;
			recordPipe.unsubscribe(recordingFile);
			sendRecordStopNotify();
		}
	}
	
	public boolean isRecording() {
		return recording;
	}

	/** {@inheritDoc} */
	public void addStreamListener(IStreamListener listener) {
		listeners.add(listener);
	}

	/** {@inheritDoc} */
	public Collection<IStreamListener> getStreamListeners() {
		return listeners;
	}

	/** {@inheritDoc} */
	public void removeStreamListener(IStreamListener listener) {
		listeners.remove(listener);
	}

}
