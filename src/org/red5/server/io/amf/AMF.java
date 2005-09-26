package org.red5.server.io.amf;

import java.nio.charset.Charset;

public class AMF {
	
	// add link to amf docs on the osflash wiki
	
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final int LONG_STRING_LENGTH = 65535;
	
	public final static byte TYPE_NUMBER = 0x00; 
	public final static byte TYPE_BOOLEAN = 0x01; 
	public final static byte TYPE_STRING = 0x02; 
	public final static byte TYPE_OBJECT = 0x03; 
	public final static byte TYPE_MOVIECLIP = 0x04; 
	public final static byte TYPE_NULL = 0x05; 
	public final static byte TYPE_UNDEFINED = 0x06; 
	public final static byte TYPE_REFERENCE = 0x07; 
	public final static byte TYPE_MIXED_ARRAY = 0x08;
	public final static byte TYPE_END_OF_OBJECT = 0x09;
	public final static byte TYPE_ARRAY = 0x0A;
	public final static byte TYPE_DATE = 0x0B;
	public final static byte TYPE_LONG_STRING = 0x0C;
	public final static byte TYPE_UNSUPPORTED = 0x0D;
	public final static byte TYPE_RECORDSET = 0x0E;
	public final static byte TYPE_XML = 0x0F;
	public final static byte TYPE_CLASS_OBJECT = 0x10;
	
	public final static byte VALUE_TRUE = 0x01;
	public final static byte VALUE_FALSE = 0x00;
	
}
