package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Requests and Connections
 * 
 * The following control aspects regarding Tomcat requests and database connections that can be configured based on performance
 * requirements.
 * 
 * maxThreads - The maximum number of request processing threads to be created by a Tomcat connector. This determines the maximum number of
 * simultaneous requests that can be handled.
 * 
 * maxActive - The maximum number of connections that can be allocated from this pool at the same time.
 * 
 * maxIdle - The maximum number of connections that can sit idle in this pool at the same time.
 **/

public class RequestsAndConnections implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private int maxThreads;
	private int maxActive;
	private int maxIdle;
	private int schedulerThreads;

	public RequestsAndConnections()
	{
	}

	public int getMaxThreads()
	{
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads)
	{
		this.maxThreads = maxThreads;
	}

	public int getMaxActive()
	{
		return maxActive;
	}

	public void setMaxActive(int maxActive)
	{
		this.maxActive = maxActive;
	}

	public int getMaxIdle()
	{
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle)
	{
		this.maxIdle = maxIdle;
	}

	public int getSchedulerThreads()
	{
		return schedulerThreads;
	}

	public void setSchedulerThreads(int schedulerThreads)
	{
		this.schedulerThreads = schedulerThreads;
	}
	
	
}
