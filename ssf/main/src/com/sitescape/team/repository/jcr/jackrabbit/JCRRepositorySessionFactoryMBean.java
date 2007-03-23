package com.sitescape.team.repository.jcr.jackrabbit;

public interface JCRRepositorySessionFactoryMBean {

	public String getRepositoryRootDir();
	
	public String getHomeSubdirName();
	
	public String getConfigFileName();
	
	public String getUsername();
	
	public String getPassword();
	
	public boolean isVersionDeletionAllowed();
}
