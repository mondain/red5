package org.red5.server.io.flv;

public class FLVHeader {

	// Signature
	public byte[] SIGNATURE;
	public byte VERSION; //version 1
	
	// TYPES
	public byte TYPE_FLAGS_RESERVED01;
	public byte TYPE_FLAGS_AUDIO;
	public byte TYPE_FLAGS_RESERVED02;
	public byte TYPE_FLAGS_VIDEO;
	
	// DATA OFFSET
	// reserved for data up to 4,294,967,295
	public int DATA_OFFSET;

	public int getDATA_OFFSET() {
		return DATA_OFFSET;
	}

	public void setDATA_OFFSET(int data_offset) {
		DATA_OFFSET = data_offset;
	}

	public String getSIGNATURE() {
		String ret = "";
		char[] tmpChar = new char[3];
		for(int i=0; i<SIGNATURE.length; i++) {
			tmpChar[i] = (char) SIGNATURE[i];
		}
		
		
		
		//ret = tmpChar[0] + tmpChar[1] + tmpChar[2];
		
		for(int i=0; i<3; i++) {
			ret += tmpChar[i];	
		}
		
		return ret;
	}

	public void setSIGNATURE(byte[] signature) {
		SIGNATURE = signature;
	}

	public byte getTYPE_FLAGS_AUDIO() {
		return TYPE_FLAGS_AUDIO;
	}

	public void setTYPE_FLAGS_AUDIO(byte type_flags_audio) {
		TYPE_FLAGS_AUDIO = type_flags_audio;
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

	public byte getTYPE_FLAGS_VIDEO() {
		return TYPE_FLAGS_VIDEO;
	}

	public void setTYPE_FLAGS_VIDEO(byte type_flags_video) {
		TYPE_FLAGS_VIDEO = type_flags_video;
	}

	public byte getVERSION() {
		return VERSION;
	}

	public void setVERSION(byte version) {
		VERSION = version;
	}

}
