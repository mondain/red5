package org.red5.server.io.flv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

public class FLVDecoder {

	private String fileName = null;
	FileInputStream fis = null;
	FileChannel channel = null;
	private MappedByteBuffer mappedFile;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length < 1) {
			args[0] = "flvs/nvnlogo1.flv";
			//usage();
			//return;
		}
		
		FLVDecoder decoder = new FLVDecoder(args[0]);
		
		
		decoder.decode();
	}
	
	private static void usage() {
		// TODO Auto-generated method stub
		System.out.println("java FLVDecoder.java [filename]");
	}

	public FLVDecoder(String s) {
		this.fileName = s;
		
		if(fileName == null) {
			System.out.println("Please enter a filename");
			usage();
			return;
		} 
		
		try {
			fis = new FileInputStream(fileName);
			channel = fis.getChannel();
			mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		
		//decode();
	}
	
	public void decode() {
		
		// Instantiate FLVHeader
		FLVHeader header = new FLVHeader();
		
		// SIGNATURE	
		header.setSIGNATURE(readSignatureBytes());
		
		// VERSION
		header.setVERSION((byte) mappedFile.get());
		
		// TYPE FLAGS
		header.setTYPE_FLAGS((byte) mappedFile.get());
		
		// DATA OFFSET
		header.setDATA_OFFSET(mappedFile.getInt());
		
		// Print FLVHeader
		System.out.println("HEADER:\n---------\n" + header.toString());
	
	}

	private byte[] readSignatureBytes() {
		int signatureBytes = 3;
		byte b[] = new byte[3];
		for(int i=0; i<signatureBytes; i++) {
			b[i] = mappedFile.get();
		}	
		
		return b;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileInputStream getFis() {
		return fis;
	}

	public void setFis(FileInputStream fis) {
		this.fis = fis;
	}

}
