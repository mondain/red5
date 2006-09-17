package org.red5.server.api;

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

/**
 * Mark an object that can be flow-controlled.
 * <p>
 * A flow-controlled object has the bandwidth config property and a link to the
 * parent controllable object.
 * <p>
 * The parent controllable object acts as the bandwidth provider for this
 * object, thus generates a tree structure, in which the <tt>null</tt> parent
 * means the host. The next depth level is the <tt>IClient</tt>. The
 * following is <tt>IStreamCapableConnection</tt>. The deepest level is
 * <tt>IClientStream</tt>.
 * <p>
 * The child node consumes the parent's bandwidth. We say that the child node is
 * the bandwidth consumer while the parent is the bandwidth provider.
 * <p>
 * We predefine the bandwidth configure for host and the host is always a
 * bandwidth provider. While the streams are always the bandwidth consumer. The
 * internal node is both provider and consumer.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Steven Gong (steven.gong@gmail.com)
 */
public interface IFlowControllable {
	/**
	 * Return parent IFlowControllable object
	 * 
	 * @return parent
	 */
	IFlowControllable getParentFlowControllable();

	/**
	 * Return bandwidth configuration object. Bandwidth configuration allows you
	 * to set bandwidth size for audio, video and total amount.
	 * 
	 * @return bandwidth configuration object
	 */
	IBandwidthConfigure getBandwidthConfigure();

	void setBandwidthConfigure(IBandwidthConfigure config);
}
