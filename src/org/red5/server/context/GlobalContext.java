package org.red5.server.context;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

public class GlobalContext 
	extends FileSystemXmlApplicationContext
	implements ApplicationContextAware {
	
	public static final String DEFAULT_HOST = "__default__";
	public String hostsPath = "hosts";
	
	protected static Log log =
        LogFactory.getLog(GlobalContext.class.getName());
	
	public GlobalContext(String configPath) throws BeansException {
		super(configPath);
	}

	public void setApplicationContext(ApplicationContext parent) throws BeansException {
		this.setParent(parent);
	}

	public void initialize(){
		log.debug("Initialize global context");
		loadHosts();
	}
	
	protected void loadHosts(){
		// this.getResources()
		log.debug("Loading hosts");
		try {
			Resource[] hosts = getResources(hostsPath + "/*");
			if(hosts!=null){
				for(int i=0; i<hosts.length; i++){
					Resource host = hosts[i];
					String hostname = host.getFile().getName();
					log.debug("hostname: "+hostname);
					if(!hostname.startsWith(".")){
						addHost(hostname);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addHost(String hostname){
		log.debug("Add host: "+hostname);
		String hostPath = hostsPath + "/"+ hostname;
		HostContext hostContext = new HostContext(this, hostname, hostPath);
		this.getBeanFactory().registerSingleton(hostname,hostContext);
	}
	
	public HostContext getDefaultHost(){
		return getHostContext(DEFAULT_HOST);
	}
	
	public boolean hasHostContext(String hostname){
		return containsBean(hostname);
	}
	
	public HostContext getHostContext(String hostname){
		return (HostContext) getBean(hostname);
	}
	
}
