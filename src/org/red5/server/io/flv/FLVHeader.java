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
	public int DATA_OFFSET = 0x09;

}
