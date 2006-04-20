package org.red5.io.flv;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright ? 2006 by respective authors. All rights reserved.
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
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */

/**
 * A Reader is used to read the contents of a FLV file
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 * @version 0.3
 */
public interface IReader extends IKeyFrameDataAnalyzer {

	/**
	 * Returns a FLV object
	 * @return FLV
	 */
	public IFLV getFLV();
	
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
	 * Closes the Reader
	 * @return void
	 */
	public void close();

	public void decodeHeader();
	
	/**
	 * Move the reader pointer to pos
	 * @param pos Position to move to
	 */
	public void position(long pos);
	
}
