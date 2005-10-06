package org.red5.server.io.flv;

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
