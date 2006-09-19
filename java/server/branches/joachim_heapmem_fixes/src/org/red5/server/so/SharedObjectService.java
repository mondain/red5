package org.red5.server.so;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
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

import static org.red5.server.api.so.ISharedObject.TYPE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.api.IBasicScope;
import org.red5.server.api.IScope;
import org.red5.server.api.persistence.IPersistable;
import org.red5.server.api.persistence.IPersistenceStore;
import org.red5.server.api.persistence.PersistenceUtils;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectService;
import org.red5.server.persistence.RamPersistence;

public class SharedObjectService
	implements ISharedObjectService {

	private Log log = LogFactory.getLog(SharedObjectService.class.getName());

	private static final String SO_PERSISTENCE_STORE = IPersistable.TRANSIENT_PREFIX + "_SO_PERSISTENCE_STORE_";
	private static final String SO_TRANSIENT_STORE = IPersistable.TRANSIENT_PREFIX + "_SO_TRANSIENT_STORE_";
	private String persistenceClassName = "org.red5.server.persistence.RamPersistence";
	
	public void setPersistenceClassName(String name) {
		persistenceClassName = name;
	}
	
	private IPersistenceStore getStore(IScope scope, boolean persistent) {
		IPersistenceStore store;
		if (!persistent) {
			// Use special store for non-persistent shared objects
			if (!scope.hasAttribute(SO_TRANSIENT_STORE)) {
				store = new RamPersistence(scope);
				scope.setAttribute(SO_TRANSIENT_STORE, store);
				return store;
			}
			
			return (IPersistenceStore) scope.getAttribute(SO_TRANSIENT_STORE);
		}
		
		// Evaluate configuration for persistent shared objects
		if (!scope.hasAttribute(SO_PERSISTENCE_STORE)) {
			try {
				store = PersistenceUtils.getPersistenceStore(scope, persistenceClassName);
				log.info("Created persistence store " + store + " for shared objects.");
			} catch (Exception err) {
				log.error("Could not create persistence store for shared objects, falling back to Ram persistence.", err);
				store = new RamPersistence(scope);
			}
			scope.setAttribute(SO_PERSISTENCE_STORE, store);
			return store;
		}
		
		return (IPersistenceStore) scope.getAttribute(SO_PERSISTENCE_STORE);
	}
	
	public boolean createSharedObject(IScope scope, String name, boolean persistent) {
		synchronized (scope) {
			if (hasSharedObject(scope, name))
				// The shared object already exists.
				return true;
				
			final IBasicScope soScope = new SharedObjectScope(scope, name, persistent, getStore(scope, persistent));
			return scope.addChildScope(soScope);
		}
	}

	public ISharedObject getSharedObject(IScope scope, String name) {
		return (ISharedObject) scope.getBasicScope(TYPE,name);
	}

	public ISharedObject getSharedObject(IScope scope, String name, boolean persistent) {
		synchronized (scope) {
			if (!hasSharedObject(scope, name))
				createSharedObject(scope, name, persistent);
		}
		return getSharedObject(scope, name);
	}

	public Set<String> getSharedObjectNames(IScope scope) {
		Set<String> result = new HashSet<String>();
		Iterator<String> iter = scope.getBasicScopeNames(TYPE);
		while (iter.hasNext())
			result.add(iter.next());
		return result;
	}

	public boolean hasSharedObject(IScope scope, String name) {
		return scope.hasChildScope(TYPE,name);
	}
	
}