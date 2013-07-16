package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class Clustered implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private boolean enabled;
	private String cachingProvider;
	private String cacheService;
	private String cacheGroupAddress;
	private int cacheGroupPort;
	private String memCachedAddress;
	private String jvmRoute;

	public Clustered()
	{
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getCachingProvider()
	{
		return cachingProvider;
	}

	public void setCachingProvider(String cachingProvider)
	{
		this.cachingProvider = cachingProvider;
	}

	public String getCacheService()
	{
		return cacheService;
	}

	public void setCacheService(String cacheService)
	{
		this.cacheService = cacheService;
	}

	public String getCacheGroupAddress()
	{
		return cacheGroupAddress;
	}

	public void setCacheGroupAddress(String cacheGroupAddress)
	{
		this.cacheGroupAddress = cacheGroupAddress;
	}

	public int getCacheGroupPort()
	{
		return cacheGroupPort;
	}

	public void setCacheGroupPort(int cacheGroupPort)
	{
		this.cacheGroupPort = cacheGroupPort;
	}

	public String getMemCachedAddress()
	{
		return memCachedAddress;
	}

	public void setMemCachedAddress(String memCachedAddress)
	{
		this.memCachedAddress = memCachedAddress;
	}

	public String getJvmRoute()
	{
		return jvmRoute;
	}

	public void setJvmRoute(String jvmRoute)
	{
		this.jvmRoute = jvmRoute;
	}
}
