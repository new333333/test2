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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

import com.sitescape.team.context.request.PortletSessionContext;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.web.util.WebHelper;

public class InitRequestContextInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {
	
	private boolean resolve = false;
	
	public void setResolve(boolean resolve) {
		this.resolve = resolve;
	}
	
	public boolean preHandle(PortletRequest request, PortletResponse response, 
			Object handler) throws Exception {
		RequestContextHolder.clear();
			
		if(WebHelper.isUnauthenticatedRequest(request)) {
			// The framework says that this request is being made unauthenticated,
			// that is, in no particular user's context. 
			// In this case we simply pass up in the interceptor chain. 
			return true;
		}
		
		// The rest of the code assumes that the user is logged in (ie, authenticated).
		/*
		if(!WebHelper.isUserLoggedIn(request))
			throw new UnauthenticatedAccessException();
		*/
		
		RequestContext rc = RequestContextUtil.setThreadContext(request, 
				new PortletSessionContext(request.getPortletSession(false)));
		
		if(resolve)
			rc.resolve();
    	
	    return true;
	}

	public void afterCompletion(PortletRequest request, PortletResponse response, Object handler, Exception ex) throws Exception {
		// Do not clear the thread context here to allow re-use of the context
		// for other portlets being executed as part of the single user request
		// carried out by this thread. Specifically, this prevents user objects
		// from being refetched repeatedly. The only potential danger occurs when
		// the container re-uses the same thread (which it surely does) for
		// execution of next user request. But since the thread context will
		// be reinitialized at the start of each user request, I don't think
		// it imposes a serious problem. 
    	//RequestContextUtil.clearThreadContext();
	}

}
