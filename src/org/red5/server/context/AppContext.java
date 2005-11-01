package org.red5.server.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.service.ServiceInvoker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class AppContext 
	extends GenericRed5Context {
	
	public static final String APP_CONFIG = "app.xml";
	public static final String APP_SERVICE_NAME = "appService";
	
	protected String appPath;
	protected String appName;
	protected HostContext host;
		
	protected static Log log =
        LogFactory.getLog(AppContext.class.getName());
	
	public AppContext(HostContext host, String appName, String appPath) throws BeansException {
		super(host, appPath, appPath + "/" + APP_CONFIG ); 
		this.appName = appName;
		this.appPath = appPath;
		if(!this.containsBean(APP_SERVICE_NAME)){
			BaseApplication app = new BaseApplication();
			this.getBeanFactory().registerSingleton(APP_SERVICE_NAME, app);
			app.initialize();
		}
	}
	
	public ServiceInvoker getServiceInvoker(){
		return (ServiceInvoker) getBean(ServiceInvoker.SERVICE_NAME);
	}
	
	
}
