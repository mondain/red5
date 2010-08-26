package org.red5.stream.http.xuggler;

import org.red5.logging.Red5LoggerFactory;
import org.red5.xuggler.IEventIOHandler;
import org.red5.xuggler.Message;
import org.slf4j.Logger;

public abstract class MpegTsIoHandler implements IEventIOHandler {

	protected Logger log = Red5LoggerFactory.getLogger(this.getClass(), "httplivestreaming");
	
	private String id;
	
	public MpegTsIoHandler(String id) {
		this.id = id;
		log.trace("Id: {}", this.id);
	}
	
	public Message read() throws InterruptedException {
		return null;
	}

	public void write(Message msg) throws InterruptedException {
		
	}

}
