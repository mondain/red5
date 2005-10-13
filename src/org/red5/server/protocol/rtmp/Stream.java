package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.flv.FLVBody;
import org.red5.server.io.flv.FLVDecoder;
import org.red5.server.io.flv.FLVHeader;
import org.red5.server.io.flv.FLVTag;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;

public class Stream {
	
	protected static Log log =
        LogFactory.getLog(Stream.class.getName());
	
	public static final byte STATE_STOPPED = 0x00;
	public static final byte STATE_PLAYING = 0x02;
	public static final byte STATE_PAUSED = 0x03;
	public static final byte STATE_END = 0x04;
	
	protected Channel dataChannel;
	protected Channel audioChannel;
	protected Channel videoChannel; 
	protected int source = -1;
	
	protected String flvPath;
	protected int currentTag = 0;
	protected int numTags = 0;
	
	private FLVDecoder decoder = null;
	private FLVHeader header = null;
	private FLVBody body = null;
	private FLVTag tag = null;
	
	// Int used to count upto x number of packets, then the stream will close
	// This is used to stop things going for ever when we learn how to control bandwidth
	protected int killCounter = 0;
	// When the killCounter reaches this point we cut out.. 
	protected int killPoint = 100;
	
	protected SessionHandler sessionHandler;

	protected byte status = STATE_STOPPED;
	
	public Stream(Channel data, Channel video, Channel audio, int source, SessionHandler sessionHandler){
		this.dataChannel = data;
		this.audioChannel = audio;
		this.videoChannel = video;
		this.source = source;
		this.sessionHandler = sessionHandler;
		log.debug("Created stream");
	}
	
	public void play(String flvPath){
		this.flvPath = flvPath;
		System.out.println("flvPath" + flvPath);
		// read the flv header here
		// get a hold on the mapped body
		
		// Grab FLVDecoder
		decoder = new FLVDecoder(this.flvPath);
		// Grab FLVHeader
		header = decoder.decodeHeader();
		// Grab FLVBody
		body = decoder.decodeBody();
		log.debug("Stream setup: "+flvPath);
		//writeHeader(header);

		status = STATE_PLAYING;
		
		int clientid = 1; // random
		
		// This is what john sends, so im sending it back
		String details = "0_320_high";
		
		sessionHandler.sendRuntimeStatus(videoChannel, StatusObjectService.NS_PLAY_RESET, details, clientid);
		sessionHandler.sendRuntimeStatus(dataChannel, StatusObjectService.NS_PLAY_START, details, clientid);
		
		// This is wrong its not a function 14, its a notify 12
		sessionHandler.sendStatus(videoChannel, StatusObjectService.NS_DATA_START);
		
		// send onMetaData down data channel
		
		// start sending video packets down video channel, not doing this yet
		
		//writeNextPacket();
	}
	
	public void seek(int pos){
		status = STATE_PLAYING;
		//TODO add seek support
	}
	
	public void end(){
		status = STATE_END;
	}
	
	public boolean hasMorePackets(){		
		if(killCounter >= killPoint) {
			log.debug("Kill point reached, not more packets should be written");
			return false;
		}
		//return (currentTag < numTags);
		return body.hasRemaining();
	}
	
	public void writeHeader(FLVHeader header2) {
		log.debug("Send header");
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put(header2.getSignature());
		buf.put(header2.getVersion());
		
		byte type = (byte) 0x00;
		if(header2.getFlagAudio()) {
			type |= (byte) (0x01 << 2);
		}
		
		if(header2.getFlagVideo()) {
			type |= (byte) 0x01;
		}
		
		buf.put(type);
		buf.putInt(header2.getDataOffset());
		
		int timer = 3;
		byte tagType = Packet.TYPE_VIDEO;
		// Still need to figure out how to create packet
		Packet packet = new Packet(buf, timer, tagType, 3);
		videoChannel.writePacket(packet, this);
		
	}
	
	public void writeNextPacket(){
		videoChannel.writePacket(getNextPacket(), this);
	}
	
	public Packet getNextPacket(){
		// Still need to figure out how to create packet
		Packet packet = null;
		try {
			log.debug("Send next packet");
			//currentTag++;
			// get the next chunk as nio read only buffer
			// create a new packet with correct source etc
			
			// After getting body, we can start calling body.getNextTag();
			// Grab FLVTag
			// a FLVBody consists of a int representing the previousSize and
			// a FLVTag
			int previousTagSize = body.getPreviousTagSize();
			tag = body.getNextTag();
			log.debug("Next tag?");
			byte tagType = (byte) tag.getTagType();
			int dataSize = tag.getDataSize();
			byte[] timeStamp = (byte[]) tag.getTimeStamp();
			int reserved = tag.getReserved();
			byte[] data = (byte[]) tag.getData();
			
			ByteBuffer buf = ByteBuffer.allocate(256);
			buf.setAutoExpand(true);
			buf.putInt(previousTagSize);
			buf.put(tagType);
			buf.putInt(dataSize);
			buf.put(timeStamp);
			buf.putInt(reserved);
			buf.put(data);
			
			buf.flip();
			
			int timer = 1;
			
			log.debug("Creating packet");
			packet = new Packet(buf, timer, tagType, source);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packet;
	}

}
