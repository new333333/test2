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
package com.sitescape.team.web.portlet.handler;

import java.io.PrintWriter;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.util.NLT;
import com.sitescape.team.web.util.WebHelper;

public class LoginCheckInterceptor implements HandlerInterceptor {

	public boolean preHandle(PortletRequest request, PortletResponse response, Object handler) throws Exception {
		if(WebHelper.isUnauthenticatedRequest(request)) {
			return true;
		}
		else if(!WebHelper.isUserLoggedIn(request)) {
			// User not logged in. 
			// In this case we simply display a friendly message (if possible) 
			// to the user instead of throwing an exception. In other words we 
			// treat this as a normal circumstance rather than an error, because 
			// portals can allow user to view certain pages without logging in, 
			// and we must deal graciously with the situation where one or more
			// Aspen portlets are configured on that page. 
			if(response instanceof RenderResponse) {
				String message = NLT.get("portlet.requires.login", "Please log in to view this portlet.");
				RenderResponse res = (RenderResponse) response;
			    res.setContentType("text/html");
			    PrintWriter writer = res.getWriter();
				writer.print(message);
				writer.close();
			}
			return false;
		}
		else {
			return true;
		}
	}

	public void postHandle(RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(PortletRequest request, PortletResponse response, Object handler, Exception ex) throws Exception {
	}

}
