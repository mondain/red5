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
 * Servlet implementation class AddNsvServlet
 */
public class AddNsvServlet extends HttpServlet {

	private static final long serialVersionUID = 3234254L;

	private static Logger log = Red5LoggerFactory.getLogger(AddNsvServlet.class, "nsv");
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get relevant parameters
		String outputScopeName = request.getParameter("scope");
		String outputName = request.getParameter("name");
		String password = request.getParameter("password");
		Integer port = Integer.valueOf(request.getParameter("port"));
		log.debug("New nsv stream connector requested - scope: {} name: {} password: {} port: {}", new Object[]{outputScopeName, outputName, port, password});
		
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
	    		// open up a connection point for an incomming stream
	    		NSVConsumer consumer = NSVPlugin.openServerPort(outputScope, outputName, port.intValue(), password);
	    		if (consumer != null) {
	    			log.debug("Consumer: {}", consumer);
	    		} else {
	    			log.warn("NSV consumer creation failed");
	    			response.sendError(500, "Connection point setup failed");	    			
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
