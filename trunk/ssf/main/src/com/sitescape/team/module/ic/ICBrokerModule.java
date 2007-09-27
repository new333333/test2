/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.ic;

import java.util.List;
import java.util.Map;
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

	public boolean getScreenNameExists(String zonName) throws ICException;

	public void sendIm(String from, String recipient, String message) throws ICException;

	public String getCommunityId(String communityname) throws ICException;

	public String addMeeting(Set participants, String title,
			String description, String message, String password,
			int scheduleTime, String forumToken, int[] meetingType) throws ICException;

	public String addMeeting(Set memberIds, Binder binder,
			Entry entry, String password, int scheduleTime, String forumToken, int[] meetingType) throws ICException;
	
	public Map getMeetingRecords(String meetingId) throws ICException;
	
	public List getDocumentList(String meetingId) throws ICException;
	
	public List findUserMeetings(String screenName) throws ICException;
	
	/**
	 * 
	 * @param held - <code>-1</code> - none 
	 */
	public Map getUserMeetingAttachments(String screenName, int held) throws ICException;
	
	public boolean removeRecordings(String meetingId, String recordingURL) throws ICException;
		
	public boolean isEnabled();

	public String getBASE64AuthorizationToken();
	
}
