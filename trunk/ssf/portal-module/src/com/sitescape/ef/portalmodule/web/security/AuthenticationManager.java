package com.sitescape.ef.portalmodule.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.sitescape.ef.portalmodule.CrossContextConstants;
import com.sitescape.ef.portalmodule.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.util.NullServletResponse;
import com.sitescape.util.servlet.DynamicServletRequest;

public class AuthenticationManager {
	
	public static void authenticate(HttpServletRequest request, 
			String zoneName, String userName, String password, Map updates) 
		throws ServletException, IOException {		
		DynamicServletRequest req = new DynamicServletRequest(request);
		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		req.setParameter(CrossContextConstants.PASSWORD, password);
		req.setAttribute(CrossContextConstants.USER_INFO, updates);
		NullServletResponse res = new NullServletResponse();
		
		DispatchClient.doDispatch(req, res);
	}
}