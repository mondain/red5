package org.red5.server.service;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
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
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

public class ServiceInvoker  {

	private static final Log log = LogFactory.getLog(ServiceInvoker.class);
	
	public static final String SERVICE_NAME = "serviceInvoker";
	
	protected ApplicationContext serviceContext = null;
	
	public void setServiceContext(ApplicationContext serviceContext){
		this.serviceContext = serviceContext;
	}
	
	public void invoke(Call call) {
		invoke(call, serviceContext);
	}
	
	public void invoke(Call call, ApplicationContext serviceContext) {
		
		
		
		String serviceName = call.getServiceName();
		String methodName = call.getServiceMethodName();
		log.debug("Service name " + serviceName);
		log.debug("Service method " + methodName);
		
		Object service = null;
	
		
		if(serviceContext.containsBean(serviceName)){
			service = serviceContext.getBean(serviceName);
		} 
		
		
		if(service == null) {
			call.setException(new ServiceNotFoundException(serviceName));
			call.setStatus(Call.STATUS_SERVICE_NOT_FOUND);
			log.warn("Service not found: "+serviceName);
		} else {
			log.debug("Service found: "+serviceName);
		}
		
		
		Object[] args = call.getArguments();
		Class[] typeArray = new Class[args.length];
		for(int i=0; i<typeArray.length; i++) {
			typeArray[i] = (args[i]==null) ? null : args[i].getClass();
		}
		
		
		List methods = ConversionUtils.findMethodsByNameAndNumParams(service,methodName, args.length);
	
		log.debug("Found "+methods.size()+" methods");
		
		Method method = (Method) methods.get(0);
		
		Object[] params=ConversionUtils.convertParams(args,method.getParameterTypes());
		
		//Method method = MethodUtils.getAccessibleMethod(service.getClass(), methodName, typeArray);
		if(method == null){
			log.debug("Count not find method :(");
		}
		
		
		Object result = null;
		
		try {
			result = method.invoke(service, params);
			//result = MethodUtils.invokeMethod(service, call.getServiceMethodName(), args);
			call.setResult(result);
			call.setStatus( result==null ? Call.STATUS_SUCCESS_NULL : Call.STATUS_SUCCESS_RESULT );
		} catch (IllegalAccessException accessEx){
			call.setException(accessEx);
			call.setStatus(Call.STATUS_ACCESS_DENIED);
			log.error(accessEx);
		} catch (InvocationTargetException invocationEx){
			call.setException(invocationEx);
			call.setStatus(Call.STATUS_INVOCATION_EXCEPTION);
			log.error(invocationEx);
		} catch (Exception ex){
			call.setException(ex);
			call.setStatus(Call.STATUS_GENERAL_EXCEPTION);
			log.error(ex);
		}

	
	}

}
