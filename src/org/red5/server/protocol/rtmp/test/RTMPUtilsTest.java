package org.red5.server.protocol.rtmp.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.protocol.rtmp.RTMPUtils;
import org.red5.server.utils.HexDump;

public class RTMPUtilsTest extends TestCase {

	protected static Log log =
        LogFactory.getLog(RTMPUtilsTest.class.getName());
	
	public void testDecodingHeader(){
		
		log.debug("Testing");
		/*
		log.debug(""+(0x03 >> 6));
		log.debug(""+(0x43 >> 6));
		log.debug(""+(0x83 >> 6));
		log.debug(""+((byte)(((byte)0xC3) >> 6)));
		*/
		byte test; 
		test = (byte) (0x03);
		log.debug(HexDump.byteArrayToHexString(new byte[]{test}));
		log.debug(""+test);
		log.debug(""+ RTMPUtils.decodeHeaderSize(test) );
		
		test = (byte) (0x43);
		log.debug(HexDump.byteArrayToHexString(new byte[]{test}));
		log.debug(""+test);
		log.debug(""+ RTMPUtils.decodeHeaderSize(test) );
		
		test = (byte) (0x83);
		log.debug(HexDump.byteArrayToHexString(new byte[]{test}));
		log.debug(""+test);
		log.debug(""+ RTMPUtils.decodeHeaderSize(test) );

		test = (byte) (0xC3 - 256);
		log.debug(HexDump.byteArrayToHexString(new byte[]{test}));
		log.debug(""+test);
		log.debug(""+ RTMPUtils.decodeHeaderSize(test) );
		
		Assert.assertEquals(true,false);
	}

}
