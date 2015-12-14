package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class HASearchNode implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private String name;
	private String title;
	private String hostName;
	private int rmiPort;

	public HASearchNode()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public int getRmiPort()
	{
		return rmiPort;
	}

	public void setRmiPort(int rmiPort)
	{
		this.rmiPort = rmiPort;
	}

	
	@Override
	public String toString()
	{
		return "HASearchNode [name=" + name + ", title=" + title + ", hostName=" + hostName + ", rmiPort=" + rmiPort + "]";
	}
	
	
}
