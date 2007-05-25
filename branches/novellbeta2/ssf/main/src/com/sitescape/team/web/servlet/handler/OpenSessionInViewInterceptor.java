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
package com.sitescape.team.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;

/**
 * See com.sitescape.team.web.portlet.handler.OpenSessionInViewInterceptor for 
 * explanation
 * @author Janet McCann
 *
 */
public class OpenSessionInViewInterceptor extends 
	org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor {

	public void afterCompletion(
		HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
		throws DataAccessException {
		
		super.afterCompletion(request, response, handler, ex);
		
	}
}
