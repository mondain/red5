package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SessionConfig;
import org.apache.mina.io.IoHandlerAdapter;
import org.apache.mina.io.IoSession;
import org.apache.mina.io.socket.SocketSessionConfig;
import org.red5.server.utils.BufferLogUtils;

public class NetworkHandler extends IoHandlerAdapter {

	protected static Log log =
        LogFactory.getLog(NetworkHandler.class.getName());
	
	// TODO: implement max connection code
	protected int maxConnections = -1;
	protected SessionHandler sessionHandler;
	
	public void sessionCreated(IoSession session){
		
		if(log.isDebugEnabled())
			log.debug("New connection: "+session.getRemoteAddress().toString());
		
		SessionConfig cfg = session.getConfig();
		if (cfg instanceof SocketSessionConfig) {
			((SocketSessionConfig) cfg).setSessionReceiveBufferSize(2048);
		}
	}

	public void exceptionCaught(IoSession session, Throwable cause) {
		session.close();
	}

	public void dataRead(IoSession ioSession, ByteBuffer in) {
		
		if(log.isDebugEnabled()){
			log.debug(" ====== READ DATA ===== ");
			BufferLogUtils.debug(log,"Raw packet",in);
		}
			
		Session session = (Session) ioSession.getAttachment();
		if (session == null) {
			session = new Session(ioSession, this);
			ioSession.setAttachment(session);
		}

		switch (session.getState()) {

		case Session.STATE_CONNECT:
			
			if(log.isDebugEnabled())
				log.debug("New connection, handshake packet");
			
			handshake(session, in);
			session.handshake();
			break;

		case Session.STATE_HANDSHAKE:
			
			if(log.isDebugEnabled())
				log.debug("Handshake response, first packet");
			
			// skip the first 1536 as thdese are the as server handshake
			in.skip(1536);
			session.connected();
			// fall through..

		case Session.STATE_CONNECTED:
			
			if(log.isDebugEnabled())
				log.debug("Normal packet");
			
			while(in.remaining()>0){
				
				if(log.isDebugEnabled())
					log.debug("Read data - bytes remaining: "+in.remaining());
				
				byte headerByte = in.get();
				in.position(in.position()-1); // skip back 1 :)
				
				Channel channel;
				Packet packet;
				
				byte channelId = RTMPUtils.decodeChannelId(headerByte);
				
				channel = session.getChannel(channelId);
				packet = channel.readPacket(in);
				session.setLastReadChannel(channel);
				
				if(packet.isSealed()){
					
					if(log.isDebugEnabled())
						log.debug("Packet on channel #"+channelId);
					
					sessionHandler.onPacket(packet);
				} else {
					log.debug("Not finished");
				}
				
			}
			break;

		}
		
	}

	public void handshake(Session session, ByteBuffer in) {
		// read the header byte
		byte header = in.get();

		if (header != 0x03) {
			log.error("Bad handshake header byte, expected 0x03, closing connection");
			session.close();
			return;
		}

		// create an out buffer the right size
		ByteBuffer out = ByteBuffer.allocate((1536 * 2) + 1);
		// write the server response

		// header byte
		out.put((byte) 0x03);

		// initially I used the actual bytes from micks dump
		// but I soon discovered it doesnt matter what these bytes are
		// so lets create the response filling it with 0x00
		byte[] serverHandshake = new byte[1536];
		for (int i = 0; i < serverHandshake.length; i++)
			serverHandshake[i] = 0x00;

		// write server handshake to the buffer
		out.put(serverHandshake);

		// write the client handshake back
		out.put(in);

		// flip the buffer, log it, and send to client
		out.flip();
		
		if(log.isDebugEnabled()){
			log.debug(" ====== WRITE DATA ===== ");
			BufferLogUtils.debug(log,"Handshake packet",out);
		}
		
		session.getIoSession().write(out, null);

	}



	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setSessionHandler(SessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}	

}