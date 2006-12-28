package org.red5.server.stream.provider;

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
import org.red5.io.*;
import org.red5.io.flv.IKeyFrameDataAnalyzer;
import org.red5.io.flv.IKeyFrameDataAnalyzer.KeyFrameMeta;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.messaging.*;
import org.red5.server.net.rtmp.event.*;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.stream.ISeekableProvider;
import org.red5.server.stream.message.RTMPMessage;

import java.io.File;
import java.io.IOException;

/**
 * Pullable provider for files
 */
public class FileProvider implements IPassive, ISeekableProvider,
		IPullableProvider, IPipeConnectionListener {
    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(FileProvider.class);
    /**
     * Class name
     */
	public static final String KEY = FileProvider.class.getName();
    /**
     * Provider scope
     */
	private IScope scope;
    /**
     * Source file
     */
	private File file;
    /**
     * Consumer pipe
     */
	private IPipe pipe;
    /**
     * Tag reader
     */
	private ITagReader reader;
    /**
     * Keyframe metadata
     */
	private KeyFrameMeta keyFrameMeta;
    /**
     * Position at start
     */
	private int start;

    /**
     * Create file provider for given file and scope
     * @param scope            Scope
     * @param file             File
     */
    public FileProvider(IScope scope, File file) {
		this.scope = scope;
		this.file = file;
	}

	/**
     * Setter for start position
     *
     * @param start Start position
     */
    public void setStart(int start) {
		this.start = start;
	}

	/** {@inheritDoc} */
    public synchronized IMessage pullMessage(IPipe pipe) {
		if (this.pipe != pipe) {
			return null;
		}
		if (this.reader == null) {
			init();
		}
		if (!reader.hasMoreTags()) {
			// TODO send OOBCM to notify EOF
			// Do not unsubscribe as this kills VOD seek while in buffer
			// this.pipe.unsubscribe(this);
			return null;
		}
		ITag tag = reader.readTag();
		IRTMPEvent msg = null;
		int timestamp = tag.getTimestamp();
		switch (tag.getDataType()) {
			case Constants.TYPE_AUDIO_DATA:
				msg = new AudioData(tag.getBody());
				break;
			case Constants.TYPE_VIDEO_DATA:
				msg = new VideoData(tag.getBody());
				break;
			case Constants.TYPE_INVOKE:
				msg = new Invoke(tag.getBody());
				break;
			case Constants.TYPE_NOTIFY:
				msg = new Notify(tag.getBody());
				break;
			default:
				log.warn("Unexpected type? " + tag.getDataType());
				msg = new Unknown(tag.getDataType(), tag.getBody());
				break;
		}
		msg.setTimestamp(timestamp);
		RTMPMessage rtmpMsg = new RTMPMessage();
		rtmpMsg.setBody(msg);
		return rtmpMsg;
	}

	/** {@inheritDoc} */
    public IMessage pullMessage(IPipe pipe, long wait) {
		return pullMessage(pipe);
	}

	/** {@inheritDoc} */
    public void onPipeConnectionEvent(PipeConnectionEvent event) {
		switch (event.getType()) {
			case PipeConnectionEvent.PROVIDER_CONNECT_PULL:
				if (pipe == null) {
					pipe = (IPipe) event.getSource();
				}
				break;
			case PipeConnectionEvent.PROVIDER_DISCONNECT:
				if (pipe == event.getSource()) {
					this.pipe = null;
					uninit();
				}
				break;
			case PipeConnectionEvent.CONSUMER_DISCONNECT:
				if (pipe == event.getSource()) {
					uninit();
				}
			default:
				break;
		}
	}

	/** {@inheritDoc} */
    public void onOOBControlMessage(IMessageComponent source, IPipe pipe,
			OOBControlMessage oobCtrlMsg) {
		if (IPassive.KEY.equals(oobCtrlMsg.getTarget())) {
			if (oobCtrlMsg.getServiceName().equals("init")) {
				Integer startTS = (Integer) oobCtrlMsg.getServiceParamMap()
						.get("startTS");
				setStart(startTS);
			}
		}
		if (ISeekableProvider.KEY.equals(oobCtrlMsg.getTarget())) {
			if (oobCtrlMsg.getServiceName().equals("seek")) {
				Integer position = (Integer) oobCtrlMsg.getServiceParamMap()
						.get("position");
				int seekPos = seek(position.intValue());
				// Return position we seeked to
				oobCtrlMsg.setResult(seekPos);
			}
		}
	}

    /**
     * Initializes file provider. Creates streamable file factory and service, seeks to start position
     */
    private void init() {
		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils
				.getScopeService(scope, IStreamableFileFactory.class,
						StreamableFileFactory.class);
		IStreamableFileService service = factory.getService(file);
		if (service == null) {
			log.error("No service found for " + file.getAbsolutePath());
			return;
		}
		try {
			IStreamableFile streamFile = service.getStreamableFile(file);
			reader = streamFile.getReader();
		} catch (IOException e) {
			log.error("error read stream file " + file.getAbsolutePath(), e);
		}
		if (start > 0) {
			seek(start);
		}
	}

    /**
     * Reset
     */
    private synchronized void uninit() {
		if (this.reader != null) {
			this.reader.close();
			this.reader = null;
		}
	}

	/** {@inheritDoc} */
    public synchronized int seek(int ts) {
		if (keyFrameMeta == null) {
			if (!(reader instanceof IKeyFrameDataAnalyzer)) {
				// Seeking not supported
				return ts;
			}

			keyFrameMeta = ((IKeyFrameDataAnalyzer) reader).analyzeKeyFrames();
		}

		if (keyFrameMeta.positions.length == 0) {
			// no video keyframe metainfo, it's an audio-only FLV
			// we skip the seek for now.
			// TODO add audio-seek capability
			return ts;
		}
		int frame = 0;
		for (int i = 0; i < keyFrameMeta.positions.length; i++) {
			if (keyFrameMeta.timestamps[i] > ts) {
				break;
			}
			frame = i;
		}
		reader.position(keyFrameMeta.positions[frame]);
		return keyFrameMeta.timestamps[frame];
	}
}
