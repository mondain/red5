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
				log.info("Function Call");
				onFunctionCallPacket(packet);
				break;
			case Packet.TYPE_VIDEO:
				log.info("Video");
				onVideoPacket(packet);
				break;
			case Packet.TYPE_AUDIO:
				log.info("Audio");
				onAudioPacket(packet);
				break;
			case Packet.TYPE_CLIENT_BANDWIDTH:
				log.info("Client Bandwidth");
				onClientBandwidthPacket(packet);
				break;
			case Packet.TYPE_SERVER_BANDWIDTH:
				log.info("Server Bandwidth");
				onServerBandwidthPacket(packet);
				break;
			case Packet.TYPE_PING:
				log.info("Ping");
				onPingPacket(packet);
				break;
			case Packet.TYPE_MISTERY:
				log.info("Mistery");
				onMisteryPacket(packet);
				break;
			case Packet.TYPE_SHARED_OBJECT:
				log.info("Shared Object");
				onSharedObjectPacket(packet);
				break;
			case Packet.TYPE_SHARED_OBJECT_CONNECT:
				log.info("Shared Object Connect");
				onSharedObjectConnectPacket(packet);
			default:
				log.error("Unknown datatype: "+packet.getDataType());
				break;
			}
		
		} catch (Exception ex){
			log.error("Error handling packet", ex);
			// should we close connection here ?
		} finally {
			
			log.debug("Check for remaining");
			Input input = new Input(packet.getData());
			while(packet.getData().remaining()>0){
				log.debug("Deserialize:" + deserializer.deserialize(input));
			}
			
			// destory the packet, releasing the internal buffer
			packet.release();
		}
	}
	
	private void onSharedObjectConnectPacket(Packet packet) {
		
	}
//06 00 40 08 00 00 00 00 00 00 05 06
//06 00 40 00 00 00 00 00 00 00 05 06
	
	private void onSharedObjectPacket(Packet packet) {
		Input input = new Input(packet.getData());
		String sharedObjectName = Input.getString(packet.getData());
	}

	public void onCreateStream(Packet packet, int streamId){
		log.debug("Create stream: "+2);
		log.error("Need to know what flashcom response is.");
	}
	
	public void onFunctionCallPacket(Packet packet){
		
		
		
		Input input = new Input(packet.getData());
		String action = (String) deserializer.deserialize(input);
		
		
		
		Number number = (Number) deserializer.deserialize(input);
		Map params = (Map) deserializer.deserialize(input);
		
		log.debug("Remaining: "+packet.getData().remaining());
		
		log.debug("Action:" + action);
		log.debug("Number: "+number.toString());
		log.debug("Params: "+params);
		
		if(action!=null && action.equals("connect")){
			onConnect(packet,number.intValue(),params);
		}
		
		else if(action!=null && action.equals("createStream")){
			onCreateStream(packet, number.intValue());
		}
		
		else {
			log.debug("Unknown action: "+action);
			//packet.getData().position(0);
			//echoIt(packet);
		}
		
		//Channel channel = getChannel((byte)3); 
		
		
		//handler
	}
	
	private void echoIt(Packet packet) {
		// TODO Auto-generated method stub
		
		
		
		Packet response = new Packet(packet.getData(), 0, Packet.TYPE_FUNCTION_CALL, 0);
		
		packet.getSourceChannel().writePacket(response);
	}

	
	private void onConnect(Packet packet, int num, Map params) {
		// TODO Auto-generated method stub

		ConnectionStatus cs = new ConnectionStatus();
		Map status = cs.getStatus("success");
		/*
		status.put("description","Connection succeeded.");
		status.put("code","NetConnection.Connect.Success");
		status.put("level","status");
		*/
		
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "/result"); // seems right
		serializer.serialize(output, new Integer(num)); // dont know what this number does, so im just sending it back
		serializer.serialize(output, null);
		serializer.serialize(output, status);
		
		out.flip();
		log.debug(""+out.position());
		
		Packet response = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, 0);
		
		log.debug(response);
		
		packet.getSourceChannel().writePacket(response);
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
