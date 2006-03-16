package org.red5.io.amf3;

import java.nio.charset.Charset;

public class AMF3 {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final int LONG_STRING_LENGTH = 65535;
	
	public final static byte TYPE_NULL = 0x01;
	public final static byte TYPE_BOOLEAN_TRUE = 0x02; 
	public final static byte TYPE_BOOLEAN_FALSE = 0x03;
	public final static byte TYPE_INTEGER = 0x04; 
	public final static byte TYPE_NUMBER = 0x05; 
	public final static byte TYPE_STRING = 0x06;
	
	public final static byte TYPE_DATE = 0x08; 
	public final static byte TYPE_ARRAY = 0x09; 
	public final static byte TYPE_OBJECT = 0x0A; 
	 
	/**
	 * amf3-data = null | boolean-false | boolean-true | integer | number | string | date | object | array 

null = 0×01
boolean-false = 0×02
boolean-true = 0×03 

integer = 0×04 integer-data
integer-data = 1*4OCTET 

integer-data is probably the single most used item in amf3. To save space it is an integer that can be 0-4 bytes long. The first bit of each byte determines if the next byte is inclued in this integer-data or not.
Examples:
0011 0101 = 53
1000 0001 0101 0100 = 212
1000 0110 1100 1010 0011 1111 = 107839 

number = 0×05 number-data
number-data = 8OCTET 

standard java double serialization 

string = 0×06 string-data
string-data = integer-data [ modified-utf-8 ]
modified-utf-8 = *OCTET 

The last bit of the integer-data element in string-data identifies if this string is an inline string(1) or a string reference(0).
String references are references to an already passed string. String indexes start at 0 and are in the order that the inline string is encountered. The index is the first 15 bits of the integer-data.
The first 15 bits of the ingteger-data specifies the byte length of the string. Those bytes are parsed using modified utf-8. For more on modified utf-8 see modified utf-8 on wikipedia 

date = 0×08 integer-data [ number-data ] 

The last bit of the date-data element in date identifies if this date is an inline date or a date reference. 

array = 0×09 integer-data [ 1OCTET *amf3-data ] 

object = 0x0A integer-data [ class-def ] [ *amf3-data ]
class-def = string-data [ *string-data ] 

The last bit of the integer-data element in object identifies if this object is an inline object(1) or an object reference(0). Object references are references to an already passed object. Object indexes start at 0 and are in the order that the objects are passed. Object references include dates, arrays, and objects.
With inline objects, this second to last bit of the integer-data designates whether the object uses is a reference to a previously passed class-def or it is inline. 1 is an inline class-def. 0 is a reference.
With class-def references the remaining 14 bits of the integer-data element are the index to the class-def as it was passed.
class-defs then peal off the 3 and 4 to last bits of the integer-data. These are flags that identify the way the object is serialized/deserialized. I will come back to them later.
The remaining integer-data represents the number of class members that exist. The next element of a class-def is string-data, this is the class name. 

	 */
	
}
