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
package com.sitescape.team.servlet.rss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.WebHelper;

public class ListController extends SAbstractController {

	private static final int THREESECS = 3000;
	private boolean authErr = false;
	private boolean binderExists = true;

	@Override
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Get the list of RSS items and write it to servlet response as XML.
		Long binderId = new Long(RequestUtils.getRequiredStringParameter(request, "bi"));
		Binder binder = null;
		authErr = false;
		binderExists = true;
		
		// Test if the user is authenticated or not using the flag stored in
		// the request. Don't ever make this decision based on the existence
		// of request context data, since it may be a stale data from previous
		// request that for some reason was cleared properly. 
		if(!WebHelper.isUnauthenticatedRequest(request)) {
			try {
				binder = getBinderModule().getBinder(binderId);
			} catch (NoBinderByTheIdException nbe) {
				binderExists = false;
			}
		} else {
			// the authentication key is incorrect, make them wait
			// a bit (to stop immediate retries from hackers), and 
			// and then let them know that the request failed.
			Thread.sleep(THREESECS);
			authErr = true;
			
		}
		
		response.resetBuffer();
		response.setContentType("text/xml; charset=" + XmlFileUtil.FILE_ENCODING);
		response.setHeader("Cache-Control", "private");
		//use writer to enfoce character set
		if (authErr) {
			response.getWriter().write(getRssModule().AuthError(request, response));
		} else if (!binderExists) {
			response.getWriter().write(getRssModule().BinderExistenceError(request, response));
		} else {
			response.getWriter().write(getRssModule().filterRss(request, response, binder)); 
		}
		response.flushBuffer();
		return null;
	}

}
