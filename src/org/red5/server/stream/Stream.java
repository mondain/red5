package org.red5.server.stream;

import org.red5.server.rtmp.message.Constants;
import org.red5.server.rtmp.message.Message;

public class Stream implements Constants, IStream, IStreamSink {
	
	private int writeQueue = 0;
	
	private long startTime = 0;
	private long startTS = 0;
	private long currentTS = 0;
	
	private IStreamSink downstream = null;
	private IStreamSink upstream = null;
	private IStreamSource source = null;
	
	public Stream(){
	}
	
	public IStreamSink getDownstream() {
		return downstream;
	}

	public void setDownstream(IStreamSink downstream) {
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
		if(source!=null && source.hasMore()){
			write(source.dequeue());
		}
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
