/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.jobs;

public interface FolderDelete {
	public final static String FOLDER_DELETE_GROUP="folder-delete";
	public final static String FOLDER_DELETE_DESCRIPTION="complete folder delete";
	public final static String DELETE_JOB="delete.job";
	public final static String DELETE_HOURS="timeout.hours";
	public void schedule(Long zoneId, int hours);
}
