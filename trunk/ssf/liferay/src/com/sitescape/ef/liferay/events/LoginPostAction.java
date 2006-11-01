package com.sitescape.ef.liferay.events;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;

import com.liferay.portal.util.PortalUtil;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portalmodule.web.security.AuthenticationManager;
import com.sitescape.ef.portalmodule.web.session.SessionManager;
import com.sitescape.ef.web.WebKeys;
import com.liferay.portal.service.spring.UserLocalServiceUtil;

public class LoginPostAction extends AbstractAction {

	/**
	 * This method is invoked by the Liferay portal immediately following
	 * the user's login event, hence executed in the context of Liferay. 
	 */
	public void run(HttpServletRequest req, HttpServletResponse res)
			throws ActionException {

		// Print debug information
		//testRequestEnv("Liferay.LoginPostAction", req);

		// Make sure that the request object has all parameters we need. 
		
		String companyId = PortalUtil.getCompanyId(req);
		if (companyId == null || companyId.length() == 0)
			throw new ActionException("Company ID is not found");

		String userId = PortalUtil.getUserId(req);
		if (userId == null || userId.length() == 0)
			throw new ActionException("User ID is not found");

		String password = PortalUtil.getUserPassword(req);
		if(password == null)
			password = ""; // I'm not sure if we should allow this...
		
		// Make sure that the portal created a session for the user. 
		
		HttpSession ses = req.getSession(false);
		if (ses == null)
			throw new ActionException("Session is not found");

		try {
			com.liferay.portal.model.User user = UserLocalServiceUtil.getUserById(companyId, userId);
			//sync user attributes
			Map updates = new HashMap();
			updates.put("firstName", user.getFirstName());
			updates.put("middleName", user.getMiddleName());
			updates.put("lastName", user.getLastName());
			updates.put("emailAddress", user.getEmailAddress());
			updates.put("languageId", user.getLocale().getLanguage());
			updates.put("country", user.getLocale().getCountry());
			updates.put("timeZoneName", user.getTimeZoneId());
			updates.put("organization", user.getOrganization().getName());
			updates.put("location", user.getLocation().getName());
			// First, authenticate the user against SSF user database.
			AuthenticationManager.authenticate(req, companyId, userId, password, updates);
			
			// If you're still here, the authentication was successful. 
			// Create a SSF session for the user. 
			SessionManager.createSession(req, ses.getId(), companyId, userId);
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
