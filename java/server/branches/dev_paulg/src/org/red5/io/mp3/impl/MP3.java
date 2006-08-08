package org.red5.io.mp3.impl;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

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
