package com.sitescape.ef.portalmodule.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portalmodule.web.crosscontext.DispatchClient;
import com.sitescape.ef.portalmodule.web.util.NullServletResponse;
import com.sitescape.ef.web.crosscontext.CrossContextConstants;
import com.sitescape.util.servlet.DynamicServletRequest;

public class AuthenticationManager {
	
	public static void authenticate(HttpServletRequest request, 
			String zoneName, String userName, String password) 
		throws ServletException, IOException {		
		DynamicServletRequest req = new DynamicServletRequest(request);
		req.setParameter(CrossContextConstants.OPERATION, "authenticate");
		req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		req.setParameter(CrossContextConstants.PASSWORD, password);
		NullServletResponse res = new NullServletResponse();
		
		DispatchClient.doDispatch(req, res);
	}
}