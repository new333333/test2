package com.sitescape.team.jobs;
import java.util.List;
import com.sitescape.team.domain.User;

public interface UserTitleChange {
	public final static String USER_TITLE_GROUP="user-title-change";
	public final static String USER_TITLE_DESCRIPTION="re-index entries for user title change";
	public final static String USER_TITLE_JOB="title.job";
	public void schedule(User user, List binderIds, List entryIds);
}
