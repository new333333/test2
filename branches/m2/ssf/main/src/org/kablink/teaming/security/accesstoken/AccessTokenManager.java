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
package org.kablink.teaming.security.accesstoken;

import java.util.Date;

import org.kablink.teaming.security.accesstoken.AccessToken.BinderAccessConstraints;

public interface AccessTokenManager {

	/**
	 * Validate the access token.
	 * 
	 * @param tokenStr string representation from which <code>token</code> was created
	 * @param token
	 * @throws InvalidAccessTokenException thrown if the access token is invalid
	 * @throws ExpiredAccessTokenException thrown if the access token is expired
	 */
	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException, ExpiredAccessTokenException;
	
	/**
	 * Returns an access token of request scoped.
	 * 
	 * @param applicationId
	 * @param userId
	 * @return
	 */
	public AccessToken getRequestScopedToken(Long applicationId, Long userId);
	
	/**
	 * Returns an access token of request scoped.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @param binderAccessConstraints
	 * @return
	 */
	public AccessToken getRequestScopedToken(Long applicationId, Long userId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints);
	
	/**
	 * Destroy the token of request scoped.
	 * 
	 * @param token
	 */
	public void destroyRequestScopedToken(AccessToken token);
	
	/**
	 * Destroy all <code>TokenInfoRequest</code> objects in the system.
	 */
	public void destroyAllTokenInfoRequest();
	
	/**
	 * Returns an access token of session scoped.
	 * 
	 * @param applicationId
	 * @param infoId
	 * @return
	 */
	public AccessToken getSessionScopedToken(Long applicationId, Long userId, String infoId);
	
	/**
	 * Returns an access token of session scoped.
	 * 
	 * @param applicationId
	 * @param infoId
	 * @param binderId optional
	 * @param binderAccessConstraints
	 * @return
	 */
	public AccessToken getSessionScopedToken(Long applicationId, Long userId, String infoId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints);
	
	/**
	 * Create a <code>TokenInfoSession</code> object that the system 
	 * will use to manage the session-scoped tokens issued during the 
	 * user's specific interactive session. Typically this is called 
	 * as a notification that a HTTP session was created for the user.
	 * 
	 * @param userId
	 * @return ID of the created object.
	 */
	public String createTokenInfoSession(Long userId, String httpSessionId);
	
	/**
	 * Update the <code>TokenInfoSession</code> object represented by the
	 * ID with the new user ID. It also changces its seed value.
	 * 
	 * @param infoId
	 * @param newUserId
	 */
	public void updateTokenInfoSession(String infoId, Long newUserId, String httpSessionId);
	
	/**
	 * Destroy all <code>TokenInfoSession</code> objects that belong
	 * to any user in the system. 
	 */
	public void destroyAllTokenInfoSession();
	
	/**
	 * Destroy all <code>TokenInfoSession</code> objects that belong
	 * to the user. Effectively this invalidates all existing/outstanding
	 * session-scoped tokens bound to any of the interactive sessions held
	 * by the user.
	 * 
	 * @param userId
	 */
	public void destroyUserTokenInfoSession(Long userId);
	
	/**
	 * Destroy the <code>TokenInfoSession</code> object represented by the
	 * ID. Effectively this invalidates all existing/outstanding session-scoped
	 * tokens bound to the specific session. 
	 * <p>
	 * Typically this is called as a notification that the user's HTTP session
	 * was destroyed.
	 * 
	 * @param infoId
	 */
	public void destroyTokenInfoSession(String infoId);
	
	/**
	 * Returns an access token of application scoped.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param requesterId
	 * @return
	 */
	public AccessToken getApplicationScopedToken(Long applicationId, Long userId, Long requesterId);
	
	/**
	 * Returns an access token of application scoped.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @param binderAccessConstraints
	 * @return
	 */
	public AccessToken getApplicationScopedToken(Long applicationId, Long userId, Long requesterId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints);
	
	/**
	 * Destroy the token of application scoped.
	 * 
	 * @param token
	 */
	public void destroyApplicationScopedToken(AccessToken token);
	
	/**
	 * Destroy all <code>TokenInfoApplication</code> objects in the system.
	 */
	public void destroyAllTokenInfoApplication();
	
	/**
	 * Destroy all <code>TokenInfo</code> objects whose timestamps are older than the specified date.
	 */
	public void destroyTokenInfoOlderThan(Date thisDate);
}
