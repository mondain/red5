package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SessionConfig;
import org.apache.mina.io.IoHandlerAdapter;
import org.apache.mina.io.IoSession;
import org.apache.mina.io.socket.SocketSessionConfig;
import org.red5.server.utils.HexDump;

public class ProtocolHandler extends IoHandlerAdapter {

	protected static Log log =
        LogFactory.getLog(ProtocolHandler.class.getName());

	public void sessionCreated(IoSession session) {
		SessionConfig cfg = session.getConfig();
		if (cfg instanceof SocketSessionConfig) {
			((SocketSessionConfig) cfg).setSessionReceiveBufferSize(2048);
		}
	}

	public void exceptionCaught(IoSession session, Throwable cause) {
		session.close();
	}

	public void dataRead(IoSession ioSession, ByteBuffer in) {

		Session session = (Session) ioSession.getAttachment();
		if (session == null) {
			session = new Session(ioSession, this);
			ioSession.setAttachment(session);
		}

		switch (session.getState()) {

		case Session.STATE_CONNECT:
			handshake(session, in);
			session.handshake();
			break;

		case Session.STATE_HANDSHAKE:
			// skip the first 1536 as thdese are the as server handshake
			in.skip(1536);
			session.connected();
			// fall through..

		case Session.STATE_CONNECTED:
			while(in.remaining()>0){
				log.debug("Remaining: "+in.remaining());
				Packet packet = readPacket(session, in);
				if(packet.full){
					session.onPacket(packet);
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
		//logBuffer("<< Server handshake", out);
		session.getIoSession().write(out, null);

	}

	public Packet readPacket(Session session, ByteBuffer in) {
		
		log.debug("Read packet");
		
		byte header = in.get();
		
		Channel channel; 
		
		byte kont = 0xC3 - 256;
		
		log.debug("Header byte: "+HexDump.toHexString(header));
		
		if(header == kont){
			
			log.debug("Continue same channel");
			channel = session.getLastChannel();
			
		} else {
			
			log.debug("New headers");
			
			byte headerSize = readHeaderSize(header);
			byte channelId = readChannelId(header);
			
			log.debug("Channel Id: "+channelId);
			
			channel = session.getChannel(channelId);
			if (headerSize < 3) // 3 bytes
				channel.timer = readMediumInt(in);
			if (headerSize <= 2) // 3 bytes
				channel.size = readMediumInt(in);
			if (headerSize <= 1) // 1 byte
				channel.dataType = in.get();
			if (headerSize == 0) // 4 bytes
				in.get(channel.source);
			
			if(log.isDebugEnabled()){
				log.debug("Header size: " + headerSize);
				log.debug(channel);
			}
		
		}
		
		Packet packet = channel.getPacket();
		int maxChunkSize = (packet.dataType == Packet.TYPE_AUDIO) ? 64 : 128;
		
		log.debug("Put chunk max size: "+maxChunkSize);
		packet.putChunk(in, maxChunkSize);
		
		// this basicaly resets the packet in the channel
		if(packet.full) channel.clearPacket();
		
		return packet;
	}

	public int readMediumInt(ByteBuffer in) {
		byte[] bytes = new byte[3];
		in.get(bytes);
		int val = 0;
		val += bytes[0] * 256 * 256;
		val += bytes[1] * 256;
		val += bytes[2];
		if (val < 0)
			val += 256;
		return val;
	}

	public byte readHeaderSize(byte header) {
		return (byte) (header >> 2);
	}

	public byte readChannelId(byte header) {
		return (byte) ((header << 2) >> 2);
	}
	
	public void writePacket(Session session, Packet packet){
		// write correct header
		// write data in chunks (if needed)
	}

	public void logBuffer(String msg, ByteBuffer buf) {
		if(log.isDebugEnabled()){
			log.debug(msg);
			log.debug("Size: " + buf.remaining());
			log.debug(HexDump.formatHexDump(buf.getHexDump()));
		}
	}

}