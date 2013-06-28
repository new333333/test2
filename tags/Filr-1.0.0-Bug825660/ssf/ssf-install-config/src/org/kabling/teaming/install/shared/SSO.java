package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Single Sign-On Support
 * 
 * <SSO>
 * 
 * iChain/NAM Single Sign-On Support
 * 
 * 
 * To use iChain/NAM SSO, set the enable attribute to true. Set the Logoff URL to the address used to trigger an iChain logoff. Also set the
 * ip address of the iChain proxy server. Only transactions from that address and localhost are allowed.
 **/
public class SSO implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private boolean iChainEnabled;
	private String iChainType;
	private String iChainLogoffUrl;
	private String iChainProxyAddr;
	private boolean iChainWebDAVProxyEnabled;
	private String iChainWebDAVProxyHost;

	/**
	 * IIS for Windows Authentication Single Sign-On Support
	 * 
	 * 
	 * To use IIS for Windows Authentication SSO, set the enable attribute to true. Set the Logoff URL to the address used to trigger an IIS
	 * logoff. Also set the ip IIS server. Only transactions from that address and localhost are allowed.
	 **/
	private boolean winAuthEnabled;
	private String winAuthLogoffUrl;
	private String winAuthProxyAddr;

	public SSO()
	{
	}

	public boolean isiChainEnabled()
	{
		return iChainEnabled;
	}

	public void setiChainEnabled(boolean iChainEnabled)
	{
		this.iChainEnabled = iChainEnabled;
	}

	public String getiChainType()
	{
		return iChainType;
	}

	public void setiChainType(String iChainType)
	{
		this.iChainType = iChainType;
	}

	public String getiChainLogoffUrl()
	{
		return iChainLogoffUrl;
	}

	public void setiChainLogoffUrl(String iChainLogoffUrl)
	{
		this.iChainLogoffUrl = iChainLogoffUrl;
	}

	public String getiChainProxyAddr()
	{
		return iChainProxyAddr;
	}

	public void setiChainProxyAddr(String iChainProxyAddr)
	{
		this.iChainProxyAddr = iChainProxyAddr;
	}

	public boolean isiChainWebDAVProxyEnabled()
	{
		return iChainWebDAVProxyEnabled;
	}

	public void setiChainWebDAVProxyEnabled(boolean iChainWebDAVProxyEnabled)
	{
		this.iChainWebDAVProxyEnabled = iChainWebDAVProxyEnabled;
	}

	public String getiChainWebDAVProxyHost()
	{
		return iChainWebDAVProxyHost;
	}

	public void setiChainWebDAVProxyHost(String iChainWebDAVProxyHost)
	{
		this.iChainWebDAVProxyHost = iChainWebDAVProxyHost;
	}

	public boolean isWinAuthEnabled()
	{
		return winAuthEnabled;
	}

	public void setWinAuthEnabled(boolean winAuthEnabled)
	{
		this.winAuthEnabled = winAuthEnabled;
	}

	public String getWinAuthLogoffUrl()
	{
		return winAuthLogoffUrl;
	}

	public void setWinAuthLogoffUrl(String winAuthLogoffUrl)
	{
		this.winAuthLogoffUrl = winAuthLogoffUrl;
	}

	public String getWinAuthProxyAddr()
	{
		return winAuthProxyAddr;
	}

	public void setWinAuthProxyAddr(String winAuthProxyAddr)
	{
		this.winAuthProxyAddr = winAuthProxyAddr;
	}

}
