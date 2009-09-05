package org.red5.server.plugin.security;

import java.util.List;
import org.red5.server.api.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectSecurity;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.ArrayUtils;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SharedObjectSecurityHandler extends SecurityBase implements ISharedObjectSecurity {
  
  private Boolean creationAllowed;
  private Boolean connectionAllowed;
  private Boolean deleteAllowed;
  private Boolean sendAllowed;
  private Boolean writeAllowed;
  private Boolean enableSharedObjects;
  
  private String sharedObjectNames;
  private String[] names;
  private Boolean NamesAuth = false;
  private String[] allowedSharedObjectNames;
  
  private static Logger log = Red5LoggerFactory.getLogger(SharedObjectSecurityHandler.class, "plugins");
	
  public SharedObjectSecurityHandler()
  {
	  allowedSharedObjectNames = this.readValidNames(sharedObjectNames);
		
	  if(this.NamesAuth){
		  log.debug("Authentication of Shared Object Names is enabled");
	  }
  }
  
  public void setConnectionAllowed(Boolean value)
  {
	  connectionAllowed = value;
  }
  
  public void setCreationAllowed(Boolean value)
  {
	  creationAllowed = value;
  }
  
  public void setDeleteAllowed(Boolean value)
  {
	  deleteAllowed = value;
  }
  
  public void setSendAllowed(Boolean value)
  {
	  sendAllowed = value;
  }
  
  public void setWriteAllowed(Boolean value)
  {
	  writeAllowed = value;
  }
  
  public void setEnableSharedObjects(Boolean value)
  {
	  enableSharedObjects = value;
  }
  
  public void setSharedObjectNames(String names)
  {
	  sharedObjectNames = names;
  }
  
  public void init()
  {
	  
  }
  
  private Boolean validate(String name, String[] patterns)
	{
		if (ArrayUtils.indexOf(patterns, name) > 0) return true; 
		return false;
	}
	
	private String[] readValidNames(String fileName)
	{
		String[] namesArray = {};
		
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
		
		    		namesArray[index] =  strLine.toLowerCase();
					index++;
					
					if(strLine == "*")
					{
						log.debug("Found wildcard (*) entry: disabling authentication of HTML file domains ")	;
						NamesAuth = false;
						
					}
				}
		    }
	    
		    in.close();
		} catch (Exception e) {
			log.error(e.getMessage());
			NamesAuth = false;
		}

		return namesArray;
	}
  
  public boolean isConnectionAllowed(ISharedObject so) {
      // Note: we don't check for the name here as only one SO can be
      //       created with this handler.
      return (enableSharedObjects && connectionAllowed);
  }
  
  public boolean isCreationAllowed(IScope scope, String name,
    boolean persistent) {
	
	   if (enableSharedObjects && creationAllowed)
	   {
		  if (NamesAuth &&  !this.validate(name, this.allowedSharedObjectNames ) )
		  {
				log.debug("Authentication failed for shared object name: " + name);
				return false;
		  }
		  return true;
	   }
      return false;
  }
  
  public boolean isDeleteAllowed(ISharedObject so, String key) {
      return deleteAllowed;
  }
  
  public boolean isSendAllowed(ISharedObject so, String message,
    List arguments) {
      return sendAllowed;
  }
  
  public boolean isWriteAllowed(ISharedObject so, String key,
    Object value) {
      return writeAllowed;
  }
  
}