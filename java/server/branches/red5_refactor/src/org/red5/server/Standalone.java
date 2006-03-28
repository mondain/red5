package org.red5.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors (see below). All rights reserved.
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
 * @author Dominick Accattato (Dominick@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

/**
 * Entry point from which the server config file is loaded
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (Dominick@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public class Standalone {
	
	// Initialize Logging
	protected static Log log =
        LogFactory.getLog(Standalone.class.getName());
	
	protected static String red5ConfigPath = "./conf/red5.xml";
	
	/**
	 * Main entry point for the Red5 Server 
	 * usage java Standalone
	 * @param args String passed in that points to a red5.xml config file
	 * @return void
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length == 1) {
			red5ConfigPath = args[0];
		}
		
		if(log.isInfoEnabled()){ 
			log.info("RED5 Server (http://www.osflash.org/red5)");
			log.info("Loading Spring Application Context: "+red5ConfigPath);
		}
		
		// Spring Loads the xml config file which initializes 
		// beans and loads the server
		long time = System.currentTimeMillis();
		FileSystemXmlApplicationContext appCtx = new FileSystemXmlApplicationContext(red5ConfigPath);
		if(log.isDebugEnabled()) {
			long startupIn = System.currentTimeMillis() - time;
			log.debug("Startup in: "+startupIn+" ms");
		}

	}

}
