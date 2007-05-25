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
import java.util.List;
import com.sitescape.team.domain.User;

public interface UserTitleChange {
	public final static String USER_TITLE_GROUP="user-title-change";
	public final static String USER_TITLE_DESCRIPTION="re-index entries for user title change";
	public final static String USER_TITLE_JOB="title.job";
	public void schedule(User user, List binderIds, List entryIds);
}
