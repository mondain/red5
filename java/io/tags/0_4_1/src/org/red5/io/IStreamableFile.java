package org.red5.io;

import java.io.IOException;

public interface IStreamableFile {

	/**
	 * Returns a reader to parse and read the tags inside the file.
	 * 
	 * @return the reader
	 */
	public ITagReader getReader() throws IOException;

	/**
	 * Returns a writer that creates a new file or truncates existing contents. 
	 * 
	 * @return the writer
	 */
	public ITagWriter getWriter() throws IOException;

	/**
	 * Returns a Writer which is setup to append to the file.
	 * 
	 * @return the writer
	 */
	public ITagWriter getAppendWriter() throws IOException;
	

}
