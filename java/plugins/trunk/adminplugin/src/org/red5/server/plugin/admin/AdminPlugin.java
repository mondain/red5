package org.red5.server.plugin.admin;

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

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.Context;
import org.red5.server.plugin.Red5Plugin;
import org.red5.server.plugin.admin.client.AuthClientRegistry;
import org.slf4j.Logger;

/**
 * Admin for Red5
 * 
 * @author Paul Gregoire
 * @author Dan Rossi
 */
public class AdminPlugin extends Red5Plugin {

	private static Logger log = Red5LoggerFactory.getLogger(AdminPlugin.class, "admin");

	@Override
	public void doStart() throws Exception {
		super.doStart();
	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
	}

	@Override
	public String getName() {
		return "adminPlugin";
	}

	@Override
	public void init() {
		log.debug("Initializing");
		super.init();
		Context ctx = (Context) context.getBean("web.context");
		ctx.setClientRegistry(new AuthClientRegistry());
	}

	public AdminHandler getHandler() {
		AdminHandler ah = null;
		try {
			ah = (AdminHandler) Class.forName("org.red5.server.plugin.admin.AdminHandler").newInstance();
		} catch (Exception e) {
			log.error("AdminHandler could not be loaded", e);
		}
		ah.setContext(this.context);
		return ah;
	}

}
