package org.red5.io.utils;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.mina.common.ByteBuffer;

public class IOUtils {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static void writeReverseInt(ByteBuffer out, int value) {
		byte[] bytes = new byte[4];
		ByteBuffer rev = ByteBuffer.allocate(4);
		rev.putInt(value);
		rev.flip();
		bytes[3] = rev.get();
		bytes[2] = rev.get();
		bytes[1] = rev.get();
		bytes[0] = rev.get();
		rev.release();
		out.put(bytes);
	}
	
	public static void writeMediumInt(ByteBuffer out, int value) {
		byte[] bytes = new byte[3];
		bytes[0] = (byte) ((value >>> 16) & 0x000000FF);
		bytes[1] = (byte) ((value >>> 8) & 0x000000FF);
		bytes[2] = (byte) (value & 0x00FF);
		out.put(bytes);
	}
	
	public static int readUnsignedMediumInt(ByteBuffer in) {
		byte[] bytes = new byte[3];
		in.get(bytes);
		int val = 0;
		val += (bytes[0] & 0xFF) * 256 * 256;
		val += (bytes[1] & 0xFF) * 256;
		val += (bytes[2] & 0xFF);
		return val;
	}
	
	public static int readMediumInt(ByteBuffer in) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.put((byte)0x00);
		buf.put(in.get());
		buf.put(in.get());
		buf.put(in.get());
		buf.flip();
		return buf.getInt();
	}
	
	public static int readMediumInt2(ByteBuffer in) {
		byte[] bytes = new byte[3];
		in.get(bytes);
		int val = 0;
		val += bytes[0] * 256 * 256;
		val += bytes[1] * 256;
		val += bytes[2];
		if (val < 0)
			val += 256;
		return val;
	}

	public static int readReverseInt(ByteBuffer in) {
		byte[] bytes = new byte[4];
		in.get(bytes);
		int val = 0;
		val += bytes[3] * 256 * 256 * 256;
		val += bytes[2] * 256 * 256;
		val += bytes[1] * 256;
		val += bytes[0];
		return val;
	}
	

	
	public static void debug(Log log, String msg, ByteBuffer buf){
		if(log.isDebugEnabled()){
			
			log.debug(msg);
			log.debug("Size: " + buf.remaining());
			log.debug("Data:\n\n" + HexDump.formatHexDump(buf.getHexDump()));
			
			final String string = toString(buf);
			
			log.debug("\n"+string+"\n");
			
			
			//log.debug("Data:\n\n" + b);
		}
	}

	public static String toString(ByteBuffer buf) {
		int pos = buf.position();
		int limit = buf.limit();
		final java.nio.ByteBuffer strBuf = buf.buf();
		final String string = CHARSET.decode(strBuf).toString();
		buf.position(pos);
		buf.limit(limit);
		return string;
	}
	
}
