package org.red5.server.io;

import org.apache.mina.common.ByteBuffer;

public class BufferUtils {


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
	
	public static int put(ByteBuffer out, ByteBuffer in, int numBytesMax){
		final int limit = in.limit();
		final int numBytesRead = (numBytesMax > in.remaining()) ? in.remaining() : numBytesMax;
		in.limit(in.position()+numBytesRead);
		out.put(in);
		in.limit(limit);
		return numBytesRead;
	}
	
}
