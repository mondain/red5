package org.red5.server.io;

import java.util.Date;

public interface Output {

	boolean supportsDataType(byte type);
	
	// Basic Data Types
	void writeNumber(Number num);
	void writeBoolean(Boolean bol);
	void writeString(String string);
	void writeDate(Date date);
	void writeNull();
	
	// Complex Data Types
	void writeStartArray(int length);
	void markElementSeparator();
	void markEndArray();
	
	void writeStartObject(String classname);
	void writePropertyName(String name);
	void markPropertySeparator();
	void markEndObject();
	
	void writeXML(String xml);

	// Reference to Complex Data Type
	void writeReference(Object obj);
	
	// Custom datatypes can be handled by
	boolean isCustom(Object custom);
	void writeCustom(Object custom);
	
	void storeReference(Object obj);
	boolean hasReference(Object obj);
	void clearReferences();
}
