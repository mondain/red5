package org.red5.server.stream;

import java.util.Iterator;
import java.util.LinkedList;

import org.red5.server.rtmp.message.Message;

public class MultiStreamSink implements IStreamSink {

	protected LinkedList outs = new LinkedList();

	public void connect(IStreamSink out){
		outs.add(out);
	}
	
	public boolean canAccept(){
		return true;
	}
	
	// push message to all connected streams
	public void enqueue(Message message) {
		final Iterator it = outs.iterator();
		while(it.hasNext()){
			IStreamSink out = (IStreamSink) it.next();
			if(out.canAccept()){
				out.enqueue(message);
			}
		}
	}

	public void close(){
		final Iterator it = outs.iterator();
		while(it.hasNext()){
			IStreamSink out = (IStreamSink) it.next();
			out.close();
		}
		outs.clear();
	}
	
}
