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
package org.kablink.teaming.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.util.SPropsUtil;
import org.springframework.security.Authentication;
import org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.ui.logout.LogoutHandler;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class PreAuthenticationLogoutFilter extends org.springframework.security.ui.logout.LogoutFilter {

	private String preAuthenticationLogoutSuccessUrl = "/bitbucket";
    private LogoutHandler[] handlers;
	private boolean allowLogoutViaGet;
	
	public PreAuthenticationLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
		super(logoutSuccessUrl, handlers);
		this.handlers = handlers;
		allowLogoutViaGet = SPropsUtil.getBoolean("allow.logout.via.get", false);
	}

	public String getPreAuthenticationLogoutSuccessUrl() {
		return preAuthenticationLogoutSuccessUrl;
	}

	public void setPreAuthenticationLogoutSuccessUrl(
			String preAuthenticationLogoutSuccessUrl) {
		this.preAuthenticationLogoutSuccessUrl = preAuthenticationLogoutSuccessUrl;
	}

    public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
    ServletException {
        if (requiresLogout(request, response)) {
        	// We've got logout request.
        	if("GET".equalsIgnoreCase(request.getMethod())) { // GET method
        		if(allowLogoutViaGet) {
        			// Allow logout via GET.
        			dodoFilterHttp(request, response, chain);
        		}
        		else {
        			// Don't allow logout via GET. Simply return without rendering anything new.
        			return;
        		}
        	}
        	else { // non-GET, probably POST
        		dodoFilterHttp(request, response, chain);
        	}
        }
        else { // It's not logout request.
        	chain.doFilter(request, response);
        }
    }

    public void dodoFilterHttp(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (requiresLogout(request, response)) {
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();

			if (logger.isDebugEnabled()) {
				logger.debug("Logging out user '" + auth
						+ "' and redirecting to logout page");
			}

			for (int i = 0; i < handlers.length; i++) {
				handlers[i].logout(request, response, auth);
			} 

			String targetUrl;
			
			if(auth instanceof PreAuthenticatedAuthenticationToken) {
				targetUrl = determinePreAuthenticationTargetUrl(request, response);
			}
			else {
				targetUrl = determineTargetUrl(request, response);
			}

			sendRedirect(request, response, targetUrl);

			return;
		}

		chain.doFilter(request, response);
	}
	
    protected String determinePreAuthenticationTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = request.getParameter("logoutSuccessUrl");

        if(!StringUtils.hasLength(targetUrl)) {
            targetUrl = getPreAuthenticationLogoutSuccessUrl();
        }

        if (!StringUtils.hasLength(targetUrl)) {
            targetUrl = request.getHeader("Referer");
        }        

        if (!StringUtils.hasLength(targetUrl)) {
            targetUrl = "/";
        }

        return targetUrl;
    }

}
