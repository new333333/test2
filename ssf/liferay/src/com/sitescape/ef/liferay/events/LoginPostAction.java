package com.sitescape.ef.liferay.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.PortalUtil;
import com.sitescape.ef.portalmodule.web.security.AuthenticationManager;
import com.sitescape.ef.portalmodule.web.session.SessionManager;

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
			// First, authenticate the user against SSF user database.
			AuthenticationManager.authenticate(req, companyId, userId, password);
			
			// If you're still here, the authentication was successful. 
			// Create a SSF session for the user. 
			SessionManager.createSession(req, ses.getId(), companyId, userId);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
}
