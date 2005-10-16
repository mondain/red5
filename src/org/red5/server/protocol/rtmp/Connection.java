package org.red5.server.protocol.rtmp;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
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
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.io.IoSession;
import org.red5.server.context.AppContext;

public class Connection { 
	
	public static final int HANDSHAKE_SIZE = 1536;
	
	public static final byte STATE_UNKNOWN = -1;
	public static final byte STATE_CONNECT = 0;
	public static final byte STATE_HANDSHAKE = 1;
	public static final byte STATE_CONNECTED = 2;
	public static final byte STATE_DISCONNECTED = 3;

	public static final String PARAM_SWFURL = "swfUrl";
	public static final String PARAM_APP = "app";
	public static final String PARAM_FLASHVER = "flashVer";
	public static final String PARAM_TCURL = "tcUrl";
	
	private byte state = STATE_UNKNOWN;
	private ByteBuffer handshakeBuffer;
	private int handshakeRemaining = HANDSHAKE_SIZE;
	
	private IoSession io;
	private NetworkHandler handler;
	
	private int packetsRead = 0;
	private int packetsWritten = 0;
	
	private int clientBytesRead = 0;
	
	private Channel lastReadChannel = null;
	private Channel lastWriteChannel = null;
	
	// Application variables
	private Map params = null;	
	private String appName = null;
	private AppContext appContext;
	private String serviceName = null;
	private Object service = null;
	
	// List of streams
	private ArrayList streams;
	
	protected static Log log =
        LogFactory.getLog(Connection.class.getName());

	public Connection(IoSession ioSession, NetworkHandler protocolHandler){
		state = STATE_CONNECT;
		io = ioSession;
		handler = protocolHandler;
		handshakeBuffer = ByteBuffer.allocate(HANDSHAKE_SIZE+1);
	}
	
	private Channel[] channels = new Channel[64];
	
	public int getNextAvailableChannelId(){
		int result = -1;
		for(byte i=3; i<channels.length; i++){
			if(!isChannelUsed(i)){
				result = i;
				break;
			}
		}
		return result;
	}
	
	public boolean isChannelUsed(byte channelId){
		return (channels[channelId] != null);
	}

	public Channel getChannel(byte channelId){
		if(!isChannelUsed(channelId)) 
			channels[channelId] = new Channel(this, channelId);
		return channels[channelId];
	}
	
	public void closeChannel(byte channelId){
		channels[channelId] = null;
	}

	public Channel getLastReadChannel() {
		return lastReadChannel;
	}

	public void setLastReadChannel(Channel lastReadChannel) {
		this.lastReadChannel = lastReadChannel;
	}

	public Channel getLastWriteChannel() {
		return lastWriteChannel;
	}

	public void setLastWriteChannel(Channel lastWriteChannel) {
		this.lastWriteChannel = lastWriteChannel;
	}

	public byte getState(){
		return state;
	}
	
	public ByteBuffer getHandshakeBuffer() {
		return handshakeBuffer;
	}

	public int getHandshakeRemaining() {
		return handshakeRemaining;
	}

	public void setHandshakeRemaining(int handshakeResponseRead) {
		this.handshakeRemaining = handshakeResponseRead;
	}

	public void handshake(){
		state = STATE_HANDSHAKE;
	}
	
	public void connected(){
		state = STATE_CONNECTED;
	}
		
	public void close(){
		log.debug("Closing connection");
		io.close();
	}
	
	public IoSession getIoSession(){
		return io;
	}
	
	
	
	// -------------------------------------------
	
	public int getClientBytesRead() {
		return clientBytesRead;
	}

	public void setClientBytesRead(int clientBytesRead) {
		this.clientBytesRead = clientBytesRead;
	}

	public String getParameter(String name){
		if(params == null) return null;
		if(name == null) return null;
		return (String) params.get(name);
	}

	public boolean hasParameter(String name){
		return ( params!=null && params.containsKey(name) );
	}
	
	public void setParams(Map params) {
		this.params = params;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName(){
		return appName;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public Object getService() {
		return service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}
