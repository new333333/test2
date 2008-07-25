package org.apache.directory.server.jndi;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class StupidExecutor extends ThreadPoolExecutor implements java.util.concurrent.Executor
{
	public StupidExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTimeout, TimeUnit unit, BlockingQueue queue)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTimeout, unit, queue);
	}
}
