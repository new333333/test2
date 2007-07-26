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
package com.sitescape.team.liferay.events;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;

import com.liferay.portal.util.PortalUtil;
import com.sitescape.team.portalmodule.web.security.AuthenticationManager;
import com.sitescape.team.portalmodule.web.session.SessionManager;

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
			Map updates = new HashMap();
			if(user.getFirstName() != null)
				updates.put("firstName", user.getFirstName());
			if(user.getMiddleName() != null)
				updates.put("middleName", user.getMiddleName());
			if(user.getLastName() != null)
				updates.put("lastName", user.getLastName());
			if(user.getEmailAddress() != null)
				updates.put("emailAddress", user.getEmailAddress());
			if(user.getLocale() != null)
				updates.put("locale", user.getLocale());
			if(user.getTimeZone() != null)
				updates.put("timeZone", user.getTimeZone());
			// Due to a bug in Liferay, the following information we get directly
			// from the user model object does not match the user attributes
			// obtainable from the portal in a portable way (using
			// javax.portlet.PortletRequest.USER_INFO). So do not deal with it
			// here. Additional information, if any, can be updated when the
			// user actually accesses the system. 
			//updates.put("organization", user.getOrganization().getName());
			// First, authenticate the user against SSF user database.
			AuthenticationManager.authenticate(company.getWebId(), user.getScreenName(), password, updates);
			
			// If you're still here, the authentication was successful. 
			// Create a SSF session for the user. 
			SessionManager.createSession(req, ses.getId(), company.getWebId(), user.getScreenName());
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
