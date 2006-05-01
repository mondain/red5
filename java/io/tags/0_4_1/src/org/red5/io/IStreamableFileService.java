package org.red5.io;

import java.io.File;
import java.io.IOException;

/**
 * Provides access to files that can be streamed. 
 */
public interface IStreamableFileService {

	public String getPrefix();
	
	public boolean canHandle(File file);
	
	public IStreamableFile getStreamableFile(File file) throws IOException;
	
}
