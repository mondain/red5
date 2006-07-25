package org.red5.server.net.proxy;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import java.nio.channels.WritableByteChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

public class NetworkDumpFilter extends IoFilterAdapter {

	protected static Log log =
        LogFactory.getLog(ProxyFilter.class.getName());
	
	protected WritableByteChannel raw;
	protected WritableByteChannel headers;
	
	public NetworkDumpFilter(WritableByteChannel headers, WritableByteChannel raw){
		this.raw = raw;
		this.headers = headers;
	}
	
	public void messageReceived(NextFilter next, IoSession session, Object message) throws Exception {
		if(message instanceof ByteBuffer){
			ByteBuffer out = (ByteBuffer) message;
			if(headers != null){
				ByteBuffer header = ByteBuffer.allocate(12);
				header.putLong(System.currentTimeMillis());
				header.putInt(out.limit() - out.position());
				header.flip();
				headers.write( header.buf() );
			}
			if(raw != null){
				raw.write( out.asReadOnlyBuffer().buf() );
			}
		}
		next.messageReceived(session, message);
	}

	public void sessionClosed(NextFilter next, IoSession session) throws Exception {
		if(headers.isOpen()) headers.close();
		if(raw.isOpen()) raw.close();
		next.sessionClosed(session);
	}

}
