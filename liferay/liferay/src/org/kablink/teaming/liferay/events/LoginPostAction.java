/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.liferay.events;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.liferay.util.Util;
import org.kablink.teaming.portalmodule.security.AuthenticationManager;
import org.kablink.teaming.portalmodule.web.session.SessionManager;

import com.liferay.portal.struts.ActionException;

import com.liferay.portal.util.PortalUtil;

public class LoginPostAction extends AbstractAction {

	/**
	 * This method is invoked by the Liferay portal immediately following
	 * the user's login event, hence executed in the context of Liferay. 
	 */
	public void run(HttpServletRequest req, HttpServletResponse res)
			throws ActionException {

		// Print debug information
		//testRequestEnv("Liferay.LoginPostAction", req);

		// Make sure that the portal created a session for the user. 
		
		HttpSession ses = req.getSession(false);
		if (ses == null)
			throw new ActionException("Session is not found");
		
		try {
			// Make sure that the request object has all parameters we need. 
			
			com.liferay.portal.model.Company company = PortalUtil.getCompany(req);

			com.liferay.portal.model.User user = PortalUtil.getUser(req);
			if(user == null)
				throw new ActionException("User not found");

			String password = PortalUtil.getUserPassword(req);
			if(password == null)
				password = ""; // I'm not sure if we should allow this...
			
			//sync user attributes
			Map updates = Util.getUpdatesMap(user);

			// First, authenticate the user against SSF user database.
			AuthenticationManager.authenticate(company.getWebId(), user.getScreenName(), password, updates);
			
			// If you're still here, the authentication was successful. 
			// Create a SSF session for the user. 
			SessionManager.setupSession(req, ses.getId(), company.getWebId(), user.getScreenName());
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
/*	Would prefer to do this in a controller, but I cannot get hold of the timezone in a portable way	
 * com.liferay.portal.model.User user = UserLocalServiceUtil.getUserById(companyId, userId);
		PortletSession ses = request.getPortletSession();

		if (ses != null) {
			Boolean sync = (Boolean)ses.getAttribute(WebKeys.PORTLET_USER_SYNC, PortletSession.APPLICATION_SCOPE);
			if ((sync == null) || sync.equals(Boolean.FALSE)) {
				//sync user attributes
				Map updates = new HashMap();
				Map userAttrs = (Map)request.getAttribute(javax.portlet.PortletRequest.USER_INFO);
				String val = null;
				if (userAttrs.containsKey("user.name.given")) {
					val = (String)userAttrs.get("user.name.given");
					if (!val.equals(user.getFirstName())) updates.put("firstName", val);
				}
				if (userAttrs.containsKey("user.name.family")) {
					val = (String)userAttrs.get("user.name.family");
					if (!val.equals(user.getLastName())) updates.put("lastName", val);
				}
				if (userAttrs.containsKey("user.business-info.online.email")) {
					val = (String)userAttrs.get("user.business-info.online.email");
					if (!val.equals(user.getEmailAddress())) updates.put("emailAddress", val);
				}
				val = request.getLocale().getLanguage();
				if (!val.equals(user.getLanguageId())) updates.put("languageId", val);
				val = request.getLocale().getCountry();
				if (!val.equals(user.getCountry())) updates.put("country", val);
				if (!updates.isEmpty()) getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
				ses.setAttribute(WebKeys.PORTLET_USER_SYNC, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);				
			}
	*/		
}
