package org.red5.server.service.test;

import java.util.Map;
import junit.framework.TestCase;

import org.red5.server.service.DemoService;

/**
 * Tests the DemoService.
 * @author Chris Allen
 *
 */
public class TestDemoService extends TestCase {
	DemoService demoService;
	
	protected void setUp() throws Exception {
		super.setUp();
		demoService = new DemoService("./conf/global.xml");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		demoService = null;
	}
	
	public void testGetListOfAvailableFLVs() {
		Map flvFiles = demoService.getListOfAvailableFLVs();
		Map flvInfo = (Map) flvFiles.get("spark_with_audio.flv");
		String name = (String) flvInfo.get("name");
		String lastModified = (String) flvInfo.get("lastModified");
		String size = (String) flvInfo.get("size");
 		
		assertNotNull(flvFiles);
		assertNotNull(flvInfo);
		assertEquals("spark_with_audio.flv", name);
		assertEquals("07/10/05 15:12:50", lastModified);
		assertEquals("892766", size);
		
	}

}
