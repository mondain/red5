package org.red5.io;

public interface ITagReader {

	/**
	 * Return the file that is loaded.
	 * 
	 * @return
	 */
	public IStreamableFile getFile();
	
	/**
	 * Returns the offet length
	 * @return int
	 */
	public int getOffset();
	
	/**
	 * Returns the amount of bytes read
	 * @return long
	 */
	public long getBytesRead();
	
	/**
	 * Decode the header of the stream;
	 *
	 */
	public void decodeHeader();
	
	/**
	 * Move the reader pointer to pos
	 * @param pos Position to move to
	 */
	public void position(long pos);
	
	/**
	 * Returns a boolean stating whether the FLV has more tags
	 * @return boolean
	 */
	public boolean hasMoreTags();
	
	/**
	 * Returns a Tag object
	 * @return Tag
	 */
	public ITag readTag();
	
	/**
	 * Closes the reader and free any allocated memory.
	 */
	public void close();
	
}
