package org.red5.io.mp3.impl;

import java.io.File;
import java.io.IOException;

import org.red5.io.mp3.IMP3;
import org.red5.io.mp3.IMP3Service;

public class MP3Service implements IMP3Service {

	public IMP3 getMP3(File file) throws IOException {
		return new MP3(file);
	}

}
