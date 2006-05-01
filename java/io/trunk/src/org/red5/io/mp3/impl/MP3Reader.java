package org.red5.io.mp3.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.io.IStreamableFile;
import org.red5.io.ITag;
import org.red5.io.ITagReader;
import org.red5.io.flv.impl.Tag;
import org.red5.io.flv.IKeyFrameDataAnalyzer;
import org.red5.io.flv.IKeyFrameDataAnalyzer.KeyFrameMeta;

public class MP3Reader implements ITagReader, IKeyFrameDataAnalyzer {

	protected static Log log =
        LogFactory.getLog(MP3Reader.class.getName());
	
	private FileInputStream fis = null;
	private FileChannel channel = null;
	private MappedByteBuffer mappedFile = null;
	private ByteBuffer in = null;
	private ITag tag = null;
	private int prevSize = 0;
	private double currentTime = 0;
	private KeyFrameMeta frameMeta = null;
	private HashMap<Integer,Double> posTimeMap = null;
	
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
		while (in.remaining() > 1) {
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
	
	public IStreamableFile getFile() {
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

	public synchronized ITag readTag() {
		MP3Header header = null;
		while (header == null && in.remaining() > 4) {
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
		tag = new Tag(ITag.TYPE_AUDIO, (int) currentTime, frameSize + 1, null, prevSize);
		prevSize = frameSize + 1;
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
		in.limit(in.position()+frameSize-4);
		body.put(in);
		body.flip();
		in.limit(limit);
		
		tag.setBody(body);
	
		return tag;
	}

	public void close() {
		if (posTimeMap != null)
			posTimeMap.clear();
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
		// Advance to next frame
		searchNextFrame();
		// Make sure we can resolve file positions to timestamps
		analyzeKeyFrames();
		Double time = posTimeMap.get(in.position());
		if (time != null)
			currentTime = time.doubleValue();
		else
			// Unknown frame position - this should never happen
			currentTime = 0;
	}

	public synchronized KeyFrameMeta analyzeKeyFrames() {
		if (frameMeta != null)
			return frameMeta;
		
		List<Integer> positionList = new ArrayList<Integer>();
		List<Double> timestampList = new ArrayList<Double>();
		int origPos = in.position();
		in.position(0);
		searchNextFrame();
		double time = 0;
		while (this.hasMoreTags()) {
			MP3Header header = null;
			while (header == null && in.remaining() > 4) {
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
				// No more tags
				break;
			
			int pos = in.position() - 4;
			positionList.add(new Integer(pos));
			timestampList.add(new Double(time));
			time += header.frameDuration();
			in.position(pos + header.frameSize() + (header.isProtected() ? 2 : 0));
		}
		// restore the pos
		in.position(origPos);
		
		posTimeMap = new HashMap<Integer,Double>();
		frameMeta = new KeyFrameMeta();
		frameMeta.positions = new int[positionList.size()];
		frameMeta.timestamps = new int[timestampList.size()];
		for (int i = 0; i < frameMeta.positions.length; i++) {
			frameMeta.positions[i] = positionList.get(i).intValue();
			frameMeta.timestamps[i] = timestampList.get(i).intValue();
			posTimeMap.put(positionList.get(i), timestampList.get(i));
		}
		return frameMeta;
	}

}
