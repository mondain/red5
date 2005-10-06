package org.red5.server.io.flv;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

public class FLVHeader {

	// Signature
	public byte[] SIGNATURE = null;
	public byte VERSION = 0x00; //version 1
	 
	// TYPES
	public byte TYPE_FLAGS_RESERVED01 = 0x00;
	public boolean TYPE_FLAGS_AUDIO = false;
	public byte TYPE_FLAGS_RESERVED02 = 0x00;
	public boolean TYPE_FLAGS_VIDEO = false;
	
	// DATA OFFSET
	// reserved for data up to 4,294,967,295
	public int DATA_OFFSET = 0x00;

	public int getDATA_OFFSET() {
		return DATA_OFFSET;
	}

	public void setDATA_OFFSET(int data_offset) {
		DATA_OFFSET = data_offset;
	}

	public byte[] getSIGNATURE() {		
		return SIGNATURE;
	}
	
	public String toString() {
		String ret = "";
		//ret += "SIGNATURE: \t" + getSIGNATURE() + "\n";
		ret += "SIGNATURE: \t\t" + (char) SIGNATURE[0] + (char) SIGNATURE[1] + (char) SIGNATURE[2] + "\n";  
		ret += "VERSION: \t\t" + ((byte) getVERSION()) + "\n";
		ret += "TYPE FLAGS VIDEO: \t" + getTYPE_FLAGS_VIDEO() + "\n";
		ret += "TYPE FLAGS AUDIO: \t" + getTYPE_FLAGS_AUDIO() + "\n";
		ret += "DATA OFFSET: \t\t" + getDATA_OFFSET() + "\n";
		//byte b = 0x01;
		
		return ret;
		
	}

	public void setSIGNATURE(byte[] signature) {
		SIGNATURE = signature;
	}

	public boolean getTYPE_FLAGS_AUDIO() {
		return TYPE_FLAGS_AUDIO;
	}

	public void setTYPE_FLAGS_AUDIO(boolean type_flags_audio) {
		TYPE_FLAGS_AUDIO = type_flags_audio;
	}

	public void setTYPE_FLAGS(byte type_flags) {
		TYPE_FLAGS_VIDEO = (((type_flags << 7) >> 7) > 0x00)? true : false;
		TYPE_FLAGS_AUDIO = (((type_flags << 5) >> 7) > 0x00)? true : false;
	}
	
	public byte getTYPE_FLAGS_RESERVED01() {
		return TYPE_FLAGS_RESERVED01;
	}

	public void setTYPE_FLAGS_RESERVED01(byte type_flags_reserved01) {
		TYPE_FLAGS_RESERVED01 = type_flags_reserved01;
	}

	public byte getTYPE_FLAGS_RESERVED02() {
		return TYPE_FLAGS_RESERVED02;
	}

	public void setTYPE_FLAGS_RESERVED02(byte type_flags_reserved02) {
		TYPE_FLAGS_RESERVED02 = type_flags_reserved02;
	}

	public boolean getTYPE_FLAGS_VIDEO() {
		return TYPE_FLAGS_VIDEO;
	}

	public void setTYPE_FLAGS_VIDEO(boolean type_flags_video) {
		TYPE_FLAGS_VIDEO = type_flags_video;
	}

	public byte getVERSION() {
		return VERSION;
	}

	public void setVERSION(byte version) {
		VERSION = version;
	}

}
