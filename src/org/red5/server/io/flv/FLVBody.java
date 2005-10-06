package org.red5.server.io.flv;

/*
 * RED5 Open Source Flash Server 
 * http://www.osflash.org/red5
 * 
 * Copyright © 2006 by respective authors. All rights reserved.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 */

public class FLVBody {
	private int previousTagSize;
	private FLVTag tag;
	
	public int getPreviousTagSize() {
		return previousTagSize;
	}
	public void setPreviousTagSize(int previousTagSize) {
		this.previousTagSize = previousTagSize;
	}
	public FLVTag getTag() {
		return tag;
	}
	public void setTag(FLVTag tag) {
		this.tag = tag;
	}
}
