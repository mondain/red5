package org.red5.server;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.red5.server.api.IClientRegistry;
import org.red5.server.api.IContext;
import org.red5.server.api.IMappingStrategy;
import org.red5.server.api.IScope;
import org.red5.server.api.IScopeHandler;
import org.red5.server.api.IScopeResolver;
import org.red5.server.api.persistence.IPersistenceStore;
import org.red5.server.api.service.IServiceInvoker;
import org.red5.server.exception.ScopeHandlerNotFoundException;
import org.red5.server.service.ServiceNotFoundException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.core.io.Resource;

public class Context implements IContext, ApplicationContextAware {
	
	private ApplicationContext applicationContext; 
	private BeanFactory coreContext;
	private String contextPath = "";
	
	private IScopeResolver scopeResolver;
	private IClientRegistry clientRegistry;
	private IServiceInvoker serviceInvoker;
	private IMappingStrategy mappingStrategy;
	private IPersistenceStore persistanceStore;
	private Map<String, Object> beanCache = new HashMap<String, Object>();
	
	public Context(){
		coreContext = ContextSingletonBeanFactoryLocator
			.getInstance("red5.xml").useBeanFactory("red5.core").getFactory();
	}
	
	public Context(ApplicationContext context, String contextPath){
		this.applicationContext = context;
		this.contextPath = contextPath;
	}
	
	public IScope getGlobalScope(){
		return scopeResolver.getGlobalScope();
	}
	
	public IScope resolveScope(String path) {
		return scopeResolver.resolveScope(path);
	}

	public void setClientRegistry(IClientRegistry clientRegistry) {
		this.clientRegistry = clientRegistry;
	}

	public void setMappingStrategy(IMappingStrategy mappingStrategy) {
		this.mappingStrategy = mappingStrategy;
	}

	public void setScopeResolver(IScopeResolver scopeResolver) {
		this.scopeResolver = scopeResolver;
	}

	public void setServiceInvoker(IServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}

	public IPersistenceStore getPersistanceStore() {
		return persistanceStore; 
	}

	public void setPersistanceStore(IPersistenceStore persistanceStore) {
		this.persistanceStore = persistanceStore;
	}

	public void setApplicationContext(ApplicationContext context) {
		this.applicationContext = context;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public void setContextPath(String contextPath){
		if(!contextPath.endsWith("/")) contextPath += "/";
		this.contextPath = contextPath;
	}

	public IClientRegistry getClientRegistry() {
		return clientRegistry;
	}

	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceInvoker getServiceInvoker() {
		return serviceInvoker;
	}

	public Object lookupService(String serviceName) {
		serviceName = getMappingStrategy().mapServiceName(serviceName);
		try {
			Object bean = applicationContext.getBean(serviceName);
			if(bean != null ) return bean;
			else throw new ServiceNotFoundException(serviceName);
		} catch (NoSuchBeanDefinitionException err) {
			throw new ServiceNotFoundException(serviceName);
		}
	}

	/*
	public IScopeResolver getScopeResolver() {
		return scopeResolver;
	}
	*/

	public IScopeHandler lookupScopeHandler(String contextPath) {
		String scopeHandlerName = getMappingStrategy().mapScopeHandlerName(contextPath); 
		Object bean = applicationContext.getBean(scopeHandlerName);
		if(bean != null && bean instanceof IScopeHandler){
			return (IScopeHandler) bean;
		} else throw new ScopeHandlerNotFoundException(scopeHandlerName);
	}

	public IMappingStrategy getMappingStrategy() {
		return mappingStrategy;
	}

	public Resource[] getResources(String pattern) throws IOException {
		return applicationContext.getResources(contextPath + pattern);
	}

	public Resource getResource(String path) {
		return applicationContext.getResource(contextPath + path);
	}

	public IScope resolveScope(String host, String path) {
		return scopeResolver.resolveScope(path);
	}

	public Object getBean(String beanId) {
		if (beanCache.containsKey(beanId))
			return beanCache.get(beanId);
		
		Object bean = applicationContext.getBean(beanId);
		beanCache.put(beanId, bean);
		return bean;
	}

	public Object getCoreService(String beanId) {
		return coreContext.getBean(beanId);
	}
	
}