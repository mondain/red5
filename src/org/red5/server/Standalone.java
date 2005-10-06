package org.red5.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (Dominick@gmail.com)
 */

public class Standalone {
	
	protected static Log log =
        LogFactory.getLog(Standalone.class.getName());
	
	protected static String red5ConfigPath = "./conf/red5.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length == 1) {
			red5ConfigPath = args[0];
		}
		
		if(log.isInfoEnabled()){ 
			log.info("RED5 Server (http://www.osflash.org/red5)");
			log.info("Loading Spring Application Context: "+red5ConfigPath);
		}
		
		FileSystemXmlApplicationContext appCtx = new FileSystemXmlApplicationContext(red5ConfigPath);

	}

}
