/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
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
 */
package org.red5.server.script;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;


/**
 * Script object model
 * 
 * @author Luke Hubbard <luke@codegent.com>
 * @author Paul Gregoire <mondain@gmail.com>
 */
public class ScriptObjectContext implements ApplicationContextAware, ResourceLoader, ResourcePatternResolver {

	protected static Log log = LogFactory.getLog(ScriptObjectContext.class.getName());
	//Configurations for the scripting context
	protected String config = "classpath:/scripting.xml";	
	//
	private ApplicationContext parentContext;
	private ApplicationContext appCtx;
	//ScriptEngine manager
	private static ScriptEngineManager mgr = new ScriptEngineManager();
	
	public void init() {
		log.info("Loading scripting");
		//load up our configs
		try {
			getApplicationContext().getResource(config).getInputStream();
		} catch (Exception e) {
			log.error("Error loading scripting configuration", e);
		}
		
	}	
	
	public void setParentContext(ApplicationContext parentContext) {
		this.parentContext = parentContext;
	}
	
	public ListableBeanFactory getBeans(){
		return appCtx;
	}
	
	public ApplicationContext getApplicationContext(){
		return appCtx;
	}
	
	public void setApplicationContext(ApplicationContext appCtx){
		this.appCtx = appCtx;
	}	
	
	/*
	public ListableBeanFactory getScripts(){
		return getScriptBeanFactory();
	}
	
	public ListableBeanFactory getScriptBeanFactory(){
		return appCtx;
	}*/
	
	public MessageSource getMessageSource(){
		return appCtx;
	}
	
	public Resource getResource(String path) {
		return appCtx.getResource(path);
	}
	
	public Resource[] getResources(String pattern) throws IOException {
		return appCtx.getResources(pattern);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Javascript
		ScriptEngine jsEngine = mgr.getEngineByName("rhino");
		try {
			System.out.println("Engine: " + jsEngine.getClass().getName());
			jsEngine.eval(new FileReader("D:/tmp/red5/java/scripting/branches/paulg_0.6/samples/E4X/e4x_example.js"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	

	}
	
	
}
