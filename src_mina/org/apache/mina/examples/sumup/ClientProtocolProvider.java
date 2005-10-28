/*
 *   @(#) $Id: ClientProtocolProvider.java 327113 2005-10-21 06:59:15Z trustin $
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
package org.apache.mina.examples.sumup;

import org.apache.mina.examples.sumup.codec.SumUpProtocolCodecFactory;
import org.apache.mina.protocol.ProtocolCodecFactory;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolProvider;

/**
 * {@link ProtocolProvider} for SumUp client.
 * 
 * @author The Apache Directory Project
 * @version $Rev: 327113 $, $Date: 2005-10-21 15:59:15 +0900 $,
 */
public class ClientProtocolProvider implements ProtocolProvider
{

    private static final ProtocolCodecFactory CODEC_FACTORY = new SumUpProtocolCodecFactory(
            false );

    private final ProtocolHandler handler;

    public ClientProtocolProvider( int[] values )
    {
        handler = new ClientSessionHandler( values );
    }

    public ProtocolCodecFactory getCodecFactory()
    {
        return CODEC_FACTORY;
    }

    public ProtocolHandler getHandler()
    {
        return handler;
    }
}