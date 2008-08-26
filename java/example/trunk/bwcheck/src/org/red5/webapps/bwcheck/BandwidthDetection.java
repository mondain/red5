package org.red5.webapps.bwcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IStreamCapableConnection;

import java.util.*;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author The Red5 Project (red5@osflash.org)
 * @author Dan Rossi
 */
public class BandwidthDetection  {
	

	
	protected static Logger log = LoggerFactory.getLogger(BandwidthDetection.class.getName());
	
	public BandwidthDetection()
	{

	}
	

	public Map<String,Object> onClientBWCheck(Object[] params) {
		ClientServerDetection clientServer = new ClientServerDetection();
		return clientServer.onClientBWCheck(params);
    }
	
	public void onServerClientBWCheck() {
		IConnection conn = Red5.getConnectionLocal();
		ServerClientDetection serverClient = new ServerClientDetection();
		serverClient.checkBandwidth(conn);
	}
	
	
	
}
