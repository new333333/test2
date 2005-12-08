package com.sitescape.ef.liferay.events;

import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.ActionException;
import com.liferay.portal.struts.SessionAction;
import com.sitescape.ef.portalmodule.web.session.SessionManager;

public class SessionDestroyAction extends SessionAction {

	public void run(HttpSession ses) throws ActionException {
		try {
			SessionManager.destroySession(ses.getId());
		} catch (Exception e) {
		    throw new ActionException(e);
		}
	}

}
