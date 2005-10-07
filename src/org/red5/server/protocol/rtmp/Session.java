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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.io.IoSession;
import org.red5.server.io.Deserializer;
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Input;
import org.red5.server.io.amf.Output;
import org.red5.server.utils.HexDump;

public class Session {
	
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
	private IoSession io;
	private NetworkHandler handler;
	
	private int packetsRead = 0;
	private int packetsWritten = 0;
	
	private Channel lastReadChannel = null;
	private Channel lastWriteChannel = null;
	
	private Map params = null;	
	private String appName = null;
	
	protected static Log log =
        LogFactory.getLog(Session.class.getName());

	public Session(IoSession ioSession, NetworkHandler protocolHandler){
		state = STATE_CONNECT;
		io = ioSession;
		handler = protocolHandler;
	}
	
	private Channel[] channels = new Channel[64];
	
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
	
	public void handshake(){
		state = STATE_HANDSHAKE;
	}
	
	public void connected(){
		state = STATE_CONNECTED;
	}
	
	
	
	// TODO: ADD OTHER PACKET TYPE YANNIK SPOKE ABOUT
	
	public void writePacket(Packet packet){
		//handler.writePacket(this, packet);
	}
	
	public void close(){
		log.debug("Closing connection");
		io.close();
	}
	
	public IoSession getIoSession(){
		return io;
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
		if(params != null && params.containsKey(PARAM_APP)){
			appName = (String) params.get(PARAM_APP);
		}
	}
	
	public String getAppName(){
		return appName;
	}
	
}
