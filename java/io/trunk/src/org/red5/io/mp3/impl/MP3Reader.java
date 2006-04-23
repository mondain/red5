package org.red5.io.mp3.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.io.flv.IFLV;
import org.red5.io.flv.IReader;
import org.red5.io.flv.ITag;
import org.red5.io.flv.impl.Tag;

public class MP3Reader implements IReader {

	protected static Log log =
        LogFactory.getLog(MP3Reader.class.getName());
	
	private FileInputStream fis = null;
	private FileChannel channel = null;
	private MappedByteBuffer mappedFile = null;
	private ByteBuffer in = null;
	private ITag tag = null;
	private int prevSize = 0;
	private double currentTime = 0;
	
	public MP3Reader(FileInputStream stream) {
		fis = stream;
		channel = fis.getChannel();
		try {
			mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mappedFile.order(ByteOrder.BIG_ENDIAN);
		in = ByteBuffer.wrap(mappedFile);
		if (in.limit() > 4)
			searchNextFrame();
	}
	
	/**
	 * Search for next frame sync.
	 */
	public void searchNextFrame() {
		while (in.limit() > 0) {
			int ch = (int) in.get() & 0xff;
			if (ch != 0xff)
				continue;
			
			if ((in.get() & 0xe0) == 0xe0) {
				// Found it
				in.position(in.position()-2);
				return;
			}
		}
	}
	
	public IFLV getFLV() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getBytesRead() {
		return in.position();
	}

	public boolean hasMoreTags() {
		return in.remaining() > 4;
	}

	public ITag readTag() {
		MP3Header header = null;
		while (header == null && in.limit() > 4) {
			try {
				header = new MP3Header(in.getInt());
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				searchNextFrame();
			}
		}
		
		if (header == null)
			return null;
		
		int frameSize = header.frameSize();
		tag = new Tag(ITag.TYPE_AUDIO, (int) currentTime, frameSize + 1 + (header.isProtected() ? 2 : 0), null, prevSize);
		prevSize = frameSize + 1 + (header.isProtected() ? 2 : 0);
		currentTime += header.frameDuration();
		ByteBuffer body = ByteBuffer.allocate(tag.getBodySize());
		byte tagType = (ITag.FLAG_FORMAT_MP3 << 4) | (ITag.FLAG_SIZE_16_BIT << 1);
		switch (header.getSampleRate()) {
		case 44100:
			tagType |= ITag.FLAG_RATE_44_KHZ << 2;
			break;
		case 22050:
			tagType |= ITag.FLAG_RATE_22_KHZ << 2;
			break;
		case 11025:
			tagType |= ITag.FLAG_RATE_11_KHZ << 2;
			break;
		default:
			tagType |= ITag.FLAG_RATE_5_5_KHZ << 2;
		}
		tagType |= (header.isStereo() ? ITag.FLAG_TYPE_STEREO : ITag.FLAG_TYPE_MONO);
		body.put(tagType);
		final int limit = in.limit();
		body.putInt(header.getData());
		if (header.isProtected())
			body.putShort(in.getShort());
		in.limit(in.position()+frameSize-4);
		body.put(in);
		body.flip();
		in.limit(limit);
		
		tag.setBody(body);
	
		return tag;
	}

	public void close() {
		mappedFile.clear();
		if (in != null) {
			in.release();
			in = null;
		}
		try {
			fis.close();
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void decodeHeader() {
	}

	public void position(long pos) {
		in.position((int) pos);
	}

	public KeyFrameMeta analyzeKeyFrames() {
		// TODO Auto-generated method stub
		return null;
	}

}
