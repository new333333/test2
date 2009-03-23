/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.ic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.User;


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
