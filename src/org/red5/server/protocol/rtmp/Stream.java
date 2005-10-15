package org.red5.server.protocol.rtmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.flv.FLVHeader;
import org.red5.server.io.flv2.FLVReader;
import org.red5.server.io.flv2.FLVTag;
import org.red5.server.protocol.rtmp.status2.StatusObjectService;

public class Stream {
	
	protected static Log log =
        LogFactory.getLog(Stream.class.getName());
	
	public static final byte STATE_STOPPED = 0x00;
	public static final byte STATE_PLAYING = 0x02;
	public static final byte STATE_PAUSED = 0x03;
	public static final byte STATE_END = 0x04;
	
	protected Channel dataChannel = null;
	protected Channel audioChannel = null;
	protected Channel videoChannel = null; 
	protected int source = -1;
	
	protected String flvPath;
	protected int currentTag = 0;
	protected int numTags = 0;
	
	private FLVReader flvReader = null;
	
	// Int used to count upto x number of packets, then the stream will close
	// This is used to stop things going for ever when we learn how to control bandwidth
	protected int killCounter = 0;
	// When the killCounter reaches this point we cut out.. 
	protected int killPoint = 100;
	
	protected SessionHandler sessionHandler;
	protected Connection conn;
	
	protected byte status = STATE_STOPPED;
	
	public Stream(Connection conn, int source, SessionHandler sessionHandler){
		this.source = source;
		this.sessionHandler = sessionHandler;
		this.conn = conn;
		log.debug("Created stream");
	}
	
	public void play(String flvPath){
		
		
		
		this.flvPath = flvPath;
		System.out.println("flvPath" + flvPath);
		// read the flv header here
		// get a hold on the mapped body
		
		// Grab FLVDecoder
		try {
		
			flvReader = new FLVReader(this.flvPath);
			
			dataChannel = conn.getChannel((byte)conn.getNextAvailableChannelId());
			log.debug("data channel: "+dataChannel.getId());
			
			if(flvReader.getHeader().getFlagVideo()){
				videoChannel = conn.getChannel((byte)conn.getNextAvailableChannelId());
				log.debug("video channel: "+videoChannel.getId());
			}
			
			if(flvReader.getHeader().getFlagAudio()){
				audioChannel = conn.getChannel((byte)conn.getNextAvailableChannelId());
				log.debug("audio channel: "+audioChannel.getId());
			}
			
			
			
			
			//conn.close();
			
			
		} catch(Exception ex){
			log.error(ex);
			return;
		}
		
		
		
		log.debug("Stream setup: "+flvPath);
		//writeHeader(header);

		status = STATE_PLAYING;
		
		int clientid = 1; // random
		
		// This is what john sends, so im sending it back
		String details = "0_320_high";
		
		sessionHandler.sendRuntimeStatus(videoChannel, StatusObjectService.NS_PLAY_RESET, details, clientid);
		
		
		
		sessionHandler.sendRuntimeStatus(dataChannel, StatusObjectService.NS_PLAY_START, details, clientid);
		
		// This is wrong its not a function 14, its a notify 12
		sessionHandler.sendNotify(videoChannel, StatusObjectService.NS_DATA_START);
	
		
		
		// send onMetaData down data channel
		
		// start sending video packets down video channel, not doing this yet
		
		writeNextPacket();
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
		return flvReader.hasMoreTags();
	}
	
	protected int statusPacketID = 16777216;
	
	protected boolean continueTag = false;
	protected FLVTag lastTag = null;
	
	public void writeNextPacket(){
		// Still need to figure out how to create packet
		Packet packet = null;
		try {
			log.debug("Send next packet");
			
			FLVTag tag = null;
			
			if(continueTag){
				tag = lastTag;
			} else {
				tag = flvReader.getNextTag();
			}
			
			lastTag = tag;
			
			byte dataType = tag.getDataType();
			
			if(dataType == FLVTag.TYPE_METADATA){
				packet = new Packet(tag.getBody(), tag.getTimestamp(), Packet.TYPE_FUNCTION_CALL_NOREPLY, source);
				dataChannel.writePacket(packet, this);
			}
			else if(dataType == FLVTag.TYPE_VIDEO){
				
				/* doesnt work
				ByteBuffer buff = null;
				ByteBuffer body = tag.getBody(); 
				
				if(body.remaining() > 1024){
					log.debug("Chunking");
					continueTag = true;
					buff = ByteBuffer.allocate(1024);
					int limit = body.limit();
					body.limit(body.position()+1024);
					buff.put(body);
					body.limit(limit);
					buff.flip();
				} else {
					log.debug("Last write");
					continueTag = false;
					buff = body;
				}
				*/
				
				log.info("ts: "+tag.getTimestamp());
				
				//packet = new Packet(buff, tag.getTimestamp(), tag.getDataType(), statusPacketID);
				
				packet = new Packet(tag.getBody(), tag.getTimestamp(), tag.getDataType(), statusPacketID);
				
				
				videoChannel.writePacket(packet, this);
			}
			
			else if(dataType == FLVTag.TYPE_AUDIO){
				packet = new Packet(tag.getBody(), tag.getTimestamp(), tag.getDataType(), statusPacketID);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				audioChannel.writePacket(packet, this);
			} else {
				log.error("Unexpected datatype: "+dataType);
			}
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
