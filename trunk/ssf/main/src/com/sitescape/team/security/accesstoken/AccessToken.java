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

	public enum TokenScope {
		session (1),
		request (2);
		int number;
		TokenScope(int number) {
			this.number = number;
		}
		public int getNumber() {return number;}
		public static TokenScope valueOf(int number) {
			switch (number) {
			case 1: return TokenScope.session;
			case 2: return TokenScope.request;
			default: throw new IllegalArgumentException(String.valueOf(number));
			}
		}
	};

	public enum BinderAccessConstraints {
		NONE (1),
		BINDER_ONLY (2),
		BINDER_AND_DESCENDANTS (3);
		int number;
		BinderAccessConstraints(int number) {
			this.number = number;
		}
		public int getNumber() {return number;}
		public static BinderAccessConstraints valueOf(int number) {
			switch (number) {
			case 1: return BinderAccessConstraints.NONE;
			case 2: return BinderAccessConstraints.BINDER_ONLY;
			case 3: return BinderAccessConstraints.BINDER_AND_DESCENDANTS;
			default: throw new IllegalArgumentException(String.valueOf(number));
			}
		}
	};

	private TokenScope scope;									// required
	private Long applicationId; 								// required
	private Long userId; 										// required
	private String digest; 										// required
	private Long binderId; 										// optional
	private BinderAccessConstraints binderAccessConstraints; 	// optional, this value is meaningful iff binderId is non-null
	private String infoId;										// required for interactive, null for background
	
	public static AccessToken sessionScopedToken(String infoId, Long applicationId, 
			Long userId, String digest, Long binderId, BinderAccessConstraints binderAccessConstraints) {
		return new AccessToken(TokenScope.session, infoId, applicationId,
				userId, digest, binderId, binderAccessConstraints);
	}
	
	public static AccessToken requestScopedToken(Long applicationId, 
			Long userId, String digest, Long binderId, BinderAccessConstraints binderAccessConstraints) {
		return new AccessToken(TokenScope.request, null, applicationId,
				userId, digest, binderId, binderAccessConstraints);
	}
	
	private AccessToken(TokenScope scope, String infoId, Long applicationId, 
			Long userId, String digest, Long binderId, BinderAccessConstraints binderAccessConstraints) {
		this.scope = scope;
		this.infoId = infoId;
		this.applicationId = applicationId;
		this.userId = userId;
		this.digest = digest;
		this.binderId = binderId;
		this.binderAccessConstraints = binderAccessConstraints;
	}
	
	public TokenScope getScope() {
		return scope;
	}

	public void setScope(TokenScope scope) {
		this.scope = scope;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BinderAccessConstraints getBinderAccessConstraints() {
		return binderAccessConstraints;
	}

	public void setIncludeDescendants(BinderAccessConstraints binderAccessConstraints) {
		this.binderAccessConstraints = binderAccessConstraints;
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
		// Access token string encoded representation:
		// 
		// tokenScopeNumber-appId-userId-digest-binderId-binderAccessConstraintsNumber-[infoId]
		// 
		// where [infoId] is present only for session-scoped token 
		String[] s = StringUtil.split(accessTokenStr, "-");
		if(s.length < 6)
			throw new MalformedAccessTokenException(accessTokenStr);
		if(s.length > 7)
			throw new MalformedAccessTokenException(accessTokenStr);
		scope = TokenScope.valueOf(Integer.valueOf(s[0]));
		applicationId = Long.valueOf(s[1]);
		userId = Long.valueOf(s[2]);
		digest = s[3];
		binderId = Long.valueOf(s[4]);
		if(binderId.longValue() == 0L)
			binderId = null;
		binderAccessConstraints = BinderAccessConstraints.valueOf(Integer.valueOf(s[5]));
		if(TokenScope.session.equals(scope)) {
			if(s.length == 7)
				infoId = s[6];
			else
				throw new MalformedAccessTokenException(accessTokenStr);
		} else if(TokenScope.request.equals(scope)) {
			if(s.length == 7)
				throw new MalformedAccessTokenException(accessTokenStr);
		} else {
			throw new MalformedAccessTokenException(accessTokenStr);
		}
	}
	
	/**
	 * Returns string representation of the access token.
	 * It throws exception if the current state of the token would yield to
	 * malformed string representation.
	 * This method does NOT validate the token itself.
	 */
	public String toStringRepresentation() throws IllegalStateException {
		// tokenScopeNumber-appId-userId-digest-binderId-binderAccessConstraintsNumber-[infoId]
		if(scope == null)
			throw new IllegalStateException("scope is missing");
		if(TokenScope.session.equals(scope)) {
			if(infoId == null)
				throw new IllegalStateException("info id is missing");
		} else if(TokenScope.request.equals(scope)) {
			if(infoId != null)
				throw new IllegalStateException("info id is present");
		} else {
			throw new IllegalStateException("something's serious broken");
		}
		if(applicationId == null)
			throw new IllegalStateException("application id is missing");
		if(userId == null)
			throw new IllegalStateException("user id is missing");
		if(digest == null)
			throw new IllegalStateException("digest is missing");
		StringBuilder sb = new StringBuilder()
		.append(scope.getNumber()).append("-")
		.append(applicationId).append("-")
		.append(userId).append("-")
		.append(digest).append("-")
		.append((binderId != null)? String.valueOf(binderId) : "0").append("-")
		.append(binderAccessConstraints.getNumber());
		if(infoId != null)
			sb.append("-").append(infoId);
		return sb.toString();
	}

	
	public static void main(String[] args) {
		String[] s = StringUtil.split("a.", ".");
		int i = 10;
	}
}
