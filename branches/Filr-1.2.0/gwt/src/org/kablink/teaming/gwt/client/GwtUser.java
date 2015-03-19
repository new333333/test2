/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.PrincipalType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used in GWT RPC calls to represent a User.
 * 
 * @author drfoster@novell.com
 */
public class GwtUser extends GwtPrincipal
	implements IsSerializable, VibeRpcResponseData
{
	private String m_name;
	private String m_title;
	private String m_userId;
	private String m_viewUrl;
	private String m_wsId;
	private String m_wsTitle;
	private String m_email;
	private String m_avatarUrl;
	private ExtUserProvState m_extUserProvState;
	private PrincipalType m_principalType = PrincipalType.UNKNOWN;
	private boolean m_disabled = false;
	
	/**
	 * This represents the provisioned state of an external user.
	 * Keep this up-to-date with ExtProvState in User.java
	 */
	public enum ExtUserProvState implements IsSerializable
	{
		INITIAL,
		
		/**
		 * The invited external user responded, and through self-provisioning interface
		 * successfully supplied his credential to use with local Filr authentication
		 * in the future. The account still needs to be confirmed/verified before
		 * the user can actually log into Fir using the specified credential and
		 * start accessing data. 
		 */
		CREDENTIALED,

		/**
		 * The user has been successfully verified and is ready to use the system.
		 * Verification is needed/performed only once for each user account.
		 */
		VERIFIED,
		
		/**
		 * The user has requested to reset their password
		 */
		PWD_RESET_REQUESTED,
		
		/**
		 * The user has reset their password and has not responded to the verification email.
		 */
		PWD_RESET_WAITING_FOR_VERIFICATION,
		
		UNKNOWN,
	}
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtUser() {
		super();
	}	
		
	/**
	 * 
	 */
	public ExtUserProvState getExtUserProvState()
	{
		return m_extUserProvState;
	}
	
	/**
	 * 
	 */
	public String getAvatarUrl()
	{
		return m_avatarUrl;
	}
	
	/**
	 * 
	 */
	public String getEmail()
	{
		return m_email;
	}
	
	/**
	 * 
	 */
	@Override
	public Long getIdLong()
	{
		if ( m_userId != null )
			return Long.valueOf( m_userId );
		
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public String getImageUrl()
	{
		String url;
		
		// Does the user have an avatar?
		url = getAvatarUrl();
		if ( url != null && url.length() > 0 )
		{
			// Yes
			return url;
		}
		
		return GwtMainPage.m_requestInfo.getImagesPath() + "pics/UserPhoto.png";
	}
	
	/**
	 * Returns the user's name.
	 */
	@Override
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns the user's title.
	 */
	@Override
	public String getTitle() {
		return m_title;
	}
	
	/**
	 * 
	 */
	@Override
	public PrincipalClass getPrincipalClass()
	{
		return PrincipalClass.USER;
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
	 * Implements the GwtTeamingItem.getSecondaryDisplayText() abstract
	 * method.
	 */
	@Override
	public String getSecondaryDisplayText() {
		return getEmail();
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
	 * Returns the user type.
	 * 
	 * @return
	 */
	public PrincipalType getPrincipalType() {
		return m_principalType;
	}

	/**
	 * 
	 */
	public boolean isDisabled()
	{
		return m_disabled;
	}
	
	/**
	 * 
	 */
	public void setAvatarUrl( String url )
	{
		m_avatarUrl = url;
	}
	
	/**
	 * 
	 */
	public void setDisabled( boolean disabled )
	{
		m_disabled = disabled;
	}
	
	/**
	 * 
	 */
	public void setEmail( String email )
	{
		m_email = email;
	}
	
	/**
	 * 
	 */
	public void setExtUserProvState( ExtUserProvState state )
	{
		m_extUserProvState = state;
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
		if ( userId != null )
			setUserId(String.valueOf(userId));
		else
			m_userId = null;
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

	/**
	 * Stores the principal type.
	 * 
	 * @param principalType
	 */
	public void setPrincipalType(PrincipalType principalType) {
		m_principalType = principalType;
	}
}
