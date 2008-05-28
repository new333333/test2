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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class LoginFilter  implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		if(WebHelper.isUserLoggedIn(req)) {
			// User is logged in. Proceed as normal. 
			chain.doFilter(request, response);
		}
		else {
			// User is not logged in.
			String path = req.getPathInfo();
			String actionValue = request.getParameter("action");
			if((path != null && (path.equals("/"+WebKeys.SERVLET_PORTAL_LOGIN) || path.equals("/"+WebKeys.SERVLET_PORTAL_LOGOUT))) || 
					(actionValue != null && (actionValue.startsWith("__") || actionValue.equals(WebKeys.ACTION_VIEW_PERMALINK)))) {
				// The action value indicates that the framework should allow
				// execution of the controller corresponding to the action value
				// even when the user is not authenticated (or previous 
				// authentication has expired). 
				// Note that this does NOT open up a security hole as one might
				// be tempted to think, because the action value to controller
				// mapping is done with the special prefix (i.e., "__") value
				// already taken into consideration. Therefore, user's attempt
				// to prefix the action value with the special characters to
				// bypass the filter's security check will bear no fruit since
				// the mapping doesn't exist. 
				
				request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
				
				chain.doFilter(request, response); // Proceed
			}
			else {
				// Unauthenticated access is not allowed. So don't bother 
				// invoking the servlet. Instead simply redirect the user to 
				// the portal login page.
				RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/forum/portal_login.jsp");
				request.setAttribute("ss_portalLoginUrl", SPropsUtil.getString("permalink.login.url"));
				request.setAttribute("ss_screens_to_login_screen", 
						SPropsUtil.getString("permalink.login.screensToLoginScreen"));
				request.setAttribute("ss_screens_after_login_screen_to_logged_in", 
						SPropsUtil.getString("permalink.login.screensAfterLoginScreenToLoggedIn"));
				dispatcher.forward(request, response);
			}
		}
	}

	public void destroy() {
	}

}
