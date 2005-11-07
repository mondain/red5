package org.red5.server.stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Message;
import org.red5.server.rtmp.message.Status;

public class Stream implements Constants, IStream, IStreamSink {
	
	protected static Log log =
        LogFactory.getLog(Stream.class.getName());
	
	private int writeQueue = 0;
	
	private long startTime = 0;
	private long startTS = 0;
	private long currentTS = 0;
	
	private DownStreamSink downstream = null;
	private IStreamSink upstream = null;
	private IStreamSource source = null;
	
	private int streamId = 0;
	
	public Stream(){
	}
	
	public int getStreamId() {
		return streamId;
	}
	
	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}

	public DownStreamSink getDownstream() {
		return downstream;
	}

	public void setDownstream(DownStreamSink downstream) {
		this.downstream = downstream;
	}

	public IStreamSource getSource() {
		return source;
	}

	public void setSource(IStreamSource source) {
		this.source = source;
	}

	public IStreamSink getUpstream() {
		return upstream;
	}

	public void setUpstream(IStreamSink upstream) {
		this.upstream = upstream;
	}

	public void start(){
		startTime = System.currentTimeMillis();
		
		Status reset = new Status(Status.NS_PLAY_RESET);
		Status start = new Status(Status.NS_PLAY_START);
		reset.setClientid(1);
		start.setClientid(1);
		
		downstream.getVideo().sendStatus(reset);
		downstream.getData().sendStatus(start);
		downstream.getVideo().sendStatus(new Status(Status.NS_DATA_START));
		
		/*
		if(source!=null && source.hasMore()){
			write(source.dequeue());
		}
		*/
	}
	
	public void stop(){
		
	}

	public long getBufferLength(){
		final long now = System.currentTimeMillis();
		final long time = now - startTime;
		final long sentTS = currentTS - startTS;
		return time - sentTS;
	}
	
	public boolean canAccept(){
		return downstream.canAccept();
	}
	
	public void enqueue(Message message){ 
		write(message);
	}
	
	protected void write(Message message){
		if(downstream.canAccept()){
			writeQueue++;
			currentTS = message.getTimestamp();
			downstream.enqueue(message);
		}
	}
	
	public void publish(Message message){
		if(upstream.canAccept()){
			upstream.enqueue(message);
		}
	}
	
	public void written(Message message){
		writeQueue--;
		if(source !=null && source.hasMore()){
			write(source.dequeue());
		}
	}
	
	public void close(){
		if(upstream!=null) upstream.close();
		if(downstream!=null) downstream.close();
		if(source!=null) source.close();
	}
	
}
