package org.red5.server;

import org.apache.mina.common.TransportType;
import org.apache.mina.io.IoAcceptor;
import org.apache.mina.registry.Service;
import org.apache.mina.registry.ServiceRegistry;
import org.apache.mina.registry.SimpleServiceRegistry;
import org.red5.server.net.IoLoggingFilter;
import org.red5.server.protocol.rtmp.ProtocolHandler;

public class Server {

    private static final int RTMP_PORT = 1935;
    private static final int HTTP_PORT = 8005;

    public static void main( String[] args ) throws Exception
    {
        ServiceRegistry registry = new SimpleServiceRegistry();
        //addLogger( registry );
        
        // Bind
        Service rtmpService = new Service( "rtmp", TransportType.SOCKET, RTMP_PORT );
        registry.bind( rtmpService, new ProtocolHandler() );

        Service remotingService = new Service( "rtmp", TransportType.SOCKET, HTTP_PORT );
        registry.bind( remotingService, new org.red5.server.protocol.remoting.ProtocolHandler() );
        
        System.out.println( "Listening on rtmp port " + RTMP_PORT );
        System.out.println( "Listening on http port " + HTTP_PORT );
    }
    
    private static void addLogger( ServiceRegistry registry )
    {
        IoAcceptor acceptor = registry.getIoAcceptor( TransportType.SOCKET );
        acceptor.getFilterChain().addLast( "logger", new IoLoggingFilter() );
        System.out.println( "Logging ON" );
    }

}
