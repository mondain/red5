package org.red5.io.object;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Deserializer class reads data input and handles the data 
 * according to the core data types 
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */
public class Deserializer {

	// Initialize Logging
	protected static Logger log = LoggerFactory.getLogger(Deserializer.class);

	/**
	 * Deserializes the input parameter and returns an Object
	 * which must then be cast to a core data type
	 * @param in
	 * @param target
     * @return Object
	 */
	public <T> T deserialize(Input in, Class<T> target) {

		byte type = in.readDataType();
		log.debug("Type: {}", type);

		while (type == DataTypes.CORE_SKIP) {
			type = in.readDataType();
			log.debug("Type (skip): {}", type);
		}

		log.debug("Datatype: " + DataTypes.toStringValue(type));

		Object result;

		switch (type) {
			case DataTypes.CORE_NULL:
				result = in.readNull();
				break;
			case DataTypes.CORE_BOOLEAN:
				log.debug("Reading boolean");
				result = in.readBoolean();
				break;
			case DataTypes.CORE_NUMBER:
				log.debug("Reading number");
				result = in.readNumber();
				break;
			case DataTypes.CORE_STRING:
				log.debug("Reading string");
				result = in.readString();
				break;
			case DataTypes.CORE_DATE:
				result = in.readDate();
				break;
			case DataTypes.CORE_ARRAY:
				log.debug("Reading array");
				result = in.readArray(this);
				break;
			case DataTypes.CORE_MAP:
				log.debug("Reading map");
				result = in.readMap(this);
				break;
			case DataTypes.CORE_XML:
				result = in.readXML();
				break;
			case DataTypes.CORE_OBJECT:
				log.debug("Reading object");
				result = in.readObject(this);
				break;
			case DataTypes.CORE_BYTEARRAY:
				result = in.readByteArray();
				break;
			case DataTypes.OPT_REFERENCE:
				result = in.readReference();
				break;
			default:
				result = in.readCustom();
				break;
		}

        return (T) postProcessExtension(result, target);
	}

	/**
	 * Post processes the result
	 * TODO Extension Point
     * @param result
     * @param target
     * @return
     */
	protected Object postProcessExtension(Object result, Class target) {
		// does nothing at the moment, but will later!
		return result;
	}

}
