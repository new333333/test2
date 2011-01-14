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
package org.kablink.teaming.asmodule.spring.security.preauth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.ui.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.Assert;

public class RequestCookiePreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
	private String principalRequestCookie; 		// required
	private String credentialsRequestCookie; 	// optional

    public void afterPropertiesSet() throws Exception {
    	super.afterPropertiesSet();
        Assert.notNull(principalRequestCookie, "An principalRequestCookie must be set");
    }

	/**
	 * Read and returns the header named by <tt>principalRequestCookie</tt> from the request.
	 * 
	 * @throws PreAuthenticatedCredentialsNotFoundException if the cookie is missing 
	 */
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Remote address: " + request.getRemoteAddr() + 
    				", Remote host: " + request.getRemoteHost() + 
    				", Server name: " + request.getServerName());    		
    	}
    	
		Cookie principalCookie = getCookie(request, principalRequestCookie);
		
		if(principalCookie == null) {
			return handleNoPrincipalCookie(request);
		}
		else {
			String principal = extractPrincipalFromCookie(request, principalCookie);
			
			if (principal == null) {
				return handleNoPrincipal(request);
			}
			else {
				return principal;
			}
		}
	}	
	
	/**
	 * Credentials aren't usually applicable, but if a <tt>credentialsRequestCookie</tt> is set, this
	 * will be read and used as the credentials value. Otherwise a dummy value will be used. 
	 */
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		if (credentialsRequestCookie != null) {
			Cookie credentialsCookie = getCookie(request, credentialsRequestCookie);
			
			if(credentialsCookie == null) {
				return handleNoCredentialsCookie(request);
			}
			else {
				String credentials = extractCredentialsFromCookie(request, credentialsCookie);
				
				if(credentials == null) {
					return handleNoCredentials(request);
				}
				else {
					return credentials;
				}
			}
		}
		else {
			// The system is configured such that credentials cookie is not applicable.
			return "N/A";
		}
	}
	
	public void setPrincipalRequestCookie(String principalRequestCookie) {
		Assert.hasText(principalRequestCookie, "principalRequestCookie must not be empty or null");
		this.principalRequestCookie = principalRequestCookie;
	}

	public void setCredentialsRequestCookie(String credentialsRequestCookie) {
		Assert.hasText(credentialsRequestCookie, "credentialsRequestCookie must not be empty or null");		
		this.credentialsRequestCookie = credentialsRequestCookie;
	}

	public int getOrder() {
		return FilterChainOrder.PRE_AUTH_FILTER;
	}
	
	protected Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie:cookies) {
				if(cookie.getName().equals(cookieName))
					return cookie; 
			}
		}
		return null;
	}
	
	protected Object handleNoPrincipalCookie(HttpServletRequest request) {
		/*
		if(SPropsUtil.getBoolean("requestcookiepreauthenticatedprocessingfilter.noprincipalcookie.throw", true))
			throw new PreAuthenticatedCredentialsNotFoundException(principalRequestCookie 
					+ " cookie not found in request.");
		else */
			return null;
	}
	
	protected Object handleNoPrincipal(HttpServletRequest request) {
		/*
		if(SPropsUtil.getBoolean("requestcookiepreauthenticatedprocessingfilter.noprincipal.throw", true))
			throw new PreAuthenticatedCredentialsNotFoundException("Failed to extract principal from "
				+ principalRequestCookie + " cookie in request.");
		else */
			return null;
	}
	
	protected String extractPrincipalFromCookie(HttpServletRequest request, Cookie principalCookie) {
		// This default implementation simply returns the value of the cookie.
		// A subclass can override this method to provide different behavior.
		return principalCookie.getValue();
	}
	
	protected String extractCredentialsFromCookie(HttpServletRequest request, Cookie credentialsCookie) {
		// This default implementation simply returns the value of the cookie.
		// A subclass can override this method to provide different behavior.
		return credentialsCookie.getValue();
	}
	
	protected Object handleNoCredentialsCookie(HttpServletRequest request) {
		/*
		if(SPropsUtil.getBoolean("requestcookiepreauthenticatedprocessingfilter.nocredentialscookie.throw", true))
			throw new PreAuthenticatedCredentialsNotFoundException(credentialsRequestCookie 
				+ " cookie not found in request.");
		else */
			return null;
	}
	
	protected Object handleNoCredentials(HttpServletRequest request) {
		/*
		if(SPropsUtil.getBoolean("requestcookiepreauthenticatedprocessingfilter.nocredentials.throw", true))
			throw new PreAuthenticatedCredentialsNotFoundException("Failed to extract credentials from "
				+ credentialsRequestCookie + " cookie in request.");
		else */
			return null;
	}
}

