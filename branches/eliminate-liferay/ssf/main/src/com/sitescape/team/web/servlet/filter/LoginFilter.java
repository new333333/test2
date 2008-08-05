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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.portal.PortalLogin;
import com.sitescape.team.runas.RunasCallback;
import com.sitescape.team.runas.RunasTemplate;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.BrowserSniffer;

public class LoginFilter  implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if(isAtRoot(req) && req.getMethod().equalsIgnoreCase("get")) {
			// We're at the root URL. Re-direct the client to its workspace.
			// Do this only if the request method is GET.
			if (BrowserSniffer.is_wap_xhtml(req)) {
				String landingPageUrl = getWapLandingPageURL(req);
				res.sendRedirect(landingPageUrl);
			} else {
				String workspaceUrl = getWorkspaceURL(req);
				res.sendRedirect(workspaceUrl);
			}
		}
		else {
			if(WebHelper.isGuestLoggedIn(req)) {
				// User is logged in as guest. Proceed to the login screen.
				String refererUrl = getOriginalURL(req);
				req.setAttribute(WebKeys.REFERER_URL, refererUrl);
				chain.doFilter(request, response);
			}
			else {
				String url = req.getQueryString();
				String redirectUrl = url;
				if (url != null) { 
					redirectUrl = redirectUrl.replace(WebKeys.USERID_PLACEHOLDER, WebHelper.getRequiredUserId(req).toString());
				}

				// User is logged in as regular user. Proceed as normal.
				if(! redirectUrl.equals(url)) {
					res.sendRedirect(req.getRequestURI() + "?" + redirectUrl);
				} else {
					req.setAttribute("referer", req.getQueryString());
					chain.doFilter(request, response);
				}
			}
		}
	}
	
	public void destroy() {
	}

	protected String getWorkspaceURL(final HttpServletRequest req) {
		final String userId;
		if(WebHelper.isGuestLoggedIn(req))
			userId = WebKeys.USERID_PLACEHOLDER;
		else
			userId = WebHelper.getRequiredUserId(req).toString();
		
		return (String) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return PermaLinkUtil.getWorkspaceURL(req, userId);
			}
		}, WebHelper.getRequiredZoneName(req));									
	}
	
	protected String getWapLandingPageURL(final HttpServletRequest req) {
		final String userId;
		if(WebHelper.isGuestLoggedIn(req))
			userId = WebKeys.USERID_PLACEHOLDER;
		else
			userId = WebHelper.getRequiredUserId(req).toString();
		
		return (String) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return PermaLinkUtil.getWapLandingPageURL(req, userId);
			}
		}, WebHelper.getRequiredZoneName(req));									
	}
	
	protected boolean isAtRoot(HttpServletRequest req) {
		String path = req.getPathInfo();
		if(path == null || path.equals("/"))
			return true;
		else
			return false;
	}
	
	protected String getOriginalURL(HttpServletRequest req) {
		String url;
		if(req.getQueryString() != null)
			url = req.getRequestURL().append("?").append(req.getQueryString()).toString();
		else
			url = req.getRequestURL().toString();
		return url;
	}
	
	protected boolean isPathPermittedUnauthenticated(String path) {
		return (path != null && 
				(path.equals("/"+WebKeys.SERVLET_PORTAL_LOGIN) || 
						path.equals("/"+WebKeys.SERVLET_PORTAL_LOGOUT) || 
						path.equals("/"+WebKeys.SERVLET_VIEW_CSS)));
	}
	
	protected boolean isActionPermittedUnauthenticated(String actionValue) {
		return (actionValue != null && 
				(actionValue.startsWith("__") || 
						actionValue.equals(WebKeys.ACTION_VIEW_PERMALINK)));
	}
	
	private PortalLogin getPortalLogin() {
		return (PortalLogin) SpringContextUtil.getBean("portalLoginBean");
	}
}
