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
