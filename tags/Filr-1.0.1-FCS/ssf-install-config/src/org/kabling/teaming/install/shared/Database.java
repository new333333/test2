package org.kabling.teaming.install.shared;

import java.io.Serializable;
import java.util.List;

/**
 * Database Configuration
 * 
 * Modify the configName to your desired configuration in the Datatabase element below. You must set the configName to the exact
 * configuration in the file: MySQL_Default - For MySQL SQLServer_Default - For Microsoft SQL Server
 * 
 * Change the Resources for the configuration you chose (the defaults are pretty good for a simple configuration with the database running
 * locally, but you will probably have different passwords!).
 **/
public class Database implements Serializable
{

	@Override
	public String toString()
	{
		return "Database [configName=" + configName + ", config=" + config + "]";
	}

	private static final long serialVersionUID = -645027619353219289L;

	private String configName;
	private List<DatabaseConfig> config;

	public Database()
	{
	}

	public String getConfigName()
	{
		return configName;
	}

	public void setConfigName(String configName)
	{
		this.configName = configName;
	}

	public List<DatabaseConfig> getConfig()
	{
		return config;
	}

	public void setConfig(List<DatabaseConfig> config)
	{
		this.config = config;
	}
	
	public DatabaseConfig getDatabaseConfig(String configName)
	{
		if (config != null)
		{
			for (DatabaseConfig obj : config)
			{
				if (obj.getId().equals(configName))
					return obj;
			}
		}
		return null;
	}

}
