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
package org.kablink.teaming.remoting.ws.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;


public class AccessTokenValidationInterceptor implements MethodInterceptor {

	private AccessTokenManager accessTokenManager;
	private ZoneModule zoneModule;
		
	protected AccessTokenManager getAccessTokenManager() {
		return accessTokenManager;
	}

	public void setAccessTokenManager(AccessTokenManager accessTokenManager) {
		this.accessTokenManager = accessTokenManager;
	}

	protected ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();
		if(args == null || args.length < 1) {
			throw new IllegalArgumentException("Expecting at least one argument");
		}
		
		RequestContext rc = RequestContextHolder.getRequestContext();
		String authenticator = AuthenticationContextHolder.getAuthenticator();
		if(rc != null &&
				LoginAudit.AUTHENTICATOR_REMOTING_T.equals(authenticator)) {
			// token-based remoting
			if(args == null || args.length < 1) {
				throw new IllegalArgumentException("No argument in the call");
			}
			else if(args[0] instanceof String) {
				// This performs syntactic check.
				AccessToken token = new AccessToken((String) args[0]);
				// If you're still here, the token is well formed.
				// Now let's see if the token is a valid one.
				getAccessTokenManager().validate((String) args[0], token);
				// If you're still here, the validation was successful.
				// Update the request context with the newly obtained information 
				// (performed as side effect of this interceptor)
	            rc.setUserId(token.getUserId());
				rc.setAccessToken(token);
			}
			else {
				if (args[0] == null) {
					throw new IllegalArgumentException("Argument type mismatch: null");
				} else {
					throw new IllegalArgumentException("Argument type mismatch: " + args[0].getClass().getName());
				}
			}
		}
		
		// Nullify the first argument which should be access token. 
		// This is to prevent service implementing classes from doing anything with the raw token.
		args[0] = null;
		return invocation.proceed();
	}

}
