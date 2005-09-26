package org.red5.server.protocol.rtmp;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.io.IoSession;
import org.red5.server.io.Deserializer;
import org.red5.server.io.amf.Input;
import org.red5.server.utils.HexDump;

public class Session {
	
	public static final byte STATE_UNKNOWN = -1;
	public static final byte STATE_CONNECT = 0;
	public static final byte STATE_HANDSHAKE = 1;
	public static final byte STATE_CONNECTED = 2;
	public static final byte STATE_DISCONNECTED = 3;

	private byte state = STATE_UNKNOWN;
	private IoSession io;
	private ProtocolHandler handler;
	private int packetsRead = 0;
	private int packetsWritten = 0;
	private byte lastChannelId = -1;
	
	protected static Log log =
        LogFactory.getLog(Session.class.getName());

	public Session(IoSession ioSession, ProtocolHandler protocolHandler){
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
			channels[channelId] = new Channel(channelId);
		lastChannelId = channelId;
		return channels[channelId];
	}
	
	public Channel getLastChannel(){
		return getChannel(lastChannelId);
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
	
	public void onPacket(Packet packet){
		
		packetsRead++;
		
		log.debug(HexDump.formatHexDump(packet.getData().getHexDump()));
		
		try { 
		
			switch(packet.dataType){
			case Packet.TYPE_FUNCTION_CALL:
				onFunctionCallPacket(packet);
				break;
			case Packet.TYPE_VIDEO:
				onVideoPacket(packet);
				break;
			case Packet.TYPE_AUDIO:
				onAudioPacket(packet);
				break;
			case Packet.TYPE_CLIENT_BANDWIDTH:
				onClientBandwidthPacket(packet);
				break;
			case Packet.TYPE_SERVER_BANDWIDTH:
				onServerBandwidthPacket(packet);
				break;
			case Packet.TYPE_PING:
				onPingPacket(packet);
				break;
			case Packet.TYPE_MISTERY:
				onMisteryPacket(packet);
				break;
			default:
				log.error("Unknown datatype: "+packet.dataType);
				break;
			}
		
		} catch (Exception ex){
			log.error("Error handling packet", ex);
			// should we close connection here ?
		} finally {
			// destory the packet, releasing the internal buffer
			packet.destory();
		}
	}
	
	public void onFunctionCallPacket(Packet packet){
		Deserializer deserializer = new Deserializer();
		Input input = new Input(packet.getData());
		String action = (String) deserializer.deserialize(input);
		Number number = (Number) deserializer.deserialize(input);
		Map params = (Map) deserializer.deserialize(input);
		log.debug("Action:" + action);
		log.debug("Number: "+number.toString());
		log.debug("Params: "+params);
	}
	
	public void onAudioPacket(Packet packet){
		// what to do with audio
		// write it to a file (see it its an mp3) ? 
		// send it back ?
	}
	
	public void onVideoPacket(Packet packet){
		// what to do with video
	}
	
	public void onClientBandwidthPacket(Packet packet){
		
	}
	
	public void onServerBandwidthPacket(Packet packet){
		
	}

	public void onPingPacket(Packet packet){
	
	}

	public void onMisteryPacket(Packet packet){
	
	}
	
	// TODO: ADD OTHER PACKET TYPE YANNIK SPOKE ABOUT
	
	public void writePacket(Packet packet){
		handler.writePacket(this, packet);
	}
	
	public void close(){
		log.debug("Closing connection");
		io.close();
	}
	
	public IoSession getIoSession(){
		return io;
	}
	
}
