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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SZoneConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;


public class ZoneAwareAnonymousProcessingFilter extends AnonymousAuthenticationFilter {

	public ZoneAwareAnonymousProcessingFilter(String key, String principalName, String authorityName) {
		super(key, principalName, AuthorityUtils.createAuthorityList(authorityName));
	}

	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }

	private AuthenticationModule authenticationModule;
	public AuthenticationModule getAuthenticationModule() {
		return authenticationModule;
	}
	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}

	@Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken auth = (AnonymousAuthenticationToken) super.createAuthentication(request);
        return new AnonymousAuthenticationToken(getKeyForZone(), auth.getPrincipal(), auth.getAuthorities());
    }
	
	protected String getKeyForZone()
	{
    	String zoneName = getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName());
    	return SZoneConfig.getGuestUserName(zoneName);
	}
	
	/*
	 * Spring 4 removed the deprecated abstract method applyAnonymousForThisRequest
	 * from its AnonymousAuthenticationFilter class. Therefore, we need to implement
	 * the same effect by overriding the doFilter() method as in this.
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
	    if (applyAnonymousForThisRequest((HttpServletRequest) req)) {
	    	super.doFilter(req, res, chain);
	    }
	    else {
	    	chain.doFilter(req, res);
	    }
	}
	
	protected boolean applyAnonymousForThisRequest(HttpServletRequest request) {
		if(SecurityContextHolder.getContext().getAuthentication() == null) {
	    	Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
	    	AuthenticationConfig config = getAuthenticationModule().getAuthenticationConfigForZone(zoneId);
	    	return config.isAllowAnonymousAccess();
		} else {
			return false;
		}
	}
}
