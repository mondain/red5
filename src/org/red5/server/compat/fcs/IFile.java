package org.red5.server.compat.fcs;

public interface IFile {
	
	public boolean close();
	public boolean copyTo(String name);
	public boolean eof();
	public boolean flush();
	public Object[] list(Object filter);
	public boolean mkdir(String newDir);
	public boolean open(String type, String mode);
	public String read(int numChars);
	public char[] readAll();
	public int readByte();
	public String readln();
	public boolean remove();
	public boolean renameTo(String name);
	public int seek(int numBytes);
	public String toString();
	public boolean write();
	public boolean writeAll();
	public boolean writeByte();
	public boolean writeln();
	
}
