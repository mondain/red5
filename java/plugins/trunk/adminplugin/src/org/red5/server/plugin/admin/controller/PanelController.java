package org.red5.server.plugin.admin.controller;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.red5.server.plugin.admin.domain.UserDetails;
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
