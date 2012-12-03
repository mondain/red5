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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
 * The Echo service is used to test all of the different data types and to make
 * sure that they are being returned properly.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Chris Allen (mrchrisallen@gmail.com)
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class EchoService implements IEchoService {

	private Logger log = Red5LoggerFactory.getLogger(EchoService.class, "echo");

	/** {@inheritDoc} */
	public void startUp() {
		log.info("The Echo Service has started...");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean echoBoolean(boolean bool) {
		return bool;
	}

	/**
	 * {@inheritDoc}
	 */
	public double echoNumber(double number) {
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public String echoString(String string) {
		return string;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date echoDate(Date date) {
		return date;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object echoObject(Object obj) {
		return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] echoArray(Object[] array) {
		return array;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List echoList(List list) {
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public Document echoXML(Document xml) {
		return xml;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] echoMultiParam(Map team, List words, String str) {
		Object[] result = new Object[3];
		result[0] = team;
		result[1] = words;
		result[2] = str;
		return result;
	}

	public Object echoAny(Object any) {
		log.info("Received: {}", any);
		return any;
	}

	/**
	 * Test serialization of arbitrary objects.
	 * 
	 * @param any
	 *            object to echo
	 * @return list containing distinct objects
	 */
	public List<Object> returnDistinctObjects(Object any) {
		List<Object> result = new ArrayList<Object>();
		for (int i = 0; i < 4; i++) {
			result.add(new SampleObject());
		}
		return result;
	}

	/**
	 * Test references.
	 * 
	 * @param any
	 *            object to echo
	 * @return list containing same objects
	 */
	public List<Object> returnSameObjects(Object any) {
		List<Object> result = new ArrayList<Object>();
		SampleObject object = new SampleObject();
		for (int i = 0; i < 4; i++) {
			result.add(object);
		}
		return result;
	}

	/**
	 * Test returning of internal objects.
	 * 
	 * @param any
	 *            object to echo
	 * @return the current connection
	 */
	public IConnection returnConnection(Object any) {
		return Red5.getConnectionLocal();
	}

	/**
	 * Sample object that contains attributes with all access possibilities.
	 * This will test the serializer of arbitrary objects.
	 */
	public class SampleObject {

		public String value1 = "one";

		public int value2 = 2;

		protected int value4 = 4;

	}

}
