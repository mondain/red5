package org.red5.io.mp3.impl;

import java.io.File;
import java.io.IOException;

import org.red5.io.IStreamableFile;
import org.red5.io.mp3.IMP3Service;

public class MP3Service implements IMP3Service {

	public String getPrefix() {
		return "mp3";
	}
	
	public boolean canHandle(File fp) {
		return (fp.isFile() && fp.getAbsolutePath().toLowerCase().endsWith(".mp3"));
	}
	
	public IStreamableFile getStreamableFile(File file) throws IOException {
		return new MP3(file);
	}

}
