package org.red5.io;

import java.io.IOException;

import org.apache.mina.common.ByteBuffer;

public interface ITagWriter {
	
	/**
	 * Return the file that is written.
	 * 
	 * @return
	 */
	public IStreamableFile getFile();
	
	/**
	 * Return the offset
	 * @return int offset
	 */
	public int getOffset();
	
	/**
	 * Return the bytes written
	 * @return long bytesWritten
	 */
	public long getBytesWritten();
	
	/**
	 * Writes the header bytes
	 * @throws IOException 
	 */
	public void writeHeader() throws IOException;
	
	/**
	 * Writes a Tag object
	 * @param tag
	 * @return boolean 
	 * @throws IOException
	 */
	public boolean writeTag(ITag tag) throws IOException;
	
	/**
	 * Write a Tag using bytes
	 * @param type
	 * @param data
	 * @return boolean
	 * @throws IOException
	 */
	public boolean writeTag(byte type, ByteBuffer data) throws IOException;
	
	/**
	 * Write a Stream to disk using bytes
	 * @param b
	 * @return boolean
	 * @throws IOException
	 */
	public boolean writeStream(byte[] b);
	
	
	/**
	 * Closes a Writer
	 * @return void
	 */
	public void close();

}
