package org.red5.server.net;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.TransportType;
import org.apache.mina.io.IoHandlerAdapter;
import org.apache.mina.protocol.ProtocolProvider;
import org.apache.mina.registry.Service;
import org.apache.mina.registry.ServiceRegistry;
import org.apache.mina.registry.SimpleServiceRegistry;

public class NetworkManager {

	public static final byte STATUS_NETWORK_UP = 0x00;
	public static final byte STATUS_NETWORK_DOWN = 0x01;
	public static final byte STATUS_NETWORK_ERROR = 0x02;
	
	protected static Log log =
        LogFactory.getLog(NetworkManager.class.getName());

	protected byte networkStatus = STATUS_NETWORK_DOWN;
	protected Map serviceConfig;
	protected ServiceRegistry registry;
	
	public NetworkManager(){
		if(log.isDebugEnabled()) log.debug("Creating network service registry");
		registry = new SimpleServiceRegistry(); 
	}
	
	public void setServiceConfig(Map serviceConfig){
		this.serviceConfig = serviceConfig;		
	}
	
	public void up(){
		if(networkStatus == STATUS_NETWORK_UP){
			log.warn("Network is already up, taking no action, call down() first or restart()");
			return;
		}
		log.info("Bringing up network services");
		try{
			// Loop over the serviceConfig
			Iterator it = serviceConfig.keySet().iterator();
			while(it.hasNext()){
				String serviceName = (String) it.next();
				Map conf = (Map) serviceConfig.get(serviceName);
				int port = Integer.parseInt((String) conf.get("port"));
				Object handler = conf.get("handler");
				// TODO: add support for other transport types, socket should be default
				TransportType transportType = TransportType.SOCKET;
				Service service = new Service(serviceName, transportType, port);
				if(handler instanceof IoHandlerAdapter){
					IoHandlerAdapter ioHandlerAdapter = (IoHandlerAdapter) handler;
					if(log.isDebugEnabled())
						log.debug("Binding IO Handler Adapter for: "+service);
					registry.bind(service, ioHandlerAdapter);
				} else if(handler instanceof ProtocolProvider){
					ProtocolProvider protocolProvider = (ProtocolProvider) handler;
					if(log.isDebugEnabled())
						log.debug("Binding Protocol Provider for: "+service);
					registry.bind(service, protocolProvider);
				}
			}
			networkStatus = STATUS_NETWORK_UP;
			log.info("Network services up");
		} catch(Exception ex){
			log.error("Error bringing up network", ex);
			networkStatus = STATUS_NETWORK_ERROR;
		}
	}
	
	public void down(){
		if(networkStatus != STATUS_NETWORK_UP){
			log.warn("Network is already down, taking no action, call up() first.");
			return;
		}
		log.info("Shutting down network services");
		try{
			registry.unbindAll();
			networkStatus = STATUS_NETWORK_DOWN;
			log.info("Network services down");
		} catch(Exception ex){
			log.error("Error shutting down network", ex);
			networkStatus = STATUS_NETWORK_ERROR;
		}
	}
	
	public void restart(){
		if(networkStatus == STATUS_NETWORK_UP){
			down();
		}
		if(networkStatus != STATUS_NETWORK_UP){
			up();
		}
	}
	
}
