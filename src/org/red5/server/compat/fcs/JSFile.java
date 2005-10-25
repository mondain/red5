package org.red5.server.compat.fcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class JSFile implements IFile {
	
	File file = null;
	
	FileInputStream fis = null;
	FileOutputStream fos = null;
	private boolean canAppend = false;
	private boolean canRead = false;
	private boolean canReplace = false;
	private boolean canWrite = false;
	private Date creationTime = null;
	private boolean isDirectory = false;
	private boolean isFile = false;
	private boolean isOpen = false;
	private Date lastModified = null;
	private int length = 0;
	private String mode = "";
	private String name = "";
	private int position = 0;
	private String type = "";
	
	public JSFile(String name) {
		
		file = new File(name);
		try {
			fis = new FileInputStream(file);
			//fos = new FileOutputStream(outfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO
	}
	
	public boolean close() {
		// TODO Auto-generated method stub
		if(fis != null) {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return false;
	}

	public boolean copyTo(String name) {
		// TODO Auto-generated method stub
		if(fis != null) {
			//TODO add copyTo Code
		}
		return false;
	}

	public boolean eof() {
		// TODO Auto-generated method stub
		
		return false;
	}

	public boolean flush() {
		// TODO Auto-generated method stub
		if(fos != null) {
			try {
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public Object[] list(Object filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean mkdir(String newDir) {
		
		File tmpDir = new File(newDir);
		boolean ret = false;
		
		if(!file.exists()) {
			try {
				ret = tmpDir.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	public boolean open(String type, String mode) {
		// TODO Auto-generated method stub
		File f = new File(type, mode);
		
		return f.exists();
		
	}

	public String read(int numChars) {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] readAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public int readByte() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String readln() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean renameTo(String name) {
		
		File tmpFile = new File(name);
		boolean ret = false;
		
		if(!tmpFile.exists()) {
			ret = file.renameTo(tmpFile);
		}
		return ret;
	}

	public int seek(int numBytes) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean write() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean writeAll() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean writeByte() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean writeln() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCanAppend() {
		return canAppend;
	}

	public void setCanAppend(boolean canAppend) {
		this.canAppend = canAppend;
	}

	public boolean isCanRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean isCanReplace() {
		return canReplace;
	}

	public void setCanReplace(boolean canReplace) {
		this.canReplace = canReplace;
	}

	public boolean isCanWrite() {
		
		if(!file.exists()) {
			return false;
		}
		
		return canWrite;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
