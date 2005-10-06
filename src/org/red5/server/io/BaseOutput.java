package org.red5.server.io;

import java.util.HashMap;
import java.util.Map;

public class BaseOutput {

	protected Map refMap;
	protected short refId = 0;
	
	protected BaseOutput(){
		refMap = new HashMap();
	}
	
	public void storeReference(Object obj){
		refMap.put(obj,new Short(refId++));
	}
	
	public boolean hasReference(Object obj){
		//System.out.println("obj"+obj);
		//System.out.println("simpletest"+obj.hashCode());
		//System.out.println("has reference?"+refMap.containsKey(obj));
		return refMap.containsKey(obj);
	}
	
	public void clearReferences(){
		refMap.clear();
		refId = 0;
	}
	
	protected short getReferenceId(Object obj){
		return ((Short) refMap.get(obj)).shortValue();
	}

}
