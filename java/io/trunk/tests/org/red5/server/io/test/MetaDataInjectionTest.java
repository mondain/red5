package org.red5.server.io.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeSet;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.amf.Output;
import org.red5.io.flv.FLV;
import org.red5.io.flv.FLVService;
import org.red5.io.flv.FLVServiceImpl;
import org.red5.io.flv.MetaDataImpl;
import org.red5.io.flv.Reader;
import org.red5.io.flv.Tag;
import org.red5.io.flv.TagImpl;
import org.red5.io.flv.Writer;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;
import org.red5.io.utils.IOUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MetaDataInjectionTest extends TestCase {

	private FLVService service;
	
	/**
	 * SetUp is called before each test
	 * @return void
	 */
	public void setUp() {
		service = new FLVServiceImpl();
		service.setSerializer(new Serializer());
		service.setDeserializer(new Deserializer());
	}
	
	/*
	public void testMetaDataInjection() throws IOException {
		File f = new File("tests/test_cue3.flv");
		
		if(f.exists()) {			
			f.delete();
		}
		
		// Create new file
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		//fos.write((byte)0x01);
		FLV flv = service.getFLV(fos);
		Writer writer = flv.writer();
		
		// Create a reader for testing
		File readfile = new File("tests/test_cue.flv");
		FileInputStream fis = new FileInputStream(readfile);
		FLV readflv = service.getFLV(fis);
		Reader reader = readflv.reader();
		
		writeTagsWithInjection(reader, writer);		
	}
	*/

	
	private void writeTagsWithInjection(Reader reader, Writer writer) throws IOException {
		Tag tag = null;
		
		MetaDataImpl mdi1 = new MetaDataImpl("mdi1");
		mdi1.setTimestamp(100);
		mdi1.put("name", "test1");
		
		MetaDataImpl mdi2 = new MetaDataImpl("mdi2");
		mdi2.setTimestamp(150);
		mdi2.put("name", "test2");
		
		MetaDataImpl mdi3 = new MetaDataImpl("mdi3");
		mdi3.setTimestamp(300);
		mdi3.put("name", "test3333");
		
		MetaDataImpl mdi4 = new MetaDataImpl("mdi3");
		mdi4.setTimestamp(300);
		mdi4.put("name", "test3333");
		
		Object o = new Object();
		
		// Place in Treeset for sorting
		TreeSet ts = new TreeSet();
		ts.add(mdi1);
		ts.add(mdi2);
		ts.add(mdi3);
						
		System.out.println("ts: " + ts);
		int tmpTimeStamp = ((MetaDataImpl) ts.first()).getTimestamp();
		TagImpl injectedTag = null;
		while(reader.hasMoreTags()) {
			tag = reader.readTag();
			
			if(!ts.isEmpty()) {
				
				while(tag.getTimestamp() > tmpTimeStamp) {
					injectedTag = (TagImpl) injectMetaData(ts.first(), writer, tag);
					writer.writeTag(injectedTag);
					System.out.println("BodySize: " + injectedTag.getBodySize());
					
					tag.setPreviousTagSize((injectedTag.getBodySize() + 11));
					ts.remove(ts.first());
					if(ts.isEmpty()) {
						break;						
					}
					tmpTimeStamp = ((MetaDataImpl) ts.first()).getTimestamp();
				}
				
			}
			//System.out.println("injectedTag: " + injectedTag);
			writer.writeTag(tag);
			
			//printTag(tag);
		}
		
	}
		
	private Tag injectMetaData(Object mdi, Writer writer, Tag tag) {
		System.out.println("in inject");
		
		Tag retTag = null;
		
		byte tmpDataType = 0x00;
		int tmpTimestamp = 0;
		int tmpBodySize = 0;
		ByteBuffer tmpBody = null;
		int tmpPreviousTagSize = 0;
		
		ByteBuffer buf = ByteBuffer.allocate(1000);
		Output out = new Output(buf);
		Serializer ser = new Serializer();
				
		MetaDataImpl tmpMdi = (MetaDataImpl) mdi;
		
		out.buf().clear();
		ser.serialize(out, tmpMdi);
						
		tmpBody = out.buf().flip();		
		tmpBodySize = out.buf().limit();	
		tmpPreviousTagSize = tag.getPreviousTagSize();
		tmpDataType = ((byte)(Tag.TYPE_METADATA));
		tmpTimestamp = tmpMdi.getTimestamp();

		
		retTag = new TagImpl(tmpDataType, tmpTimestamp, tmpBodySize, tmpBody, tmpPreviousTagSize);
		System.out.println("tmpBody: " + tmpBody);
		System.out.println("tmpBodySize: " + tmpBodySize);
		System.out.println("tmpDataType: " + tmpDataType);
		System.out.println("tmpTimestamp: " + tmpTimestamp);
		
		return retTag;
	}

	public void testMetaDataOrder() {
		MetaDataImpl mdi1 = new MetaDataImpl("mdi1");
		mdi1.setTimestamp(100);
		
		MetaDataImpl mdi2 = new MetaDataImpl("mdi2");
		mdi2.setTimestamp(50);
		
		MetaDataImpl mdi3 = new MetaDataImpl("mdi3");
		mdi3.setTimestamp(0);
		
		TreeSet ts = new TreeSet();
		ts.add(mdi1);
		ts.add(mdi2);
		ts.add(mdi3);
		
		System.out.println("ts: " + ts);
		
		Assert.assertEquals(true, true);
	}
	
}
