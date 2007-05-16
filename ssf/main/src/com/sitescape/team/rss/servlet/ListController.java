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
package com.sitescape.team.rss.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.User;
import com.sitescape.team.rss.RssGenerator;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.WebHelper;

public class ListController extends SAbstractController {

	private RssGenerator rssGenerator;
	
	protected RssGenerator getRssGenerator() {
		return rssGenerator;
	}
	public void setRssGenerator(RssGenerator rssGenerator) {
		this.rssGenerator = rssGenerator;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Get the list of RSS items and write it to servlet response as XML.
		Long binderId = new Long(RequestUtils.getRequiredStringParameter(request, "bi"));
		Binder binder = null;
		User user = null;
		// Test if the user is authenticated or not using the flag stored in
		// the request. Don't ever make this decision based on the existence
		// of request context data, since it may be a stale data from previous
		// request that for some reason was cleared properly. 
		if(!WebHelper.isUnauthenticatedRequest(request)) {
			binder = getBinderModule().getBinder(binderId);
			user = RequestContextHolder.getRequestContext().getUser();
		}
		
		response.resetBuffer();
		response.setContentType("text/xml; charset=" + XmlFileUtil.FILE_ENCODING);
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		//use writer to enfoce character set
		response.getWriter().write(getRssGenerator().filterRss(request, response, binder,user));
		response.flushBuffer();
		return null;
	}

}
