package org.red5.server;

import java.io.IOException;

import org.red5.server.api.IClientRegistry;
import org.red5.server.api.IContext;
import org.red5.server.api.IMappingStrategy;
import org.red5.server.api.IScope;
import org.red5.server.api.IScopeHandler;
import org.red5.server.api.IScopeResolver;
import org.red5.server.api.service.IServiceInvoker;
import org.red5.server.exception.ScopeHandlerNotFoundException;
import org.red5.server.service.ServiceNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class Context implements IContext, ApplicationContextAware {
	
	private ApplicationContext applicationContext; 
	private String contextPath = "";
	
	private IScopeResolver scopeResolver;
	private IClientRegistry clientRegistry;
	private IServiceInvoker serviceInvoker;
	private IMappingStrategy mappingStrategy;
	
	public Context(){
		
	}
	
	public Context(ApplicationContext context, String contextPath){
		this.applicationContext = context;
		this.contextPath = contextPath;
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
		Object bean = applicationContext.getBean(serviceName);
		if(bean != null ) return bean;
		else throw new ServiceNotFoundException(serviceName);
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
	
	public IScope getRootScope() {
		return scopeResolver.resolveScope(null,null);
	}

	public IScope resolveScope(String host, String path) {
		return scopeResolver.resolveScope(host,path);
	}

	
	
}