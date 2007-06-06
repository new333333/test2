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
package com.sitescape.team.module.rss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;

public interface RssModule {
	
	public void updateRssFeed(Entry entry);
	public String filterRss(HttpServletRequest request, HttpServletResponse response, Binder binder, User user);
	public String AuthError(HttpServletRequest request, HttpServletResponse response);
}