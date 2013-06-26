package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Novell Messenger Presence Configuration Settings
 * 
 * 
 * Use these settings to link your Teaming and Novell Messenger servers together. Leave the presence.service.enable setting at false if you
 * do not use the Novell Messenger software.
 **/
public class Presence implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private boolean resourceEnabled;
	private String resourceAddress;
	private int resourcePort;
	private String resourceCert;
	private String resourceUserDn;
	private String resourceUserPassword;

	public Presence()
	{
	}

	public boolean isResourceEnabled()
	{
		return resourceEnabled;
	}

	public void setResourceEnabled(boolean resourceEnabled)
	{
		this.resourceEnabled = resourceEnabled;
	}

	public String getResourceAddress()
	{
		return resourceAddress;
	}

	public void setResourceAddress(String resourceAddress)
	{
		this.resourceAddress = resourceAddress;
	}

	public int getResourcePort()
	{
		return resourcePort;
	}

	public void setResourcePort(int resourcePort)
	{
		this.resourcePort = resourcePort;
	}

	public String getResourceCert()
	{
		return resourceCert;
	}

	public void setResourceCert(String resourceCert)
	{
		this.resourceCert = resourceCert;
	}

	public String getResourceUserDn()
	{
		return resourceUserDn;
	}

	public void setResourceUserDn(String resourceUserDn)
	{
		this.resourceUserDn = resourceUserDn;
	}

	public String getResourceUserPassword()
	{
		return resourceUserPassword;
	}

	public void setResourceUserPassword(String resourceUserPassword)
	{
		this.resourceUserPassword = resourceUserPassword;
	}
}
