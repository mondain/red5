package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolSession;
import org.red5.server.context.AppContext;
import org.red5.server.context.GlobalContext;
import org.red5.server.context.HostContext;
import org.red5.server.context.Scope;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;
import org.red5.server.rtmp.message.AudioData;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Handshake;
import org.red5.server.rtmp.message.HandshakeReply;
import org.red5.server.rtmp.message.InPacket;
import org.red5.server.rtmp.message.Invoke;
import org.red5.server.rtmp.message.Message;
import org.red5.server.rtmp.message.Notify;
import org.red5.server.rtmp.message.PacketHeader;
import org.red5.server.rtmp.message.Ping;
import org.red5.server.rtmp.message.StreamBytesRead;
import org.red5.server.rtmp.message.VideoData;
import org.red5.server.service.Call;
import org.red5.server.service.ServiceInvoker;
import org.red5.server.stream.Stream;

public class RTMPSessionHandler implements ProtocolHandler, Constants{

	protected static Log log =
        LogFactory.getLog(RTMPSessionHandler.class.getName());
	
	public StatusObjectService statusObjectService = null;
	public GlobalContext globalContext = null;
	public ServiceInvoker serviceInvoker = null;

	public void setStatusObjectService(StatusObjectService statusObjectService) {
		this.statusObjectService = statusObjectService;
	}
	
	public void setGlobalContext(GlobalContext globalContext) {
		this.globalContext = globalContext;
	}
	
	public void setServiceInvoker(ServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}
	
	//	 ------------------------------------------------------------------------------

	public void exceptionCaught(ProtocolSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		log.error("Exception caught", cause);
	}

	public void messageReceived(ProtocolSession session, Object in) throws Exception {
		
		try {
			log.debug("Message recieved");
			
			final Connection conn = (Connection) session.getAttachment();
			final InPacket packet = (InPacket) in;
			final Message message = packet.getMessage();
			final PacketHeader source = packet.getSource();
			final Channel channel = conn.getChannel(packet.getSource().getChannelId());
			
			Scope.setClient(conn);
			Scope.setStream(conn.getStreamByChannelId(channel.getId()));
			Scope.setStatusObjectService(statusObjectService);
			
			log.debug("Channel: "+channel);
			
			switch(message.getDataType()){
			case TYPE_HANDSHAKE:
				onHandshake(conn, channel, source, (Handshake) message);
				break;
			case TYPE_INVOKE:
			case TYPE_NOTIFY: // just like invoke, but does not return
				onInvoke(conn, channel, source, (Invoke) message);
				break;
			case TYPE_PING:
				onPing(conn, channel, source, (Ping) message);
				break;
			case TYPE_STREAM_BYTES_READ:
				onStreamBytesRead(conn, channel, source, (StreamBytesRead) message);
				break;
			case TYPE_AUDIO_DATA:
				onAudioData(conn, channel, source, (AudioData) message);
				break;
			case TYPE_VIDEO_DATA:
				onVideoData(conn, channel, source, (VideoData) message);
				break;
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			log.debug("Exception",e);
		}
	}

	public void messageSent(ProtocolSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		log.debug("Message sent");
	}

	public void sessionClosed(ProtocolSession session) throws Exception {
		final Connection conn = (Connection) session.getAttachment();
		conn.setState(Connection.STATE_DISCONNECTED);
		invokeCall(conn, new Call("disconnect"));
		log.debug("Session closed");
	}

	public void sessionCreated(ProtocolSession session) throws Exception {
		log.debug("Session created");
		session.setAttachment(new Connection(session));
	}

	public void sessionIdle(ProtocolSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		log.debug("Session idle");
	}

	public void sessionOpened(ProtocolSession session) throws Exception {
		log.debug("Session opened");
	}

	// ------------------------------------------------------------------------------
	
	public AppContext lookupAppContext(Connection conn){
		
		final String app = conn.getParameter("app");
		final String hostname = conn.getParameter("tcUrl").split("/")[2];
		
		if(log.isDebugEnabled()){
			log.debug("Hostname: "+hostname);
			log.debug("App: "+app);
		}
			
		final HostContext host = (globalContext.hasHostContext(hostname)) ?
				globalContext.getHostContext(hostname) : globalContext.getDefaultHost();
		
		if(!host.hasAppContext(app)){
			log.warn("Application not found");
			return null; // todo close connection etc, send status etc
		}
		
		return host.getAppContext(app);
		
	}
	
	public void invokeCall(Connection conn, Call call){
		
		if(call.getServiceName()==null){
			call.setServiceName(AppContext.APP_SERVICE_NAME);
		} 
		
		serviceInvoker.invoke(call, conn.getAppContext());
		
	}
	
	// ------------------------------------------------------------------------------
	
	public void onHandshake(Connection conn, Channel channel, PacketHeader source, Handshake handshake){
		log.debug("Handshake Connect");
		log.debug("Channel: "+channel);

		conn.setState(Connection.STATE_HANDSHAKE);
		final HandshakeReply reply = new HandshakeReply();
		reply.getData().put(handshake.getData()).flip();
		channel.write(reply);
	}
	

	public void onInvoke(Connection conn, Channel channel, PacketHeader source, Invoke invoke){
		
		log.debug("Invoke");
		
		if(invoke.getConnectionParams()!=null){
			log.debug("Setting connection params: "+invoke.getConnectionParams());
			conn.setParameters(invoke.getConnectionParams());
			log.debug("Setting application context");
			conn.setAppContext(lookupAppContext(conn));
		}
		
		final Call call = invoke.getCall();
		
		invokeCall(conn,call);
		
		if(invoke.isAndReturn()){
		
			Invoke reply = new Invoke();
			reply.setCall(call);
			reply.setInvokeId(invoke.getInvokeId());
			channel.write(reply);
		
		}
		
	}
	
	public void onPing(Connection conn, Channel channel, PacketHeader source, Ping ping){
		// respond to the ping
		log.debug("Ping!!!");
	}
	
	public void onStreamBytesRead(Connection conn, Channel channel, PacketHeader source, StreamBytesRead streamBytesRead){
		// get the stream, pass the event to the stream
		
	}
	
	public void onAudioData(Connection conn, Channel channel, PacketHeader source, AudioData audioData){
		// get the stream, pass the event to the stream
		
	}
	
	public void onVideoData(Connection conns, Channel channel, PacketHeader source, VideoData videoData){
		// get the stream, pass the event to the stream
		
	}
	
	//	 ---------------------------------------------------------------------------
	
	
	
}
