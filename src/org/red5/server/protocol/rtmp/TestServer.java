package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.TransportType;
import org.apache.mina.registry.Service;
import org.apache.mina.registry.ServiceRegistry;
import org.apache.mina.registry.SimpleServiceRegistry;

public class TestServer {

	private static int PORT = 1935; // default port

	protected static Log log =
        LogFactory.getLog(NetworkHandler.class.getName());
	
	public static void main(String[] args) throws Exception 
	{
		for (int i=0; i< args.length; i++)
		{
			if (args[0].startsWith("-p"))
			{
				String [] portArgs = args[0].split(" ");
				if (portArgs.length >1)
					if (portArgs[1] != null)
					{
						PORT= Integer.parseInt(portArgs[1]);
						log.info("Using command line set port of " + PORT);
					}
			}
		}
		
		ServiceRegistry registry = new SimpleServiceRegistry();
		
		// Bind
		
		try
		{
			Service service = new Service("rtmp", TransportType.SOCKET, PORT);
			registry.bind(service, new NetworkHandler());
			log.info("RED5 Listening on port " + PORT);
		}
		catch (Exception e)
		{
			log.fatal("Could not start RED5 server - " + e.toString());
		}
		catch (Throwable t)
		{
			log.fatal("Could not start RED5 server - " + t.toString());
		}
	}

}
