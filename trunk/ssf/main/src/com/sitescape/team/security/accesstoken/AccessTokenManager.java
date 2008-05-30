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
package com.sitescape.team.security.accesstoken;

import com.sitescape.team.security.accesstoken.AccessToken.BinderAccessConstraints;

public interface AccessTokenManager {

	/**
	 * Validate the access token.
	 * 
	 * @param token
	 * @throws InvalidAccessTokenException thrown if the access token is invalid
	 */
	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException;
	
	/**
	 * Returns an access token of background type.
	 * 
	 * @param applicationId
	 * @param userId
	 * @return
	 */
	public AccessToken getBackgroundToken(Long applicationId, Long userId);
	
	/**
	 * Returns an access token of background type.
	 * Its corresponding <code>binderAccessConstraints</code> value is set to 
	 * <code>BinderAccessConstraints.BINDER_AND_DESCENDANTS</code> whether
	 * <code>binderId</code> is specified or not.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @return
	 */
	public AccessToken getBackgroundToken(Long applicationId, Long userId, Long binderId);
	
	/**
	 * Returns an access token of background type.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @param binderAccessConstraints
	 * @return
	 */
	public AccessToken getBackgroundToken(Long applicationId, Long userId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints);
	
	/**
	 * This invalidates all existing/outstanding background tokens issued for
	 * the combination.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 */
	public void invalidateBackgroundTokens(Long applicationId, Long userId, Long binderId);
	
	/**
	 * Returns an access token of interactive type.
	 * 
	 * @param applicationId
	 * @param infoId
	 * @return
	 */
	public AccessToken getInteractiveToken(Long applicationId, Long userId, String infoId);
	
	/**
	 * Returns an access token of interactive type.
	 * Its corresponding <code>binderAccessConstraints</code> value is set to 
	 * <code>BinderAccessConstraints.BINDER_AND_DESCENDANTS</code> whether
	 * <code>binderId</code> is specified or not.
	 * 
	 * @param applicationId
	 * @param infoId
	 * @param binderId optional
	 * @return
	 */
	public AccessToken getInteractiveToken(Long applicationId, Long userId, String infoId, Long binderId);
	
	/**
	 * Returns an access token of interactive type.
	 * 
	 * @param applicationId
	 * @param infoId
	 * @param binderId optional
	 * @param binderAccessConstraints
	 * @return
	 */
	public AccessToken getInteractiveToken(Long applicationId, Long userId, String infoId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints);
	
	/**
	 * Create a <code>TokenInfoInteractive</code> object that the system 
	 * will use to manage the interactive tokens issued during the user's
	 * specific interactive session. Typically this is called as a 
	 * notification that a HTTP session was created for the user.
	 * 
	 * @param userId
	 * @return ID of the created object.
	 */
	public String createTokenInfoInteractive(Long userId);
	
	/**
	 * Update the <code>TokenInfoInteractive</code> object represented by the
	 * ID with the new user ID. It also changes its seed value.
	 * 
	 * @param infoId
	 * @param newUserId
	 */
	public void updateTokenInfoInteractive(String infoId, Long newUserId);
	
	/**
	 * Destroy all <code>TokenInfoInteractive</code> objects that belong
	 * to any user in the system. 
	 */
	public void destroyAllTokenInfoInteractive();
	
	/**
	 * Destroy all <code>TokenInfoInteractive</code> objects that belong
	 * to the user. Effectively this invalidates all existing/outstanding
	 * interactive tokens bound to any of the interactive sessions held
	 * by the user.
	 * 
	 * @param userId
	 */
	public void destroyUserTokenInfoInteractive(Long userId);
	
	/**
	 * Destroy the <code>TokenInfoInteractive</code> object represented by the
	 * ID. Effectively this invalidates all existing/outstanding interactive
	 * tokens bound to the specific session. 
	 * <p>
	 * Typically this is called as a notification that the user's HTTP session
	 * was destroyed.
	 * 
	 * @param infoId
	 */
	public void destroyTokenInfoInteractive(String infoId);
	
}
