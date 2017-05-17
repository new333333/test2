package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class Environment implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	// JDK Home and Type
	private String jdkHome;
	private String jdkType;

	// What userid to run as (Linux-only)
	// Also what userId and groupId to use
	// as owner of the data directories.
	private String userId;
	private String groupId;

	// Where does the Kablink software reside?
	private String softwareLocation;

	// The default locale to be used by Teaming. Defaults
	// to the i18n.default.locale.* settings in
	// ssf.properties.
	private String defaultLanguage;
	private String defaultCountry;
	
	//The default password into the Teaming keystore.	
	private String keyStorePassword;

	public Environment()
	{
	}

	public String getJdkHome()
	{
		return jdkHome;
	}

	public void setJdkHome(String jdkHome)
	{
		this.jdkHome = jdkHome;
	}

	public String getJdkType()
	{
		return jdkType;
	}

	public void setJdkType(String jdkType)
	{
		this.jdkType = jdkType;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public String getSoftwareLocation()
	{
		return softwareLocation;
	}

	public void setSoftwareLocation(String softwareLocation)
	{
		this.softwareLocation = softwareLocation;
	}

	public String getDefaultLanguage()
	{
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultCountry()
	{
		return defaultCountry;
	}

	public void setDefaultCountry(String defaultCountry)
	{
		this.defaultCountry = defaultCountry;
	}

	public String getKeyStorePassword()
	{
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword)
	{
		this.keyStorePassword = keyStorePassword;
	}
}
