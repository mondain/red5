package org.red5.server.service;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.protocol.remoting.RemotingService;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.context.ApplicationContext;

import com.sun.tools.example.debug.bdi.MethodNotFoundException;

public class ServiceInvoker {

	private static final Log log = LogFactory.getLog(RemotingService.class);
	
	protected ApplicationContext serviceContext = null;
	
	public void setServiceContext(ApplicationContext serviceContext){
		this.serviceContext = serviceContext;
	}
	
	public void invoke(Call call) {
		
		
		
		String serviceName = call.getServiceName();
		
		log.debug("Service name " + serviceName);
		log.debug("Service method " + call.getServiceMethodName());
		
		Object service = null;
	
		
		if(serviceContext.containsBean(serviceName)){
			service = serviceContext.getBean(serviceName);
		} 
		
		if(service == null) {
			call.setException(new ServiceNotFoundException(serviceName));
			call.setStatus(Call.STATUS_SERVICE_NOT_FOUND);
		}
		
		ArgumentConvertingMethodInvoker methodInvoker = new ArgumentConvertingMethodInvoker();
		
		methodInvoker.setTargetObject(service);
		
		boolean foundMethod = false;
		Exception exception = null; 
		
		// First lets try the params we have
		try{
			methodInvoker.setArguments(call.getArguments());
			methodInvoker.prepare();
		
		} catch (Exception ex){
			exception = ex;
		} 
		
		if(!foundMethod){
			// look for a method which takes an array as the only param
			methodInvoker.setArguments(new Object[]{call.getArguments()});
			try{
				methodInvoker.prepare();
				foundMethod = true;
			} catch (Exception ex){
				exception = ex;
			}
		}
		
		if(foundMethod){
		
			try{
				Object result = methodInvoker.invoke();
				call.setResult(result);
				call.setStatus( result==null ? Call.STATUS_SUCCESS_NULL : Call.STATUS_SUCCESS_RESULT );
			} catch (IllegalAccessException accessEx){
				call.setException(accessEx);
				call.setStatus(Call.STATUS_ACCESS_DENIED);
			} catch (InvocationTargetException invocationEx){
				call.setException(invocationEx);
				call.setStatus(Call.STATUS_INVOCATION_EXCEPTION);
			} catch (Exception ex){
				call.setException(ex);
				call.setStatus(Call.STATUS_GENERAL_EXCEPTION);
			}
	
		} else {
			
			if(exception instanceof MethodNotFoundException){
				call.setException(exception);
				call.setStatus(Call.STATUS_METHOD_NOT_FOUND);
			} else {
				call.setException(exception);
				call.setStatus(Call.STATUS_GENERAL_EXCEPTION);
			}
			
		}
	
	}

}
