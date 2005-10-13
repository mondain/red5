/*
 *   @(#) $Id: WriteTimeoutException.java 264677 2005-08-30 02:44:35Z trustin $
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
package org.apache.mina.io;

import java.io.IOException;

import org.apache.mina.common.SessionConfig;

/**
 * An {@link IOException} which is thrown when write buffer is not flushed for
 * {@link SessionConfig#getWriteTimeout()} seconds.
 * 
 * @author Trustin Lee (trustin@apache.org)
 * @version $Rev: 264677 $, $Date: 2005-08-30 11:44:35 +0900 $, 
 */
public class WriteTimeoutException extends IOException
{
    private static final long serialVersionUID = 3906931157944579121L;

	/**
     * Creates a new exception.
     */
    public WriteTimeoutException()
    {
        super();
    }

    /**
     * Creates a new exception.
     */
    public WriteTimeoutException( String s )
    {
        super( s );
    }
}