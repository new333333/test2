package org.kabling.teaming.install.shared;

import java.io.Serializable;
import java.util.List;

public class InstallerConfig implements Serializable
{
	
	private boolean initialConfiguration;
	
	public enum WebDAV
	{
		BASIC, DIGEST
	}

	public enum EncryptionAlgorithm
	{
		SHA_256, PBE_WITH_MD5_AND_DES
	}

	private static final long serialVersionUID = -7582693324152615159L;

	private String installVersion;
	private String installType;
	private Environment environment;
	private Network network;

	/**
	 * Memory (RAM) Settings
	 * 
	 * Kablink requires a minimum of 512m to operate. 1g is recommended for basic production operation. Generally do not allocate more than
	 * 75% of available physical memory to Kablink.
	 * 
	 * Specify amounts as Nm for N megabytes (e.g., 1500m) or Ng for N gigabytes (e.g., 3g).
	 **/
	private int jvmMemory;

	private RequestsAndConnections requestsAndConnections;
	private FileSystem fileSystem;

	/**
	 * WebDAV Authentication Method
	 * 
	 * Two forms of WebDAV authentication are supported, basic and digest. The choice of which one to select must take the following into
	 * consideration:
	 * 
	 * 1) Windows 7 users will have to modify their registry in order to use basic authentication; - 2) Digest authentication will require
	 * user passwords to be stored using symmetric encryption; and 3) If external authentication services such as Novell Access Manager or
	 * IIS are used to authenticate end users, select basic authentication.
	 * 
	 * Valid method values are 'basic' and 'digest'.
	 **/
	private WebDAV webDav;
	private Database database;
	private Lucene lucene;
	private RSS rss;
	private EmailSettings emailSettings;
	private Presence presence;
	private List<MirroredFolder> mirroredFolderList;
	private SSO sso;
	private Clustered clustered;
	private EncryptionAlgorithm encryption;

	public InstallerConfig()
	{
	}

	public String getInstallVersion()
	{
		return installVersion;
	}

	public void setInstallVersion(String installVersion)
	{
		this.installVersion = installVersion;
	}

	public String getInstallType()
	{
		return installType;
	}

	public void setInstallType(String installType)
	{
		this.installType = installType;
	}

	public Environment getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(Environment environment)
	{
		this.environment = environment;
	}

	public Network getNetwork()
	{
		return network;
	}

	public void setNetwork(Network network)
	{
		this.network = network;
	}

	public int getJvmMemory()
	{
		return jvmMemory;
	}

	public void setJvmMemory(int jvmMemory)
	{
		this.jvmMemory = jvmMemory;
	}

	public RequestsAndConnections getRequestsAndConnections()
	{
		return requestsAndConnections;
	}

	public void setRequestsAndConnections(RequestsAndConnections requestsAndConnections)
	{
		this.requestsAndConnections = requestsAndConnections;
	}

	public FileSystem getFileSystem()
	{
		return fileSystem;
	}

	public void setFileSystem(FileSystem fileSystem)
	{
		this.fileSystem = fileSystem;
	}

	public WebDAV getWebDav()
	{
		return webDav;
	}

	public void setWebDav(WebDAV webDav)
	{
		this.webDav = webDav;
	}

	public Database getDatabase()
	{
		return database;
	}

	public void setDatabase(Database database)
	{
		this.database = database;
	}

	public Lucene getLucene()
	{
		return lucene;
	}

	public void setLucene(Lucene lucene)
	{
		this.lucene = lucene;
	}

	public RSS getRss()
	{
		return rss;
	}

	public void setRss(RSS rss)
	{
		this.rss = rss;
	}

	public EmailSettings getEmailSettings()
	{
		return emailSettings;
	}

	public void setEmailSettings(EmailSettings emailSettings)
	{
		this.emailSettings = emailSettings;
	}

	public Presence getPresence()
	{
		return presence;
	}

	public void setPresence(Presence presence)
	{
		this.presence = presence;
	}

	public List<MirroredFolder> getMirroredFolderList()
	{
		return mirroredFolderList;
	}

	public void setMirroredFolderList(List<MirroredFolder> mirroredFolderList)
	{
		this.mirroredFolderList = mirroredFolderList;
	}

	public SSO getSso()
	{
		return sso;
	}

	public void setSso(SSO sso)
	{
		this.sso = sso;
	}

	public Clustered getClustered()
	{
		return clustered;
	}

	public void setClustered(Clustered clustered)
	{
		this.clustered = clustered;
	}

	public EncryptionAlgorithm getEncryption()
	{
		return encryption;
	}

	public void setEncryption(EncryptionAlgorithm encryption)
	{
		this.encryption = encryption;
	}

	public boolean isInitialConfiguration()
	{
		return initialConfiguration;
	}

	public void setInitialConfiguration(boolean initialConfiguration)
	{
		this.initialConfiguration = initialConfiguration;
	}
}
