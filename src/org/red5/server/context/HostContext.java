package org.red5.server.context;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

public class HostContext 
	extends FileSystemXmlApplicationContext
	implements ApplicationContextAware {
	
	public static final String HOST_CONFIG = "host.xml";
	public static final String APP_DIR = "apps";
	protected String hostPath;
	protected String hostname;
	protected GlobalContext global;
		
	protected static Log log =
        LogFactory.getLog(HostContext.class.getName());
	
	public HostContext(GlobalContext global, String hostname, String hostPath) throws BeansException {
		super(new String[]{hostPath + "/" + HOST_CONFIG}, global);
		this.global = global;
		this.hostname = hostname;
		this.hostPath = hostPath;
		loadApps();
	}

	public void setApplicationContext(ApplicationContext parent) throws BeansException {
		this.setParent(parent);
	}
	
	protected void loadApps(){
		// this.getResources()
		if(log.isDebugEnabled()) {
			log.debug("Loading apps");
		}
		try {
			Resource[] apps = getResources(hostPath +"/"+ APP_DIR + "/*");
			if(apps!=null){
				for(int i=0; i<apps.length; i++){
					Resource app = apps[i];
					String appName = app.getFile().getName();
					if(log.isDebugEnabled()) {
						log.debug("appName: "+appName);
					}
					if(!appName.startsWith(".")){
						addApp(appName);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addApp(String appName){
		String appPath = hostPath +"/"+ APP_DIR +"/"+appName;
		AppContext appContext = new AppContext(this, appName,  appPath);
		this.getBeanFactory().registerSingleton(appName, appContext);
	}
	
	public boolean hasAppContext(String appName){
		return containsBean(appName);
	}
	
	public AppContext getAppContext(String appName){
		return (AppContext) getBean(appName);
	}
	
}
