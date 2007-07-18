/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
