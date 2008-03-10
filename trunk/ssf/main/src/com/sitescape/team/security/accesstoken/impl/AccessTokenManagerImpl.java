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
package com.sitescape.team.security.accesstoken.impl;

import java.util.UUID;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.security.accesstoken.AccessToken;
import com.sitescape.team.security.accesstoken.AccessTokenManager;
import com.sitescape.team.security.accesstoken.InvalidAccessTokenException;
import com.sitescape.team.security.accesstoken.AccessToken.BinderAccessConstraints;
import com.sitescape.team.security.accesstoken.AccessToken.TokenType;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.util.EncryptUtil;

public class AccessTokenManagerImpl implements AccessTokenManager {

	private SecurityDao securityDao;
	
	protected SecurityDao getSecurityDao() {
		return securityDao;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}
	
	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfo info;
		if(token.getType() == AccessToken.TokenType.interactive) {
			info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), token.getInfoId());
		}
		else {
			info = getSecurityDao().loadTokenInfoBackground(rc.getZoneId(), token.getApplicationId(), token.getUserId(), token.getBinderId());	
		}
		if(info != null) {
			String digest = computeDigest(token, info.getSeed());
			if(!digest.equals(token.getDigest()))
				throw new InvalidAccessTokenException(tokenStr);
		}
		else {
			throw new InvalidAccessTokenException(tokenStr);
		}
	}

	public AccessToken newBackgroundToken(Long applicationId, Long userId) {
		return newBackgroundToken(applicationId, userId, null);
	}

	public AccessToken newBackgroundToken(Long applicationId, Long userId, Long binderId) {
		return newBackgroundToken(applicationId, userId, binderId, BinderAccessConstraints.BINDER_AND_DESCENDANTS);
	}

	public AccessToken newBackgroundToken(Long applicationId, Long userId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints) {
		TokenInfoBackground info = loadOrCreateBackground(applicationId, userId, binderId);
		
		String digest = computeDigest(TokenType.background, applicationId, userId, binderId, binderAccessConstraints, info.getSeed());
		
		return AccessToken.backgroundToken(applicationId, userId, digest, binderId, binderAccessConstraints);
	}

	public void invalidateBackgroundTokens(Long applicationId, Long userId, Long binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoBackground info = getSecurityDao().loadTokenInfoBackground(rc.getZoneId(), applicationId, userId, binderId);
		if(info != null) {
			info.setSeed(getRandomSeed());
			getSecurityDao().update(info);
		}
	}

	public void destroyAllTokenInfoInteractive() {
		getSecurityDao().deleteAll(TokenInfoInteractive.class);
	}

	public void destroyUserTokenInfoInteractive(Long userId) {
		getSecurityDao().deleteUserTokenInfoInteractive(userId);
	}

	public void destroyTokenInfoInteractive(String infoId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoInteractive info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), infoId);	
		if(info != null)
			getSecurityDao().delete(info);
	}

	public AccessToken newInteractiveToken(Long applicationId, String infoId) {
		return newInteractiveToken(applicationId, infoId, null);
	}

	public AccessToken newInteractiveToken(Long applicationId, String infoId, Long binderId) {
		return newInteractiveToken(applicationId, infoId, binderId, BinderAccessConstraints.BINDER_AND_DESCENDANTS);
	}

	public AccessToken newInteractiveToken(Long applicationId, String infoId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoInteractive info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), infoId);	
		
		String digest = computeDigest(TokenType.interactive, applicationId, info.getUserId(), binderId, binderAccessConstraints, info.getSeed());
		
		return AccessToken.interactiveToken(infoId, applicationId, info.getUserId(), digest, binderId, binderAccessConstraints);
	}

	public String createTokenInfoInteractive(Long userId) {
		/*
		 * Implementation note:
		 * 
		 * 1. The application needs a handle to map the user's HTTP session to 
		 * the corresponding TokenInfoInteractive object in the dabase.
		 * We could use either the session id or the database internal id for
		 * the purpose. The latter has at least two advantages over the former: 
		 * (a) The object id remains the same for the duration of the session, 
		 * whereas the HTTP session id can change when the system configuration
		 * involves session replication and transparent fail over. 
		 * (b) By using database id for record lookup (as opposed to querying 
		 * it by session id), the chance of cache hit is much higher. 
		 * 
		 * 2. Although we could use the session id as the input to the digest
		 * computation, we simply use random seed so that we could re-use the
		 * same code for both interactive and background tokens. 
		 */
		
		TokenInfoInteractive info = new TokenInfoInteractive(userId, getRandomSeed());
		getSecurityDao().save(info);
		return info.getId();	
	}

	private String computeDigest(AccessToken token, String seed) {
		return computeDigest(token.getType(), token.getApplicationId(), token.getUserId(), token.getBinderId(), token.getBinderAccessConstraints(), seed);
	}
	
	private String getRandomSeed() {
		return UUID.randomUUID().toString();
	}
	
	private String computeDigest(TokenType type, Long applicationId, Long userId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints, String seed) {
		// For the purpose of computing digest, we use string representations of the enum values
		// while their shorter numeric representations are stored in the token. 
		if(binderId == null) {
			return EncryptUtil.encryptSHA1(type.name(), applicationId.toString(), userId.toString(), seed);
		}
		else {
			return EncryptUtil.encryptSHA1(type.name(), applicationId.toString(), 
					userId.toString(), binderId.toString(), binderAccessConstraints.name(), seed);
		}
	}
	
	private TokenInfoBackground loadOrCreateBackground(Long applicationId, Long userId, Long binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoBackground info = getSecurityDao().loadTokenInfoBackground(rc.getZoneId(), applicationId, userId, binderId);	
		if(info == null) {
			info = new TokenInfoBackground(applicationId, userId, binderId, getRandomSeed());
			getSecurityDao().save(info);
		}
		return info;
	}

	/*
	private TokenInfo loadOrCreate(TokenType type, Long applicationId, Long userId, Long binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(type == AccessToken.TokenType.interactive) {
			TokenInfoInteractive info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), userId);
			if(info == null) {
				info = new TokenInfoInteractive(userId, getRandomNumber());
				getSecurityDao().save(info);
			}
			return info;
		}
		else {
			TokenInfoBackground info = getSecurityDao().loadTokenInfoBackground(rc.getZoneId(), applicationId, userId, binderId);	
			if(info == null) {
				info = new TokenInfoBackground(applicationId, userId, binderId, getRandomNumber());
				getSecurityDao().save(info);
			}
			return info;
		}
	}*/

}
