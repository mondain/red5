package org.red5.server.io;

import java.util.Date;

public interface Input {

	byte readDataType(); 
	
	// Data Types
	Object readNull();
	Boolean readBoolean();
	Number readNumber();
	String readString(); 
	Date readDate();
	
	// Stuctures
	int readStartArray(); 
	void skipElementSeparator();
	void skipEndArray();
	
	int readStartList();
	int readItemIndex();
	void skipItemSeparator();
	boolean hasMoreItems();
	void skipEndList();
	
	String readStartObject();
	String readPropertyName();
	void skipPropertySeparator();
	boolean hasMoreProperties();
	void skipEndObject();
	
	//int readStartXML();
	String readXML();
	
	Object readCustom();
	
	//void readEndXML();

	// Reference to Complex Data Type
	Object readReference();
	void storeReference(Object obj);
	void clearReferences();

}
