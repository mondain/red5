/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright 2006-2012 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.server.so;

import java.util.Set;

import org.red5.server.api.persistence.IPersistable;
import org.red5.server.api.persistence.IPersistenceStore;
import org.red5.server.api.persistence.PersistenceUtils;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectService;
import org.red5.server.persistence.RamPersistence;
import org.red5.server.scheduling.QuartzSchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Shared object service
 */
public class SharedObjectService implements ISharedObjectService, ApplicationContextAware, InitializingBean, DisposableBean {

	private Logger log = LoggerFactory.getLogger(SharedObjectService.class);

	/**
	 * Persistence store prefix
	 */
	private static final String SO_PERSISTENCE_STORE = IPersistable.TRANSIENT_PREFIX + "_SO_PERSISTENCE_STORE_";

	/**
	 * Transient store prefix
	 */
	private static final String SO_TRANSIENT_STORE = IPersistable.TRANSIENT_PREFIX + "_SO_TRANSIENT_STORE_";

	/**
	 * Service used to provide updates / notifications.
	 */
	private static QuartzSchedulingService schedulingService;

	/**
	 * Spring application context
	 */
	private ApplicationContext applicationContext;

	/** 
	 * Maximum messages to send at once
	 */
	public static int MAXIMUM_EVENTS_PER_UPDATE = 16;

	/**
	 * Persistence class name
	 */
	private String persistenceClassName = "org.red5.server.persistence.RamPersistence";

	/**
	 * Initialization section.
	 */
	public void afterPropertiesSet() throws Exception {
		SharedObjectService.schedulingService = (QuartzSchedulingService) applicationContext.getBean("schedulingService");
	}

	/**
	 * Pushes a job to the scheduler for single execution.
	 * 
	 * @param job
	 */
	public static void submitJob(IScheduledJob job) {
		schedulingService.addScheduledOnceJob(10, job);
	}

	/**
	 * Setter for Spring application context
	 * 
	 * @param applicationContext Application context
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param maximumEventsPerUpdate the maximumEventsPerUpdate to set
	 */
	public void setMaximumEventsPerUpdate(int maximumEventsPerUpdate) {
		MAXIMUM_EVENTS_PER_UPDATE = maximumEventsPerUpdate;
	}

	/**
	 * Setter for persistence class name.
	 * 
	 * @param name Setter for persistence class name
	 */
	public void setPersistenceClassName(String name) {
		persistenceClassName = name;
	}

	/**
	 * Return scope store
	 * 
	 * @param scope Scope
	 * @param persistent Persistent store or not?
	 * @return Scope's store
	 */
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
				log.info("Created persistence store {} for shared objects", store);
			} catch (Exception err) {
				log.warn("Could not create persistence store ({}) for shared objects, falling back to Ram persistence", persistenceClassName, err);
				store = new RamPersistence(scope);
			}
			scope.setAttribute(SO_PERSISTENCE_STORE, store);
			return store;
		}
		return (IPersistenceStore) scope.getAttribute(SO_PERSISTENCE_STORE);
	}

	/** {@inheritDoc} */
	public boolean createSharedObject(IScope scope, String name, boolean persistent) {
		boolean added = false;
		if (!hasSharedObject(scope, name)) {
			added = scope.addChildScope(new SharedObjectScope(scope, name, persistent, getStore(scope, persistent)));
			if (!added) {
				boolean soExists = hasSharedObject(scope, name);
				log.debug("Add failed on create, shared object already exists: {}", soExists);
				return soExists;
			}
		}
		// the shared object already exists
		log.trace("Shared object ({}) already exists. Persistent: {}", name, persistent);
		return true;
	}

	/** {@inheritDoc} */
	public ISharedObject getSharedObject(IScope scope, String name) {
		return (ISharedObject) scope.getBasicScope(ScopeType.SHARED_OBJECT, name);
	}

	/** {@inheritDoc} */
	public ISharedObject getSharedObject(IScope scope, String name, boolean persistent) {
		if (!hasSharedObject(scope, name)) {
			createSharedObject(scope, name, persistent);
		}
		return getSharedObject(scope, name);
	}

	/** {@inheritDoc} */
	public Set<String> getSharedObjectNames(IScope scope) {
		return scope.getBasicScopeNames(ScopeType.SHARED_OBJECT);
	}

	/** {@inheritDoc} */
	public boolean hasSharedObject(IScope scope, String name) {
		return scope.hasChildScope(ScopeType.SHARED_OBJECT, name);
	}

	/** {@inheritDoc} */
	public boolean clearSharedObjects(IScope scope, String name) {
		boolean result = false;
		if (hasSharedObject(scope, name)) {
			// '/' clears all local and persistent shared objects associated with the instance
			// /foo/bar clears the shared object /foo/bar; if bar is a directory name, no shared objects are deleted.
			// /foo/bar/* clears all shared objects stored under the instance directory /foo/bar. 
			// The bar directory is also deleted if no persistent shared objects are in use within this namespace.
			// /foo/bar/XX?? clears all shared objects that begin with XX, followed by any two characters. If a directory name matches
			// this specification, all the shared objects within this directory are cleared.
			result = ((ISharedObject) scope.getBasicScope(ScopeType.SHARED_OBJECT, name)).clear();
		}
		return result;
	}

	public void destroy() throws Exception {
	}

}
