package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class FileConfig implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private String id;
	private String rootDirPath;
	private String simpleFileRepPath;
	private String stellantLinuxFontsPath;
	private String jackRabbitRepPath;
	private String extensionRepPath;
	private String archiveStorePath;
	private String databaseLogStorePath;
	private String cacheStorePath;
	//private ?? luceneIndex;
	
	
	public FileConfig()
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


	public String getRootDirPath()
	{
		return rootDirPath;
	}


	public void setRootDirPath(String rootDirPath)
	{
		this.rootDirPath = rootDirPath;
	}


	public String getSimpleFileRepPath()
	{
		return simpleFileRepPath;
	}


	public void setSimpleFileRepPath(String simpleFileRepPath)
	{
		this.simpleFileRepPath = simpleFileRepPath;
	}


	public String getStellantLinuxFontsPath()
	{
		return stellantLinuxFontsPath;
	}


	public void setStellantLinuxFontsPath(String stellantLinuxFontsPath)
	{
		this.stellantLinuxFontsPath = stellantLinuxFontsPath;
	}


	public String getJackRabbitRepPath()
	{
		return jackRabbitRepPath;
	}


	public void setJackRabbitRepPath(String jackRabbitRepPath)
	{
		this.jackRabbitRepPath = jackRabbitRepPath;
	}


	public String getExtensionRepPath()
	{
		return extensionRepPath;
	}


	public void setExtensionRepPath(String extensionRepPath)
	{
		this.extensionRepPath = extensionRepPath;
	}


	public String getCacheStorePath()
	{
		return cacheStorePath;
	}


	public void setCacheStorePath(String cacheStorePath)
	{
		this.cacheStorePath = cacheStorePath;
	}


	public String getArchiveStorePath() {
		return archiveStorePath;
	}


	public void setArchiveStorePath(String archiveStorePath) {
		this.archiveStorePath = archiveStorePath;
	}

	public String getDatabaseLogStorePath() {
		return databaseLogStorePath;
	}


	public void setDatabaseLogStorePath(String databaseLogStorePath) {
		this.databaseLogStorePath = databaseLogStorePath;
	}
}
