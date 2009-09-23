package org.red5.server.plugin.icy;

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

import org.red5.server.api.IScope;
import org.red5.server.plugin.icy.marshal.transpose.AudioFramer;
import org.red5.server.plugin.icy.marshal.transpose.VideoFramer;
import org.red5.server.plugin.icy.stream.ICYStream;

/**
 * Provides access to running data.
 * 
 * @author Wittawas Nakkasem (vittee@hotmail.com)
 * @author Andy Shaules (bowljoman@hotmail.com)
 */
public interface IICYMarshal extends IICYHandler {
	
	public AudioFramer getAudioFramer();

	public VideoFramer getVideoFramer();

	public IScope getScope();

	public ICYStream getStream();

	public String getContentType();

	public String getAudioType();

	public String getVideoType();
	
}
