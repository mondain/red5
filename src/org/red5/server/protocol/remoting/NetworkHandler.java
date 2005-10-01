package org.red5.server.protocol.remoting;

import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.red5.server.protocol.http.BinaryRequestProtocolHandler;

public class NetworkHandler extends BinaryRequestProtocolHandler {

	RemotingService remotingService;
	
	public NetworkHandler(){
		super();
		remotingService = new RemotingService();
	}
	
	public ByteBuffer handleRequest(ByteBuffer in, String request, Map headers) {	
		ByteBuffer out = remotingService.handleRequest(in);
		headers.clear(); // clear the incomming headers
		headers.put("Content-type","application/amf");
		headers.put("Content-length",Integer.toString(out.limit()));
		return out;
	}

}
