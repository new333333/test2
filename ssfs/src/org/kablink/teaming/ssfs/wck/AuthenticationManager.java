/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.ssfs.wck;


import javax.servlet.http.HttpServletRequest;

import org.apache.slide.simple.authentication.SessionAuthenticationManager;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.ssfs.CrossContextConstants;
import org.kablink.teaming.ssfs.web.crosscontext.DispatchClient;
import org.kablink.teaming.web.util.AttributesAndParamsOnlyServletRequest;
import org.kablink.teaming.web.util.NullServletResponse;


public class AuthenticationManager implements SessionAuthenticationManager {

	public Object getAuthenticationSession(String userName, String password) throws Exception {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());

		setAttributes(req, ZoneContextHolder.getServerName(), userName, password);
		
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
			// Use user name as session object.
			return userName;
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

	protected void setAttributes(HttpServletRequest req, 
			String serverName, String userName, String password) {
		req.setAttribute(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		if(serverName != null)
			req.setAttribute(CrossContextConstants.SERVER_NAME, serverName);
		req.setAttribute(CrossContextConstants.USER_NAME, userName);
		req.setAttribute(CrossContextConstants.PASSWORD, password);
	}
}
