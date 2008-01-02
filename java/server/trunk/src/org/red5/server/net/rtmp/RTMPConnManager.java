package org.red5.server.net.rtmp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.red5.server.api.scheduling.ISchedulingService;
import org.red5.server.net.rtmpt.RTMPTConnection;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RTMPConnManager implements IRTMPConnManager,
		ApplicationContextAware {
	private ConcurrentMap<Integer, RTMPConnection> connMap = new ConcurrentHashMap<Integer, RTMPConnection>();

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	private ApplicationContext appCtx;

	public RTMPConnection createConnection(Class connCls) {
		if (!RTMPConnection.class.isAssignableFrom(connCls)) {
			return null;
		}
		try {
			RTMPConnection conn = (RTMPConnection) createConnectionInstance(connCls);
			lock.writeLock().lock();
			try {
				int offset = 0;
				int base = conn.hashCode();
				while (offset >= 0) {
					if (!connMap.containsKey(base + offset)) {
						conn.setId(base + offset);
						connMap.put(base + offset, conn);
						break;
					} else if (!connMap.containsKey(base - offset)) {
						conn.setId(base - offset);
						connMap.put(base - offset, conn);
						break;
					}
					offset++;
				}
			} finally {
				lock.writeLock().unlock();
			}
			return conn;
		} catch (Exception e) {
			return null;
		}
	}

	public RTMPConnection getConnection(int clientId) {
		lock.readLock().lock();
		try {
			return connMap.get(clientId);
		} finally {
			lock.readLock().unlock();
		}
	}

	public RTMPConnection removeConnection(int clientId) {
		lock.writeLock().lock();
		try {
			return connMap.remove(clientId);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public Collection<RTMPConnection> removeConnections() {
		ArrayList<RTMPConnection> list = new ArrayList<RTMPConnection>(connMap.size());
		lock.writeLock().lock();
		try {
			list.addAll(connMap.values());
			return list;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void setApplicationContext(ApplicationContext appCtx)
			throws BeansException {
		this.appCtx = appCtx;
	}

	public RTMPConnection createConnectionInstance(Class cls) throws Exception {
		RTMPConnection conn = null;
		if (cls == RTMPMinaConnection.class) {
			conn = (RTMPMinaConnection) appCtx.getBean("rtmpMinaConnection");
		} else if (cls == EdgeRTMPMinaConnection.class) {
			conn = (EdgeRTMPMinaConnection) appCtx
					.getBean("rtmpEdgeMinaConnection");
		} else if (cls == RTMPTConnection.class) {
			conn = (RTMPTConnection) appCtx.getBean("rtmptConnection");
		} else {
			conn = (RTMPConnection) cls.newInstance();
		}
		//set the scheduling service for ez access in the connection
		conn.setSchedulingService((ISchedulingService) appCtx.getBean(ISchedulingService.BEAN_NAME));
		return conn;
	}
}
