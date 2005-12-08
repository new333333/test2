package com.sitescape.ef.liferay.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.PortalUtil;
import com.sitescape.ef.portalmodule.web.session.SessionManager;

public class LoginPostAction extends AbstractAction {

	public void run(HttpServletRequest req, HttpServletResponse res)
			throws ActionException {

		// Print debug information
		//testRequestEnv("Liferay.LoginPostAction", req);

		String companyId = PortalUtil.getCompanyId(req);
		if (companyId == null || companyId.length() == 0)
			throw new ActionException("Company ID is not found");

		String userId = PortalUtil.getUserId(req);
		if (userId == null || userId.length() == 0)
			throw new ActionException("User ID is not found");

		HttpSession ses = req.getSession(false);
		if (ses == null)
			throw new ActionException("Session is not found");

		try {
			SessionManager.createSession(req, ses.getId(), companyId, userId);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
}
