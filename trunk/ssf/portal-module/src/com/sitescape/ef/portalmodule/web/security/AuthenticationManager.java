package com.sitescape.ef.portalmodule.web.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.util.Map;

import com.sitescape.ef.ascore.cc.SiteScapeUtil;
import com.sitescape.ef.portalmodule.CrossContextConstants;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;

public class AuthenticationManager {
	
	/**
	 * 
	 * @param request
	 * @param zoneName may be null
	 * @param userName
	 * @param password
	 * @param updates may be null
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void authenticate(String zoneName, String userName, String password, Map updates) 
		throws ServletException, IOException {		
		RequestDispatcher rd = SiteScapeUtil.getCCDispatcher();

		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(SiteScapeUtil.getSSFContextPath());

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		if(zoneName != null)
			req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		req.setParameter(CrossContextConstants.PASSWORD, password);
		if(updates != null)
			req.setAttribute(CrossContextConstants.USER_INFO, updates);
		
		NullServletResponse res = new NullServletResponse();
		
		rd.include(req, res);
	}
}
