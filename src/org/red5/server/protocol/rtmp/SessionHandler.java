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

import java.io.IOException;
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
import org.red5.server.protocol.rtmp.status2.RuntimeStatusObject;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.red5.server.service.ServiceInvoker;
import org.red5.server.utils.HexDump;
import org.springframework.core.io.Resource;

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
	protected StatusObjectService statusObjectService;
	
	public void handlePacket(Connection session, Packet packet){
		
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
				if(log.isInfoEnabled())
					log.info("Function Call");
				onFunctionCallPacket(packet);
				break;
			case Packet.TYPE_VIDEO:
				if(log.isInfoEnabled())
					log.info("Video");
				onVideoPacket(packet);
				break;
			case Packet.TYPE_AUDIO:
				if(log.isInfoEnabled())
					log.info("Audio");
				onAudioPacket(packet);
				break;
			case Packet.TYPE_CLIENT_BANDWIDTH:
				if(log.isInfoEnabled())
					log.info("Client Bandwidth");
				onClientBandwidthPacket(packet);
				break;
			case Packet.TYPE_SERVER_BANDWIDTH:
				if(log.isInfoEnabled())
					log.info("Server Bandwidth");
				onServerBandwidthPacket(packet);
				break;
			case Packet.TYPE_PING:
				if(log.isInfoEnabled())
					log.info("Ping");
				onPingPacket(packet);
				break;
			case Packet.TYPE_CLIENT_BYTES_READ:
				if(log.isInfoEnabled())
					log.info("Client bytes read");
				onClientBytesReadPacket(packet);
				break;
			case Packet.TYPE_SHARED_OBJECT:
				if(log.isInfoEnabled())
					log.info("Shared Object");
				onSharedObjectPacket(packet);
				break;
			case Packet.TYPE_SHARED_OBJECT_CONNECT:
				if(log.isInfoEnabled())
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
			
			/*
			log.debug("Check for remaining");
			Input input = new Input(packet.getData());
			while(packet.getData().remaining()>0){
				log.debug("Deserialize:" + deserializer.deserialize(input));
			}
			*/
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
	
	public void onCreateStream(Packet packet, int streamId, Object[] params){
		if(log.isDebugEnabled())
			log.debug("Create stream: "+streamId);
		
		Connection conn = packet.getSourceChannel().getConnection();

		Stream stream = new Stream(
				packet.getSourceChannel().getConnection(),
				packet.getSource(),
				this);

		conn.setStream(stream);

		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "_result"); 
		serializer.serialize(output, new Integer(streamId)); 
		serializer.serialize(output, null);
		serializer.serialize(output, new Integer(1)); // pick a number at random	
		out.flip();
		Packet response = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, packet.getSource());
		packet.getSourceChannel().writePacket(response);
	}
	
	public void onPlay(Packet packet, int streamId, Object[] params){
		Stream stream = packet.getSourceChannel().getConnection().getStream();
		
		if(stream==null){
			onCreateStream(packet, streamId, params);
			stream = packet.getSourceChannel().getConnection().getStream();
		}
		
		if(stream.getState()!= Stream.STATE_STOPPED){
			stream.end();
		}
		
		String filename = (String) params[0];
		log.info("Play flv: "+filename);
		//stream.play("flvs/"+filename);
		stream.play(filename);
		
		
		/*HostContext host = (globalContext.hasHostContext(hostname)) ?
				globalContext.getHostContext(hostname) : globalContext.getDefaultHost();
				*/
		Connection connection = packet.getSourceChannel().getConnection();
		AppContext app = connection.getAppContext();
		 try {
			Resource[] flvs = app.getResources("../../../../flvs/*.flv");
			System.out.println("flvs.length: " + flvs.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// play
	// deleteStream
	// cancelStream
	
	public void onFunctionCallPacket(Packet packet){
		
		Channel channel = packet.getSourceChannel();
		Connection conn = channel.getConnection();
		
		Input input = new Input(packet.getData());
		
		String action = (String) deserializer.deserialize(input);
		int packetId = ((Number) deserializer.deserialize(input)).intValue();
		Object headers = deserializer.deserialize(input);
		
		if(log.isInfoEnabled())
			log.info("Action:" + action);
		
		if(log.isDebugEnabled()){
			log.debug("Number: "+packetId);
			log.debug("Headers: "+headers);
		}
		
		Object[] params = new Object[]{};

		if(packet.getData().hasRemaining()){
			// log.debug("Multiple params");
			ArrayList paramList = new ArrayList();
			while(packet.getData().hasRemaining()){
				paramList.add(deserializer.deserialize(input));
			}		
			params = paramList.toArray();
			if(log.isDebugEnabled()){
				log.debug("Num params: "+paramList.size()); 
				for(int i=0; i<params.length; i++){
					log.debug(" > "+i+": "+params[i]);
				}
			}
		} 
		
		// How best to support these internal actions ? 
		// How about a hashmap of methods mapping to services in spring ?
		
		if(action!=null && action.equals("connect")){
			conn.setParams((Map) headers);
			onConnect(packet,packetId, (Map) headers);
		}
		
		else if(action!=null && action.equals("createStream")){
			onCreateStream(packet,packetId, params);
		}
		
		else if(action!=null && action.equals("play")){
			onPlay(packet,packetId, params);
		}
		
		else if(action!=null && action.equals("pause")){
			log.info("Pause");
			if(conn.getStream()!=null){
				conn.getStream().pause();
			}
		}
		
		else if(action!=null && action.equals("closeStream")){
			log.info("Close Stream");
			if(conn.getStream()!=null){
				conn.getStream().end();
			}
		}
		
		else if(action!=null && action.equals("deleteStream")){
			log.info("DeleteStream");
			if(conn.getStream()!=null){
				conn.setStream(null);
			}
		}
		
		else if(action!=null && action.equals("_error")){
			log.error("We have an error status from client");
			// what should we do now
		}
		
		// Otherwise create a call object and sent it to the service object.
		
		else {
			
			RTMPCall call = new RTMPCall(conn.getServiceName(), action, params, packetId, packet.getSource(), packet.getSourceChannel());
			
			// TODO: add worker threads here after 0.2
			ServiceInvoker invoker = conn.getAppContext().getServiceInvoker();
			invoker.invoke(call, conn.getAppContext());
			writeResponse(call);
			
		}
		
	}
	
	private void writeResponse(RTMPCall call){
		
		
		if(call.isSuccess()){
			if(log.isDebugEnabled())
				log.debug("Result: "+ call.getResult());
		} else {
			if(log.isDebugEnabled())
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
		
		Connection connection = packet.getSourceChannel().getConnection();
		
		//packet.
		String appName = (String) params.get("app");
		String serviceName = null;
		if(appName.indexOf("/")!=-1){
			serviceName = appName.substring(appName.indexOf("/")+1,appName.length());
			if(log.isDebugEnabled())
				log.debug("Service Name: "+serviceName);
			appName = appName.substring(0, appName.indexOf("/"));
		}
		String tcUrl = (String) params.get("tcUrl");
		String hostname = tcUrl.split("/")[2];
		
		if(log.isDebugEnabled())
			log.debug("Hostname: "+hostname);
		
		HostContext host = (globalContext.hasHostContext(hostname)) ?
				globalContext.getHostContext(hostname) : globalContext.getDefaultHost();
		
		AppContext app = host.getAppContext(appName);
		connection.setAppName(appName);
		connection.setAppContext(app);
		
		if(serviceName!=null){
			//Object service = app.getBean(serviceName);
			connection.setServiceName(serviceName);
			//connection.setService(service);
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
		
		out.put(statusObjectService.getCachedStatusObjectAsByteArray(statusObjectService.NC_CONNECT_SUCCESS));
		
		out.flip();
		
		//log.debug(""+out.position());
		
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
		// get the channel from the packet
		// get the stream from the channel
	}
	
	public void onClientBandwidthPacket(Packet packet){
		// need to understand the format of this packet
	}
	
	public void onServerBandwidthPacket(Packet packet){
		// need to understand the format of this packlet
	}

	public void onPingPacket(Packet packet){
		
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
	
		
		ByteBuffer in = packet.getData();
		
		short firstShort = in.getShort();
		int firstInt = in.getInt();
		
		firstShort++;
		
		out.putShort(firstShort);
		out.putInt(firstInt);
		
		out.flip();
		
		Packet ping = new Packet(out, 0, Packet.TYPE_PING, packet.getSource());
		log.debug(ping);
		packet.getSourceChannel().writePacket(ping);
		
		ByteBuffer out2 = ByteBuffer.allocate(256);
		out2.setAutoExpand(true);
	
		out2.putShort((short) 0);
		out2.putInt(1);
		
		out2.flip();
		
		Packet ping2 = new Packet(out2, 0, Packet.TYPE_PING, packet.getSource());
		log.debug(ping2);
		packet.getSourceChannel().writePacket(ping2);
		
	}

	public void onClientBytesReadPacket(Packet packet){
		int bytesRead = packet.getData().getInt();
		if(log.isInfoEnabled())
			log.info("Client bytes read: "+bytesRead);
		packet.getSourceChannel().getConnection().setClientBytesRead(bytesRead);
	}

	public void setStatusObjectService(StatusObjectService statusObjectService) {
		this.statusObjectService = statusObjectService;
	}
	
	protected int statusPacketID = 16777216;
	
	public void sendRuntimeStatus(Channel channel, String statusCode, String details, int clientId){
		
		Object obj = statusObjectService.getStatusObject(statusCode);
		if(obj == null || !(obj instanceof RuntimeStatusObject)){
			log.error(statusCode + " is not a runtime status object, not writing status");
			return;
		}
		
		if(log.isDebugEnabled())
			log.debug("Sending runtime status: "+statusCode);
		
		RuntimeStatusObject statusObject = (RuntimeStatusObject) obj;
	
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "onStatus"); 
		serializer.serialize(output, new Integer(1)); 
		serializer.serialize(output, null);	
		
		statusObject.setDetails(details);
		statusObject.setClientid(clientId);
		
		statusObjectService.serializeStatusObject(out, statusObject);
		
		out.flip();
	
		Packet status = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, statusPacketID);
		log.debug(status);
		
		channel.writePacket(status);
	}
	
	public void sendStatus(Channel channel, String statusCode){
		
		if(log.isDebugEnabled())
			log.debug("Sending status: "+statusCode);
		
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "onStatus"); 
		serializer.serialize(output, new Integer(1)); 
		serializer.serialize(output, null);	
		out.put(statusObjectService.getCachedStatusObjectAsByteArray(statusCode));
		out.flip();
	
		Packet status = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL, statusPacketID);
		log.debug(status);
		
		channel.writePacket(status);
	}
	
	public void sendNotify(Channel channel, String statusCode){
		
		if(log.isDebugEnabled())
			log.debug("Sending status: "+statusCode);
		
		Serializer serializer = new Serializer();
		ByteBuffer out = ByteBuffer.allocate(256);
		out.setAutoExpand(true);
		Output output = new Output(out);
		serializer.serialize(output, "onStatus"); 
		serializer.serialize(output, new Integer(1)); 
		serializer.serialize(output, null);	
		out.put(statusObjectService.getCachedStatusObjectAsByteArray(statusCode));
		out.flip();
	
		Packet status = new Packet(out, 0, Packet.TYPE_FUNCTION_CALL_NOREPLY, statusPacketID);
		log.debug(status);
		
		channel.writePacket(status);
	}
	
}
