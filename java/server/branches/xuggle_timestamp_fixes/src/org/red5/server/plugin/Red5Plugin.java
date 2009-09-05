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

import org.red5.server.Server;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.plugin.IRed5Plugin;
import org.springframework.context.ApplicationContext;

/**
 * Provides more features to the plug-in system.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public abstract class Red5Plugin implements IRed5Plugin {

	protected ApplicationContext context;
	
	protected Server server;
	
	@Override
	public void doStart() throws Exception {
	}
	
	@Override
	public void doStop() throws Exception {
	}

	/**
	 * Initialize the plug-in
	 */
	public void init() {
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Return the server reference.
	 * 
	 * @return
	 */
	public Server getServer() {
		return server;
	}
	
	@Override
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Set the application making use of this plug-in.
	 * 
	 * @param application
	 */
	public void setApplication(MultiThreadedApplicationAdapter application) {	
	}
	
}
