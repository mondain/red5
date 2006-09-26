package org.red5.server.net.remoting.codec;

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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.red5.io.amf.Input;
import org.red5.io.object.Deserializer;
import org.red5.server.net.protocol.ProtocolState;
import org.red5.server.net.protocol.SimpleProtocolDecoder;
import org.red5.server.net.remoting.message.RemotingCall;
import org.red5.server.net.remoting.message.RemotingPacket;

public class RemotingProtocolDecoder implements SimpleProtocolDecoder {

	protected static Log log =
        LogFactory.getLog(RemotingProtocolDecoder.class.getName());

	protected static Log ioLog =
        LogFactory.getLog(RemotingProtocolDecoder.class.getName()+".in");

	private Deserializer deserializer = null;

	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	public List decodeBuffer(ProtocolState state, ByteBuffer buffer) {
		List list = new LinkedList();
		Object packet = null;
		try {
			packet = decode(state, buffer);
		} catch (Exception e) {
			log.error("Decoding error", e);
			packet = null;
		}
		if (packet != null)
			list.add(packet);
		return list;
	}

	public Object decode(ProtocolState state, ByteBuffer in) throws Exception {
		skipHeaders(in);
		List calls = decodeCalls(in);
		return new RemotingPacket(calls);
	}

	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	protected void skipHeaders(ByteBuffer in) {
		log.debug("Skip headers");
		int version = in.getUnsignedShort(); // skip the version
		int count = in.getUnsignedShort();
		log.debug("Version: " + version);
		log.debug("Count: " + count);
		for (int i = 0; i < count; i++) {
			log.debug("Header: " + Input.getString(in));
			boolean required = in.get() == 0x01;
			log.debug("Required: " + required);
			in.skip(in.getInt());
		}
	}

	protected List decodeCalls(ByteBuffer in) {
		log.debug("Decode calls");
		// in.getInt();
		List<RemotingCall> calls = new LinkedList<RemotingCall>();
		Input input = new Input(in);
		int count = in.getUnsignedShort();
		log.debug("Calls: " + count);
		int limit = in.limit();

		// Loop over all the body elements
		for (int i = 0; i < count; i++) {

			in.limit(limit);
			input.reset();

			String serviceString = Input.getString(in);
			String clientCallback = Input.getString(in);
			log.debug("callback: " + clientCallback);
			int length = in.getInt();

			// set the limit and deserialize
			// NOTE: disabled because the FP sends wrong values here
			/*
			 * if (length != -1) in.limit(in.position()+length);
			 */
			Object value = deserializer.deserialize(input);

			// log.info(value);

			String serviceName;
			String serviceMethod;
			int dotPos = serviceString.lastIndexOf(".");
			if (dotPos != -1) {
				serviceName = serviceString.substring(0, dotPos);
				serviceMethod = serviceString.substring(dotPos + 1,
						serviceString.length());
			} else {
				serviceName = serviceString;
				serviceMethod = "";
			}

			log.info("Service: " + serviceName + " Method: " + serviceMethod);
			Object[] args = null;
			if (value instanceof Object[]) {
				args = (Object[]) value;
			} else if (value instanceof List) {
				List valueList = (List) value;
				args = new Object[valueList.size()];
				for (int j = 0; j < valueList.size(); j++)
					args[j] = valueList.get(j);
			} else if (value instanceof Set) {
				Set valueSet = (Set) value;
				args = new Object[valueSet.size()];
				int j = 0;
				for (Object item : valueSet)
					args[j++] = item;
			} else {
				args = new Object[] { value };
			}

			for (int j = 0; j < args.length; j++) {
				log.info("> " + args[j]);
			}

			// Add the call to the list
			calls.add(new RemotingCall(serviceName, serviceMethod, args,
					clientCallback));
		}
		return calls;
	}

}
