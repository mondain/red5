package org.red5.server.rtmp.message;

public class HandshakeReply extends Packet {

	public HandshakeReply(){
		super(HANDSHAKE_SIZE * 2);
	}
	
}
