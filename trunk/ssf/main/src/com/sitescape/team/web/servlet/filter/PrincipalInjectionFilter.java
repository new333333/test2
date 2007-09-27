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
package com.sitescape.team.web.servlet.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.PrincipalServletRequest;

public class PrincipalInjectionFilter implements Filter {

	private FilterConfig filterConfig;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		Principal principal = req.getUserPrincipal();
		
		if(principal == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			HttpSession ses = req.getSession(false);

			if(ses != null) {
				principal = (Principal) ses.getAttribute(WebKeys.USER_PRINCIPAL);
				
				if (principal != null) {
					// Cached principal object is found in the session. 
					// Wrap the request object with the information about the
					// principal in it.
					PrincipalServletRequest reqWithPrincipal = new PrincipalServletRequest(req, principal);
					
					chain.doFilter(reqWithPrincipal, response);
				}
				else {
					// No principal object is cached in the session.
					// Note: This occurs when a SSF web component (either a servlet
					// or an adapted portlet) is accessed BEFORE at least one SSF
					// portlet is invoked  by the portal through regular cross-context
					// dispatch. 
					throw new ServletException("No user information available - Illegal request sequence.");
				}
			}
			else {
				throw new ServletException("No session in place - Illegal request sequence.");
			}
		}
		else {
			// Actually, this condition is never met, hence never executed. 
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}

}
