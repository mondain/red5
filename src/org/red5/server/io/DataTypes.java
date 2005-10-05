package org.red5.server.io;

public class DataTypes {

	// The core datatypes supported by red5, I have left out undefined (this is up for debate).
	// If a codec returns one of these datatypes its handled by the base serializer
	
	public final static byte CORE_SKIP = 0x00; // padding
	
	public final static byte CORE_NULL = 0x01; // no undefined type
	public final static byte CORE_BOOLEAN = 0x02;
	public final static byte CORE_NUMBER = 0x03;
	public final static byte CORE_STRING = 0x04; 
	public final static byte CORE_DATE = 0x05; 
	
	// Basic stuctures
	public final static byte CORE_ARRAY = 0x06; 
	public final static byte CORE_LIST = 0x07;
	public final static byte CORE_XML = 0x08; 
	public final static byte CORE_OBJECT = 0x09;
	
	// Reference type, this is optional for codecs to support
	public final static byte OPT_REFERENCE = 0x10;
	
	// More datatypes can be added but they should be prefixed by the type
	// If a codec return one of these datatypes its handled by a custom serializer
	
	public final static byte CUSTOM_MOCK_MASK = 0x20;
	public final static byte CUSTOM_AMF_MASK = 0x30;
	public final static byte CUSTOM_RTMP_MASK = 0x40;
	public final static byte CUSTOM_JSON_MASK = 0x50;
	public final static byte CUSTOM_XML_MASK = 0x60;
	
	// Some helper methods..
	
	public static String toStringValue(byte dataType){
		
		switch(dataType){
			case CORE_SKIP: return "skip";
			case CORE_NULL: return "null";
			case CORE_BOOLEAN: return "Boolean";
			case CORE_NUMBER: return "Number";
			case CORE_STRING: return "String";
			case CORE_DATE: return "Date";
			case CORE_ARRAY: return "Array";
			case CORE_LIST: return "List";
			case CORE_XML: return "XML";
			case CORE_OBJECT: return "Object";
			case OPT_REFERENCE: return "Reference";
		}
		
		if(dataType >= CUSTOM_MOCK_MASK && dataType < CUSTOM_AMF_MASK){
			return "MOCK[" + (dataType - CUSTOM_MOCK_MASK)+"]";
		}
		
		if(dataType >= CUSTOM_AMF_MASK && dataType < CUSTOM_RTMP_MASK){
			return "AMF[" + (dataType - CUSTOM_AMF_MASK)+"]";
		}
		
		if(dataType >= CUSTOM_RTMP_MASK && dataType < CUSTOM_JSON_MASK){
			return "RTMP[" + (dataType - CUSTOM_RTMP_MASK)+"]";
		}
		
		if(dataType >= CUSTOM_JSON_MASK && dataType < CUSTOM_XML_MASK){
			return "JSON[" + (dataType - CUSTOM_JSON_MASK)+"]";
		}
		
		return "XML[" + (dataType - CUSTOM_XML_MASK)+"]";

	}
	
	public static boolean isBasicType(byte type){
		return type <= CORE_DATE;
	}
	
	public static boolean isComplexType(byte type){
		return type >= CORE_ARRAY ||  type <= CORE_OBJECT;
	}
	
	public static boolean isCustomType(byte type){
		return type >= CUSTOM_AMF_MASK;
	}

}
