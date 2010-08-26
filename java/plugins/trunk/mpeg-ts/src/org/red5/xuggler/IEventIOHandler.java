package org.red5.xuggler;

/**
 * Called by the UrlProtocolHandler to read and write Events. 
 */
public interface IEventIOHandler {
	
	/**
	 * Called by a handler to get the next message.
	 * 
	 * @return The next IAVMessage; blocks until a message is ready.
	 * @throws InterruptedException if interrupted while waiting
	 */
	Message read() throws InterruptedException;

	/**
	 * Writes the given message.
	 * 
	 * @param msg The message to write
	 * @throws InterruptedException if interrupted while waiting
	 */
	void write(Message msg) throws InterruptedException;
	
}
