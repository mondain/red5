package org.red5.server.protocol.rtmp;

import org.apache.mina.common.ByteBuffer;

public class RTMPUtils {

	public static void writeMediumInt(ByteBuffer out, int value) {
		byte[] bytes = new byte[3];
		bytes[0] = (byte) ((value >>> 16) & 0x000000FF);
		bytes[1] = (byte) ((value >>> 8) & 0x000000FF);
		bytes[2] = (byte) (value & 0x00FF);
		out.put(bytes);
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

	public static byte encodeHeaderByte(byte headerSize, byte channelId){
		return (byte) ((headerSize << 6) + channelId);
	}
	
	/*
	public static byte decodeHeaderSize(byte header) {
		return (byte) (header >> 2);
	}

	public static byte decodeChannelId(byte header) {
		return (byte) ((header << 2) >> 2);
	}
	*/
	
	public static byte decodeChannelId(byte header) {
		return (byte) (((byte)(header << 2)) >>> 2) ;
	}

	/*
	public static byte decodeHeaderSize(byte header) {
		byte size = (byte) (header >> 6);
		return size;
	}
	*/
	
	public static byte decodeHeaderSize(byte header) {
		int headerInt = (header>=0) ? header : header+256;
		byte size = (byte) (headerInt >> 6);
		return size;
	}

	
}
