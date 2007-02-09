package com.sitescape.team.ic;

import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionResponse;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;

public interface ICBroker {
	
	public static final int[] REGULAR_MEETING = new int[] {0, 0, 0};
	
	public static final int[] SCHEDULED_MEETING = new int[] {1, 0, 0};
		
	public static final int[] CALL = new int[] {0, 768 + 8192, 3072};

	public boolean getScreenNameExists(String zonName);

	public void sendIm(String from, String recipient, String message);

	public String getCommunityId(String communityname);

	public String addMeeting(Set participants, String title,
			String description, String message, String password,
			int scheduleTime, String forumToken, int[] meetingType) throws ICException;

	public String addMeeting(Set memberIds, String title, Binder binder,
			Entry entry, String password, int scheduleTime, String forumToken, int[] meetingType) throws ICException;

}
