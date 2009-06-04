/*
 * Copyright 2004-2005 the original author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.red5.server.pooling;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.red5.server.jmx.JMXAgent;

/**
 * ThreadPool
 *
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class ThreadPool implements ThreadPoolMBean {

	//private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

	private ExecutorService pool;

	@SuppressWarnings("unused")
	private Executor executor;	
	
	// maximum pool threads
	private int poolSize;

	public ThreadPool() {
		JMXAgent.registerMBean(this, this.getClass().getName(),
				ThreadPoolMBean.class, "threadpool");
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
		if (pool == null) {
			pool = Executors.newFixedThreadPool(poolSize);
		}	
	}

	public void execute(Runnable command) {
		pool.execute(command);
	}
	
	public void shutdown() {
		pool.shutdown();
	}

}