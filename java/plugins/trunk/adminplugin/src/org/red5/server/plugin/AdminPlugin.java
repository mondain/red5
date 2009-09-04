package org.red5.server.plugin;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2009 by respective authors (see below). All rights reserved.
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
 */


import org.red5.io.object.Serializer;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.Server;
import org.red5.server.api.plugin.IRed5Plugin;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.red5.server.Context;

import org.red5.webapps.admin.client.AuthClientRegistry;

/**
 * Provides FMS-style authentication features.
 * 
 * @author Paul Gregoire
 */
public class AdminPlugin implements IRed5Plugin {

	private static Logger log = Red5LoggerFactory.getLogger(AdminPlugin.class, "plugins");
	
	private static Serializer serializer = new Serializer();
	
	@SuppressWarnings("unused")
	private ApplicationContext context;
	
	@SuppressWarnings("unused")
	private Server server;
	
	public void doStart() throws Exception {
		log.debug("Start");
		Context ctx = (Context)this.context.getBean("web.context");
		ctx.setClientRegistry(new AuthClientRegistry());
	}

	public void doStop() throws Exception {
		log.debug("Stop");
	}


	public void setApplicationContext(ApplicationContext context) {
		log.debug("Set application context: {}", context);
		this.context = context;
	}


	public void setServer(Server server) {
		log.debug("Set server: {}", server);
		this.server = server;
	}

	
	public String getName() {
		return "adminPlugin";
	}
	
	public AdminHandler getHandler()
	{
		AdminHandler ah = null;
		try {
			ah = (AdminHandler) Class.forName("org.red5.server.plugin.AdminHandler").newInstance();
		} catch (Exception e) {
			log.error("AdminHandler could not be loaded", e);
		}
		return ah;	
	}
	
}