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

import com.sitescape.util.StringUtil;

public class AccessToken {

	private Long applicationId; // required
	private Long userId; // required
	private Long expiration; // required - 0 indicates no expiration
	private String digest; // required
	private Long binderId; // optional

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public Long getBinderId() {
		return binderId;
	}

	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Creates access token from the string representation.
	 * It throws exception if the string is not well formed. 
	 * This method does NOT validate the token itself.
	 * 
	 * @param accessTokenStr
	 * @throws IllegalArgumentException
	 * @throws MalformedAccessTokenException
	 * @throws NumberFormatException
	 */
	public AccessToken(String accessTokenStr) throws IllegalArgumentException,
	MalformedAccessTokenException, NumberFormatException {
		if(accessTokenStr == null)
			throw new IllegalArgumentException();
		// Access token str representation
		// appId-userId-expiration-digest-[binderId]
		String[] s = StringUtil.split(accessTokenStr, "-");
		if(s.length < 4)
			throw new MalformedAccessTokenException("not enough pieces");
		if(s.length > 5)
			throw new MalformedAccessTokenException("too many pieces");
		applicationId = Long.valueOf(s[0]);
		userId = Long.valueOf(s[1]);
		expiration = Long.valueOf(s[2]);
		digest = s[3];
		if(s.length > 4)
			binderId = Long.valueOf(s[4]);
	}
	
	/**
	 * Returns string representation of the access token.
	 * It throws exception if the current state of the token would yield to
	 * malformed string representation.
	 * This method does NOT validate the token itself.
	 */
	public String toStringRepresentation() throws IllegalStateException {
		if(applicationId == null)
			throw new IllegalStateException("application id is missing");
		if(userId == null)
			throw new IllegalStateException("user id is missing");
		if(expiration == null)
			throw new IllegalStateException("expiration is missing");
		if(digest == null)
			throw new IllegalStateException("digest is missing");
		StringBuilder sb = new StringBuilder()
		.append(applicationId).append("-")
		.append(userId).append("-")
		.append(expiration).append("-")
		.append(digest);
		if(binderId != null)
			sb.append("-").append(binderId);
		return sb.toString();
	}

}
