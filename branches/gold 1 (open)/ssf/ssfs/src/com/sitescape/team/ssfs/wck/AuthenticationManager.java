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
package com.sitescape.team.ssfs.wck;

import org.apache.slide.simple.authentication.SessionAuthenticationManager;

import com.sitescape.team.ssfs.CrossContextConstants;
import com.sitescape.team.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.team.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.team.web.util.NullServletResponse;

public class AuthenticationManager implements SessionAuthenticationManager {

	public Object getAuthenticationSession(String user, String password) throws Exception {
		String[] id = Util.parseUserIdInput(user);
		
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());
		req.setAttribute(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		req.setAttribute(CrossContextConstants.ZONE_NAME, id[0]);
		req.setAttribute(CrossContextConstants.USER_NAME, id[1]);
		req.setAttribute(CrossContextConstants.PASSWORD, password);
		NullServletResponse res = new NullServletResponse();
		
		DispatchClient.doDispatch(req, res);
		
		String errorCode = (String) req.getAttribute(CrossContextConstants.ERROR);
		
		if(errorCode != null) { // The authentication failed
			// It doesn't really matter what kind of Exception object 
			// we throw from here. So I'll simply use the base class.
			String errorMessage = (String) req.getAttribute(CrossContextConstants.ERROR_MESSAGE);
			throw new Exception(errorMessage);
		}
		else {
			// We simply use the original user id as session object. 
			return user;
		}
	}

	public Object getAuthenticationSession(String user) throws Exception {
		// Since this method is called only for successfully authenticated
		// user, we can safely return the session object which is the same
		// string as the user id. 
		// We do not really need connection-oriented session object because
		// 1) SSFS does not need separate session/state for each login (i.e. 
		// multiple logins from different users or even from the same user), 
		// and 2) keeping session map is problematic hence best avoided. 
		
		return user;
	}

	public void closeAuthenticationSession(Object session) throws Exception {
	}

	
}
