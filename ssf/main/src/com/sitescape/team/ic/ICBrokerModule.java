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
package com.sitescape.team.ic;

import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionResponse;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;

public interface ICBrokerModule {
	
	public static final int[] REGULAR_MEETING = new int[] {0, 0, 0};
	
	public static final int[] SCHEDULED_MEETING = new int[] {1, 0, 0};
		
	public static final int[] CALL = new int[] {0, 768 + 8192, 3072};

	public boolean getScreenNameExists(String zonName);

	public void sendIm(String from, String recipient, String message);

	public String getCommunityId(String communityname);

	public String addMeeting(Set participants, String title,
			String description, String message, String password,
			int scheduleTime, String forumToken, int[] meetingType) throws ICException;

	public String addMeeting(Set memberIds, Binder binder,
			Entry entry, String password, int scheduleTime, String forumToken, int[] meetingType) throws ICException;

}
