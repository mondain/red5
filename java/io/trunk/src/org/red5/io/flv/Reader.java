package org.red5.io.flv;

public interface Reader {

	public FLV getFLV();
	public long getOffset();
	public long getBytesRead();
	
	public boolean hasMoreTags();
	public Tag readTag();
	
	public void close();
	
}
