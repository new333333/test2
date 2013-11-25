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
package org.kablink.teaming.gwt.client;


import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a User.
 * 
 * @author drfoster@novell.com
 */
public class GwtUser extends GwtTeamingItem implements IsSerializable {
	private String m_name;
	private String m_title;
	private String m_userId;
	private String m_viewUrl;
	private String m_wsId;
	private String m_wsTitle;
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtUser() {
		// Nothing to do.
	}	
	
	/**
	 * Returns the user's name.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns the user's title.
	 */
	public String getTitle() {
		return m_title;
	}
	
	/**
	 * Returns the user's ID. 
	 */
	public String getUserId() {
		return m_userId;
	}
	
	/**
	 * Returns the user's workspace ID. 
	 */
	public String getWorkspaceId() {
		return m_wsId;
	}
	
	/**
	 * Returns the user's workspace title.
	 */
	public String getWorkspaceTitle() {
		return m_wsTitle;
	}
	
	/**
	 * Return the name of the parent binder.
	 * 
	 * Implements the GwtTeamingItem.getSecondaryDisplayText() abstract
	 * method.
	 */
	@Override
	public String getSecondaryDisplayText() {
		return "";
	}
		
	/**
	 * Return the name that should be displayed when this entry is
	 * displayed.
	 * 
	 * Implements the GwtTeamingItem.getShortDisplayName() abstract
	 * method.
	 */
	@Override
	public String getShortDisplayName() {
		return getWorkspaceTitle();
	}
		
	/**
	 * Return the URL that can be used to view this user's workspace.
	 */
	public String getViewWorkspaceUrl() {
		return m_viewUrl;
	}
	
	/**
	 * Stores the user's name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Stores the user's ID. 
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		m_userId = userId;
	}
	
	/**
	 * Stores the user's ID.
	 * 
	 * @param userId
	 */
	public void setUserId(Long userId) {
		setUserId(String.valueOf(userId));
	}
	
	/**
	 * Stores the user's workspace ID. 
	 * 
	 * @param wsId
	 */
	public void setWorkspaceId(String wsId) {
		m_wsId = wsId;
	}
	
	/**
	 * Stores the user's workspace ID.
	 * 
	 * @param wsId
	 */
	public void setWorkspaceId(Long wsId) {
		setWorkspaceId(String.valueOf(wsId));
	}
	
	/**
	 * Stores the user's title. 
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = title;
	}
	
	/**
	 * Stores the URL that can be used to view the user's workspace.
	 * 
	 * @param url
	 */
	public void setViewWorkspaceUrl(String url) {
		m_viewUrl = url;
	}
	
	/**
	 * Stores the user's workspace title.
	 * 
	 * @param wsTitle
	 */
	public void setWorkspaceTitle(String wsTitle) {
		m_wsTitle = wsTitle;
	}
}
