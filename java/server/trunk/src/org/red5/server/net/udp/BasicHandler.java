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

package org.red5.server.net.udp;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.red5.io.utils.HexDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * => client send to server
 * <= server send to client
 * << server broadcast
 *
 * Connecting to the server
 *
 * => byte(join)
 * << byte(join) int(id)
 * => byte(list)
 * <= byte(list) int(count) int(id) int(id) ...
 *
 * Sending a message to all
 *
 * => byte(send) [..anything..]
 * << byte(send) [..anything..]
 *
 * Server ping client to keep alive, every second
 *
 * <= byte(noop)
 * => byte(noop)
 *
 * Timeouts (after 10s no reply)
 *
 * << byte(exit) int(id)
 *
 * @author luke
 */
public class BasicHandler extends IoHandlerAdapter {

	protected static Logger log = LoggerFactory.getLogger(BasicHandler.class);

	static final int TICK = 1000;
	static final int TIMEOUT = 10000;

	static final byte NOOP = 0x00;
	static final byte JOIN = 0x01;
	static final byte LIST = 0x02;
	static final byte SEND = 0x03;
	static final byte EXIT = 0x04;

	final IoBuffer NOOP_MSG = IoBuffer.wrap(new byte[]{NOOP}).asReadOnlyBuffer();

	protected Timer timer = new Timer("Timer", true);
	protected Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	protected boolean showInfo = false;

	public BasicHandler(){
		timer.scheduleAtFixedRate(new TimeoutTask(), 0, TICK);
		showInfo = log.isInfoEnabled();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable ex) throws Exception {
		if(showInfo) log.info("Exception: "+session.getRemoteAddress().toString(), ex);
		sessions.remove(session);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (showInfo) {
			log.info("Incomming: {}", session.getRemoteAddress().toString());
		}

		IoBuffer data = (IoBuffer) message;

		// IN HEX DUMP
		log.info(HexDump.prettyPrintHex(data.asReadOnlyBuffer().array()));

		final byte type = data.get();
		data.position(0);

		switch(type){
		case NOOP:
			// drop
			break;
		case JOIN:
			if(!sessions.contains(session)){
				sessions.add(session);
				join(session);
			}
			break;
		case LIST:
			list(session);
			break;
		case SEND:
			broadcast(session, data);
			break;
		case EXIT:
			if (sessions.contains(session)) {
				sessions.remove(session);
				session.close(true);
				leave(session);
			}
			break;
		default:
			if(showInfo) log.info("Unknown (play echo): "+session.getRemoteAddress().toString());
			echo(session, data);
			break;
		}
	}

	protected void echo(IoSession session, IoBuffer data){
		session.write(data);
	}

	protected void broadcast(IoSession exclude, IoBuffer data){
		for (IoSession session : sessions) {
			if (exclude != null && exclude.equals(session)) {
				continue;
			}
			if (showInfo) { 
				log.info("Sending: {}", session.getRemoteAddress().toString());
			}
			session.write(data);
		}
	}

	protected void list(IoSession to){
		final int size = 1 + 4 +  (sessions.size()*4);
		IoBuffer msg = IoBuffer.allocate(size);
		msg.put(LIST);
		msg.putInt(sessions.size());
		for (IoSession session : sessions) {
			msg.putInt(session.getRemoteAddress().hashCode());
		}
		msg.flip();
		to.write(msg);
	}

	protected void leave(IoSession session){
		final int size = 5;
		IoBuffer msg = IoBuffer.allocate(size);
		msg.put(EXIT);
		msg.putInt(session.getRemoteAddress().hashCode());
		msg.flip();
		broadcast(null, msg);
	}

	protected void join(IoSession session){
		final int size = 5;
		IoBuffer msg = IoBuffer.allocate(size);
		msg.put(JOIN);
		msg.putInt(session.getRemoteAddress().hashCode());
		msg.flip();
		broadcast(null, msg);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		if (showInfo) {
			log.info("Created: {}", session.getRemoteAddress().toString());
		}
	}

	protected class TimeoutTask extends TimerTask {

		public void run(){
			long kill = System.currentTimeMillis() - TIMEOUT;
			LinkedList<IoSession> remove = new LinkedList<IoSession>();
			for (IoSession session : sessions){
				if (session.getLastReadTime() < kill){
					if (showInfo) {
						log.info("Timout: {}", session.getRemoteAddress().toString());
					}
					remove.add(session);
				} else {
					session.write(NOOP_MSG.asReadOnlyBuffer());
				}
			}
			if (remove.size() == 0) {
				return;
			}
			for (IoSession session : remove) {
				sessions.remove(session);
				session.close(true);
			}
			for (IoSession session : remove) {
				leave(session);
			}
		}

	}

}
