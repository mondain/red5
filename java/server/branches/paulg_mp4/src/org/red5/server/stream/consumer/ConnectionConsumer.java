package org.red5.server.stream.consumer;

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

import org.red5.server.api.IBWControllable;
import org.red5.server.api.IBandwidthConfigure;
import org.red5.server.api.IConnectionBWConfig;
import org.red5.server.api.stream.IClientStream;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.IPushableConsumer;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.BytesRead;
import org.red5.server.net.rtmp.event.ChunkSize;
import org.red5.server.net.rtmp.event.FlexStreamSend;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.Ping;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.stream.StreamTracker;
import org.red5.server.stream.message.RTMPMessage;
import org.red5.server.stream.message.ResetMessage;
import org.red5.server.stream.message.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RTMP connection consumer.
 */
public class ConnectionConsumer implements IPushableConsumer,
		IPipeConnectionListener {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
			.getLogger(ConnectionConsumer.class);

	/**
	 * Connection consumer class name
	 */
	public static final String KEY = ConnectionConsumer.class.getName();

	/**
	 * Connection object
	 */
	private RTMPConnection conn;

	/**
	 * Video channel
	 */
	private Channel video;

	/**
	 * Audio channel
	 */
	private Channel audio;

	/**
	 * Data channel
	 */
	private Channel data;

	/**
	 * Chunk size. Packets are sent chunk-by-chunk.
	 */
	private int chunkSize = 4096;

	/**
	 * Stream tracker
	 */
	private StreamTracker streamTracker;
	
	
	/**
	 * Whether or not the chunk size has been sent. This seems to be 
	 * required for h264.
	 */
	private boolean chunkSizeSent;

	/**
	 * Create rtmp connection consumer for given connection and channels
	 * 
	 * @param conn
	 *            RTMP connection
	 * @param videoChannel
	 *            Video channel
	 * @param audioChannel
	 *            Audio channel
	 * @param dataChannel
	 *            Data channel
	 */
	public ConnectionConsumer(RTMPConnection conn, int videoChannel,
			int audioChannel, int dataChannel) {
		log.debug("Channel ids - video: {} audio: {} data: {}", new Object[]{videoChannel, audioChannel, dataChannel});
		this.conn = conn;
		this.video = conn.getChannel(videoChannel);
		this.audio = conn.getChannel(audioChannel);
		this.data = conn.getChannel(dataChannel);
		streamTracker = new StreamTracker();
	}

	/** {@inheritDoc} */
	public void pushMessage(IPipe pipe, IMessage message) {
		log.debug("pushMessage - type: {}", message.getMessageType());
		if (message instanceof ResetMessage) {
			streamTracker.reset();
		} else if (message instanceof StatusMessage) {
			StatusMessage statusMsg = (StatusMessage) message;
			video.sendStatus(statusMsg.getBody());
		} else if (message instanceof RTMPMessage) {
			//make sure chunk size has been sent
			if (!chunkSizeSent) {
				log.debug("Sending chunk size");
				ChunkSize chunkSizeMsg = new ChunkSize(chunkSize);
				conn.getChannel((byte) 2).write(chunkSizeMsg);		
				chunkSizeSent = true;
			}
			
			RTMPMessage rtmpMsg = (RTMPMessage) message;
			IRTMPEvent msg = rtmpMsg.getBody();
			Header header = new Header();
			int timestamp = streamTracker.add(msg);
			if (timestamp < 0) {
				log.warn("Skipping message with negative timestamp: {}",
						timestamp);
				return;
			}
			header.setTimerRelative(streamTracker.isRelative());
			header.setTimer(timestamp);

			log.debug("RTMP event data type: {}", msg.getDataType());
			//switch is sorted to handle most common to least common
			switch (msg.getDataType()) {
				case Constants.TYPE_AUDIO_DATA:
					log.trace("Audio data");
					AudioData audioData = new AudioData(((AudioData) msg)
							.getData().asReadOnlyBuffer());
					audioData.setHeader(header);
					audioData.setTimestamp(header.getTimer());
					audio.write(audioData);
					break;
				case Constants.TYPE_VIDEO_DATA:
					log.trace("Video data");
					VideoData videoData = new VideoData(((VideoData) msg)
							.getData().asReadOnlyBuffer());
					videoData.setHeader(header);
					videoData.setTimestamp(header.getTimer());
					video.write(videoData);
					break;
				case Constants.TYPE_PING:
					log.trace("Ping");					
					Ping ping = new Ping(((Ping) msg));
					header.setTimerRelative(false);
					header.setTimer(0);
					ping.setHeader(header);
					ping.setTimestamp(header.getTimer());
					conn.ping(ping);
					break;
				case Constants.TYPE_STREAM_METADATA:
					log.trace("Meta data");
					Notify notify = new Notify(((Notify) msg).getData()
							.asReadOnlyBuffer());
					notify.setHeader(header);
					notify.setTimestamp(header.getTimer());
					data.write(notify);
					break;
				case Constants.TYPE_FLEX_STREAM_SEND:
					log.trace("Flex stream send");
					// TODO: okay to send this also to AMF0 clients?
					FlexStreamSend send = new FlexStreamSend(((Notify) msg)
							.getData().asReadOnlyBuffer());
					send.setHeader(header);
					send.setTimestamp(header.getTimer());
					data.write(send);
					break;
				case Constants.TYPE_BYTES_READ:
					log.trace("Bytes read");
					BytesRead bytesRead = new BytesRead(((BytesRead) msg)
							.getBytesRead());
					header.setTimerRelative(false);
					header.setTimer(0);
					bytesRead.setHeader(header);
					bytesRead.setTimestamp(header.getTimer());
					conn.getChannel((byte) 2).write(bytesRead);
					break;
				default:
					log.trace("Default");
					video.write(msg);
			}
		}
	}

	/** {@inheritDoc} */
	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
			case PipeConnectionEvent.PROVIDER_DISCONNECT:
				// XXX should put the channel release code in ConsumerService
				conn.closeChannel(video.getId());
				conn.closeChannel(audio.getId());
				conn.closeChannel(data.getId());
				break;
			default:
		}
	}

	/** {@inheritDoc} */
	public void onOOBControlMessage(IMessageComponent source, IPipe pipe,
			OOBControlMessage oobCtrlMsg) {
		if (!"ConnectionConsumer".equals(oobCtrlMsg.getTarget())) {
			return;
		}

		if ("pendingCount".equals(oobCtrlMsg.getServiceName())) {
			oobCtrlMsg.setResult(conn.getPendingMessages());
		} else if ("pendingVideoCount".equals(oobCtrlMsg.getServiceName())) {
			IClientStream stream = conn.getStreamByChannelId(video.getId());
			if (stream != null) {
				oobCtrlMsg.setResult(conn.getPendingVideoMessages(stream
						.getStreamId()));
			} else {
				oobCtrlMsg.setResult((long) 0);
			}
		} else if ("writeDelta".equals(oobCtrlMsg.getServiceName())) {
			long maxStream = 0;
			IBWControllable bwControllable = conn;
			// Search FC containing valid BWC
			while (bwControllable != null
					&& bwControllable.getBandwidthConfigure() == null) {
				bwControllable = bwControllable.getParentBWControllable();
			}
			if (bwControllable != null
					&& bwControllable.getBandwidthConfigure() != null) {
				IBandwidthConfigure bwc = bwControllable
						.getBandwidthConfigure();
				if (bwc instanceof IConnectionBWConfig) {
					maxStream = ((IConnectionBWConfig) bwc)
							.getDownstreamBandwidth() / 8;
				}
			}
			if (maxStream <= 0) {
				// Use default value
				// TODO: this should be configured somewhere and sent to the
				// client when connecting
				maxStream = 120 * 1024;
			}

			// Return the current delta between sent bytes and bytes the client
			// reported to have received, and the interval the client should use
			// for generating BytesRead messages (half of the allowed
			// bandwidth).
			oobCtrlMsg.setResult(new Long[] {
					conn.getWrittenBytes() - conn.getClientBytesRead(),
					maxStream / 2 });
		} else if ("chunkSize".equals(oobCtrlMsg.getServiceName())) {
			int newSize = (Integer) oobCtrlMsg.getServiceParamMap().get(
					"chunkSize");
			if (newSize != chunkSize) {
				chunkSize = newSize;
				ChunkSize chunkSizeMsg = new ChunkSize(chunkSize);
				conn.getChannel((byte) 2).write(chunkSizeMsg);
			}
		}
	}

}
