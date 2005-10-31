package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolSession;
import org.red5.server.protocol.rtmp.status2.StatusObject;
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
import org.red5.server.stream.Stream;

public class RTMPSessionHandler implements ProtocolHandler, Constants{

	protected static Log log =
        LogFactory.getLog(RTMPSessionHandler.class.getName());
	
	public StatusObjectService statusObjectService = null;

	public void setStatusObjectService(StatusObjectService statusObjectService) {
		this.statusObjectService = statusObjectService;
	}

	//	 ------------------------------------------------------------------------------
	
	public void exceptionCaught(ProtocolSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void messageReceived(ProtocolSession session, Object in) throws Exception {
		
		try {
			log.debug("Message recieved");
			
			final Connection conn = (Connection) session.getAttachment();
			final InPacket packet = (InPacket) in;
			final Message message = packet.getMessage();
			final PacketHeader source = packet.getSource();
			final Channel channel = conn.getChannel(packet.getSource().getChannelId());
			
			log.debug("Channel: "+channel);
			
			switch(message.getDataType()){
			case TYPE_HANDSHAKE:
				onHandshake(conn, channel, source, (Handshake) message);
				break;
			case TYPE_INVOKE:
				onInvoke(conn, channel, source, (Invoke) message);
				break;
			case TYPE_NOTIFY:
				onNotify(conn, channel, source, (Notify) message);
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
		
	}

	public void sessionClosed(ProtocolSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionCreated(ProtocolSession session) throws Exception {
		session.setAttachment(new Connection(session));
	}

	public void sessionIdle(ProtocolSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionOpened(ProtocolSession session) throws Exception {
		
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
		
		final Call call = invoke.getCall();
		
		//final ServiceInvoker serviceInvoker = conn.getContext().getServiceInvoker();
		if(call.getServiceName()==null){
			// internal call, so lookup the service name
			log.debug("Internal: "+call.getServiceMethodName());
			
			if(call.getServiceMethodName().equals("connect")){
				StatusObject status = statusObjectService.getStatusObject(StatusObjectService.NC_CONNECT_SUCCESS);
				Call result = new Call(null,"_result",new Object[]{status});
				Invoke reply = new Invoke();
				reply.setInvokeId(invoke.getInvokeId());
				reply.setCall(call);
				channel.write(reply);
			}
			
		} 
	}
	
	public void onNotify(Connection conn, Channel channel, PacketHeader source, Notify notify){
		
	}
	
	public void onPing(Connection conn, Channel channel, PacketHeader source, Ping ping){
		// respond to the ping
	}
	
	public void onStreamBytesRead(Connection conn, Channel channel, PacketHeader source, StreamBytesRead streamBytesRead){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	public void onAudioData(Connection conn, Channel channel, PacketHeader source, AudioData audioData){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	public void onVideoData(Connection conns, Channel channel, PacketHeader source, VideoData videoData){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	//	 ---------------------------------------------------------------------------
	
	
	
}
