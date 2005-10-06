package org.red5.server.io.flv;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright � 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */
public interface FLVHeader {

	// Signature
	public static final String SIGNATURE = "FLV";
	public static final byte VERSION = (byte) 0x01; //version 1
	
	// TYPES
	public static final byte TYPE_FLAGS_RESERVED01 = (byte) 0x00;
	public static final byte TYPE_FLAGS_AUDIO = (byte) 0x04;
	public static final byte TYPE_FLAGS_RESERVED02 = (byte) 0x00;
	public static final byte TYPE_FLAGS_VIDEO = (byte) 0x01;
	
	// DATA OFFSET
	// reserved for data up to 4,294,967,295
	public static final int DATA_OFFSET = 0x09;
	
}
