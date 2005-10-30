package org.red5.server.stream;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class StreamManager implements ApplicationContextAware {

	private ApplicationContext appCtx = null;
	private String streamDir = "streams";

	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		this.appCtx = appCtx;
	}

}
