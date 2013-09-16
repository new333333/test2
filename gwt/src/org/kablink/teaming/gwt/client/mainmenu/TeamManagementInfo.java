/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate team management information between the
 * client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTeamManagementInfo().)
 * 
 * @author drfoster@novell.com
 */
public class TeamManagementInfo implements IsSerializable, VibeRpcResponseData {
	private boolean m_viewAllowed;		// Is viewing teaming membership allowed?
	private String  m_manageUrl;		// The URL for team management.
	private String  m_sendMailUrl;		// The URL to sending mail to the team.
	private String  m_teamMeetingUrl;	// The URL to start a team meeting.

	// Values used for the various team management popup windows.
	public static final int		POPUP_HEIGHT		= 500;
	public static final int		POPUP_WIDTH			= 600;
	public static final String	POPUP_WINDOW_NAME	= "_blank";
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TeamManagementInfo() {
		// Nothing to do.
	}
	
	/**
	 * Returns the URL for team management.
	 * 
	 * @return
	 */
	public String getManageUrl() {
		return m_manageUrl;
	}

	/**
	 * Returns the URL for sending mail.
	 * 
	 * @return
	 */
	public String getSendMailUrl() {
		return m_sendMailUrl;
	}

	/**
	 * Returns the URL for starting a team meeting.
	 * 
	 * @return
	 */
	public String getTeamMeetingUrl() {
		return m_teamMeetingUrl;
	}
	
	/*
	 * Returns true if s refers to a non-null, non-0 length string.
	 */
	private static boolean hasString(String s) {
		return ((null != s) && (0 < s.length()));
	}

	/**
	 * Returns true if team management is allowed.
	 *  
	 * @return
	 */
	public boolean isManageAllowed() {
		return hasString(m_manageUrl);
	}

	/**
	 * Returns true if sending mail to the team is allowed.
	 * 
	 * @return
	 */
	public boolean isSendMailAllowed() {
		return hasString(m_sendMailUrl);
	}
	
	/**
	 * Returns true if any of the team management capabilities are
	 * enabled.
	 * 
	 * @return
	 */
	public boolean isTeamManagementEnabled() {
		return
			(isViewAllowed()     ||
			 isManageAllowed()   ||
			 isSendMailAllowed() ||
			 isTeamMeetingAllowed());
	}
	
	/**
	 * Returns true if starting a team meeting is allowed.
	 * 
	 * @return
	 */
	public boolean isTeamMeetingAllowed() {
		return hasString(m_teamMeetingUrl);
	}
	
	/**
	 * Returns true if viewing the team membership is allowed.
	 * 
	 * @return
	 */
	public boolean isViewAllowed() {
		return m_viewAllowed;
	}

	/**
	 * Stores the URL for team management.
	 * 
	 * @param manageUrl
	 */
	public void setManageUrl(String manageUrl) {
		m_manageUrl = manageUrl;
	}
	
	/**
	 * Stores the URL for sending mail to the team.
	 * 
	 * @param sendMailUrl
	 */
	public void setSendMailUrl(String sendMailUrl) {
		m_sendMailUrl = sendMailUrl;
	}
	
	/**
	 * Stores the URL for starting a team meeting.
	 * 
	 * @param teamMeetingUrl
	 */
	public void setTeamMeetingUrl(String teamMeetingUrl) {
		m_teamMeetingUrl = teamMeetingUrl;
	}
	
	/**
	 * Stores whether viewing team membership is allowed.
	 * 
	 * @param viewAllowed
	 */
	public void setViewAllowed(boolean viewAllowed) {
		m_viewAllowed = viewAllowed;
	}
}
