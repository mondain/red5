package org.red5.server.messaging;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright ? 2006 by respective authors (see below). All rights reserved.
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

import java.util.List;
import java.util.Map;

/**
 * Output Endpoint for a provider to connect.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Steven Gong (steven.gong@gmail.com)
 */
public interface IMessageOutput {
	/**
	 * Push a message to this output endpoint. May block the pusher when output
	 * can't handle the message at the time.
	 * 
	 * @param message
	 *            Message to be pushed.
	 */
	void pushMessage(IMessage message);

	/**
	 * Connect to a provider.
	 * 
	 * @param provider
	 * @param paramMap
	 * @return <tt>true</tt> when successfully subscribed, <tt>false</tt>
	 *         otherwise.
	 */
	boolean subscribe(IProvider provider, Map paramMap);

	/**
	 * Disconnect from a provider.
	 * 
	 * @param provider
	 * @return <tt>true</tt> when successfully unsubscribed, <tt>false</tt>
	 *         otherwise.
	 */
	boolean unsubscribe(IProvider provider);

	List<IProvider> getProviders();

	/**
	 * Send OOB Control Message to all consumers on the other side of pipe.
	 * 
	 * @param provider
	 *            The provider that sends the message
	 * @param oobCtrlMsg
	 */
	void sendOOBControlMessage(IProvider provider, OOBControlMessage oobCtrlMsg);
}
