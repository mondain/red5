package org.red5.server.plugin.security;

import org.red5.server.api.IScope;
import org.red5.server.api.stream.IStreamPublishSecurity;

import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.ArrayUtils;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PublishSecurityHandler extends SecurityBase implements IStreamPublishSecurity {
	
	private Boolean enablePublish = true;
    private String[] names;
    private String publishNames;
    private Boolean NamesAuth = false;
    private HashMap<String,String[]> allowedPublishNames;
    
    private static Logger log = Red5LoggerFactory.getLogger(PublishSecurityHandler.class, "securityTest");
	
	
	public PublishSecurityHandler()
    {
		allowedPublishNames = this.readValidNames(publishNames);
		
		if(this.NamesAuth){
			log.debug("Authentication of Publish Names is enabled");
		}
    }
	
	public void setEnablePublish(Boolean value)
	{
		enablePublish = value;
	}
    
	public void setPublishNames(String names)
	{
		publishNames = names;
	}
	
	public boolean isPublishAllowed(IScope scope, String name, String mode) {
		
		if (enablePublish)
		{
			if (NamesAuth &&  !this.validate(name, mode, this.allowedPublishNames ) )
			{
				log.debug("Authentication failed for publish name: " + name);
				return false;
			}
			return true;
		}
		
		return false;
    }
	
	private Boolean validate(String name, String mode, HashMap<String,String[]> patterns)
	{
		if (!patterns.get(name).equals(null))
		{
			String[] modes = patterns.get(name);
			if (ArrayUtils.indexOf(modes, mode) > 0) return true; 
		}
		return false;
	}
	
	private HashMap readValidNames(String fileName)
	{
		String[] namesArray = {};
		
		HashMap map = new HashMap();
		
		try {
			NamesAuth = true;
			//FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(application.getResource(fileName).getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			
			int index = 0;
			int lineCount = 0;
			
			String strLine = "";
		   
		    while ((strLine = br.readLine()) != null)   {
		    	if( strLine.equals("")  || strLine.indexOf("#") == 0)
				{
					continue;
				}
		    	
		    	if(strLine.indexOf(" ") < 0)
				{
		    		String line = strLine.toLowerCase();
		    		String[] nameMode = line.split(";");
		    		String name = nameMode[0];
		    		String[] modes = nameMode[1].split(",");
		    		
		    		map.put(name, modes);
		    		
		    		//namesArray[index]
		    		//namesArray[index] =  strLine.toLowerCase();
					//index++;
					
					if(strLine == "*")
					{
						log.debug("Found wildcard (*) entry: disabling authentication of publish names ");
						NamesAuth = false;
						
					}
				}
		    }
	    
		    in.close();
		} catch (Exception e) {
			log.error(e.getMessage());
			NamesAuth = false;
		}
		
		return map;
		//return namesArray;
	}
    
}