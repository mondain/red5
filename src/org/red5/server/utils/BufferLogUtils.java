package org.red5.server.utils;

import org.apache.commons.logging.Log;
import org.apache.mina.common.ByteBuffer;

public class BufferLogUtils {

	public static void debug(Log log, String msg, ByteBuffer buf){
		if(log.isDebugEnabled()){
			log.debug(msg);
			log.debug("Size: " + buf.remaining());
			log.debug("Data:\n\n" + HexDump.formatHexDump(buf.getHexDump()));
		}
	}
	
}
