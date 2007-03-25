package com.sitescape.team.module.folder.impl;

public interface FolderModuleImplMBean {

	public void clearStatistics();
	
	public int getAddEntryCount();
	public int getModifyEntryCount();
	public int getDeleteEntryCount();
	
	public int getAddReplyCount();
	
	public int getAddFolderCount();
}
