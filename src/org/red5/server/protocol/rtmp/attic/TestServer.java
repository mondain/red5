package org.red5.server.protocol.rtmp.attic;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors (see below). All rights reserved.
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
import org.apache.mina.common.TransportType;
import org.apache.mina.registry.Service;
import org.apache.mina.registry.ServiceRegistry;
import org.apache.mina.registry.SimpleServiceRegistry;
import org.red5.server.protocol.rtmp.NetworkHandler;

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
