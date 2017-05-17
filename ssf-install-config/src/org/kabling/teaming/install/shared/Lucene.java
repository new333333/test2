package org.kabling.teaming.install.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lucene Configuration Settings
 * 
 * 
 * The Lucene index can be run "local" (within the context of this application); "server" (run as its own server on this system or on a
 * remote system) or "high availability" (with multiple remote servers.)
 * 
 * Note: The rmi port need only be set if running in server mode. (And then, only if the default port cannot be used.
 **/
public class Lucene implements Serializable
{

	@Override
	public String toString()
	{
		return "Lucene [location=" + location + ", indexHostName=" + indexHostName + ", maxBooleans=" + maxBooleans
				+ ", highAvailabilitySearchNodes=" + highAvailabilitySearchNodes + ", mergeFactor=" + mergeFactor
				+ ", rmiPort=" + rmiPort + ", searchNodesList=" + searchNodesList + "]";
	}

	private static final long serialVersionUID = -645027619353219289L;

	private String location;
	private String indexHostName;
	private int maxBooleans;
	private int highAvailabilitySearchNodes;
	private int mergeFactor;
	private int rmiPort;
	
	private String serverPassword;
	private String serverLogin;
	
	private List<HASearchNode> searchNodesList;

	public Lucene()
	{
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getIndexHostName()
	{
		return indexHostName;
	}

	public void setIndexHostName(String indexHostName)
	{
		this.indexHostName = indexHostName;
	}

	public int getMaxBooleans()
	{
		return maxBooleans;
	}

	public void setMaxBooleans(int maxBooleans)
	{
		this.maxBooleans = maxBooleans;
	}

	public int getHighAvailabilitySearchNodes()
	{
		return highAvailabilitySearchNodes;
	}

	public void setHighAvailabilitySearchNodes(int highAvailabilitySearchNodes)
	{
		this.highAvailabilitySearchNodes = highAvailabilitySearchNodes;
	}

	public int getMergeFactor()
	{
		return mergeFactor;
	}

	public void setMergeFactor(int mergeFactor)
	{
		this.mergeFactor = mergeFactor;
	}

	public int getRmiPort()
	{
		return rmiPort;
	}

	public void setRmiPort(int rmiPort)
	{
		this.rmiPort = rmiPort;
	}

	public List<HASearchNode> getSearchNodesList()
	{
		if (searchNodesList == null)
		{
			searchNodesList = new ArrayList<HASearchNode>();
		}
		return searchNodesList;
	}

	public void setSearchNodesList(List<HASearchNode> searchNodesList)
	{
		this.searchNodesList = searchNodesList;
	}

	public String getServerPassword()
	{
		return serverPassword;
	}

	public void setServerPassword(String serverPassword)
	{
		this.serverPassword = serverPassword;
	}

	public String getServerLogin()
	{
		return serverLogin;
	}

	public void setServerLogin(String serverLogin)
	{
		this.serverLogin = serverLogin;
	}
	
}
