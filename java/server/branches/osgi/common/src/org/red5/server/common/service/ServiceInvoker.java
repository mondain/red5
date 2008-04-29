package org.red5.server.common.service;

import java.util.Map;

/**
 * A service invoker that invokes a service in a service registry.
 * 
 * @author Steven Gong (steven.gong@gmail.com)
 */
public interface ServiceInvoker {
	/**
	 * Invoke a service synchronously.
	 * The service matching algorithm will use the service call object
	 * to best match the service object in registry.
	 * 
	 * @param registry
	 * @param call
	 * @return
	 * @throws ServiceNotFoundException
	 * @throws ServiceInvocationException
	 */
	Object syncInvoke(ServiceRegistry registry, ServiceCall call)
	throws ServiceNotFoundException, ServiceInvocationException;
	
	/**
	 * Invoke a service asynchronously.
	 * This method is just like the syncInvoke except it will return immediately.
	 * 
	 * @param registry
	 * @param call
	 * @param callback
	 * @return
	 * @throws ServiceNotFoundException
	 */
	Object asyncInvoke(ServiceRegistry registry, ServiceCall call, ServiceCallback callback)
	throws ServiceNotFoundException;
}