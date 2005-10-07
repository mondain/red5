package org.red5.server.io;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors (see below). All rights reserved.
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
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.utils.XMLUtils;
import org.w3c.dom.Document;

public class Deserializer {

	protected static Log log =
        LogFactory.getLog(Deserializer.class.getName());
	
	public Object deserialize(Input in){
		
		byte type = in.readDataType();
		
		while(type == DataTypes.CORE_SKIP) 
			type = in.readDataType();
		
		log.debug("Datatype: "+DataTypes.toStringValue(type));
		
		Object result = null;
		
		switch(type){
			case DataTypes.CORE_NULL:
				result = in.readNull();
				break;
			case DataTypes.CORE_BOOLEAN:
				result = in.readBoolean();
				break;
			case DataTypes.CORE_NUMBER:
				result = in.readNumber();
				break;
			case DataTypes.CORE_STRING:
				result = in.readString();
				break;
			case DataTypes.CORE_DATE:
				result = in.readDate();
				break;
			case DataTypes.CORE_ARRAY:
				result = readArray(in);
				break;
			case DataTypes.CORE_LIST:
				result = readList(in);
				break;
			case DataTypes.CORE_XML:
				result = readXML(in);
				break;
			case DataTypes.CORE_OBJECT:
				result = readObject(in);
				break;
			case DataTypes.OPT_REFERENCE:
				result = readReference(in);
				break;
			default:
				result = in.readCustom();
				break;
		}
		
		if(type >= DataTypes.CORE_OBJECT){
			result = postProcessExtension(result);
		}
		
		return result;
	}
	
	protected Object readArray(Input in){
		log.debug("Read array");
		final int arraySize = in.readStartArray();
		Object[] array = new Object[arraySize];
		in.storeReference(array);
		for(int i=0; i<arraySize; i++){
			array[i] = deserialize(in);
			in.skipElementSeparator();
		}
		in.skipEndArray();
 		return array;
	}
	
	protected List readList(Input in){
		log.debug("read list");
		
		int highestIndex = in.readStartList();
		
		log.debug("Read start list: "+highestIndex);
		
		List list = new ArrayList(highestIndex);
		for(int i=0; i<highestIndex; i++){
			list.add(i, null); // fill with null
		}
			
		in.storeReference(list);
		while(in.hasMoreItems()){
			int index = in.readItemIndex();
			log.debug("index: "+index);
			Object item = deserialize(in);
			log.debug("item: "+item);
			list.set(index, item);
			if(in.hasMoreItems()) 
				in.skipItemSeparator();
		}
		in.skipEndList();
		return list;
	}
	
	protected Object readXML(Input in){
		final String xmlString = in.readString();
		Document doc = null;
		try {
			doc = XMLUtils.stringToDoc(xmlString);
		} catch(IOException ioex){
			log.error("IOException converting xml to dom", ioex);
		}
		in.storeReference(doc);
		return doc;
	}
	
	protected Object readObject(Input in){
		log.debug("read object");
		final String className = in.readStartObject();
		if(className != null){
			log.debug("read class object");
			Object instance = newInstance(className);
			if(instance!=null) {
				return readBean(in, instance);
			} // else fall through
		} 
		return readMap(in);
	}
	
	protected Object readBean(Input in, Object bean){
		log.debug("read bean");
		in.storeReference(bean);
		while(in.hasMoreProperties()){
			String name = in.readPropertyName();
			log.debug("property: "+name);
			Object property = deserialize(in);
			log.debug("val: "+property);
			//log.debug("val: "+property.getClass().getName());
			try {
				if(property != null){
					BeanUtils.setProperty(bean, name, property);
				} else {
					log.debug("Skipping null property: "+name);
				}
			} catch(Exception ex){
				log.error("Error mapping property: "+name);
			}
			if(in.hasMoreProperties()) 
				in.skipPropertySeparator();
		}
		in.skipEndObject();
		return bean;
	}
	
	protected Map readMap(Input in){
		log.debug("read map");
		Map map = new HashMap();
		in.storeReference(map);
		while(in.hasMoreProperties()){
			String name = in.readPropertyName();
			log.debug("property: "+name);
			Object property = deserialize(in);
			log.debug("val: "+property);
			//log.debug("val: "+property.getClass().getName());
			map.put(name,property);
			if(in.hasMoreProperties()) 
				in.skipPropertySeparator();
		}
		in.skipEndObject();
		return map;
	}
	
	protected Object newInstance(String className){
		Object instance = null; 
		try	{ 
			Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			instance = clazz.newInstance();
		} catch(Exception ex){
			log.error("Error loading class: "+className, ex);
		} 
		return instance;
	}
	
	protected Object readReference(Input in){
		final Object ref = in.readReference();
		if(ref==null) log.error("Reference returned by input is null");
		return ref;
	}
	
	// Extension point
	
	protected Object postProcessExtension(Object result){
		// does nothing at the moment, but will later!
		return result;
	}
	

}
