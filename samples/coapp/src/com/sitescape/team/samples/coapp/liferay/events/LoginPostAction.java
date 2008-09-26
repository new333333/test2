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
package com.sitescape.team.samples.coapp.liferay.events;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.util.servlet.DynamicServletRequest;
import com.liferay.util.servlet.NullServletResponse;
import com.sitescape.team.liferay.events.AbstractAction;
import com.sitescape.team.portalmodule.web.session.SessionManager;

public class LoginPostAction extends AbstractAction {

	@Override
	public void run(HttpServletRequest req, HttpServletResponse res) throws ActionException {
		HttpSession ses = req.getSession(false);
		if (ses == null)
			throw new ActionException("Session not found");
		
		try {
			// Make sure that the request object has all parameters we need. 
			
			com.liferay.portal.model.Company company = PortalUtil.getCompany(req);

			com.liferay.portal.model.User user = PortalUtil.getUser(req);
			if(user == null)
				throw new ActionException("User not found");

			String password = PortalUtil.getUserPassword(req);

			doSSOWithCoApp(req, res, company.getWebId(), user.getScreenName(), password);
			
			SessionManager.createSession(req, ses.getId(), company.getWebId(), user.getScreenName());
		} catch(ActionException e) {
			throw e;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	protected void doSSOWithCoApp(HttpServletRequest request, HttpServletResponse response, String zoneName, String userName, String password) 
	throws ActionException, ServletException, IOException {
		// Retrieve the servlet context of the portal
		ServletContext ctx = (ServletContext) request.getAttribute(WebKeys.CTX);
		if(ctx == null)
			throw new ActionException("No servlet context");
		
		// Retrieve the servlet context of the co-app
		ServletContext coAppCtx = ctx.getContext("/coapp");
		// Retrieve request dispatcher for the co-app
		RequestDispatcher dispatcher = coAppCtx.getNamedDispatcher("ssoServlet");
		
		// Set up the request/response objects appropriately. The name of the
		// request and the necessary data (username) are passed in. 
		DynamicServletRequest req = new DynamicServletRequest(request);

		req.setParameter("teaming.coapp.operation", "sso");
		req.setParameter("teaming.coapp.sso.username", userName);
		req.setParameter("teaming.coapp.sso.password", password);
		
		NullServletResponse res = new NullServletResponse(response);

		// Finally, make a cross-context invocation.
		dispatcher.include(req, res);
	}
}
