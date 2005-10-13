/*
 * @(#) $Id: AnonymousVmPipeAddress.java 264677 2005-08-30 02:44:35Z trustin $
 */
package org.apache.mina.protocol.vmpipe;

import java.net.SocketAddress;

/**
 * A {@link SocketAddress} which represents anonymous in-VM pipe port.
 * 
 * @author Trustin Lee (trustin@apache.org)
 * @version $Rev: 264677 $, $Date: 2005-08-30 11:44:35 +0900 $
 */
class AnonymousVmPipeAddress extends SocketAddress implements Comparable
{
    private static final long serialVersionUID = 3258135768999475512L;

	static final AnonymousVmPipeAddress INSTANCE = new AnonymousVmPipeAddress();

    /**
     * Creates a new instance with the specifid port number.
     */
    private AnonymousVmPipeAddress()
    {
    }

    public int hashCode()
    {
        return 1432482932;
    }

    public boolean equals( Object o )
    {
        if( o == null )
            return false;
        if( this == o )
            return true;
        return o instanceof AnonymousVmPipeAddress;
    }

    public int compareTo( Object o )
    {
        return this.hashCode() - ( ( AnonymousVmPipeAddress ) o ).hashCode();
    }

    public String toString()
    {
        return "vm:anonymous";
    }
}