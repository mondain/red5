package org.red5.io.flv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

public interface FLVService {

	public void setSerializer(Serializer serializer);
	public void setDeserializer(Deserializer deserializer);
	
	public FLV getFLV(String filename) throws FileNotFoundException, IOException;
	public FLV getFLV(FileInputStream fis) throws IOException;
	public FLV getFLV(File file) throws IOException;
	
}
