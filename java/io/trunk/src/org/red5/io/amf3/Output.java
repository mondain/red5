package org.red5.io.amf3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;

import java.util.Date;

/**
 * AMF3 output writer
 *
 * @see  org.red5.io.amf3.AMF3
 * @see  org.red5.io.amf3.Input
 * @author The Red5 Project (red5@osflash.org)
 */
public class Output implements org.red5.io.object.Output {

	protected static Log log = LogFactory.getLog(Output.class.getName());
	protected ByteBuffer buffer;


	/**
	 * Constructor of AMF3 output.
	 *
	 * @param buffer instance of ByteBuffer
	 * @see ByteBuffer
	 */
	public Output(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public boolean supportsDataType(byte type) {
		return false;
	}// Basic Data Types

	public void writeNumber(Number num) {

	}

	public void writeBoolean(Boolean bol) {

	}

	public void writeString(String string) {

	}

	public void writeDate(Date date) {

	}

	public void writeNull() {

	}// Complex Data Types

	public void writeStartArray(int length) {

	}

	public void markElementSeparator() {

	}

	public void markEndArray() {

	}

	public void writeStartMap(int size) {

	}

	public void writeItemKey(String key) {

	}

	public void markItemSeparator() {

	}

	public void markEndMap() {

	}

	public void writeStartObject(String classname) {

	}

	public void writePropertyName(String name) {

	}

	public void markPropertySeparator() {

	}

	public void markEndObject() {

	}

	public void writeXML(String xml) {

	}// Reference to Complex Data Type

	public void writeReference(Object obj) {

	}// Custom datatypes can be handled by

	public boolean isCustom(Object custom) {
		return false;
	}

	public void writeCustom(Object custom) {

	}

	public void storeReference(Object obj) {

	}

	public boolean hasReference(Object obj) {
		return false;
	}

	public void clearReferences() {

	}
}
