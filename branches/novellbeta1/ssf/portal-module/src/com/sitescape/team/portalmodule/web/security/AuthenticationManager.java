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
package com.sitescape.team.portalmodule.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import java.util.Map;

import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;
import com.sitescape.team.web.crosscontext.CrossContextConstants;
import com.sitescape.team.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.team.web.util.NullServletResponse;

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
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(SiteScapeBridgeUtil.getSSFContextPath());

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		if(zoneName != null)
			req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		req.setParameter(CrossContextConstants.PASSWORD, password);
		if(updates != null)
			req.setAttribute(CrossContextConstants.USER_INFO, updates);
		
		NullServletResponse res = new NullServletResponse();
		
		SiteScapeBridgeUtil.include(req, res);
	}
}
