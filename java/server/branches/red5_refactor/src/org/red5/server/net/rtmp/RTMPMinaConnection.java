package org.red5.server.net.rtmp;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.red5.server.net.rtmp.message.OutPacket;

public class RTMPMinaConnection extends RTMPConnection {

	protected static Log log =
        LogFactory.getLog(RTMPMinaConnection.class.getName());

	private IoSession ioSession;
	
	public RTMPMinaConnection(IoSession protocolSession) {
		super(PERSISTENT);
		this.ioSession = protocolSession;
	}
		
	public IoSession getIoSession() {
		return ioSession;
	}

	/*
	public void dispatchEvent(Object packet){
		ioSession.write(packet);
	}
	*/
	
	@Override
	public void rawWrite(ByteBuffer out) {
		ioSession.write(out);
	}

	@Override
	public void write(OutPacket out) {
		ioSession.write(out);
	}

	public boolean isConnected() {
		return this.ioSession.isConnected();
	}
}
