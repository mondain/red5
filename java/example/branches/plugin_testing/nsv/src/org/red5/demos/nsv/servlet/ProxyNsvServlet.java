package org.red5.demos.nsv.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IScope;
import org.red5.server.plugin.NSVPlugin;
import org.red5.server.plugin.PluginRegistry;
import org.red5.server.plugin.icy.stream.NSVConsumer;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Servlet implementation class ProxyNsvServlet
 *
 * Test setup:
 * Shoutcast DNAS - portbase: 8887, listen: 8888
 * Source: AAC+ (HE), AAC Raw (LC) or MP3 
 * name: test password: test
 * port: 8887
 *
 * http://localhost/nsv/proxynsv.jspx?scope=stream&name=test&host=http://localhost:8887/test
 */
public class ProxyNsvServlet extends HttpServlet {
	
	private static final long serialVersionUID = 324215L;
       
	private static Logger log = Red5LoggerFactory.getLogger(ProxyNsvServlet.class, "nsv");	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get relevant parameters
		String outputScopeName = request.getParameter("scope");
		String outputName = request.getParameter("name");
		String remoteHost = request.getParameter("host");
		log.debug("New nsv stream connector requested - scope: {} name: {} host: {}", new Object[]{outputScopeName, outputName, remoteHost});
		
		// get the scope of our application
		ApplicationContext appCtx = (ApplicationContext) getServletContext().getAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		// get the red5 application
		ApplicationAdapter app = (ApplicationAdapter) appCtx.getBean("web.handler");
		IScope appScope = app.getScope();
		
		// get child scope
		IScope outputScope = appScope.getScope(outputScopeName);
		if (outputScope == null) {
			if (appScope.createChildScope(outputScopeName)) {
				// get it!
				outputScope = appScope.getScope(outputScopeName);
			} else {
				log.warn("Scope could not be created");
			}
		}
		
		if (outputScope != null) {
			// get the nsv plugin, just to ensure it exists. we are using static methods
			// below, so normally this call would not be required.
			NSVPlugin plugin = (NSVPlugin) PluginRegistry.getPlugin("nsvPlugin");
			if (plugin == null) {
				log.error("NSV plugin does not exist");
				response.sendError(500, "NSV plugin is not available");
			} else {			
	    		// open up a connection point for an incoming stream
	    		NSVConsumer consumer = NSVPlugin.openExternalURI(outputScope, outputName, remoteHost);
	    		if (consumer != null) {
	    			log.debug("Consumer: {}", consumer);
	    		} else {
	    			log.warn("NSV consumer (external) creation failed");
	    			response.sendError(500, "External connection setup failed");	    			
	    		}
			}			
		} else {
			log.warn("Scope was not found");
			response.sendError(500, "Requested scope was not found");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}
}
