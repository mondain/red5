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
import org.red5.server.SessionRegistry;
import org.red5.server.context.AppContext;
import org.red5.server.context.GlobalContext;
import org.red5.server.context.HostContext;
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
	protected GlobalContext globalContext;
	protected StatusService statusService;
	
	public void handlePacket(Session session, Packet packet){
		
	}

	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public void setGlobalContext(GlobalContext globalContext) {
		this.globalContext = globalContext;
	}

	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
	
	public void onPacket(Packet packet){
 		
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
		int packetId = ((Number) deserializer.deserialize(input)).intValue();
		Object headers = deserializer.deserialize(input);
		
		log.debug("Action:" + action);
		log.debug("Number: "+packetId);
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
		
		// How best to support these internal actions ? 
		// How about a hashmap of methods mapping to services in spring ?
		
		if(action!=null && action.equals("connect")){
			log.debug("Call connect action");
			session.setParams((Map) headers);
			onConnect(packet,packetId, (Map) headers);
		}
		
		else if(action!=null && action.equals("createStream")){
			onCreateStream(packet,packetId);
		}
		
		else if(action!=null && action.equals("_error")){
			log.error("We have an error status from client");
			// what should we do now
		}
		
		// Otherwise create a call object and sent it to the service object.
		
		else {
			
			RTMPCall call = new RTMPCall(session.getServiceName(), action, params, packetId, packet.getSource(), packet.getSourceChannel());
			
			// TODO: add worker threads here after 0.2
			ServiceInvoker invoker = session.getAppContext().getServiceInvoker();
			invoker.invoke(call, session.getAppContext());
			writeResponse(call);
			
		}
		
	}
	
	private void writeResponse(RTMPCall call){
		
		if(call.isSuccess()){
			log.debug("Result: "+ call.getResult());
		} else {
			log.debug("Error: ", call.getException());
		}
			
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, (call.isSuccess()) ? "_result" : "_status" ); 
		serializer.serialize(output, new Integer(call.getPacketId())); 
		serializer.serialize(output, null);	
		if(call.isSuccess()){
			serializer.serialize(output, call.getResult());
		} else {
			// TODO: Send correct status here 
			serializer.serialize(output, call.getException().getMessage());
		}

		out.flip();
	
		Packet response = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, call.getPacketSource());
					
		log.debug(response);
		
		call.getChannel().writePacket(response);

	}
	
	private void onConnect(Packet packet, int num, Map params) {
		
		// TODO Clean this mess of a method up :)
		
		Session session = packet.getSourceChannel().getSession();
		
		//packet.
		String appName = (String) params.get("app");
		String serviceName = null;
		if(appName.indexOf("/")!=-1){
			serviceName = appName.substring(appName.indexOf("/")+1,appName.length());
			log.debug("Service Name: "+serviceName);
			appName = appName.substring(0, appName.indexOf("/"));
		}
		String tcUrl = (String) params.get("tcUrl");
		String hostname = tcUrl.split("/")[2];
		log.debug("hostname: "+hostname);
		
		HostContext host = (globalContext.hasHostContext(hostname)) ?
				globalContext.getHostContext(hostname) : globalContext.getDefaultHost();
		
		AppContext app = host.getAppContext(appName);
		session.setAppName(appName);
		session.setAppContext(app);
		
		if(serviceName!=null){
			Object service = app.getBean(serviceName);
			session.setServiceName(serviceName);
			session.setService(service);
		}
		
		log.debug(app.getServiceInvoker());
		
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "/result"); // seems right
		// dont know what this number does, so im just sending it back
		serializer.serialize(output, new Integer(num)); 
		serializer.serialize(output, null);
		out.put(statusService.getStatus("SUCCESS"));
		
		// TODO: status service should return static byte[]
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
