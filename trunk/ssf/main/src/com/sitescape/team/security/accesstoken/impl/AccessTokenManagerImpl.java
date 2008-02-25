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

import java.security.SecureRandom;

import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.security.accesstoken.AccessToken;
import com.sitescape.team.security.accesstoken.AccessTokenManager;
import com.sitescape.team.security.accesstoken.InvalidAccessTokenException;
import com.sitescape.team.security.accesstoken.AccessToken.TokenType;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.util.EncryptUtil;

public class AccessTokenManagerImpl implements AccessTokenManager, InitializingBean {

	private SecurityDao securityDao;
	private SecureRandom random;
	
	protected SecurityDao getSecurityDao() {
		return securityDao;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public void afterPropertiesSet() throws Exception {
		random = SecureRandom.getInstance("SHA1PRNG");
	}
	
	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfo info;
		if(token.getType() == AccessToken.TokenType.interactive) {
			info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), token.getUserId());
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

	public AccessToken newAccessToken(TokenType type, Long applicationId, Long userId) {
		return newAccessToken(type, applicationId, userId, null);
	}

	public AccessToken newAccessToken(TokenType type, Long applicationId, Long userId, Long binderId) {
		return newAccessToken(type, applicationId, userId, binderId, ((binderId == null)? null : Boolean.TRUE));
	}

	public AccessToken newAccessToken(TokenType type, Long applicationId, Long userId, Long binderId, Boolean includeDescendants) {
		TokenInfo info = loadOrCreate(type, applicationId, userId, binderId);
		
		String digest = computeDigest(type, applicationId, userId, binderId, includeDescendants, info.getSeed());
		
		return new AccessToken(type, applicationId, userId, digest, binderId, includeDescendants);
	}

	public void updateSeedForInteractive(Long userId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoInteractive info = getSecurityDao().loadTokenInfoInteractive(rc.getZoneId(), userId);
		if(info != null) {
			info.setSeed(Long.valueOf(getRandomNumber()));
			getSecurityDao().update(info);
		}
	}

	public void updateSeedForBackground(Long applicationId, Long userId, Long binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoBackground info = getSecurityDao().loadTokenInfoBackground(rc.getZoneId(), applicationId, userId, binderId);
		if(info != null) {
			info.setSeed(Long.valueOf(getRandomNumber()));
			getSecurityDao().update(info);
		}
	}

	public void emptyAllInteractive() {
		getSecurityDao().deleteAll(TokenInfoInteractive.class);
	}

	private String computeDigest(AccessToken token, Long seed) {
		return computeDigest(token.getType(), token.getApplicationId(), token.getUserId(), token.getBinderId(), token.getIncludeDescendants(), seed);
	}
	
	private String computeDigest(TokenType type, Long applicationId, Long userId, Long binderId, Boolean includeDescendants, Long seed) {
		if(binderId == null) {
			return EncryptUtil.encryptSHA1(type.name(), applicationId.toString(), userId.toString(), seed.toString());
		}
		else if(includeDescendants == null) {
			return EncryptUtil.encryptSHA1(type.name(), applicationId.toString(), userId.toString(), binderId.toString(), seed.toString());
		}
		else {
			return EncryptUtil.encryptSHA1(type.name(), applicationId.toString(), userId.toString(), binderId.toString(), includeDescendants.toString(), seed.toString());
		}
	}
	
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
	}

	private Long getRandomNumber() {
		return random.nextLong();
	}
}
