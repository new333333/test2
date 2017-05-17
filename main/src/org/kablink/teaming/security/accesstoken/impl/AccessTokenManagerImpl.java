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
package org.kablink.teaming.security.accesstoken.impl;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.NoApplicationByTheIdException;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.accesstoken.AccessTokenException;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.security.accesstoken.ExpiredAccessTokenException;
import org.kablink.teaming.security.accesstoken.InvalidAccessTokenException;
import org.kablink.teaming.security.accesstoken.AccessToken.BinderAccessConstraints;
import org.kablink.teaming.security.accesstoken.AccessToken.TokenScope;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class AccessTokenManagerImpl implements AccessTokenManager {

	private ProfileDao profileDao;
	private SecurityDao securityDao;

	protected Log logger = LogFactory.getLog(getClass());
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	protected SecurityDao getSecurityDao() {
		return securityDao;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}
	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException, ExpiredAccessTokenException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(token.getScope() == AccessToken.TokenScope.session) {
			TokenInfoSession info = getSecurityDao().loadTokenInfoSession(rc.getZoneId(), token.getInfoId());
			if(info != null) {
				String digest = computeDigest(token.getScope(), token.getApplicationId(), token.getUserId(),
						token.getBinderId(), token.getBinderAccessConstraints(), info.getSeed());
				if(!digest.equals(token.getDigest()))
					throw new InvalidAccessTokenException(tokenStr);
			}
			else {
				throw new InvalidAccessTokenException(tokenStr);				
			}
		}
		else if(token.getScope() == AccessToken.TokenScope.request) {
			TokenInfoRequest info = getSecurityDao().loadTokenInfoRequest(rc.getZoneId(), token.getInfoId());	
			if(info != null) {
				String digest = computeDigest(token.getScope(), info.getApplicationId(), info.getUserId(),
						info.getBinderId(), info.getBinderAccessConstraints(), info.getSeed());
				if(digest.equals(token.getDigest())) { // match
					// Copy the following pieces of information from the tokeninfo into the accesstoken.
					// This allows the application to access those information without having a direct
					// access to the lower-level tokeninfo object (ie, just serves as temporary cache).
					token.setApplicationId(info.getApplicationId());
					token.setUserId(info.getUserId());
					token.setBinderId(info.getBinderId());
					token.setBinderAccessConstraints(info.getBinderAccessConstraints());
				}
				else { // invalid
					throw new InvalidAccessTokenException(tokenStr);
				}
			}
			else {
				throw new InvalidAccessTokenException(tokenStr);				
			}
		}
		else if(token.getScope() == AccessToken.TokenScope.application) {
			final TokenInfoApplication info = getSecurityDao().loadTokenInfoApplication(rc.getZoneId(), token.getInfoId());	
			if(info != null) {
				String digest = computeDigest(token.getScope(), info.getApplicationId(), info.getUserId(),
						info.getBinderId(), info.getBinderAccessConstraints(), info.getSeed());
				if(digest.equals(token.getDigest())) { // digest match
					Application application = getApplication(tokenStr, info);
					if(application.isSameAddrPolicy()) {
						if(!info.getClientAddr().equalsIgnoreCase(ZoneContextHolder.getClientAddr())) {
							if(logger.isWarnEnabled())
								logger.warn(ZoneContextHolder.getClientAddr() + " attempting to use token " + tokenStr + " which was obtained by " + info.getClientAddr());
							throw new InvalidAccessTokenException(tokenStr);
						}
					}
					Date now = new Date();
					if(now.getTime() - info.getLastAccessTime().getTime() > application.getMaxIdleTime()*1000) {
						// The token has expired. Remove the token right here.
						getTransactionTemplate().execute(
								new TransactionCallback() {
									public Object doInTransaction(TransactionStatus status) {
										getSecurityDao().delete(info);
										return null;
									}
								});
						throw new ExpiredAccessTokenException(tokenStr);
					}
					// Everything looks good. Update the last accessed time. 
					info.setLastAccessTime(now);
					getTransactionTemplate().execute(
							new TransactionCallback() {
								public Object doInTransaction(TransactionStatus status) {
									getSecurityDao().update(info);
									return null;
								}
							});
					// Copy the following pieces of information from the tokeninfo into the accesstoken.
					// This allows the application to access those information without having a direct
					// access to the lower-level tokeninfo object (ie, just serves as temporary cache).
					token.setApplicationId(info.getApplicationId());
					token.setUserId(info.getUserId());
					token.setRequesterId(info.getRequesterId());
					token.setBinderId(info.getBinderId());
					token.setBinderAccessConstraints(info.getBinderAccessConstraints());
				}
				else { // invalid
					throw new InvalidAccessTokenException(tokenStr);
				}
			}
			else {
				throw new InvalidAccessTokenException(tokenStr);				
			}
		}
		else {
			throw new IllegalArgumentException(token.getScope().name());
		}
	}

	public AccessToken getRequestScopedToken(Long applicationId, Long userId) {
		return getRequestScopedToken(applicationId, userId, null, BinderAccessConstraints.NONE);
	}

	public AccessToken getRequestScopedToken(Long applicationId, Long userId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints) {
		TokenInfoRequest info = new TokenInfoRequest(applicationId, userId, binderId, binderAccessConstraints, getRandomSeed());
		
		getSecurityDao().save(info);
				
		String digest = computeDigest(TokenScope.request, applicationId, userId, binderId, binderAccessConstraints, info.getSeed());
		
		return AccessToken.requestScopedToken(info.getId(), digest);
	}

	public void destroyRequestScopedToken(AccessToken token) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoRequest info = getSecurityDao().loadTokenInfoRequest(rc.getZoneId(), token.getInfoId());
		if(info != null)
			getSecurityDao().delete(info);
	}

	public void destroyAllTokenInfoRequest() {
		getSecurityDao().deleteAll(TokenInfoRequest.class);
	}

	public void destroyAllTokenInfoSession() {
		getSecurityDao().deleteAll(TokenInfoSession.class);
	}

	public void destroyUserTokenInfoSession(Long userId) {
		getSecurityDao().deleteUserTokenInfoSession(userId);
	}

	public void destroyTokenInfoSession(String infoId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoSession info = getSecurityDao().loadTokenInfoSession(rc.getZoneId(), infoId);	
		if(info != null)
			getSecurityDao().delete(info);
	}

	public AccessToken getSessionScopedToken(Long applicationId, Long userId, String infoId) {
		return getSessionScopedToken(applicationId, userId, infoId, null, BinderAccessConstraints.NONE);
	}

	public AccessToken getSessionScopedToken(Long applicationId, Long userId, String infoId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoSession info = getSecurityDao().loadTokenInfoSession(rc.getZoneId(), infoId);	
		
		if(!info.getUserId().equals(userId))
			throw new AccessTokenException("User IDs do not match: " + userId + " " + info.getUserId());
		
		String digest = computeDigest(TokenScope.session, applicationId, info.getUserId(), binderId, binderAccessConstraints, info.getSeed());
		
		return AccessToken.sessionScopedToken(infoId, applicationId, info.getUserId(), digest, binderId, binderAccessConstraints);
	}

	public String createTokenInfoSession(Long userId, String httpSessionId) {
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
		 * 2. We use HTTP session id as the input (seed) to the digest computation
		 * which also means that the HTTP session id is stored in the database 
		 * as part of the TokenInfo record. 
		 */
		
		TokenInfoSession info = new TokenInfoSession(userId, (httpSessionId == null)? getRandomSeed() : httpSessionId);
		getSecurityDao().save(info);
		return info.getId();	
	}

	private String getRandomSeed() {
		return UUID.randomUUID().toString();
	}
	
	private String computeDigest(TokenScope type, Long applicationId, Long userId, Long binderId, 
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

	public void updateTokenInfoSession(String infoId, Long newUserId, String httpSessionId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoSession info = getSecurityDao().loadTokenInfoSession(rc.getZoneId(), infoId);	
		if(info != null) {
			info.setUserId(newUserId);
			info.setSeed((httpSessionId == null)? getRandomSeed() : httpSessionId);
			getSecurityDao().update(info);
		}
		else {
			throw new AccessTokenException("Interactive token info with the id " + infoId + " is not found");
		}
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

	public AccessToken getApplicationScopedToken(Long applicationId, Long userId, Long requesterId) {
		return getApplicationScopedToken(applicationId, userId, requesterId, null, null);
	}

	public AccessToken getApplicationScopedToken(Long applicationId, Long userId, Long requesterId, Long binderId, 
			BinderAccessConstraints binderAccessConstraints) {
		TokenInfoApplication info = new TokenInfoApplication
		(applicationId, userId, requesterId, binderId, binderAccessConstraints, 
		ZoneContextHolder.getClientAddr(), new Date(), getRandomSeed());
		
		getSecurityDao().save(info);
				
		String digest = computeDigest(TokenScope.application, applicationId, userId, binderId, binderAccessConstraints, info.getSeed());
		
		return AccessToken.applicationScopedToken(info.getId(), digest);
	}

	public void destroyApplicationScopedToken(AccessToken token) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		TokenInfoApplication info = getSecurityDao().loadTokenInfoApplication(rc.getZoneId(), token.getInfoId());
		if(info != null)
			getSecurityDao().delete(info);
	}

	public void destroyAllTokenInfoApplication() {
		getSecurityDao().deleteAll(TokenInfoApplication.class);
	}

	private Application getApplication(String tokenStr, TokenInfoApplication info) throws InvalidAccessTokenException {
		try {
			return getProfileDao().loadApplication(info.getApplicationId(), RequestContextHolder.getRequestContext().getZoneId());
		}
		catch(NoApplicationByTheIdException e) {
			throw new InvalidAccessTokenException(tokenStr);
		}
	}

	public void destroyTokenInfoOlderThan(Date thisDate) {
		getSecurityDao().deleteTokenInfoOlderThan(thisDate);
	}
}
