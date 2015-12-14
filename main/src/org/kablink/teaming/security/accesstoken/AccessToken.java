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

import org.kablink.util.StringUtil;

public class AccessToken {

	public enum TokenScope {
		session (1),
		request (2),
		application (3);
		int number;
		TokenScope(int number) {
			this.number = number;
		}
		public int getNumber() {return number;}
		public static TokenScope valueOf(int number) {
			switch (number) {
			case 1: return TokenScope.session;
			case 2: return TokenScope.request;
			case 3: return TokenScope.application;
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
	private String infoId;										// required
	private Long requesterId;									// optional, this value is meaningful iff scope is application
	
	public static AccessToken sessionScopedToken(String infoId, Long applicationId, 
			Long userId, String digest, Long binderId, BinderAccessConstraints binderAccessConstraints) {
		return new AccessToken(TokenScope.session, infoId, applicationId,
				userId, digest, binderId, binderAccessConstraints);
	}
	
	public static AccessToken requestScopedToken(String infoId, String digest) {
		return new AccessToken(TokenScope.request, infoId, null, null, digest, null, null);
	}
	
	public static AccessToken applicationScopedToken(String infoId, String digest) {
		return new AccessToken(TokenScope.application, infoId, null, null, digest, null, null);
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

	public void setBinderAccessConstraints(BinderAccessConstraints binderAccessConstraints) {
		this.binderAccessConstraints = binderAccessConstraints;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
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
		// tokenScopeNumber-digest-infoId-[appId-userId-binderId-binderAccessConstraintsNumber]
		//
		// where [] part is present only for session-scoped token.
		// 
		String[] s = StringUtil.split(accessTokenStr, "-");
		if(s.length < 3)
			throw new MalformedAccessTokenException(accessTokenStr);
		scope = TokenScope.valueOf(Integer.valueOf(s[0]));
		if(TokenScope.session.equals(scope)) {
			if(s.length != 7)
				throw new MalformedAccessTokenException(accessTokenStr);
		} else if(TokenScope.request.equals(scope) || TokenScope.application.equals(scope)) {
			if(s.length != 3)
				throw new MalformedAccessTokenException(accessTokenStr);
		} else {
			throw new MalformedAccessTokenException(accessTokenStr);
		}
		digest = s[1];
		infoId = s[2];
		if(TokenScope.session.equals(scope)) {
			applicationId = Long.valueOf(s[3]);
			userId = Long.valueOf(s[4]);
			binderId = Long.valueOf(s[5]);
			if(binderId.longValue() == 0L)
				binderId = null;
			binderAccessConstraints = BinderAccessConstraints.valueOf(Integer.valueOf(s[6]));
		}
	}
	
	/**
	 * Returns string representation of the access token.
	 * It throws exception if the current state of the token would yield to
	 * malformed string representation.
	 * This method does NOT validate the token itself.
	 */
	public String toStringRepresentation() throws IllegalStateException {
		// Access token string encoded representation:
		// 
		// tokenScopeNumber-digest-infoId-[appId-userId-binderId-binderAccessConstraintsNumber]
		//
		// where [] part is present only for session-scoped token.
		if(scope == null)
			throw new IllegalStateException("scope is missing");
		if(digest == null)
			throw new IllegalStateException("digest is missing");
		if(infoId == null)
			throw new IllegalStateException("info id is missing");
		if(TokenScope.session.equals(scope)) {
			if(applicationId == null)
				throw new IllegalStateException("application id is missing");
			if(userId == null)
				throw new IllegalStateException("user id is missing");
		} else if(TokenScope.request.equals(scope) || TokenScope.application.equals(scope)) {
			// Do NOT encode applicationId, userId, binderId, and binderAccessConstraints
			// into the string EVEN IF they are present. This same information is stored
			// in the corresponding token info object in the database, and therefore,
			// doesn't have to be placed into the token itself. However, once a client-
			// supplied token is successfully validated, these fields are automatically
			// populated with the values from the token info simply as an additional
			// programming convenience. So, whether these fields should be null or not
			// is determined by the time and context of the usage related to the token's
			// lifecycle (sacrificing some robustness over convenience...)
			if(applicationId != null)
				throw new IllegalStateException("application id is present");
			if(userId != null)
				throw new IllegalStateException("user id is present");
			if(binderId != null)
				throw new IllegalStateException("binder id is present");	
			if(binderAccessConstraints != null)
				throw new IllegalStateException("binder access constraints is present");	
		} else {
			throw new IllegalStateException("something's serious broken");
		}
		StringBuilder sb = new StringBuilder()
		.append(scope.getNumber()).append("-")
		.append(digest).append("-")
		.append(infoId);
		if(TokenScope.session.equals(scope)) {
			sb.append("-").append(applicationId).append("-")
			.append(userId).append("-")
			.append((binderId != null)? String.valueOf(binderId) : "0").append("-")
			.append(binderAccessConstraints.getNumber());
		}
		return sb.toString();
	}

	
	public static void main(String[] args) {
		String[] s = StringUtil.split("a.", ".");
		int i = 10;
	}
}
