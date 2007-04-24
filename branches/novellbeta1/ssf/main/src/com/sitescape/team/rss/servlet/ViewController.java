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

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.servlet.SAbstractController;

public class ViewController extends SAbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO To be done by Roy 
		// This serves request from RSS reader for a specific page. 
		// Unlike ListController, this deals with regular control flow, which
		// normally involves invoking some business methods, preparing models,
		// and then delegating the rendering procedure to a view component
		// such as a JSP page, etc. 

		// Just for testing
		String data = "<html><body>How are you?</body></html>";
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(data.getBytes());

		response.getOutputStream().flush();
		
		return null; // Normally rendering should be delegated to a view
	}
}
