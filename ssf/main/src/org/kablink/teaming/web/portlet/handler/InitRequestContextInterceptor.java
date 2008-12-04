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
package org.kablink.teaming.web.portlet.handler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.kablink.teaming.context.request.PortletSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.web.util.WebHelper;


public class InitRequestContextInterceptor extends AbstractInterceptor {
	
	private boolean resolve = false;
	
	public void setResolve(boolean resolve) {
		this.resolve = resolve;
	}
	
	public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
		throws Exception {
		return preHandle(request, response, handler);
	}

	public boolean preHandleAction(ActionRequest request, ActionResponse response, Object handler)
    throws Exception {
		return preHandle(request, response, handler);
	}
	private boolean preHandle(PortletRequest request, PortletResponse response, Object handler) {
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
}
