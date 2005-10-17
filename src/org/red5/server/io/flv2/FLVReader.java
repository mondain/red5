package org.red5.server.io.flv2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ByteBufferProxy;
import org.red5.server.io.flv.FLVHeader;
import org.red5.server.protocol.rtmp.RTMPUtils;
import org.red5.server.protocol.rtmp.Stream;
import org.red5.server.utils.HexDump;

public class FLVReader {
	
	protected static Log log =
        LogFactory.getLog(Stream.class.getName());
	
	protected String fileName = null;
	protected FileInputStream fis = null;
	protected FileChannel channel = null;
	protected MappedByteBuffer mappedFile;
	protected ByteBuffer in;
	protected FLVHeader header = new FLVHeader();
	protected int limit = 0;
	
	public static void main(String[] args) throws Exception{
		FLVReader reader = new FLVReader("flvs/nvnlogo1.flv");
	}
	
	public FLVReader(String fileName) throws FileNotFoundException, IOException {
		this.fileName = fileName;
		
		if(log.isDebugEnabled ()) {
			log.debug("Reading: "+fileName);
		}
		
		fis = new FileInputStream(fileName);
		channel = fis.getChannel();
		mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		mappedFile.order(ByteOrder.BIG_ENDIAN);
		in = ByteBuffer.wrap(mappedFile);
		limit = in.limit();
		decodeHeader();
		if(log.isDebugEnabled()) {
			log.debug(header);
		}
		
		/*
		int counter = 0;
		while(hasMoreTags()){
			counter++;
			if(counter>5) return;
			getNextTag();
		}
		*/
		
		//decodeBody();
		
	}
	
	public FLVHeader getHeader() {
		return header;
	}

	protected void decodeHeader(){
		// SIGNATURE, lets just skip
		in.skip(3);
		header.setVersion((byte) in.get());
		header.setTypeFlags((byte) in.get());
		header.setDataOffset(in.getInt());
		//in.skip(header.getDataOffset());
	}
	
	protected void decodeBody(){
		// SIGNATURE, lets just skip
		
		int counter = 0;
		
		while(in.remaining() > 0){
			
			if(log.isDebugEnabled()) {
				log.debug("PREV TAG SIZE: "+ in.getInt());
			}
			counter++;
			
			if(counter > 5) break;
			
			if(in.remaining() == 0) break;
			
			if(log.isDebugEnabled()) {
				log.debug("TAG TYPE: " + in.get());
			}
			
			int dataSize = RTMPUtils.readUnsignedMediumInt(in);
		
			if(log.isDebugEnabled()) {
				log.debug("DATA SIZE: "+dataSize);
				log.debug("TIMESTAMP: "+RTMPUtils.readUnsignedMediumInt(in));
				log.debug("RESERVED: "+ in.getInt());
			}
			
			int limit = in.limit();
			
			in.limit(in.position()+dataSize);
			if(log.isDebugEnabled()) {
				log.debug(HexDump.formatHexDump(in.getHexDump()));
			}
			in.limit(limit);
			
			
			//ByteBuffer.w
			
			//in.skip(dataSize);
				
		}
		//System.out.println("test: " + mappedFile.get());
		// DATA SIZE
		
		//tag.setDataSize(this.readDataSize());
		
		// TIME STAMP
		//tag.setTimeStamp(this.readTimeStamp());
		
		// RESERVED
		//tag.setReserved(mappedFile.getInt());
	}
	
	public FLVTag getNextTag(){
		
		// skip the prev tag size..
		if(log.isDebugEnabled()) {
			log.debug("PREV TAG SIZE: "+ in.getInt());
		}
		
		byte dataType = in.get();
		int bodySize = RTMPUtils.readUnsignedMediumInt(in);
		int timestamp = RTMPUtils.readUnsignedMediumInt(in);
		
		if(log.isDebugEnabled()) {
			log.debug("RESERVED: "+ in.getInt());
		}
		
		ByteBuffer body = ByteBuffer.allocate(bodySize);
		in.limit(in.position()+bodySize);
		body.put(in);
		body.flip();
		in.limit(limit);
		//body.flip();
		
		//in.skip(bodySize);
		
		FLVTag tag = new FLVTag(dataType, timestamp, bodySize, body);
		
		//log.debug(HexDump.formatHexDump(body.getHexDump()));
		
		return tag;
	}
	
	public boolean hasMoreTags(){
		return in.remaining() > 4;
	}
	
}
