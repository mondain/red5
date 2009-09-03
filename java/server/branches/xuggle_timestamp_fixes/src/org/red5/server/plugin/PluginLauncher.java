package org.red5.server.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.red5.server.Server;
import org.red5.server.api.plugin.IRed5Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates the plug-in environment and cleans up on shutdown.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class PluginLauncher implements ApplicationContextAware, InitializingBean, DisposableBean {

	// Initialize Logging
	protected static Logger log = LoggerFactory.getLogger(PluginLauncher.class);

	/**
	 * Spring application context
	 */
	private ApplicationContext applicationContext;

	public void afterPropertiesSet() throws Exception {

		ApplicationContext common = (ApplicationContext) applicationContext.getBean("red5.common");
		Server server = (Server) common.getBean("red5.server");

		//server should be up and running at this point so load any plug-ins now			

		//get the plugins dir
		File pluginsDir = new File(System.getProperty("red5.root"), "plugins");

		File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});
		
		if (plugins != null) {

			IRed5Plugin red5Plugin = null;

			for (File plugin : plugins) {
    			JarFile jar = new JarFile(plugin, false);
    			if (jar == null) {
    				continue;
    			}
    			Manifest manifest = jar.getManifest();
    			if (manifest == null) {
    				continue;
    			}
    			Attributes attributes = manifest.getMainAttributes();
    			if (attributes == null) {
    				continue;
    			}
    			String pluginMainClass = attributes.getValue("Red5-Plugin-Main-Class");
    			if (pluginMainClass == null || pluginMainClass.length() <= 0) {
    				continue;
    			}
    			// attempt to load the class; since it's in the lib directory this should work
    			ClassLoader loader = common.getClassLoader();
    			Class<?> pluginClass;
    			String pluginMainMethod=null;
    			try {
    				pluginClass = Class.forName(pluginMainClass, true, loader);
    			} catch (ClassNotFoundException e) {
    				continue;
    			}
    			try {
					//handle plug-ins without "main" methods
					pluginMainMethod = attributes.getValue("Red5-Plugin-Main-Method");
					if (pluginMainMethod == null || pluginMainMethod.length() <= 0) {
						//just get an instance of the class
						red5Plugin = (IRed5Plugin) pluginClass.newInstance();    				
					} else {
						Method method = pluginClass.getMethod(pluginMainMethod, (Class<?>[]) null);
						Object o = method.invoke(null, (Object[]) null);
						if (o != null && o instanceof IRed5Plugin) {
							red5Plugin = (IRed5Plugin) o;
						}
					}
					//register and start
					if (red5Plugin != null) {
						//set top-level context
						red5Plugin.setApplicationContext(applicationContext);
						//set server reference
						red5Plugin.setServer(server);
						//register the plug-in to make it available for lookups
						PluginRegistry.register(red5Plugin);
						//start the plugin
						red5Plugin.doStart();
					}
					log.info("Loaded plugin: {}", pluginMainClass);
				} catch (Exception e) {
					log.error("Error loading plugin: {}; Method: {}; Exception: {}",
							new Object[]{pluginMainClass, pluginMainMethod, e});
				}
    		}
		} else {
			log.info("Plugins directory cannot be accessed or doesnt exist");
		}

	}

	public void destroy() throws Exception {
		PluginRegistry.shutdown();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.debug("Setting application context");
		this.applicationContext = applicationContext;
	}

}
