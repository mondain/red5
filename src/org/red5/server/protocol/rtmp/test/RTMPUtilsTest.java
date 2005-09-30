package org.red5.server.protocol.rtmp.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.protocol.rtmp.ProtocolHandler;
import org.red5.server.protocol.rtmp.RTMPUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RTMPUtilsTest extends TestCase {

	protected static Log log =
        LogFactory.getLog(RTMPUtilsTest.class.getName());
	
	public void testDecodingHeader(){
		
		log.debug("Testing");
		
		log.debug(""+(0x03 >> 6));
		log.debug(""+(0x43 >> 6));
		log.debug(""+(0x83 >> 6));
		log.debug(""+((byte)(((byte)0xC3) >> 6)));
		Assert.assertEquals(true,false);
	}

}
