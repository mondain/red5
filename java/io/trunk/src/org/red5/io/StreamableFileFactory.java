package org.red5.io;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class StreamableFileFactory {

	private Set<IStreamableFileService> services = new HashSet<IStreamableFileService>();
	
	public void setServices(Set<IStreamableFileService> services) {
		this.services = services;
	}
	
	public IStreamableFileService getService(File fp) {
		// Return first service that can handle the passed file
		for (IStreamableFileService service : this.services) {
			if (service.canHandle(fp))
				return service;
		}
		
		return null;
	}
	
}
