package org.red5.server.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolSession;
import org.red5.server.rtmp.message.AudioData;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.HandshakeConnect;
import org.red5.server.rtmp.message.HandshakeReply;
import org.red5.server.rtmp.message.Invoke;
import org.red5.server.rtmp.message.Notify;
import org.red5.server.rtmp.message.Packet;
import org.red5.server.rtmp.message.Ping;
import org.red5.server.rtmp.message.StreamBytesRead;
import org.red5.server.rtmp.message.VideoData;
import org.red5.server.service.ServiceInvoker;
import org.red5.server.stream.Stream;

public class RTMPSessionHandler implements ProtocolHandler, Constants{

	protected static Log log =
        LogFactory.getLog(RTMPSessionHandler.class.getName());
	
	public void exceptionCaught(ProtocolSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void messageReceived(ProtocolSession session, Object message) throws Exception {
		final Connection conn = (Connection) session.getAttachment();
		final Packet packet = (Packet) message;
		final Channel channel = conn.getChannel(packet.getSource().getChannelId());
		switch(packet.getDataType()){
		case TYPE_INVOKE:
			onInvoke(conn, channel, (Invoke) packet);
			break;
		case TYPE_NOTIFY:
			onNotify(conn, channel, (Notify) packet);
			break;
		case TYPE_PING:
			onPing(conn, channel, (Ping) packet);
			break;
		case TYPE_STREAM_BYTES_READ:
			onStreamBytesRead(conn, channel, (StreamBytesRead) packet);
			break;
		case TYPE_AUDIO_DATA:
			onAudioData(conn, channel, (AudioData) packet);
			break;
		case TYPE_VIDEO_DATA:
			onVideoData(conn, channel, (VideoData) packet);
			break;
		}
	}

	public void messageSent(ProtocolSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionClosed(ProtocolSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionCreated(ProtocolSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionIdle(ProtocolSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionOpened(ProtocolSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	// ------------------------------------------------------------------------------
	
	public void onHandshakeConnect(Connection conn, Channel channel, HandshakeConnect connect){
		final HandshakeReply reply = new HandshakeReply();
		reply.getData().fill((byte) 0x00, HANDSHAKE_SIZE).put(connect.getData()).flip();
		conn.write(reply);
	}
	
	public void onInvoke(Connection conn, Channel channel, Invoke invoke){
		final ServiceInvoker serviceInvoker = conn.getContext().getServiceInvoker();
		if(invoke.getCall().getServiceName()==null){
			// internal call, so lookup the service name
		} 
	}
	
	public void onNotify(Connection conn, Channel channel, Notify notify){
		
	}
	
	public void onPing(Connection conn, Channel channel, Ping ping){
		// respond to the ping
	}
	
	public void onStreamBytesRead(Connection conn, Channel channel, StreamBytesRead streamBytesRead){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	public void onAudioData(Connection conn, Channel channel, AudioData audioData){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	public void onVideoData(Connection conns, Channel channel, VideoData videoData){
		// get the stream, pass the event to the stream
		final Stream stream = channel.getStream();
	}
	
	//	 ---------------------------------------------------------------------------
	
	
	
}
