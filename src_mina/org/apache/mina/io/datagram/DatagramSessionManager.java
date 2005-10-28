/*
 *   @(#) $Id: DatagramSessionManager.java 327113 2005-10-21 06:59:15Z trustin $
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.mina.io.datagram;

import org.apache.mina.common.BaseSessionManager;
import org.apache.mina.io.IoSessionManager;

/**
 * A base class for {@link DatagramAcceptor} and {@link DatagramConnector}.
 * Session interacts with this abstract class instead of those two concrete
 * classes.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev: 327113 $, $Date: 2005-10-21 15:59:15 +0900 $
 */
abstract class DatagramSessionManager extends BaseSessionManager implements IoSessionManager
{
    /**
     * Requests this processor to flush the write buffer of the specified
     * session.  This method is invoked by MINA internally.
     */
    abstract void flushSession( DatagramSession session );

    /**
     * Requests this processor to close the specified session.
     * This method is invoked by MINA internally.
     */
    abstract void closeSession( DatagramSession session );
}