package org.red5.server.net.remoting;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.mina.core.buffer.IoBuffer;
import org.red5.io.amf.Input;
import org.red5.io.amf.Output;
import org.red5.io.object.Deserializer;
import org.red5.io.object.RecordSet;
import org.red5.io.object.Serializer;
import org.red5.server.net.servlet.ServletUtils;
import org.red5.server.pooling.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Client interface for remoting calls.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class RemotingClient implements InitializingBean {
	/**
	 * Logger
	 */
	protected static Logger log = LoggerFactory.getLogger(RemotingClient.class);

	/** Default timeout to use. */
	public static final int DEFAULT_TIMEOUT = 30000;

	/** Content MIME type for HTTP requests. */
	private static final String CONTENT_TYPE = "application/x-amf";

	/** Manages HTTP connections. */
	private static HttpConnectionManager connectionMgr = new MultiThreadedHttpConnectionManager();

	/** HTTP client for remoting calls. */
	private HttpClient client;

	/** Url to connect to. */
	private String url;

	/** Additional string to use while connecting. */
	private String appendToUrl = "";

	/** Headers to send to the server. */
	protected Map<String, RemotingHeader> headers = new ConcurrentHashMap<String, RemotingHeader>();

	/** Thread pool to use for asynchronous requests. */
	protected static ExecutorService executor;
	
	/** Maximum pool threads */
	private int poolSize = 1;

	/**
	 * Dummy constructor used by the spring configuration.
	 */
	public RemotingClient() {
		log.debug("RemotingClient created");
	}

	/**
	 * Create new remoting client for the given url.
	 * 
	 * @param url URL to connect to
	 */
	public RemotingClient(String url) {
		this(url, DEFAULT_TIMEOUT);
		log.debug("RemotingClient created  - url: {}", url);
	}

	/**
	 * Create new remoting client for the given url and given timeout.
	 * 
	 * @param url URL to connect to
	 * @param timeout Timeout for one request in milliseconds
	 */
	public RemotingClient(String url, int timeout) {
		this(url, timeout, connectionMgr);
		log.debug("RemotingClient created  - url: {} timeout: {}", url, timeout);
	}

	/**
	 * Create new remoting client in the given connection manager.
	 * 
	 * @param url URL to connect to
	 * @param mgr The connection manager to use
	 */
	public RemotingClient(String url, HttpConnectionManager mgr) {
		this(url, DEFAULT_TIMEOUT, mgr);
		log.debug("RemotingClient created  - url: {} http manager: {}", url, mgr);
	}

	/**
	 * Create new remoting client with the given connection manager and timeout.
	 * 
	 * @param url URL to connect to
	 * @param timeout Timeout for one request in milliseconds
	 * @param mgr The connection manager to use
	 */
	public RemotingClient(String url, int timeout, HttpConnectionManager mgr) {
		client = new HttpClient(mgr);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		this.url = url;
		log.debug("RemotingClient created  - url: {} timeout: {} http manager: {}", new Object[]{url, timeout, mgr});
	}
	
	public void afterPropertiesSet() throws Exception {
		executor = Executors.newFixedThreadPool(poolSize);
	}
	
	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}	
	
	/**
	 * Encode the method call.
	 * 
	 * @param method
	 *            Remote method being called
	 * @param params
	 *            Method parameters
	 * @return Byte buffer with data to perform remoting call
	 */
	private IoBuffer encodeInvoke(String method, Object[] params) {
		log.debug("RemotingClient encodeInvoke - method: {} params: {}", method, params);		
		IoBuffer result = IoBuffer.allocate(1024);
		result.setAutoExpand(true);

		// XXX: which is the correct version?
		result.putShort((short) 0);
		// Headers
		Collection<RemotingHeader> hdr = headers.values();
		result.putShort((short) hdr.size());
		for (RemotingHeader header : hdr) {
			Output.putString(result, header.name);
			result.put(header.required ? (byte) 0x01 : (byte) 0x00);

			IoBuffer tmp = IoBuffer.allocate(1024);
			tmp.setAutoExpand(true);
			Output tmpOut = new Output(tmp);
			Serializer tmpSer = new Serializer();
			tmpSer.serialize(tmpOut, header.data);
			tmp.flip();
			// Size of header data
			result.putInt(tmp.limit());
			// Header data
			result.put(tmp);
			tmp.free();
			tmp = null;
		}
		// One body
		result.putShort((short) 1);

		// Method name
		Output.putString(result, method);

		// Client callback for response
		Output.putString(result, "");

		// Serialize parameters
		IoBuffer tmp = IoBuffer.allocate(1024);
		tmp.setAutoExpand(true);
		Output tmpOut = new Output(tmp);

		//if the params are null send the NULL AMF type
		//this should fix APPSERVER-296
		if (params == null) {
			tmpOut.writeNull();
		} else {
			tmpOut.writeArray(params, new Serializer());
		}
		tmp.flip();

		// Store size and parameters
		result.putInt(tmp.limit());
		result.put(tmp);
		tmp.free();
		tmp = null;

		result.flip();
		return result;
	}

	/**
	 * Process any headers sent in the response.
	 * 
	 * @param in
	 *            Byte buffer with response data
	 */
	@SuppressWarnings("static-access")
	protected void processHeaders(IoBuffer in) {
		log.debug("RemotingClient processHeaders - buffer limit: {}", (in != null ? in.limit() : 0));				
		int version = in.getUnsignedShort(); // skip
		log.debug("Version: {}", version);
		// the version by now, AMF3 is not yet implemented
		int count = in.getUnsignedShort(); 
		log.debug("Count: {}", count);
		Deserializer deserializer = new Deserializer();
		Input input = new Input(in);
		for (int i = 0; i < count; i++) {
			String name = input.getString(in);
			//String name = deserializer.deserialize(input, String.class);
			log.debug("Name: {}", name);
			boolean required = (in.get() == 0x01);
			log.debug("Required: {}", required);
			int len = in.getInt();
			log.debug("Length: {}", len);
			Object value = deserializer.deserialize(input, Object.class);
			log.debug("Value: {}", value);

			// XXX: this is pretty much untested!!!
			if (RemotingHeader.APPEND_TO_GATEWAY_URL.equals(name)) {
				// Append string to gateway url
				appendToUrl = (String) value;
			} else if (RemotingHeader.REPLACE_GATEWAY_URL.equals(name)) {
				// Replace complete gateway url
				url = (String) value;
				// XXX: reset the <appendToUrl< here?
			} else if (RemotingHeader.PERSISTENT_HEADER.equals(name)) {
				// Send a new header with each following request
				if (value instanceof Map<?,?>) {
					@SuppressWarnings("unchecked")
					Map<String, Object> valueMap = (Map<String, Object>) value;
					RemotingHeader header = new RemotingHeader(
							(String) valueMap.get("name"), 
							(Boolean) valueMap.get("mustUnderstand"), 
							valueMap.get("data"));
					headers.put(header.name, header);
				} else {
					log.error("Expected Map but received {}", value);
				}
			} else {
				log.warn("Unsupported remoting header \"{}\" received with value \"{}\"", name, value);
			}
		}
	}

	/**
	 * Decode response received from remoting server.
	 * 
	 * @param data
	 *            Result data to decode
	 * @return Object deserialized from byte buffer data
	 */
	private Object decodeResult(IoBuffer data) {
		log.debug("decodeResult - data limit: {}", (data != null ? data.limit() : 0));
		processHeaders(data);
		int count = data.getUnsignedShort();
		if (count != 1) {
			throw new RuntimeException("Expected exactly one result but got " + count);
		}

		Input input = new Input(data);
		String target = input.getString(); // expect "/onResult"
		log.debug("Target: {}", target);
		String nullString = input.getString(); // expect "null"
		log.debug("Null string: {}", nullString);

		// Read return value
		Deserializer deserializer = new Deserializer();
		return deserializer.deserialize(input, Object.class);
	}

	/**
	 * Send authentication data with each remoting request.
	 * 
	 * @param userid User identifier
	 * @param password Password
	 */
	public void setCredentials(String userid, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("userid", userid);
		data.put("password", password);
		RemotingHeader header = new RemotingHeader(RemotingHeader.CREDENTIALS, true, data);
		headers.put(RemotingHeader.CREDENTIALS, header);
	}

	/**
	 * Stop sending authentication data.
	 */
	public void resetCredentials() {
		removeHeader(RemotingHeader.CREDENTIALS);
	}

	/**
	 * Send an additional header to the server.
	 * 
	 * @param name Header name
	 * @param required Header required?
	 * @param value Header body
	 */
	public void addHeader(String name, boolean required, Object value) {
		RemotingHeader header = new RemotingHeader(name, required, value);
		headers.put(name, header);
	}

	/**
	 * Stop sending a given header.
	 * 
	 * @param name Header name
	 */
	public void removeHeader(String name) {
		headers.remove(name);
	}

	/**
	 * Invoke a method synchronously on the remoting server.
	 * 
	 * @param method Method name
	 * @param params Parameters passed to method
	 * @return the result of the method call
	 */
	public Object invokeMethod(String method, Object[] params) {
		log.debug("invokeMethod url: {}", (url + appendToUrl));
		PostMethod post = new PostMethod(this.url + appendToUrl);
		IoBuffer resultBuffer = null;
		IoBuffer data = encodeInvoke(method, params);
		post.setRequestEntity(new InputStreamRequestEntity(data.asInputStream(), data.limit(), CONTENT_TYPE));
		try {
			log.debug("Client: {}", client);
			int resultCode = client.executeMethod(post);
			log.debug("Result code: {}", resultCode);
			if (resultCode / 100 != 2) {
				throw new RuntimeException("Didn't receive success from remoting server.");
			}
			resultBuffer = IoBuffer.allocate((int) post.getResponseContentLength());
			ServletUtils.copy(post.getResponseBodyAsStream(), resultBuffer.asOutputStream());
			resultBuffer.flip();
			Object result = decodeResult(resultBuffer);
			if (result instanceof RecordSet) {
				// Make sure we can retrieve paged results
				((RecordSet) result).setRemotingClient(this);
			}
			return result;
		} catch (Exception ex) {
			log.error("Error while invoking remoting method.", ex);
		} finally {
			post.releaseConnection();
			if (resultBuffer != null) {
				resultBuffer.free();
				resultBuffer = null;
			}
			data.free();
			data = null;
		}
		return null;
	}

	/**
	 * Invoke a method asynchronously on the remoting server.
	 * 
	 * @param method Method name
	 * @param methodParams Parameters passed to method
	 * @param callback Callback
	 */
	public void invokeMethod(String method, Object[] methodParams, IRemotingCallback callback) {
		try {
			Object[] params = new Object[]{this, method, methodParams, callback};
			Class<?>[] paramTypes = new Class[]{RemotingClient.class, String.class, Object[].class, IRemotingCallback.class};

			Worker worker = new Worker();
			worker.setClassName("org.red5.server.net.remoting.RemotingClient$RemotingWorker");
			worker.setMethodName("executeTask");
			worker.setMethodParams(params);
			worker.setParamTypes(paramTypes);
			executor.execute(worker);
		} catch (Exception err) {
			log.warn("Exception invoking method: {}", method, err);
		}
	}

	/**
	 * Worker class that is used for asynchronous remoting calls.
	 */
	public static class RemotingWorker {

		/**
		 * Execute task.
		 * 
		 * @param client Remoting client
		 * @param method Method name
		 * @param params Parameters to pass to method on call
		 * @param callback Callback
		 */
		public void executeTask(RemotingClient client, String method, Object[] params, IRemotingCallback callback) {
			try {
				Object result = client.invokeMethod(method, params);
				callback.resultReceived(client, method, params, result);
			} catch (Exception err) {
				callback.errorReceived(client, method, params, err);
			}
		}
	}

}
