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

package org.red5.client.net.rtmp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.logging.LoggingFilter;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Output;
import org.red5.io.object.Serializer;
import org.red5.io.utils.BufferUtils;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.IConnection.Encoding;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IServiceCall;
import org.red5.server.net.protocol.ProtocolState;
import org.red5.server.net.rtmp.IRTMPConnManager;
import org.red5.server.net.rtmp.IRTMPHandler;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.RTMPHandshake;
import org.red5.server.net.rtmp.RTMPMinaConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.codec.RTMPMinaProtocolDecoder;
import org.red5.server.net.rtmp.codec.RTMPMinaProtocolEncoder;
import org.red5.server.net.rtmp.codec.RTMPProtocolDecoder;
import org.red5.server.net.rtmp.codec.RTMPProtocolEncoder;
import org.red5.server.net.rtmp.event.Invoke;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.net.rtmp.status.StatusObject;
import org.red5.server.net.rtmpe.RTMPEIoFilter;
import org.red5.server.service.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all RTMP protocol events fired by the MINA framework.
 */
public class RTMPMinaIoHandler extends IoHandlerAdapter {

	private static Logger log = LoggerFactory.getLogger(RTMPMinaIoHandler.class);

	/**
	 * RTMP events handler
	 */
	protected IRTMPHandler handler;

	protected IRTMPConnManager rtmpConnManager;

	/** {@inheritDoc} */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.debug("Session created");
		// moved protocol state from connection object to RTMP object
		RTMP rtmp = new RTMP();
		session.setAttribute(ProtocolState.SESSION_KEY, rtmp);
		//add rtmpe filter
		session.getFilterChain().addFirst("rtmpeFilter", new RTMPEIoFilter());
		//add protocol filter next
		session.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(new RTMPMinaCodecFactory()));
		if (log.isTraceEnabled()) {
			session.getFilterChain().addLast("logger", new LoggingFilter());
		}
		//create a connection
		RTMPMinaConnection conn = createRTMPMinaConnection();
		conn.setIoSession(session);
		conn.setState(rtmp);
		//add the connection
		session.setAttribute(RTMPConnection.RTMP_CONNECTION_KEY, conn);
		// create an outbound handshake
		OutboundHandshake outgoingHandshake = new OutboundHandshake();
		//set the handshake type
		outgoingHandshake.setHandshakeType(RTMPConnection.RTMP_NON_ENCRYPTED);
		//if handler is rtmpe client set encryption on the protocol state
		//if (handler instanceof RTMPEClient) {
		//rtmp.setEncrypted(true);
		//set the handshake type to encrypted as well
		//outgoingHandshake.setHandshakeType(RTMPConnection.RTMP_ENCRYPTED);
		//}
		//add the handshake
		session.setAttribute(RTMPConnection.RTMP_HANDSHAKE, outgoingHandshake);
		// set a reference to the connection on the client
		((BaseRTMPClientHandler) handler).setConnection((RTMPConnection) conn);
	}

	/** {@inheritDoc} */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug("Session opened");
		super.sessionOpened(session);
		log.debug("Handshake - client phase 1");
		//get the handshake from the session
		RTMPHandshake handshake = (RTMPHandshake) session.getAttribute(RTMPConnection.RTMP_HANDSHAKE);
		IoBuffer clientRequest1 = handshake.doHandshake(null);
		session.write(clientRequest1);
	}

	/** {@inheritDoc} */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug("Session closed");
		RTMP rtmp = (RTMP) session.removeAttribute(ProtocolState.SESSION_KEY);
		log.debug("RTMP state: {}", rtmp);
		RTMPMinaConnection conn = (RTMPMinaConnection) session.removeAttribute(RTMPConnection.RTMP_CONNECTION_KEY);
		try {
			conn.sendPendingServiceCallsCloseError();
			// fire-off closed event
			handler.connectionClosed(conn, rtmp);
			// clear any session attributes we may have previously set
			// TODO: verify this cleanup code is necessary. The session is over and will be garbage collected surely?
			session.removeAttribute(RTMPConnection.RTMP_HANDSHAKE);
			session.removeAttribute(RTMPConnection.RTMPE_CIPHER_IN);
			session.removeAttribute(RTMPConnection.RTMPE_CIPHER_OUT);
		} finally {
			// DW we *always* remove the connection from the RTMP manager even if unexpected exception gets thrown e.g. by handler.connectionClosed
			// Otherwise connection stays around forever, and everything it references e.g. Client, ...
			rtmpConnManager.removeConnection(conn.getId());
		}
	}

	/**
	 * Handle raw buffer receiving event.
	 *
	 * @param in
	 *            Data buffer
	 * @param session
	 *            I/O session, that is, connection between two endpoints
	 */
	protected void rawBufferRecieved(IoBuffer in, IoSession session) {
		log.debug("rawBufferRecieved: {}", in);
		final RTMP rtmp = (RTMP) session.getAttribute(ProtocolState.SESSION_KEY);
		log.debug("state: {}", rtmp);
		final RTMPMinaConnection conn = (RTMPMinaConnection) session.getAttribute(RTMPConnection.RTMP_CONNECTION_KEY);
		RTMPHandshake handshake = (RTMPHandshake) session.getAttribute(RTMPConnection.RTMP_HANDSHAKE);
		if (handshake != null) {
			log.debug("Handshake - client phase 2 - size: {}", in.remaining());
			IoBuffer out = handshake.doHandshake(in);
			if (out != null) {
				log.debug("Output: {}", out);
				session.write(out);
				// if we are using encryption then put the ciphers in the session
				if (handshake.getHandshakeType() == RTMPConnection.RTMP_ENCRYPTED) {
					log.debug("Adding ciphers to the session");
					session.setAttribute(RTMPConnection.RTMPE_CIPHER_IN, handshake.getCipherIn());
					session.setAttribute(RTMPConnection.RTMPE_CIPHER_OUT, handshake.getCipherOut());
				}
				// update the state to connected
				rtmp.setState(RTMP.STATE_CONNECTED);
			}
		} else {
			log.warn("Handshake was not found for this connection: {}", conn);
			log.debug("RTMP state: {} Session: {}", rtmp, session);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		log.debug("messageReceived");
		if (message instanceof IoBuffer) {
			rawBufferRecieved((IoBuffer) message, session);
		} else {
			log.trace("Setting connection local");
			Red5.setConnectionLocal((IConnection) session.getAttribute(RTMPConnection.RTMP_CONNECTION_KEY));
			handler.messageReceived(message, session);
			log.trace("Removing connection local");
			Red5.setConnectionLocal(null);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.debug("messageSent");
		final RTMPMinaConnection conn = (RTMPMinaConnection) session.getAttribute(RTMPConnection.RTMP_CONNECTION_KEY);
		handler.messageSent(conn, message);
		if (message instanceof IoBuffer) {
			if (((IoBuffer) message).limit() == Constants.HANDSHAKE_SIZE) {
				RTMP rtmp = (RTMP) session.getAttribute(ProtocolState.SESSION_KEY);
				handler.connectionOpened(conn, rtmp);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.warn("Exception caught {}", cause.getMessage());
		if (log.isDebugEnabled()) {
			log.error("Exception detail", cause);
		}
	}

	/**
	 * Setter for handler.
	 *
	 * @param handler RTMP events handler
	 */
	public void setHandler(IRTMPHandler handler) {
		this.handler = handler;
	}

	public void setRtmpConnManager(IRTMPConnManager rtmpConnManager) {
		this.rtmpConnManager = rtmpConnManager;
	}

	protected IRTMPConnManager getRtmpConnManager() {
		return rtmpConnManager;
	}

	protected RTMPMinaConnection createRTMPMinaConnection() {
		return (RTMPMinaConnection) rtmpConnManager.createConnection(RTMPMinaConnection.class);
	}

	public class RTMPMinaCodecFactory implements ProtocolCodecFactory {

		private RTMPMinaProtocolDecoder decoder = new RTMPMinaProtocolDecoder();

		private RTMPMinaProtocolEncoder encoder = new RTMPMinaProtocolEncoder();

		{
			// RTMP Decoding
			decoder = new RTMPMinaProtocolDecoder();
			decoder.setDecoder(new RTMPClientProtocolDecoder());
			decoder.setDeserializer(new Deserializer());
			// RTMP Encoding
			encoder = new RTMPMinaProtocolEncoder();
			encoder.setEncoder(new RTMPClientProtocolEncoder());
			encoder.setSerializer(new Serializer());
			// two other config options are available
			//encoder.setBaseTolerance(baseTolerance);
			//encoder.setDropLiveFuture(dropLiveFuture);
		}

		/** {@inheritDoc} */
		public ProtocolDecoder getDecoder(IoSession session) {
			return decoder;
		}

		/** {@inheritDoc} */
		public ProtocolEncoder getEncoder(IoSession session) {
			return encoder;
		}

	}

	/**
	 * Class to specifically handle the client side of the handshake routine.
	 */
	public class RTMPClientProtocolDecoder extends RTMPProtocolDecoder {

		private static final int HANDSHAKE_SERVER_SIZE = (HANDSHAKE_SIZE * 2);

		/**
		 * Decodes server handshake message.
		 * 
		 * @param rtmp RTMP protocol state
		 * @param in IoBuffer
		 * @return IoBuffer
		 */
		public IoBuffer decodeHandshake(RTMP rtmp, IoBuffer in) {
			log.debug("decodeServerHandshake - rtmp: {} buffer: {}", rtmp.states[rtmp.getState()], in);
			log.trace("RTMP: {}", rtmp);
			final int remaining = in.remaining();
			if (rtmp.getState() == RTMP.STATE_CONNECT) {
				if (remaining < HANDSHAKE_SERVER_SIZE + 1) {
					log.debug("Handshake init too small, buffering. remaining: {}", remaining);
					rtmp.bufferDecoding(HANDSHAKE_SERVER_SIZE + 1);
				} else {
					final IoBuffer hs = IoBuffer.allocate(HANDSHAKE_SERVER_SIZE);
					in.get(); // skip the header byte
					BufferUtils.put(hs, in, HANDSHAKE_SERVER_SIZE);
					hs.flip();
					rtmp.setState(RTMP.STATE_HANDSHAKE);
					return hs;
				}
			} else if (rtmp.getState() == RTMP.STATE_HANDSHAKE) {
				log.debug("Handshake reply");
				if (remaining < HANDSHAKE_SERVER_SIZE) {
					log.debug("Handshake reply too small, buffering. remaining: {}", remaining);
					rtmp.bufferDecoding(HANDSHAKE_SERVER_SIZE);
				} else {
					in.skip(HANDSHAKE_SERVER_SIZE);
					rtmp.setState(RTMP.STATE_CONNECTED);
					rtmp.continueDecoding();
				}
			}
			return null;
		}
	}

	/**
	 * Class to specifically handle client side situations.
	 */
	public class RTMPClientProtocolEncoder extends RTMPProtocolEncoder {

		/**
		 * Encode notification event and fill given byte buffer.
		 *
		 * @param out               Byte buffer to fill
		 * @param invoke            Notification event
		 */
		@Override
		protected void encodeNotifyOrInvoke(IoBuffer out, Notify invoke, RTMP rtmp) {
			log.debug("encodeNotifyOrInvoke - rtmp: {} invoke: {}", rtmp.states[rtmp.getState()], invoke);
			log.trace("RTMP: {}", rtmp);
			Output output = new org.red5.io.amf.Output(out);
			final IServiceCall call = invoke.getCall();
			final boolean isPending = (call.getStatus() == Call.STATUS_PENDING);
			log.debug("Call: {} pending: {}", call, isPending);
			if (!isPending) {
				log.debug("Call has been executed, send result");
				serializer.serialize(output, call.isSuccess() ? "_result" : "_error");
			} else {
				log.debug("This is a pending call, send request");
				// for request we need to use AMF3 for client mode if the connection is AMF3
				if (rtmp.getEncoding() == Encoding.AMF3) {
					output = new org.red5.io.amf3.Output(out);
				}
				final String action = (call.getServiceName() == null) ? call.getServiceMethodName() : call.getServiceName() + '.' + call.getServiceMethodName();
				serializer.serialize(output, action);
			}
			if (invoke instanceof Invoke) {
				serializer.serialize(output, Integer.valueOf(invoke.getInvokeId()));
				serializer.serialize(output, invoke.getConnectionParams());
			}
			if (call.getServiceName() == null && "connect".equals(call.getServiceMethodName())) {
				// response to initial connect, always use AMF0
				output = new org.red5.io.amf.Output(out);
			} else {
				if (rtmp.getEncoding() == Encoding.AMF3) {
					output = new org.red5.io.amf3.Output(out);
				} else {
					output = new org.red5.io.amf.Output(out);
				}
			}
			if (!isPending && (invoke instanceof Invoke)) {
				IPendingServiceCall pendingCall = (IPendingServiceCall) call;
				if (!call.isSuccess()) {
					log.debug("Call was not successful");
					StatusObject status = generateErrorResult(StatusCodes.NC_CALL_FAILED, call.getException());
					pendingCall.setResult(status);
				}
				Object res = pendingCall.getResult();
				log.debug("Writing result: {}", res);
				serializer.serialize(output, res);
			} else {
				log.debug("Writing params");
				final Object[] args = call.getArguments();
				if (args != null) {
					for (Object element : args) {
						serializer.serialize(output, element);
					}
				}
			}
			if (invoke.getData() != null) {
				out.setAutoExpand(true);
				out.put(invoke.getData());
			}
		}		
	}
	
}
