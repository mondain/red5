package org.red5.server.api.test;

import org.red5.server.core.BaseConnection;

public class TestConnection extends BaseConnection {

	public TestConnection(String host, String path, String sessionId){
		super(PERSISTENT,host,path,sessionId,null);
	}
	
}
