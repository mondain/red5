package org.red5.server.io.amf;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors (see below). All rights reserved.
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

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.BaseOutput;

public class Output extends BaseOutput implements org.red5.server.io.Output {
	
	protected static Log log =
        LogFactory.getLog(Output.class.getName());
	
	protected ByteBuffer buf;
	
	
	public Output(ByteBuffer buf){
		super();
		this.buf = buf;
	}

	public boolean isCustom(Object custom) {
		// TODO Auto-generated method stub
		return false;
	}

	// DONE
	public void markElementSeparator() {
		// SKIP
	}

	// DONE
	public void markEndArray() {
		// SKIP
	}
	
	// DONE
	public void markEndList() {
		markEndObject();
	}

	public void markEndObject(){
		// TODO: marker bytes ?
		log.debug("Mark End Object");
		final byte pad = 0x00;
		buf.put(pad);
		buf.put(pad);
		buf.put(AMF.TYPE_END_OF_OBJECT);
	}

	// DONE
	public void markPropertySeparator() {
		// SKIP
	}

	public boolean supportsDataType(byte type) {
		// TODO Auto-generated method stub
		return false;
	}

	// DONE
	public void writeBoolean(Boolean bol) {
		buf.put(AMF.TYPE_BOOLEAN);
		buf.put( bol.booleanValue() ? AMF.VALUE_TRUE : AMF.VALUE_FALSE );
	}

	public void writeCustom(Object custom) {
		// TODO Auto-generated method stub
		
	}

	public void writeDate(Date date) {
		buf.put(AMF.TYPE_DATE);
		buf.putDouble(date.getTime());
		buf.putShort( (short) (TimeZone.getDefault().getRawOffset() / 60 / 1000) );
	}

	// DONE
	public void writeNull() {
		System.err.println("Write null");
		buf.put(AMF.TYPE_NULL);
	}

	// DONE
	public void writeNumber(Number num) {
		buf.put(AMF.TYPE_NUMBER);
		buf.putDouble(num.doubleValue());
	}

	public void writePropertyName(String name) {
		log.debug("Put property: "+name);
		putString(buf,name);
	}

	public void writeReference(Object obj) {
		log.debug("Write reference");
		buf.put(AMF.TYPE_REFERENCE);
		buf.putShort(getReferenceId(obj));
	}
	
	public void writeStartList(int length) {
		buf.put(AMF.TYPE_MIXED_ARRAY);
		buf.putInt(length);
	}
	
	public void markItemSeparator() {
		// nothing
	}

	public void writeItemIndex(int index) {
		writePropertyName(Integer.toString(index));
	}

	public void writeStartArray(int length) {
		buf.put(AMF.TYPE_ARRAY);
		buf.putInt(length);
	}

	public void writeStartObject(String className) {
		if(className == null) buf.put(AMF.TYPE_OBJECT);
		else {
			buf.put(AMF.TYPE_CLASS_OBJECT);
			putString(buf,className);
		}
		log.debug("Start object: "+className);
		
	}

	public void writeString(String string) {
		final java.nio.ByteBuffer strBuf = AMF.CHARSET.encode(string);
		final int len = strBuf.limit();
		if(len < AMF.LONG_STRING_LENGTH) {
			buf.put(AMF.TYPE_STRING);
			buf.putShort((short)len);
		} else {
			buf.put(AMF.TYPE_LONG_STRING);
			buf.putInt(len);
		}
		buf.put(strBuf);
	}
	
	public static void putString(ByteBuffer buf, String string){
		final java.nio.ByteBuffer strBuf = AMF.CHARSET.encode(string);
		buf.putShort((short)strBuf.limit());
		buf.put(strBuf);
	}

	public void writeXML(String xml) {
		// TODO Auto-generated method stub
		
	}
	
}
