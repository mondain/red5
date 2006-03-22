package org.red5.server.io.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.red5.io.flv.IFLV;
import org.red5.io.flv.impl.FLV;
import org.red5.io.flv.impl.FLVService;
import org.red5.io.flv.meta.ICueType;
import org.red5.io.flv.meta.IMetaCue;
import org.red5.io.flv.meta.MetaCue;
import org.red5.io.flv.meta.MetaData;
import org.red5.io.flv.meta.MetaService;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

import junit.framework.TestCase;

public class MetaServiceTest extends TestCase {

	private FLVService service;

	protected void setUp() throws Exception {
		super.setUp();
		
		service = new FLVService();
		service.setSerializer(new Serializer());
		service.setDeserializer(new Deserializer());
	}
	
	public void testWrite() throws IOException {
        	
		IMetaCue metaCue[] = new MetaCue[2];
		
	  	IMetaCue cp = new MetaCue();
		cp.setName("cue_1");
		cp.setTime(0.01);
		cp.setType(ICueType.EVENT);
		
		IMetaCue cp1 = new MetaCue();
		cp1.setName("cue_1");
		cp1.setTime(2.01);
		cp1.setType(ICueType.EVENT);
		
		// add cuepoints to array
		metaCue[0] = cp;
		metaCue[1] = cp1;		
		
		MetaData meta = new MetaData();
		meta.setMetaCue(metaCue);
		
		MetaService metaService = new MetaService();
		File tmp = new File("tests/CuePointTest.flv");
		IFLV flv = new FLV(tmp);
		flv.setMetaService(metaService);
		flv.setMetaData(meta);
		
	}
	

}
