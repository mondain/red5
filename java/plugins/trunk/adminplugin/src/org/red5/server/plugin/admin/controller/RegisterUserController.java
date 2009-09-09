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

import javax.servlet.ServletException;


import org.red5.server.plugin.admin.utils.PasswordGenerator;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.providers.dao.salt.SystemWideSaltSource;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.jdbc.JdbcUserDetailsManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RegisterUserController extends SimpleFormController {

	private static Logger log = Red5LoggerFactory.getLogger(RegisterUserController.class, "admin");
	
	private static DaoAuthenticationProvider daoAuthenticationProvider;
	
	private static UserDetailsService userDetailsService;
	
	private SystemWideSaltSource saltSource;
		
	public ModelAndView onSubmit(Object command) throws ServletException {
		log.debug("onSubmit {}", command);

		String salt = saltSource.getSystemWideSalt();
		UserDetails userDetails = (UserDetails) command;
		String username = userDetails.getUsername();
		String password = userDetails.getPassword();
		log.debug("User details: username={} password={}", username, password);
		PasswordGenerator passwordGenerator = new PasswordGenerator(password,
				salt);
		String hashedPassword = passwordGenerator.getPassword();
		log.debug("Password hash: {}", hashedPassword);

		try {
    		// register user here
			if (!((JdbcUserDetailsManager) userDetailsService).userExists(username)) {
    			GrantedAuthority[] auths = new GrantedAuthority[1];
            	auths[0] = new GrantedAuthorityImpl("ROLE_SUPERVISOR");
            	User usr = new User(username, hashedPassword, true, true, true, true, auths);
            	((JdbcUserDetailsManager) userDetailsService).createUser(usr);
            	
            	if (((JdbcUserDetailsManager) userDetailsService).userExists(username)) {
                		//setup security user stuff and add them to the current "cache" and current user map	
            		daoAuthenticationProvider.getUserCache().putUserInCache(usr);
                } else {
                	log.warn("User registration failed for: {}", username);
                }
			} else {
				log.warn("User {} already exists", username);
			}
        } catch (Exception e) {
        	log.error("Error during registration", e);
        }

		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	public void setDaoAuthenticationProvider(DaoAuthenticationProvider value) {
		RegisterUserController.daoAuthenticationProvider = value;
	}

	public void setSaltSource(SystemWideSaltSource saltSource) {
		this.saltSource = saltSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		RegisterUserController.userDetailsService = userDetailsService;
	}
	
}