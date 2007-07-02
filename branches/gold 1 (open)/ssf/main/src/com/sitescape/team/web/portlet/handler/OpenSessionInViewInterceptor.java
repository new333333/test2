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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.dao.DataAccessException;

/**
 *
 */
public class OpenSessionInViewInterceptor extends 
	org.springframework.web.portlet.support.hibernate3.OpenSessionInViewInterceptor {
	public void afterCompletion(
			PortletRequest request, PortletResponse response, Object handler, Exception ex)
			throws DataAccessException {
		super.afterCompletion(request, response, handler, ex);
		
	}

}
