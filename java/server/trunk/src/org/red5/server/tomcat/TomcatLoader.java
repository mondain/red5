package org.red5.server.tomcat;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Loader;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.ContextLoader;
import org.red5.server.ContextLoaderMBean;
import org.red5.server.LoaderBase;
import org.red5.server.LoaderMBean;
import org.red5.server.api.IApplicationContext;
import org.red5.server.jmx.JMXAgent;
import org.red5.server.jmx.JMXFactory;
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

/**
 * Red5 loader for Tomcat.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class TomcatLoader extends LoaderBase implements ApplicationContextAware, LoaderMBean {

	/*
	 * http://blog.springsource.com/2007/06/11/using-a-shared-parent-application-context-in-a-multi-war-spring-application/
	 */

	/**
	 * Filters directory content
	 */
	protected static class DirectoryFilter implements FilenameFilter {
		/**
		 * Check whether file matches filter rules
		 * 
		 * @param dir Directory
		 * @param name File name
		 * @return true If file does match filter rules, false otherwise
		 */
		public boolean accept(File dir, String name) {
			File f = new File(dir, name);
			log.trace("Filtering: {} name: {}", dir.getName(), name);
			log.trace("Constructed dir: {}", f.getAbsolutePath());
			// filter out all non-directories that are hidden and/or not
			// readable
			boolean result = f.isDirectory() && f.canRead() && !f.isHidden();
			// nullify
			f = null;
			return result;
		}
	}

	// Initialize Logging
	private static Logger log = Red5LoggerFactory.getLogger(TomcatLoader.class);

	public static final String defaultSpringConfigLocation = "/WEB-INF/red5-*.xml";

	public static final String defaultParentContextKey = "default.context";

	static {
		log.debug("Initializing tomcat");
	}

	/**
	 * Common name for the Service and Engine components.
	 */
	public String serviceEngineName = "red5Engine";

	/**
	 * Base container host.
	 */
	protected Host host;

	/**
	 * Tomcat connector.
	 */
	protected Connector connector;

	/**
	 * Embedded Tomcat service (like Catalina).
	 */
	protected static Embedded embedded;

	/**
	 * Tomcat engine.
	 */
	protected static Engine engine;

	/**
	 * Tomcat realm.
	 */
	protected Realm realm;

	/**
	 * Hosts
	 */
	protected List<Host> hosts;

	/**
	 * Connectors
	 */
	protected List<Connector> connectors;

	/**
	 * Valves
	 */
	protected List<Valve> valves = new ArrayList<Valve>();

	/**
	 * Additional connection properties to be set at init.
	 */
	protected Map<String, String> connectionProperties = new HashMap<String, String>();

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
			LoaderBase.setRed5ApplicationContext(path, new TomcatApplicationContext(c));
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
	 * Get base host.
	 * 
	 * @return Base host
	 */
	public Host getBaseHost() {
		return host;
	}

	/**
	 * Return connector.
	 * 
	 * @return Connector
	 */
	public Connector getConnector() {
		return connector;
	}

	/**
	 * Getter for embedded object.
	 * 
	 * @return Embedded object
	 */
	public Embedded getEmbedded() {
		return embedded;
	}

	/**
	 * Return Tomcat engine.
	 * 
	 * @return Tomcat engine
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * Getter for realm.
	 * 
	 * @return Realm
	 */
	public Realm getRealm() {
		return realm;
	}

	/**
	 * Initialization.
	 */
	public void init() {
		log.info("Loading tomcat context");

		//get a reference to the current threads classloader
		final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

		// root location for servlet container
		String serverRoot = System.getProperty("red5.root");
		log.info("Server root: {}", serverRoot);
		String confRoot = System.getProperty("red5.config_root");
		log.info("Config root: {}", confRoot);

		// create one embedded (server) and use it everywhere
		embedded = new Embedded();
		embedded.createLoader(originalClassLoader);
		embedded.setCatalinaBase(serverRoot);
		embedded.setCatalinaHome(serverRoot);
		embedded.setName(serviceEngineName);
		log.trace("Classloader for embedded: {} TCL: {}", Embedded.class.getClassLoader(), originalClassLoader);

		engine = embedded.createEngine();
		engine.setDefaultHost(host.getName());
		engine.setName(serviceEngineName);

		if (webappFolder == null) {
			// Use default webapps directory
			webappFolder = FileUtil.formatPath(System.getProperty("red5.root"), "/webapps");
		}
		System.setProperty("red5.webapp.root", webappFolder);
		log.info("Application root: {}", webappFolder);

		// scan for additional webapp contexts

		// Root applications directory
		File appDirBase = new File(webappFolder);
		// Subdirs of root apps dir
		File[] dirs = appDirBase.listFiles(new DirectoryFilter());
		// Search for additional context files
		for (File dir : dirs) {
			String dirName = '/' + dir.getName();
			// check to see if the directory is already mapped
			if (null == host.findChild(dirName)) {
				String webappContextDir = FileUtil.formatPath(appDirBase.getAbsolutePath(), dirName);
				log.debug("Webapp context directory (full path): {}", webappContextDir);
				Context ctx = null;
				if ("/root".equals(dirName) || "/root".equalsIgnoreCase(dirName)) {
					log.trace("Adding ROOT context");
					ctx = addContext("/", webappContextDir);
				} else {
					log.trace("Adding context from directory scan: {}", dirName);
					ctx = addContext(dirName, webappContextDir);
				}
				log.trace("Context: {}", ctx);

				//see if the application requests php support
				String enablePhp = ctx.findParameter("enable-php");
				//if its null try to read directly
				if (enablePhp == null) {
					File webxml = new File(webappContextDir + "/WEB-INF/", "web.xml");
					if (webxml.exists() && webxml.canRead()) {
						try {
							DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
							Document doc = docBuilder.parse(webxml);
							// normalize text representation
							doc.getDocumentElement().normalize();
							log.trace("Root element of the doc is {}", doc.getDocumentElement().getNodeName());
							NodeList listOfElements = doc.getElementsByTagName("context-param");
							int totalElements = listOfElements.getLength();
							log.trace("Total no of elements: {}", totalElements);
							for (int s = 0; s < totalElements; s++) {
								Node fstNode = listOfElements.item(s);
								if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
									Element fstElmnt = (Element) fstNode;
									NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("param-name");
									Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
									NodeList fstNm = fstNmElmnt.getChildNodes();
									String pName = (fstNm.item(0)).getNodeValue();
									log.trace("Param name: {}", pName);
									if ("enable-php".equals(pName)) {
										NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("param-value");
										Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
										NodeList lstNm = lstNmElmnt.getChildNodes();
										String pValue = (lstNm.item(0)).getNodeValue();
										log.trace("Param value: {}", pValue);
										enablePhp = pValue;
										//
										break;
									}
								}
							}
						} catch (Exception e) {
							log.warn("Error reading web.xml", e);
						}
					}
					webxml = null;
				}
				log.debug("Enable php: {}", enablePhp);
				if ("true".equals(enablePhp)) {
					log.info("Adding PHP (Quercus) servlet for context: {}", ctx.getName());
					// add servlet wrapper
					StandardWrapper wrapper = (StandardWrapper) ctx.createWrapper();
					wrapper.setServletName("QuercusServlet");
					wrapper.setServletClass("com.caucho.quercus.servlet.QuercusServlet");
					log.debug("Wrapper: {}", wrapper);
					ctx.addChild(wrapper);
					// add servlet mappings
					ctx.addServletMapping("*.php", "QuercusServlet");
				}

				webappContextDir = null;
			}
		}
		appDirBase = null;
		dirs = null;

		// Dump context list
		if (log.isDebugEnabled()) {
			for (Container cont : host.findChildren()) {
				log.debug("Context child name: {}", cont.getName());
			}
		}

		// Set a realm
		if (realm == null) {
			realm = new MemoryRealm();
		}
		embedded.setRealm(realm);

		// use Tomcat jndi or not
		if (System.getProperty("catalina.useNaming") != null) {
			embedded.setUseNaming(Boolean.valueOf(System.getProperty("catalina.useNaming")));
		}

		// add the valves to the host
		for (Valve valve : valves) {
			log.debug("Adding host valve: {}", valve);
			((StandardHost) host).addValve(valve);
		}

		// baseHost = embedded.createHost(hostName, appRoot);
		engine.addChild(host);

		// add any additional hosts
		if (hosts != null && !hosts.isEmpty()) {
			// grab current contexts from base host
			Container[] currentContexts = host.findChildren();
			log.info("Adding {} additional hosts", hosts.size());
			for (Host h : hosts) {
				log.debug("Host - name: {} appBase: {} info: {}", new Object[] { h.getName(), h.getAppBase(),
						h.getInfo() });
				//add the contexts to each host
				for (Container cont : currentContexts) {
					Context c = (Context) cont;
					addContext(c.getPath(), c.getDocBase(), h);
				}
				//add the host to the engine
				engine.addChild(h);
			}
		}

		// Add new Engine to set of Engine for embedded server
		embedded.addEngine(engine);

		// set connection properties
		for (String key : connectionProperties.keySet()) {
			log.debug("Setting connection property: {} = {}", key, connectionProperties.get(key));
			if (connectors == null || connectors.isEmpty()) {
				connector.setProperty(key, connectionProperties.get(key));
			} else {
				for (Connector ctr : connectors) {
					ctr.setProperty(key, connectionProperties.get(key));
				}
			}
		}

		// Start server
		try {
			// Add new Connector to set of Connectors for embedded server,
			// associated with Engine
			if (connectors == null || connectors.isEmpty()) {
				embedded.addConnector(connector);
				log.trace("Connector oName: {}", connector.getObjectName());
			} else {
				for (Connector ctr : connectors) {
					embedded.addConnector(ctr);
					log.trace("Connector oName: {}", ctr.getObjectName());
				}
			}

			log.info("Starting Tomcat servlet engine");
			embedded.start();

			LoaderBase.setApplicationLoader(new TomcatApplicationLoader(embedded, host, applicationContext));

			for (Container cont : host.findChildren()) {
				if (cont instanceof StandardContext) {
					StandardContext ctx = (StandardContext) cont;

					final ServletContext servletContext = ctx.getServletContext();
					log.debug("Context initialized: {}", servletContext.getContextPath());

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

			// if everything is ok at this point then call the rtmpt and rtmps
			// beans so they will init
			if (applicationContext.containsBean("red5.core")) {
				ApplicationContext core = (ApplicationContext) applicationContext.getBean("red5.core");
				if (core.containsBean("rtmpt.server")) {
					log.debug("Initializing RTMPT");
					core.getBean("rtmpt.server");
					log.debug("Finished initializing RTMPT");
				} else {
					log.info("RTMPT server bean was not found");
				}
				if (core.containsBean("rtmps.server")) {
					log.debug("Initializing RTMPS");
					core.getBean("rtmps.server");
					log.debug("Finished initializing RTMPS");
				} else {
					log.info("RTMPS server bean was not found");
				}
			} else {
				log.info("Core context was not found");
			}
		} catch (Exception e) {
			if (e instanceof BindException || e.getMessage().indexOf("BindException") != -1) {
				log
						.error(
								"Error loading tomcat, unable to bind connector. You may not have permission to use the selected port",
								e);
			} else {
				log.error("Error loading tomcat", e);
			}
		} finally {
			registerJMX();
		}

	}

	/**
	 * Starts a web application and its red5 (spring) component. This is
	 * basically a stripped down version of init().
	 * 
	 * @return true on success
	 */
	public boolean startWebApplication(String applicationName) {
		log.info("Starting Tomcat - Web application");
		boolean result = false;

		//get a reference to the current threads classloader
		final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

		log.debug("Webapp root: {}", webappFolder);

		// application directory
		String contextName = '/' + applicationName;

		Container ctx = null;

		if (webappFolder == null) {
			// Use default webapps directory
			webappFolder = System.getProperty("red5.root") + "/webapps";
		}
		System.setProperty("red5.webapp.root", webappFolder);
		log.info("Application root: {}", webappFolder);

		// scan for additional webapp contexts

		// Root applications directory
		File appDirBase = new File(webappFolder);

		// check if the context already exists for the host
		if ((ctx = host.findChild(contextName)) == null) {
			log.debug("Context did not exist in host");
			String webappContextDir = FileUtil.formatPath(appDirBase.getAbsolutePath(), applicationName);
			log.debug("Webapp context directory (full path): {}", webappContextDir);
			// set the newly created context as the current container
			ctx = addContext(contextName, webappContextDir);
		} else {
			log.debug("Context already exists in host");
		}

		final ServletContext servletContext = ((Context) ctx).getServletContext();
		log.debug("Context initialized: {}", servletContext.getContextPath());

		String prefix = servletContext.getRealPath("/");
		log.debug("Path: {}", prefix);

		try {
			Loader cldr = ctx.getLoader();
			log.debug("Loader delegate: {} type: {}", cldr.getDelegate(), cldr.getClass().getName());
			if (cldr instanceof WebappLoader) {
				log.debug("WebappLoader class path: {}", ((WebappLoader) cldr).getClasspath());
			}
			final ClassLoader webClassLoader = cldr.getClassLoader();
			log.debug("Webapp classloader: {}", webClassLoader);

			// get the (spring) config file path
			final String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation") == null ? defaultSpringConfigLocation
					: servletContext.getInitParameter("contextConfigLocation");
			log.debug("Spring context config location: {}", contextConfigLocation);

			// get the (spring) parent context key
			final String parentContextKey = servletContext.getInitParameter("parentContextKey") == null ? defaultParentContextKey
					: servletContext.getInitParameter("parentContextKey");
			log.debug("Spring parent context key: {}", parentContextKey);

			//set current threads classloader to the webapp classloader
			Thread.currentThread().setContextClassLoader(webClassLoader);

			//create a thread to speed-up application loading
			Thread thread = new Thread("Launcher:" + servletContext.getContextPath()) {
				public void run() {
					//set current threads classloader to the webapp classloader
					Thread.currentThread().setContextClassLoader(webClassLoader);

					// create a spring web application context
					XmlWebApplicationContext appctx = new XmlWebApplicationContext();
					appctx.setClassLoader(webClassLoader);
					appctx.setConfigLocations(new String[] { contextConfigLocation });

					// check for red5 context bean
					ApplicationContext parentAppCtx = null;

					if (applicationContext.containsBean(defaultParentContextKey)) {
						parentAppCtx = (ApplicationContext) applicationContext.getBean(defaultParentContextKey);
					} else {
						log.warn("{} bean was not found in context: {}", defaultParentContextKey, applicationContext
								.getDisplayName());
						// lookup context loader and attempt to get what we need from it
						if (applicationContext.containsBean("context.loader")) {
							ContextLoader contextLoader = (ContextLoader) applicationContext.getBean("context.loader");
							parentAppCtx = contextLoader.getContext(defaultParentContextKey);
						} else {
							log.debug("Context loader was not found, trying JMX");
							MBeanServer mbs = JMXFactory.getMBeanServer();
							// get the ContextLoader from jmx
							ObjectName oName = JMXFactory.createObjectName("type", "ContextLoader");
							ContextLoaderMBean proxy = null;
							if (mbs.isRegistered(oName)) {
								proxy = (ContextLoaderMBean) MBeanServerInvocationHandler.newProxyInstance(mbs, oName,
										ContextLoaderMBean.class, true);
								log.debug("Context loader was found");
								parentAppCtx = proxy.getContext(defaultParentContextKey);
							} else {
								log.warn("Context loader was not found");
							}
						}
					}
					if (log.isDebugEnabled()) {
						if (appctx.getParent() != null) {
							log.debug("Parent application context: {}", appctx.getParent().getDisplayName());
						}
					}

					appctx.setParent(parentAppCtx);

					appctx.setServletContext(servletContext);
					// set the root webapp ctx attr on the each
					// servlet context so spring can find it later
					servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appctx);
					appctx.refresh();
				}
			};
			thread.setDaemon(true);
			thread.start();

			result = true;
		} catch (Throwable t) {
			log.error("Error setting up context: {} due to: {}", servletContext.getContextPath(), t.getMessage());
			t.printStackTrace();
		} finally {
			//reset the classloader
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}

		return result;
	}

	/**
	 * Set base host.
	 * 
	 * @param baseHost Base host
	 */
	public void setBaseHost(Host baseHost) {
		log.debug("setBaseHost");
		this.host = baseHost;
	}

	/**
	 * Set connector.
	 * 
	 * @param connector Connector
	 */
	public void setConnector(Connector connector) {
		log.info("Setting connector: {}", connector.getClass().getName());
		this.connector = connector;
	}

	/**
	 * Set additional connectors.
	 * 
	 * @param connectors Additional connectors
	 */
	public void setConnectors(List<Connector> connectors) {
		log.debug("setConnectors: {}", connectors.size());
		this.connectors = connectors;
	}

	/**
	 * Set additional contexts.
	 * 
	 * @param contexts Map of contexts
	 */
	public void setContexts(Map<String, String> contexts) {
		log.debug("setContexts: {}", contexts.size());
		for (Map.Entry<String, String> entry : contexts.entrySet()) {
			host.addChild(embedded.createContext(entry.getKey(), webappFolder + entry.getValue()));
		}
	}

	/**
	 * Setter for embedded object.
	 * 
	 * @param embedded Embedded object
	 */
	public void setEmbedded(Embedded embedded) {
		log.info("Setting embedded: {}", embedded.getClass().getName());
		TomcatLoader.embedded = embedded;
	}

	/**
	 * Get the host.
	 * 
	 * @return host
	 */
	public Host getHost() {
		return host;
	}

	/**
	 * Set the host.
	 * 
	 * @param host host
	 */
	public void setHost(Host host) {
		log.debug("setHost");
		this.host = host;
	}

	/**
	 * Set additional hosts.
	 * 
	 * @param hosts List of hosts added to engine
	 */
	public void setHosts(List<Host> hosts) {
		log.debug("setHosts: {}", hosts.size());
		this.hosts = hosts;
	}

	/**
	 * Setter for realm.
	 * 
	 * @param realm Realm
	 */
	public void setRealm(Realm realm) {
		log.info("Setting realm: {}", realm.getClass().getName());
		this.realm = realm;
	}

	/**
	 * Set additional valves.
	 * 
	 * @param valves List of valves
	 */
	public void setValves(List<Valve> valves) {
		log.debug("setValves: {}", valves.size());
		this.valves.addAll(valves);
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
		JMXAgent.registerMBean(this, this.getClass().getName(), LoaderMBean.class);
	}

	/**
	 * Shut server down.
	 */
	public void shutdown() {
		log.info("Shutting down Tomcat context");
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
			//stop tomcat
			embedded.stop();
			//kill the jvm
			System.exit(0);
		} catch (Exception e) {
			log.warn("Tomcat could not be stopped", e);
			System.exit(1);
		}
	}

}
