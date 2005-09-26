package org.red5.server.io;

import java.util.HashMap;
import java.util.Map;

public class BaseInput {

	protected Map refMap = new HashMap();
	protected short refId = 0;
	
	public void storeReference(Object obj){
		refMap.put(new Short(refId++),obj);
	}
	
	public void clearReferences(){
		refMap.clear();
		refId = 0;
	}
	
	protected Object getReference(short id){
		return refMap.get(new Short(id));
	}

}
