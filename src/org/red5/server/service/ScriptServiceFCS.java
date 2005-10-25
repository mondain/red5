package org.red5.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ScriptServiceFCS implements ApplicationContextAware {

		protected static Log log = LogFactory.getLog(DemoService.class.getName());
		
		protected ApplicationContext appCtx = null;
		
		public ScriptServiceFCS(){
			
		}

		public void startUp() {
			log.debug("starting the deom service...");
		}

		public void setApplicationContext(ApplicationContext context) throws BeansException {
			appCtx = context;
		}
}
