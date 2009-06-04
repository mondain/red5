package org.red5.server.service;

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

import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;

/**
 * The Echo service is used to test all of the different data types 
 * and to make sure that they are being returned properly.
 *
 * @author The Red5 Project (red5@osflash.org)
 * @author Chris Allen (mrchrisallen@gmail.com)
 * @author Paul Gregoire (mondain@gmail.com)
 */
public interface IEchoService {

	/**
	 * Used to verify that Spring has loaded the bean.
	 *
	 */
	public abstract void startUp();

	/**
	 * Verifies that a boolean that is passed in returns correctly.
	 * 
	 * @param bool object to echo
	 * @return input value
	 */
	public abstract boolean echoBoolean(boolean bool);

	/**
	 * Verifies that a Number that is passed in returns correctly.
	 * 
	 * Flash Number = double
	 * 
	 * @param num object to echo
	 * @return input value
	 */
	public abstract double echoNumber(double num);

	/**
	 * Verifies that a String that is passed in returns correctly.
	 * 
	 * @param string object to echo
	 * @return input value
	 */
	public abstract String echoString(String string);

	/**
	 * Verifies that a Date that is passed in returns correctly.
	 * 
	 * @param date object to echo
	 * @return input value
	 */
	public abstract Date echoDate(Date date);

	/**
	 * Verifies that a Flash Object that is passed in returns correctly.
	 * Flash Object = java.utils.Map, the Apache bean utils will make
	 * the conversions for us.
	 * 
	 * @param obj object to echo
	 * @return input value
	 */
	public abstract Object echoObject(Object obj);

	/**
	 * Verifies that a Flash simple Array that is passed in returns correctly.
	 * Flash simple Array = Object[]
	 * 
	 * @param array object to echo
	 * @return input value
	 */
	public abstract Object[] echoArray(Object[] array);

	/**
	 * Verifies that a Flash multi-dimensional Array that is passed in returns itself.
	 * Flash multi-dimensional Array = java.utils.List
	 * @param <T> the type of the list item we're echoing
	 * 
	 * @param list object to echo
	 * @return input value
	 */
	public abstract <T> List<T> echoList(List<? extends T> list);

	/**
	 * Verifies that Flash XML that is passed in returns itself.
	 * Flash XML = org.w3c.dom.Document
	 * 
	 * @param xml object to echo
	 * @return input value
	 */
	public abstract Document echoXML(Document xml);

}