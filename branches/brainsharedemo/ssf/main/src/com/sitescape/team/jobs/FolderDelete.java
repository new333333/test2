package com.sitescape.team.jobs;

public interface FolderDelete {
	public final static String FOLDER_DELETE_GROUP="folder-delete";
	public final static String FOLDER_DELETE_DESCRIPTION="complete folder delete";
	public final static String DELETE_JOB="delete.job";
	public final static String DELETE_HOURS="timeout.hours";
	public void schedule(Long zoneId, int hours);
}
