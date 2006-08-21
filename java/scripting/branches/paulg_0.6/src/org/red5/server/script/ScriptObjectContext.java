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

import java.io.IOException;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
	//ScriptEngine manager - Kidnapped from JDK6
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
		ScriptEngine jsEngine = mgr.getEngineByName("javascript");
		try {
			jsEngine.eval("print('Javascript - Hello, world!\\n')");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}
		
		//Ruby
		ScriptEngine rbEngine = mgr.getEngineByName("ruby");
		try {
			rbEngine.eval("puts 'Ruby - Hello, world!'");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		//Python
		ScriptEngine pyEngine = mgr.getEngineByName("python");
		try {
			pyEngine.eval("print \"Python - Hello, world!\"");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}		
		
		//Groovy
		ScriptEngine gvyEngine = mgr.getEngineByName("groovy");
		try {
			gvyEngine.eval("println  \"Groovy - Hello, world!\"");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}		
		
		List<ScriptEngineFactory> factories = mgr.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			System.out.println("ScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			List<String> engNames = factory.getNames();
			for (String name : engNames) {
				System.out.printf("\tEngine Alias: %s\n", name);
			}
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
		}
	}
	
	
}
