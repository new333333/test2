package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class DatabaseConfig implements Serializable
{

	@Override
	public String toString()
	{
		return "DatabaseConfig [id=" + id + ", type=" + type + ", resourceFor=" + resourceFor + ", resourceUrl="
				+ resourceUrl + ", resourceUserName=" + resourceUserName + ", resourcePassword=" + resourcePassword
				+ ", resourceDriverClassName=" + resourceDriverClassName + "]";
	}

	public enum DatabaseType
	{
		MYSQL, SQLSERVER, ORACLE
	}

	private static final long serialVersionUID = -645027619353219289L;

	private String id;
	private DatabaseType type;
	private String resourceFor;
	private String resourceUrl;
	private String resourceUserName;
	private String resourcePassword;
	private String resourceDriverClassName;
	private String resourceDatabase;

	// Utility variable
	private String resourceHost;

	public DatabaseConfig()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public DatabaseType getType()
	{
		return type;
	}

	public void setType(DatabaseType type)
	{
		this.type = type;
	}

	public String getResourceFor()
	{
		return resourceFor;
	}

	public void setResourceFor(String resourceFor)
	{
		this.resourceFor = resourceFor;
	}

	public String getResourceUrl()
	{
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl)
	{
		this.resourceUrl = resourceUrl;
	}

	public String getResourceUserName()
	{
		return resourceUserName;
	}

	public void setResourceUserName(String resourceUserName)
	{
		this.resourceUserName = resourceUserName;
	}

	public String getResourcePassword()
	{
		return resourcePassword;
	}

	public void setResourcePassword(String resourcePassword)
	{
		this.resourcePassword = resourcePassword;
	}

	public String getResourceDriverClassName()
	{
		return resourceDriverClassName;
	}

	public void setResourceDriverClassName(String resourceDriverClassName)
	{
		this.resourceDriverClassName = resourceDriverClassName;
	}

	public String getResourceHost()
	{
		return resourceHost;
	}

	public void setResourceHost(String resourceHost)
	{
		this.resourceHost = resourceHost;
	}
	
	public String getHostNameFromUrl()
	{
		String url = getResourceUrl();
		// url example jdbc:jtds:sqlserver://localhost/sitescape;useCursors=true
		String pattern = "//";

		if (url != null)
		{
			int startIndex = url.indexOf(pattern) + pattern.length();
			int endIndex = url.indexOf(":", startIndex);
			url = url.substring(startIndex, endIndex);
			return url;
		}
		return url;
	}

	public String getResourceDatabase()
	{
		if (resourceDatabase != null)
			return resourceDatabase;
		
		return getDbName(resourceUrl);
	}

	public void setResourceDatabase(String resourceDatabase)
	{
		this.resourceDatabase = resourceDatabase;
	}
	
	private String getDbName(String url)
	{
		// url example jdbc:jtds:sqlserver://localhost/sitescape;useCursors=true
		String pattern = "//";
		int startIndex = url.indexOf(pattern) + pattern.length();
		
		//Should give the url from localhost/
		url = url.substring(startIndex);
		
		//Get the starting from the end of address
		startIndex = url.indexOf("/") +1;
		
		//Ends with ?
		int endIndex = url.indexOf("?", startIndex);
	
		url = url.substring(startIndex, endIndex);
		return url;
	}

}
