package org.red5.server.io.flv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FLVDecoder {

	private String fileName = null;
	FileInputStream fis = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length < 1) {
			usage();
			return;
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
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//decode();
	}
	
	public void decode() {
		if(fileName == null) {
			System.out.println("Please enter a filename");
		}
		FLVHeader header = new FLVHeader();
		
		int signatureBytes = 3;
		byte b[] = new byte[3];
		for(int i=0; i<signatureBytes; i++) {
			try {
				b[i] = (byte) fis.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		header.setSIGNATURE(b);
		System.out.println("Header Signature:" + header.getSIGNATURE());
		
		
		
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
