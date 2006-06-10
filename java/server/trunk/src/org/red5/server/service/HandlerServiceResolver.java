package org.red5.server.service;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
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
 * 
 */

import org.red5.server.api.IScope;
import org.red5.server.api.service.IServiceHandlerProvider;

/**
 * Resolves service names in custom configured services.
 *
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 * @see org.red5.server.api.service.IServiceHandlerProvider#registerServiceHandler(IScope, String, Object)
 *  
 */
public class HandlerServiceResolver implements IServiceResolver {

	public Object resolveService(IScope scope, String serviceName) {
		Object service = scope.getHandler();
		if (service instanceof IServiceHandlerProvider) {
			// Check for registered service handler
			Object handler = ((IServiceHandlerProvider) service).getServiceHandler(scope, serviceName);
			if (handler != null)
				// The application registered a custom handler, return it.
				return handler;
		}
		
		return null;
	}

}
