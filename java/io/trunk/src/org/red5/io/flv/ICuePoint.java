package org.red5.io.flv;

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
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

import java.io.IOException;
import java.util.Map;

/**
 * ICuePoint defines contract methods for use with 
 * cuepoints
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @version 0.3
 */
public interface ICuePoint extends Comparable {
	
	
	/**
	 * Sets the name
	 * @param String name
	 * @return void
	 * 
	 */
	public void setName(String name);
	
	/**
	 * Gets the name
	 * @return String name
	 * 
	 */
	public String getName();
	
	/**
	 * Sets the type
	 * type can be "event" or "navigation"
	 * @param String type
	 * @return void 
	 *
	 */
	public void setType(String type);
	
	/**
	 * Gets the type
	 * @return String type 
	 *
	 */
	public String getType();
	
	/**
	 * Sets the time
	 * @param double d
	 * @return void 
	 *
	 */
	public void setTime(double d);
	
	/**
	 * Gets the time
	 * @return double time 
	 *
	 */
	public double getTime();
}
