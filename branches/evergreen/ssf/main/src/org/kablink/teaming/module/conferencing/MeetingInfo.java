/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.conferencing;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MeetingInfo {
	private String m_meetingId;
	private String m_title;
	private String m_agenda;
	private String m_date;
	private String m_time;
	private int    m_duration;
	private MeetingRecurrance m_recurrance;
	private Vector<String> m_participants;
	private String m_password;
	private MeetingType m_type;
	private String m_hostDisplayName;

	public static enum MeetingRecurrance {None, Daily, Weekly, Monthly};
	public static enum MeetingType { Adhoc, Scheduled };

	public MeetingInfo() {
		m_participants = new Vector<String>();
	}

	public String getMeetingId() {
		return m_meetingId;
	}

	public void setType(MeetingType type) {
		m_type = type;
	}
	
	public MeetingType getType() {
		return m_type;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public String getTitle() {
		return m_title;
	}

	public void setAgenda(String agenda) {
		m_agenda = agenda;
	}

	public String getAgenda() {
		return m_agenda;
	}

	public void setStartDate(String date) {
		m_date = date;
	}

	public String getStartDate() {
		return m_date;
	}

	public void setStartTime(String time) {
		m_time = time;
	}

	public int getStartHour() {
		Integer hour = 0;
		
		if (m_time != null && m_time.length() > 0) {
			String []timeParts = m_time.split(":");
			if (timeParts.length >= 2) {
				if (timeParts[0].startsWith("T")) {
					hour = new Integer(timeParts[0].substring(1, timeParts[0].length()));						 
				} else {
					hour = new Integer(timeParts[0]);  
				}
			}
		}
		return hour;
	}
	
	public int getStartMinute() {
		Integer minute = 0;

		if (m_time != null && m_time.length() > 0) {
			String []timeParts = m_time.split(":");
			if (timeParts.length >= 2) {
				minute = new Integer(timeParts[1]);
			}
		}
		return minute;
	}

	public String getStartTime() {
		return m_time;
	}

	public void setDuration(int time) {
		m_duration = time;
	}

	public int getDuration() {
		return m_duration;
	}

	public void setRecurrance(MeetingRecurrance recurrance) {
		m_recurrance = recurrance;
	}
	
	public MeetingRecurrance getRecurrance() {
		return m_recurrance;
	}

	public void addParticipant(String name, String email) {
		m_participants.add(email);
	}

	public void removeParticipant(String name, String email) {
		for (String partEmail : m_participants) {
			if (partEmail.equalsIgnoreCase(email)) {
				m_participants.remove(partEmail);
			}
		}
	}
	
	public Iterator<String> getParticipantIterator() {
		return m_participants.iterator();
	}

	public void setMeetingPassword(String password) {
		m_password = password;
	}

	public String getMeetingPassword() {
		return m_password;
	}

	public void setHostDisplayName(String displayName) {
		m_hostDisplayName = displayName;
	}

	public String getHostDisplayName() {
		return m_hostDisplayName;
	}
}
