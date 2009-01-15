package org.red5.webapps.admin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.red5.webapps.admin.Application;
import org.red5.webapps.admin.controllers.service.UserDetails;
import org.springframework.security.userdetails.User;

import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.jdbc.JdbcUserDetailsManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PanelController implements Controller {

	private static Logger log = Red5LoggerFactory.getLogger(PanelController.class, "admin");

	private static UserDetailsService userDetailsService;
	private User user;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		log.debug("handle request");
				
		//if there arent any users then send to registration
		if (((JdbcUserDetailsManager) userDetailsService).userExists("admin")) {
			log.debug("Creating adminPanel");
			return new ModelAndView("panel");
		} else {
			//check for model
			log.debug("{}", ToStringBuilder.reflectionToString(request));
			if (request.getMethod().equalsIgnoreCase("POST")) {
    			//no model then redirect...
    			log.debug("Redirecting to register with user details");	
    			return new ModelAndView("register");		
			} else {
    			//no model then redirect...
    			log.debug("Redirecting to register");
    			UserDetails userDetails = new UserDetails();
    			((UserDetails) userDetails).setUsername("admin");
    			return new ModelAndView("register", "userDetails", userDetails);
			}
		}
	}

	public ModelAndView doRequest(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView();
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		PanelController.userDetailsService = userDetailsService;
	}
	
}
