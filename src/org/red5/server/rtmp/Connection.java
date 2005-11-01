package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.protocol.ProtocolSession;
import org.red5.server.rtmp.message.OutPacket;
import org.red5.server.context.Client;

public class Connection extends Client {

	protected static Log log =
        LogFactory.getLog(Connection.class.getName());
	
	public static final byte STATE_CONNECT = 0;
	public static final byte STATE_HANDSHAKE = 1;
	public static final byte STATE_CONNECTED = 2;
	public static final byte STATE_DISCONNECTED = 3;
	
	private ProtocolSession protocolSession;
	private Context context;
	private byte state = STATE_CONNECT;
	private Channel[] channels = new Channel[64];
	private Channel lastReadChannel = null;
	private Channel lastWriteChannel = null;
	
	public Connection(ProtocolSession protocolSession){
		this.protocolSession = protocolSession;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public ProtocolSession getProtocolSession() {
		return protocolSession;
	}
	
	public byte getState() {
		return state;
	}
	
	public void setState(byte state) {
		this.state = state;
	}
	
	public int getNextAvailableChannelId(){
		int result = -1;
		for(byte i=4; i<channels.length; i++){
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
	
	public void write(OutPacket packet){
		protocolSession.write(packet);
	}
	
}
