package org.red5.server.winstone;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 *
 * Copyright (c) 2006-2009 by respective authors (see below). All rights reserved.
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

import java.io.File;
import java.io.FilenameFilter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.ContextLoader;
import org.red5.server.LoaderBase;
import org.red5.server.api.IApplicationContext;
import org.red5.server.jmx.JMXAgent;
import org.red5.server.jmx.JMXFactory;
import org.red5.server.jmx.mxbeans.ContextLoaderMXBean;
import org.red5.server.jmx.mxbeans.LoaderMXBean;
import org.red5.server.util.FileUtil;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import winstone.Launcher;

/**
 * Red5 loader for the Winstone servlet container.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class WinstoneLoader extends LoaderBase implements ApplicationContextAware, LoaderMXBean {

	// Initialize Logging
	private static Logger log = Red5LoggerFactory.getLogger(WinstoneLoader.class);

	public static final String defaultSpringConfigLocation = "/WEB-INF/red5-*.xml";

	public static final String defaultParentContextKey = "default.context";

	static {
		log.debug("Initializing Winstone");
	}

	/**
	 * Common name for the Service and Engine components.
	 */
	public String serviceEngineName = "red5Engine";

	/**
	 * Embedded Winstone service (like Catalina).
	 */
	protected static Launcher embedded;

	/**
	 * Additional connection properties to be set at init.
	 */
	protected Map<String, String> connectionProperties = new HashMap<String, String>();

	/**
	 * IP Address to bind to.
	 */
	protected InetAddress address;

	/**
	 * Add context for path and docbase to current host.
	 * 
	 * @param path Path
	 * @param docBase Document base
	 * @return Catalina context (that is, web application)
	 */
	public Context addContext(String path, String docBase) {
		return addContext(path, docBase, host);
	}

	/**
	 * Add context for path and docbase to a host.
	 * 
	 * @param path Path
	 * @param docBase Document base
	 * @param host Host to add context to
	 * @return Catalina context (that is, web application)
	 */
	public Context addContext(String path, String docBase, Host host) {
		log.debug("Add context - path: {} docbase: {}", path, docBase);
		org.apache.catalina.Context c = embedded.createContext(path, docBase);
		if (c != null) {
			log.trace("Context name: {} docbase: {} encoded: {}", new Object[] { c.getName(), c.getDocBase(),
					c.getEncodedPath() });
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			c.setParentClassLoader(classLoader);
			//
			Object ldr = c.getLoader();
			log.trace("Context loader (null if the context has not been started): {}", ldr);
			if (ldr == null) {
				WebappLoader wldr = new WebappLoader(classLoader);
				//add the Loader to the context
				c.setLoader(wldr);
			}
			log.debug("Context loader (check): {} Context classloader: {}", c.getLoader(), c.getLoader()
					.getClassLoader());
			host.addChild(c);
			LoaderBase.setRed5ApplicationContext(getHostId() + path, new WinstoneApplicationContext(c));
		} else {
			log.trace("Context is null");
		}
		return c;
	}

	/**
	 * Remove context from the current host.
	 * 
	 * @param path Path
	 */
	@Override
	public void removeContext(String path) {
		Container[] children = host.findChildren();
		for (Container c : children) {
			if (c instanceof StandardContext && c.getName().equals(path)) {
				try {
					((StandardContext) c).stop();
					host.removeChild(c);
					break;
				} catch (Exception e) {
					log.error("Could not remove context: {}", c.getName(), e);
				}
			}
		}
		IApplicationContext ctx = LoaderBase.removeRed5ApplicationContext(path);
		if (ctx != null) {
			ctx.stop();
		} else {
			log.warn("Context could not be stopped, it was null for path: {}", path);
		}
	}

	/**
	 * Initialization.
	 */
	public void init() {
		log.info("Loading Winstone context");

		//get a reference to the current threads classloader
		final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

		// root location for servlet container
		String serverRoot = System.getProperty("red5.root");
		log.info("Server root: {}", serverRoot);
		String confRoot = System.getProperty("red5.config_root");
		log.info("Config root: {}", confRoot);

		if (webappFolder == null) {
			// Use default webapps directory
			webappFolder = FileUtil.formatPath(System.getProperty("red5.root"), "/webapps");
		}
		System.setProperty("red5.webapp.root", webappFolder);
		log.info("Application root: {}", webappFolder);

		// create one embedded (server) and use it everywhere
		Map args = new HashMap();
		args.put("webroot", webappFolder + "/root");
		//args.put("webappsDir", webappFolder);
		Launcher.initLogger(args);
		// Start server
		try {
			log.info("Starting Winstone servlet engine");
			// spawns threads, so your application doesn't block
			embedded = new Launcher(args);
			log.trace("Classloader for embedded: {} TCL: {}", Launcher.class.getClassLoader(), originalClassLoader);

			LoaderBase.setApplicationLoader(new WinstoneApplicationLoader(embedded, host, applicationContext));

			
			for (Container cont : host.findChildren()) {
				if (cont instanceof StandardContext) {
					StandardContext ctx = (StandardContext) cont;

					final ServletContext servletContext = ctx.getServletContext();
					log.debug("Context initialized: {}", servletContext.getContextPath());

					//set the hosts id
					servletContext.setAttribute("red5.host.id", getHostId());

					String prefix = servletContext.getRealPath("/");
					log.debug("Path: {}", prefix);

					try {
						if (ctx.resourcesStart()) {
							log.debug("Resources started");
						}

						log.debug("Context - available: {} privileged: {}, start time: {}, reloadable: {}",
								new Object[] { ctx.getAvailable(), ctx.getPrivileged(), ctx.getStartTime(),
										ctx.getReloadable() });

						Loader cldr = ctx.getLoader();
						log.debug("Loader delegate: {} type: {}", cldr.getDelegate(), cldr.getClass().getName());
						if (cldr instanceof WebappLoader) {
							log.debug("WebappLoader class path: {}", ((WebappLoader) cldr).getClasspath());
						}
						final ClassLoader webClassLoader = cldr.getClassLoader();
						log.debug("Webapp classloader: {}", webClassLoader);

						// get the (spring) config file path
						final String contextConfigLocation = servletContext
								.getInitParameter(org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM) == null ? defaultSpringConfigLocation
								: servletContext
										.getInitParameter(org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM);
						log.debug("Spring context config location: {}", contextConfigLocation);

						// get the (spring) parent context key
						final String parentContextKey = servletContext
								.getInitParameter(org.springframework.web.context.ContextLoader.LOCATOR_FACTORY_KEY_PARAM) == null ? defaultParentContextKey
								: servletContext
										.getInitParameter(org.springframework.web.context.ContextLoader.LOCATOR_FACTORY_KEY_PARAM);
						log.debug("Spring parent context key: {}", parentContextKey);

						//set current threads classloader to the webapp classloader
						Thread.currentThread().setContextClassLoader(webClassLoader);

						//create a thread to speed-up application loading
						Thread thread = new Thread("Launcher:" + servletContext.getContextPath()) {
							public void run() {
								//set thread context classloader to web classloader
								Thread.currentThread().setContextClassLoader(webClassLoader);
								//get the web app's parent context
								ApplicationContext parentContext = null;
								if (applicationContext.containsBean(parentContextKey)) {
									parentContext = (ApplicationContext) applicationContext.getBean(parentContextKey);
								} else {
									log.warn("Parent context was not found: {}", parentContextKey);
								}
								// create a spring web application context
								final String contextClass = servletContext
										.getInitParameter(org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM) == null ? XmlWebApplicationContext.class
										.getName()
										: servletContext
												.getInitParameter(org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM);
								//web app context (spring)
								ConfigurableWebApplicationContext appctx = null;
								try {
									Class<?> clazz = Class.forName(contextClass, true, webClassLoader);
									appctx = (ConfigurableWebApplicationContext) clazz.newInstance();
								} catch (Throwable e) {
									throw new RuntimeException("Failed to load webapplication context class.", e);
								}
								appctx.setConfigLocations(new String[] { contextConfigLocation });
								appctx.setServletContext(servletContext);
								//set parent context or use current app context
								if (parentContext != null) {
									appctx.setParent(parentContext);
								} else {
									appctx.setParent(applicationContext);
								}
								// set the root webapp ctx attr on the each servlet context so spring can find it later
								servletContext.setAttribute(
										WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appctx);
								//refresh the factory
								log.trace("Classloader prior to refresh: {}", appctx.getClassLoader());
								appctx.refresh();
								if (log.isDebugEnabled()) {
									log.debug("Red5 app is active: {} running: {}", appctx.isActive(), appctx
											.isRunning());
								}
							}
						};
						thread.setDaemon(true);
						thread.start();

					} catch (Throwable t) {
						log.error("Error setting up context: {} due to: {}", servletContext.getContextPath(), t
								.getMessage());
						t.printStackTrace();
					} finally {
						//reset the classloader
						Thread.currentThread().setContextClassLoader(originalClassLoader);
					}
				}
			}
		} catch (Exception e) {
			if (e instanceof BindException || e.getMessage().indexOf("BindException") != -1) {
				log
						.error(
								"Error loading Winstone, unable to bind connector. You may not have permission to use the selected port",
								e);
			} else {
				log.error("Error loading Winstone", e);
			}
		} finally {
			registerJMX();
		}

	}

	/**
	 * The address to which we will bind.
	 * 
	 * @param address
	 */
	public void setAddress(InetSocketAddress address) {
		log.info("Address to bind: {}", address);
		this.address = address.getAddress();
	}

	/**
	 * Set connection properties for the connector
	 * 
	 * @param props additional properties to set
	 */
	public void setConnectionProperties(Map<String, String> props) {
		log.debug("Connection props: {}", props.size());
		this.connectionProperties.putAll(props);
	}

	public void registerJMX() {
		JMXAgent.registerMBean(this, this.getClass().getName(), LoaderMXBean.class);
	}

	/**
	 * Shut server down.
	 */
	public void shutdown() {
		log.info("Shutting down Winstone context");
		//run through the applications and ensure that spring is told
		//to commence shutdown / disposal
		AbstractApplicationContext absCtx = (AbstractApplicationContext) LoaderBase.getApplicationContext();
		if (absCtx != null) {
			log.debug("Using loader base application context for shutdown");
			//get all the app (web) contexts and shut them down first
			Map<String, IApplicationContext> contexts = LoaderBase.getRed5ApplicationContexts();
			if (contexts.isEmpty()) {
				log.info("No contexts were found to shutdown");
			}
			for (Map.Entry<String, IApplicationContext> entry : contexts.entrySet()) {
				//stop the context
				log.debug("Calling stop on context: {}", entry.getKey());
				entry.getValue().stop();
			}
			if (absCtx.isActive()) {
				log.debug("Closing application context");
				absCtx.close();
			}
		} else {
			log.error("Error getting Spring bean factory for shutdown");
		}
		//shutdown jmx
		JMXAgent.shutdown();
		try {
			//stop Winstone
			embedded.shutdown();
			//kill the jvm
			System.exit(0);
		} catch (Exception e) {
			log.warn("Winstone could not be stopped", e);
			throw new RuntimeException("Winstone could not be stopped");
		}
	}

}
