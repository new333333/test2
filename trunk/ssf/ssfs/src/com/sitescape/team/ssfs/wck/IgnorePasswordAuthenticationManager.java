package com.sitescape.team.ssfs.wck;

import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.ssfs.CrossContextConstants;

public class IgnorePasswordAuthenticationManager extends AuthenticationManager {

	protected void setAttributes(HttpServletRequest req, 
			String zoneName, String userName, String password) {
		super.setAttributes(req, zoneName, userName, password);
		req.setAttribute(CrossContextConstants.IGNORE_PASSWORD, Boolean.TRUE);
	}	

}
