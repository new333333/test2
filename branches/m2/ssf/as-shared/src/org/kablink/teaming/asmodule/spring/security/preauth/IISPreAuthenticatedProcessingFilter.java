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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.util.PropsUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class IISPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpRequest) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Remote address: " + httpRequest.getRemoteAddr() + 
    				", Remote host: " + httpRequest.getRemoteHost() + 
    				", Server name: " + httpRequest.getServerName());    		
    	}
    	
        Object principal = httpRequest.getUserPrincipal() == null ? null : httpRequest.getUserPrincipal().getName();
        if (logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated IIS principal is [" + principal + "]");
        }
        if(principal == null) {
        	// CASE: The client is accessing Teaming directly, that is, without going through the IIS.
        	// In this case, simply returning null will cause the regular (form-based) authentication
        	// pipeline to be invoked.
        	return null;
        }
        else if(principal.equals("")) {
        	// CASE: The client is accessing Teaming through IIS, and the Anonymous Authentication is enabled on the IIS.
        	// In this case, Teaming is responsible for authenticating the user and the IIS is being
        	// used for purposes other than authentication, for example, as a proxy, as external access 
        	// point (in DMZ configuration, for instance), or as a load balancer in a clustered environment, etc.
        	// Simply return null so that the regular (form-based) authentication pipeline can be invoked.
        	// Returning an empty string will cause the framework an attempt to validate the identity
        	// against the LDAP (Active Directory), which will subsequently and inevitably fail resulting in
        	// undesirable effect. Returning null, instead, will instruct the framework to give up 
        	// pre-authentication and instead to proceed with the rest of the filter chain which implements
        	// regular form-based authentication.
        	return null;
        }
        else {
        	// CASE: The client is accessing Teaming through IIS, and the Anonymous Authentication is disabled on the IIS,
        	// and at least one other authentication mechanism (most likely Windows Authentication) is enabled on the IIS.
        	// 
        	// Note: Actually, it is theoretically possible that Anonymous Authentication is enabled on the IIS and some 
        	// interaction with Teaming (whether initial or subsequent - it doesn't matter) caused Teaming to return 
        	// 401 - Unauthorized HTTP status code back to IIS, which would have initiated regular authentication handshake
        	// with the browser, hence resulting in non-empty login credential being passed to Teaming through IIS. 
        	// However, that particular theoretical scenario can not actually occur in the current implementation/setup 
        	// of Teaming. So, we will rule that out for now. 
        }
        return principal;
    }

    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpRequest) {
        return "N/A";
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    	try {
    		super.doFilter(request, response, filterChain);
    	}
    	catch(AuthenticationException e) {
    		if(PropsUtil.getBoolean("iis.send.unauthorized.upon.unsuccessful.authentication", false))
    			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    		else
    			((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");	
    	}
    }

}
