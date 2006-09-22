package org.red5.server.stream.consumer;

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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITag;
import org.red5.io.ITagWriter;
import org.red5.io.StreamableFileFactory;
import org.red5.io.flv.impl.Tag;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.stream.IClientStream;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.IPipeConnectionListener;
import org.red5.server.messaging.IPushableConsumer;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.messaging.PipeConnectionEvent;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.stream.IStreamData;
import org.red5.server.stream.message.RTMPMessage;
import org.red5.server.stream.message.ResetMessage;
import org.red5.server.stream.message.StatusMessage;

public class FileConsumer implements Constants, IPushableConsumer,
		IPipeConnectionListener {
	private static final Log log = LogFactory.getLog(FileConsumer.class);

	private IScope scope;

	private File file;

	private ITagWriter writer;

	private String mode;

	private int offset;

	private int lastTimestamp;

	private int startTimestamp;

	public FileConsumer(IScope scope, File file) {
		this.scope = scope;
		this.file = file;
		offset = 0;
		lastTimestamp = 0;
		startTimestamp = -1;
	}

	public void pushMessage(IPipe pipe, IMessage message) {
		if (message instanceof ResetMessage) {
			startTimestamp = -1;
			offset += lastTimestamp;
			return;
		} else if (message instanceof StatusMessage) {
			StatusMessage statusMsg = (StatusMessage) message;
			return;
		}
		if (!(message instanceof RTMPMessage))
			return;
		if (writer == null) {
			try {
				init();
			} catch (Exception e) {
				log.error("error init file consumer", e);
			}
		}
		RTMPMessage rtmpMsg = (RTMPMessage) message;
		final IRTMPEvent msg = rtmpMsg.getBody();
		if (startTimestamp == -1) {
			startTimestamp = msg.getTimestamp();
		}
		int timestamp = msg.getTimestamp() - startTimestamp;
		if (timestamp < 0) {
			log.warn("Skipping message with negative timestamp.");
			return;
		}
		lastTimestamp = timestamp;

		ITag tag = new Tag();

		tag.setDataType(msg.getDataType());
		tag.setTimestamp(timestamp + offset);
		if (msg instanceof IStreamData) {
			ByteBuffer data = ((IStreamData) msg).getData().asReadOnlyBuffer();
			tag.setBodySize(data.limit());
			tag.setBody(data);
		}

		try {
			writer.writeTag(tag);
		} catch (IOException e) {
			log.error("error writing tag", e);
		}
	}

	public void onOOBControlMessage(IMessageComponent source, IPipe pipe,
			OOBControlMessage oobCtrlMsg) {
		// TODO Auto-generated method stub

	}

	public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
		case PipeConnectionEvent.CONSUMER_CONNECT_PUSH:
			if (event.getConsumer() != this)
				break;
			Map paramMap = event.getParamMap();
			if (paramMap != null)
				mode = (String) paramMap.get("mode");
			break;
		case PipeConnectionEvent.CONSUMER_DISCONNECT:
			if (event.getConsumer() != this)
				break;
		case PipeConnectionEvent.PROVIDER_DISCONNECT:
			// we only support one provider at a time
			// so do releasing when provider disconnects
			uninit();
			break;
		default:
			break;
		}
	}

	private void init() throws IOException {
		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils
				.getScopeService(scope, IStreamableFileFactory.KEY,
						StreamableFileFactory.class);
		if (!file.isFile())
			// Maybe the (previously existing) file has been deleted
			file.createNewFile();
		IStreamableFileService service = factory.getService(file);
		IStreamableFile flv = service.getStreamableFile(file);
		if (mode == null || mode.equals(IClientStream.MODE_RECORD)) {
			writer = flv.getWriter();
		} else if (mode.equals(IClientStream.MODE_APPEND)) {
			writer = flv.getAppendWriter();
		} else
			throw new IllegalStateException("illegal mode type: " + mode);
	}

	private void uninit() {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

}
