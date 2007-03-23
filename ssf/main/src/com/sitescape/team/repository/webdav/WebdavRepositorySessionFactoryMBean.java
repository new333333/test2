package com.sitescape.team.repository.webdav;

public interface WebdavRepositorySessionFactoryMBean {

	public String getHostUrl();
	
	public String getContextPath();
	
	public String getDocRootPath();
	
	public String getUsername();
	
	public String getPassword();
	
	public boolean isVersionDeletionAllowed();
}
