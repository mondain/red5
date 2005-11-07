package org.red5.server.io.amf;

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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.BaseInput;
import org.red5.server.io.DataTypes;

public class Input extends BaseInput implements org.red5.server.io.Input  {

	protected static Log log =
        LogFactory.getLog(Input.class.getName());
	
	protected ByteBuffer buf;
	
	protected byte currentDataType;
	
	public Input(ByteBuffer buf){
		super();
		this.buf = buf;
	}

	public byte readDataType() {
		
		if(buf==null){
			log.error("Why is buf null?");
		}
		
		currentDataType = buf.get();
		
		byte coreType;
		
		switch(currentDataType){
		
			case AMF.TYPE_NULL:
			case AMF.TYPE_UNDEFINED:
				coreType = DataTypes.CORE_NULL;
				break;
		
			case AMF.TYPE_NUMBER:
				coreType = DataTypes.CORE_NUMBER;
				break;
				
			case AMF.TYPE_BOOLEAN: 
				coreType = DataTypes.CORE_BOOLEAN;
				break;
				
			case AMF.TYPE_STRING:
			case AMF.TYPE_LONG_STRING:
				coreType = DataTypes.CORE_STRING;
				break;
				
			case AMF.TYPE_CLASS_OBJECT:
			case AMF.TYPE_OBJECT:
				coreType = DataTypes.CORE_OBJECT;
				break;
			
			case AMF.TYPE_MIXED_ARRAY:
				coreType = DataTypes.CORE_LIST;
				break;
				
			case AMF.TYPE_ARRAY:
				coreType = DataTypes.CORE_ARRAY;
				break;
				
			case AMF.TYPE_DATE:
				coreType = DataTypes.CORE_DATE;
				break;
				
			case AMF.TYPE_XML:
				coreType = DataTypes.CORE_XML;
				break;
				
			case AMF.TYPE_REFERENCE:
				coreType = DataTypes.OPT_REFERENCE;
				break;
							
			case AMF.TYPE_UNSUPPORTED:
			case AMF.TYPE_MOVIECLIP:
			case AMF.TYPE_RECORDSET:
				// These types are not handled by core datatypes
				// So add the amf mast to them, this way the deserializer 
				// will call back to readCustom, we can then handle or reutrn null
				coreType = (byte) (currentDataType + DataTypes.CUSTOM_AMF_MASK);
				break;
				
			case AMF.TYPE_END_OF_OBJECT:				
			default:
				// End of object, and anything else lets just skip
				coreType = DataTypes.CORE_SKIP;
				break;
		}
		
		return coreType;
	}
	
	// Basic
	
	public Object readNull() {
		return null;
	}

	public Boolean readBoolean() {
		// TODO: check values 
		return (buf.get() == AMF.VALUE_TRUE) ? Boolean.TRUE : Boolean.FALSE;
	}

	public Number readNumber() {
		double num = buf.getDouble();
		//if(num < Byte.MAX_VALUE) 
		//	return new Byte( (byte) num);
		//if(num < Short.MAX_VALUE) 
		//	return new Short( (short) num);
		if(num < Integer.MAX_VALUE) 
			return new Integer( (int) num);
		else return new Double(num);
	}

	
	
	public String readString(){
		int len = 0;
		switch(currentDataType){
		case AMF.TYPE_LONG_STRING:
			len = buf.getInt();
			break;
		case AMF.TYPE_STRING:
			len = buf.getShort();
			break;
		}
		int limit = buf.limit();
		final java.nio.ByteBuffer strBuf = buf.buf();
		strBuf.limit(strBuf.position() + len);
		final String string = AMF.CHARSET.decode(strBuf).toString();
		buf.limit(limit); // Reset the limit
		return string;
	}
	
	public static String getString(ByteBuffer buf){
		short len = buf.getShort();
		int limit = buf.limit();
		final java.nio.ByteBuffer strBuf = buf.buf();
		//if(log.isDebugEnabled()) {
		//	log.debug("len: "+len);
		//}
		//log.info("limit: "+strBuf.position() + len);
		strBuf.limit(strBuf.position() + len);
		final String string = AMF.CHARSET.decode(strBuf).toString();
		buf.limit(limit); // Reset the limit
		return string;
	}
	
	public Date readDate() {
		/*
		 * Date: 0x0B T7 T6 .. T0 Z1 Z2
		T7 to T0 form a 64 bit Big Endian number that specifies the number of 
		nanoseconds that have passed since 1/1/1970 0:00 to the specified time. 
		This format is ÒUTC 1970Ó. Z1 an Z0 for a 16 bit Big Endian number 
		indicating the indicated timeÕs timezone in minutes.
		 */
		long ms = (long) buf.getDouble();
		short clientTimeZoneMins = buf.getShort();
		ms += clientTimeZoneMins * 60 * 1000;
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date(ms - SimpleTimeZone.getDefault().getRawOffset()));
		Date date = cal.getTime();
		if(cal.getTimeZone().inDaylightTime(date)){
			date.setTime(date.getTime() - cal.getTimeZone().getDSTSavings());
		}
		return date;
	}

	// Array
	
	public int readStartArray() {
		return buf.getInt();
	}

	public void skipElementSeparator() {
		// SKIP
	}

	public void skipEndArray() {
		// SKIP
	}
	
//	 Object
	public int readStartList() {
		return buf.getInt();
	}

	public boolean hasMoreItems() {
		return hasMoreProperties();
	}

	public int readItemIndex() {
		return Integer.parseInt(getString(buf));
	}

	public void skipItemSeparator(){
		// SKIP
	}

	public void skipEndList(){
		skipEndObject();
	}
	
	// Object
	public String readStartObject() {
		if(currentDataType == AMF.TYPE_CLASS_OBJECT)
			return getString(buf);
		else return null;
	}

	public boolean hasMoreProperties() {
		byte pad = 0x00;
		byte pad0 = buf.get();
		byte pad1 = buf.get();
		byte type = buf.get();
		
		boolean isEndOfObject = (pad0==pad && pad1==pad && type == AMF.TYPE_END_OF_OBJECT);
		if(log.isDebugEnabled()) {
			log.debug("End of object: ? "+isEndOfObject);
		}
		buf.position(buf.position()-3);
		return !isEndOfObject;
	}

	public String readPropertyName() {
		return getString(buf);
	}

	public void skipPropertySeparator(){
		// SKIP
	}

	public void skipEndObject() {
		// skip two marker bytes
		// then end of object byte
		buf.skip(3); 
		//byte nextType = buf.get();
	}

    // Others
	
	public String readXML() {
		return readString();
	}

	public Object readCustom(){
		// Return null for now
		return null;
	}

	public Object readReference() {
		return getReference(buf.getShort());
	}
	
	public void reset(){
		this.clearReferences();
	}
	
	
	
}
