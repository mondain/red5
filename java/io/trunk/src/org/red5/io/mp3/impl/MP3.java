package org.red5.io.mp3.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.red5.io.ITagReader;
import org.red5.io.ITagWriter;
import org.red5.io.mp3.IMP3;

public class MP3 implements IMP3 {

	private File file;
	
	public MP3(File file) {
		this.file = file;
	}
	
	public ITagReader getReader() throws IOException {
		FileInputStream stream = new FileInputStream(file);
		return new MP3Reader(stream);
	}

	public ITagWriter getWriter() throws IOException {
		return null;
	}
	
	public ITagWriter getAppendWriter() throws IOException {
		return null;
	}
	
}
