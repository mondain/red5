package org.red5.server.api.test;

import static junit.framework.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.red5.server.api.IScope;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventListener;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectService;
import org.red5.server.so.ScopeWrappingSharedObjectService;

public class SharedObjectTest extends BaseTest 
	implements IEventListener {

	protected String name = "testso";
	
	@Test public void sharedObjectService(){
		IScope scope = context.resolveScope(host,path_app);
		ISharedObjectService service = new ScopeWrappingSharedObjectService(scope);
		assertTrue("should be empty",!service.hasSharedObject("blah"));
		assertTrue("create so",service.createSharedObject(name,false));
		assertTrue("so exists?",service.hasSharedObject(name));
		ISharedObject so = service.getSharedObject(name);
		assertTrue("so not null",so!=null);
		assertTrue("name same",so.getName().equals(name));
		assertTrue("persistent",!so.isPersistant());
		so.addEventListener(this);
		so.setAttribute("this","that");
	}
	

	public void notifyEvent(IEvent event) {
		log.debug("Event: "+event);
	}



	public static junit.framework.Test suite(){
		return new JUnit4TestAdapter(SharedObjectTest.class);
	}
	
}
