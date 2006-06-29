package org.red5.io;

/**
 * Constants found in FLV files / streams.
 *
 */
public interface IoConstants {

	public static final byte TYPE_VIDEO = 0x09;
	public static final byte TYPE_AUDIO = 0x08;
	public static final byte TYPE_METADATA = 0x12;
	
	public static final byte MASK_SOUND_TYPE = 0x01;
	public static final byte FLAG_TYPE_MONO = 0x00;
	public static final byte FLAG_TYPE_STEREO = 0x01;
	
	public static final byte MASK_SOUND_SIZE = 0x02;
	public static final byte FLAG_SIZE_8_BIT = 0x00;
	public static final byte FLAG_SIZE_16_BIT = 0x01;
	
	public static final byte MASK_SOUND_RATE = 0x0C;
	public static final byte FLAG_RATE_5_5_KHZ = 0x00;
	public static final byte FLAG_RATE_11_KHZ = 0x01;
	public static final byte FLAG_RATE_22_KHZ = 0x02;
	public static final byte FLAG_RATE_44_KHZ = 0x03;
	
	public static final byte MASK_SOUND_FORMAT = 0xF0 - 0xFF; // unsigned 
	public static final byte FLAG_FORMAT_RAW = 0x00;
	public static final byte FLAG_FORMAT_ADPCM = 0x01;
	public static final byte FLAG_FORMAT_MP3 = 0x02;
	public static final byte FLAG_FORMAT_NELLYMOSER_8_KHZ = 0x05;
	public static final byte FLAG_FORMAT_NELLYMOSER = 0x06;
	
	public static final byte MASK_VIDEO_CODEC = 0x0F;
	public static final byte FLAG_CODEC_H263 = 0x02;
	public static final byte FLAG_CODEC_SCREEN = 0x03;
	public static final byte FLAG_CODEC_VP6 = 0x04;
	
	public static final byte MASK_VIDEO_FRAMETYPE = 0xF0 - 0xFF; // unsigned 
	public static final byte FLAG_FRAMETYPE_KEYFRAME = 0x01;
	public static final byte FLAG_FRAMETYPE_INTERFRAME = 0x02;
	public static final byte FLAG_FRAMETYPE_DISPOSABLE = 0x03;

}
