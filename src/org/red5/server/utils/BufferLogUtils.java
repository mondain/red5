package org.red5.server.utils;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.amf.AMF;

public class BufferLogUtils {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static void debug(Log log, String msg, ByteBuffer buf){
		if(log.isDebugEnabled()){
			
			log.debug(msg);
			log.debug("Size: " + buf.remaining());
			log.debug("Data:\n\n" + HexDump.formatHexDump(buf.getHexDump()));
			int pos = buf.position();
			int limit = buf.limit();
			
			
			final java.nio.ByteBuffer strBuf = buf.buf();
			final String string = AMF.CHARSET.decode(strBuf).toString();
			buf.position(pos);
			buf.limit(limit);
			log.debug("\n"+string+"\n");
			
			
			//log.debug("Data:\n\n" + b);
		}
	}
	
}
