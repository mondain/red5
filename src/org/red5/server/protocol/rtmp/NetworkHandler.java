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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
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
	
	protected boolean stressMode = false;
	
	protected byte[] serverHandshake;
	
	public NetworkHandler(){
		fillServerHandshake();
	}
	
	private void fillServerHandshake(){
		serverHandshake = new byte[1536];
		for (int i = 0; i < serverHandshake.length; i++)
			serverHandshake[i] = 0x00;
	}
	
	
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

	public void dataWritten(IoSession ioSession, Object event) throws Exception {
		if(event!=null && event instanceof Stream){
			Stream stream = (Stream) event;
			if(stream.hasMorePackets()){
				stream.writeNextPacket();
			}
		}
	}

	public void dataRead(IoSession ioSession, ByteBuffer in){
		
		try {
			if(log.isDebugEnabled()){
				log.debug(" ====== READ DATA ===== ");
				BufferLogUtils.debug(log,"Raw packet",in);
			}
				
			Connection connection = (Connection) ioSession.getAttachment();
			if (connection == null) {
				connection = new Connection(ioSession, this);
				ioSession.setAttachment(connection);
			}

			switch (connection.getState()) {

			case Connection.STATE_CONNECT:
				
				if(log.isDebugEnabled())
					log.debug("New connection, handshake packet");
				
				ByteBuffer buf = connection.getHandshakeBuffer();
				buf.put(in);
				
				log.debug("Handskake buffer position: "+buf.position());
				
				if(buf.position()==Connection.HANDSHAKE_SIZE+1){
					log.debug("Handshake buffer full, proceeding");
					buf.flip();
					handshake(connection, buf);
					buf.release();
					connection.handshake();
				}
				
				break;

			case Connection.STATE_HANDSHAKE:
				
				if(log.isDebugEnabled())
					log.debug("Handshake response, first packet");
				
				// skip the first 1536 as thdese are the as server handshake
				
				int hsRemaining = connection.getHandshakeRemaining();
				int remaining = in.remaining();
				if(remaining < hsRemaining){
					connection.setHandshakeRemaining(hsRemaining-remaining);
					in.skip(remaining);
					break;
				} else {
					in.skip(hsRemaining);
					connection.connected();
					// fall through..
				}

			case Connection.STATE_CONNECTED:
				
				if(log.isDebugEnabled())
					log.debug("Normal packet");
				
				if(stressMode){
					int limit = in.limit();
					while(in.position() < limit){
						log.debug("stressRead");
						in.limit(in.position()+1);
						processRead(connection,in);
					}
				}
				
				else {
					while(in.remaining()>0){
						processRead(connection, in);
					}
				}
				
				break;

			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processRead(Connection session, ByteBuffer in){
		
		if(log.isDebugEnabled())
			log.debug("Read data - bytes remaining: "+in.remaining());
		
		
		// need flag to say last read channel not finished ?
		// if so we just use that channel
		
		Channel channel = session.getLastReadChannel();
		Packet packet;
		
		if(channel == null || channel.isFinishedRead()){
			byte headerByte = in.get();
			in.position(in.position()-1); // skip back 1 :)
			byte channelId = RTMPUtils.decodeChannelId(headerByte);
			channel = session.getChannel(channelId);
		}
		
		packet = channel.readPacket(in);
		session.setLastReadChannel(channel);
		
		if(packet !=null && packet.isSealed()){
			
			if(log.isDebugEnabled())
				log.debug("Packet on channel #"+channel.getId());
			
			sessionHandler.onPacket(packet);
		} else {
			log.debug("Not finished");
		}
		
	}

	public void handshake(Connection session, ByteBuffer in) {
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

	public void setStressMode(boolean stressMode) {
		this.stressMode = stressMode;
	}

	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionClosed(session);
		log.debug("Session closed");
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		super.sessionIdle(session, status);
		log.debug("Session Idle");
	}

	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionOpened(session);
		log.debug("Session Opened");
	}	
	
	

}