package org.red5.server.protocol.rtmp;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.SessionRegistry;
import org.red5.server.io.Deserializer;
import org.red5.server.io.Serializer;
import org.red5.server.io.amf.Input;
import org.red5.server.io.amf.Output;
import org.red5.server.protocol.rtmp.status.StatusService;
import org.red5.server.service.ServiceInvoker;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard (luke@red5.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

public class SessionHandler {
	
	protected static Log log =
        LogFactory.getLog(SessionHandler.class.getName());
	
	protected Serializer serializer;
	protected Deserializer deserializer;
	protected SessionRegistry sessionRegistry;
	protected ServiceInvoker serviceInvoker;
	protected StatusService statusService;
	
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
		
		//if(log)
		
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
		log.error("Shared Object");
		log.error("Need to know what flashcom response is.");
	}
	
	private void onSharedObjectPacket(Packet packet) {
		Input input = new Input(packet.getData());
		String sharedObjectName = Input.getString(packet.getData());
	}

	public void onCreateStream(Packet packet, int streamId){
		log.debug("Create stream: "+2);
		log.error("Need to know what flashcom response is.");
	}
	
	public void onFunctionCallPacket(Packet packet){
		
		Channel channel = packet.getSourceChannel();
		Session session = channel.getSession();
		
		Input input = new Input(packet.getData());
		
		String action = (String) deserializer.deserialize(input);
		Number number = (Number) deserializer.deserialize(input);
		Object headers = deserializer.deserialize(input);
		
		log.debug("Action:" + action);
		log.debug("Number: "+number.toString());
		log.debug("Headers: "+headers);
		
		Object[] params = null;

		if(packet.getData().hasRemaining()){
			log.debug("Multiple params");
			ArrayList paramList = new ArrayList();
			while(packet.getData().hasRemaining()){
				paramList.add(deserializer.deserialize(input));
			}		
			log.debug("Num params: "+paramList.size()); 
			params = paramList.toArray();
			for(int i=0; i<params.length; i++){
				log.debug(" > "+i+": "+params[i]);
			}
			
		} 
		
		
		if(action!=null && action.equals("connect")){
			log.debug("Call connect action");
			session.setParams((Map) headers);
			onConnect(packet,number.intValue(), (Map) headers);
		}
		
		else if(action!=null && action.equals("createStream")){
			onCreateStream(packet, number.intValue());
		}
		
		else if(action!=null && action.equals("_error")){
			log.error("We have an error status from client");
			// what should we do now
		}
		
		else {
			log.debug("Unknown action: "+action);
			
			RTMPCall call = new RTMPCall(session.getAppName(), action, params);
			log.debug("RTMPCall "+call);
			
			serviceInvoker.invoke(call);
			
			

				
			Serializer serializer = new Serializer();
			ByteBuffer out = ByteBuffer.allocate(256);
			out.setAutoExpand(true);
			Output output = new Output(out);
			serializer.serialize(output, "_result"); // seems right
			// dont know what this number does, so im just sending it back
			serializer.serialize(output, number); 
			serializer.serialize(output, null);
			
			if(params != null){
				if(params.length > 1){
					serializer.serialize(output, params);
				}
				else {
					serializer.serialize(output, params[0]);
				}
			}
	
			out.flip();
		
			Packet response = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, packet.getSource());
						
			log.debug(response);
			
			packet.getSourceChannel().writePacket(response);

		}

	}
	
	private void onConnect(Packet packet, int num, Map params) {
		// TODO Auto-generated method stub

		//packet.
				
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "/result"); // seems right
		serializer.serialize(output, new Integer(num)); // dont know what this number does, so im just sending it back
		serializer.serialize(output, null);
		out.put(statusService.getStatus("SUCCESS"));
		
		// HACK: status service should do this before returning buffer
		// Or another option would be have the status service return byte[]
		statusService.getStatus("SUCCESS").position(0);
		
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

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}
	
}
