package org.red5.server;

import java.util.Iterator;
import java.util.HashMap;

import org.red5.server.api.ISharedObject;
import org.red5.server.context.AppContext;

/**
 * Dummy SO persistence implementation that stores the shared objects in memory.
 * This serves as default persistence if nothing has been configured.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 */
public class SharedObjectRamPersistence implements SharedObjectPersistence {

	private HashMap objects = new HashMap();
	protected AppContext appCtx = null;
	
	public void setApplicationContext(AppContext appCtx) {
		this.appCtx = appCtx;
	}

	public void storeSharedObject(ISharedObject object) {
		this.objects.put(object.getName(), object);
	}

	public ISharedObject loadSharedObject(String name) {
		return (ISharedObject) this.objects.get(name);
	}

	public void deleteSharedObject(String name) {
		this.objects.remove(name);
	}
	
	public Iterator getSharedObjects() {
		return this.objects.values().iterator();
	}

}
