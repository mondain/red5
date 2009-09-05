package org.red5.server.plugin.security;

import java.util.Map;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.plugin.IRed5PluginHandler;

public class SecurityBase implements IRed5PluginHandler {

	protected MultiThreadedApplicationAdapter application;
	protected Map<String, Object> properties;
	
	public void setApplication(MultiThreadedApplicationAdapter app)
	{
		application = app;
	}
	
	public void init()
	{
		
	}
	
	public void setProperties(Map<String, Object> props)
	{
		properties = props;
	}
}


