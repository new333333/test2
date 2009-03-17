package org.kablink.teaming.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StandaloneLogin implements PortalLogin {

	public void loginPortal(HttpServletRequest request,
			HttpServletResponse response, String username, String password,
			boolean remember) throws Exception {
	}

	public void logoutPortal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	}

	public void touchPortal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	}

}
