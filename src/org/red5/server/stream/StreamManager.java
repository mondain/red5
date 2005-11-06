package org.red5.server.stream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.red5.server.io.flv2.FLVReader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class StreamManager implements ApplicationContextAware {

	private ApplicationContext appCtx = null;
	private String streamDir = "streams";
	private HashMap published = new HashMap();

	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		this.appCtx = appCtx;
	}
	
	public IStream lookupStream(String name){
		if(published.containsKey(name)) 
			return (IStream) published.get(name);
		return null;
	}

	protected void fileStream(String name){
		Resource[] resource = null;
		try {
			File flv = appCtx.getResource("streams/" + name).getFile();
			FLVReader reader = new FLVReader(flv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
