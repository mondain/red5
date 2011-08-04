package org.red5.server.security.realm;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 *
 * Copyright (c) 2006-2011 by respective authors (see below). All rights reserved.
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

import java.security.Principal;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.RealmBase;

/**
 * Simple implementation of a realm that does nothing but take up space.
 * 
 * @author Paul Gregoire
 */
public class NoRealm extends RealmBase {

	/**
	 * Descriptive information about this Realm implementation.
	 */
	protected final String info = "org.red5.server.security.realm.NoRealm/1.0";

	/**
	 * Descriptive information about this Realm implementation.
	 */
	protected static final String name = "NoRealm";

	/**
	 * Return descriptive information about this Realm implementation and
	 * the corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 *
	 * @param username Username of the Principal to look up
	 * @param credentials Password or other credentials to use in
	 *  authenticating this username
	 */
	public Principal authenticate(String username, String credentials) {
		return null;
	}

	/**
	 * Return a short name for this Realm implementation.
	 */
	protected String getName() {
		return NoRealm.name;
	}

	/**
	 * Return the password associated with the given principal's user name.
	 */
	protected String getPassword(String username) {
		return null;
	}

	/**
	 * Return the Principal associated with the given user name.
	 */
	protected Principal getPrincipal(String username) {
		return null;
	}

	/**
	 * Prepare for active use of the public methods of this Component.
	 *
	 * @exception LifecycleException if this component detects a fatal error
	 *  that prevents it from being started
	 */
	public synchronized void start() throws LifecycleException {
		super.start();
	}

	/**
	 * Gracefully shut down active use of the public methods of this Component.
	 *
	 * @exception LifecycleException if this component detects a fatal error
	 *  that needs to be reported
	 */
	public synchronized void stop() throws LifecycleException {
		super.stop();
	}

}
