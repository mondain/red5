package org.red5.server.protocol.rtmp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.SessionRegistry;
import org.red5.server.io.Deserializer;
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Input;
import org.red5.server.io.amf.Output;
import org.red5.server.service.ServiceInvoker;
import org.red5.server.utils.HexDump;

public class SessionHandler {
	
	protected static Log log =
        LogFactory.getLog(SessionHandler.class.getName());
	
	protected Serializer serializer;
	protected Deserializer deserializer;
	protected SessionRegistry sessionRegistry;
	protected ServiceInvoker serviceInvoker;
	
	public void handlePacket(Session session, Packet packet){
		
	}

	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public void setServiceInvoker(ServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}

	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
	
	public void onPacket(Packet packet){
		
		log.debug(HexDump.formatHexDump(packet.getData().getHexDump()));
		
		try { 
		
			switch(packet.getDataType()){
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
				log.error("Unknown datatype: "+packet.getDataType());
				break;
			}
		
		} catch (Exception ex){
			log.error("Error handling packet", ex);
			// should we close connection here ?
		} finally {
			// destory the packet, releasing the internal buffer
			packet.release();
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
		
		//Channel channel = getChannel((byte)3); 
		
		Map status = new HashMap();
		status.put("description","Connection succeeded.");
		status.put("code","NetConnection.Connect.Success");
		status.put("level","status");
		
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "/result"); // seems right
		serializer.serialize(output, number); // dont know what this number does, so im just sending it back
		serializer.serialize(output, null);
		serializer.serialize(output, status);
		
		out.flip();
		log.debug(""+out.position());
		
		Packet response = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, 0);
		
		log.debug(response);
		
		packet.getSourceChannel().writePacket(response);
		
		//handler
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
	
}
