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
package org.kablink.teaming.portalmodule.web.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.asmodule.bridge.BridgeClient;
import org.kablink.teaming.portal.CrossContextConstants;
import org.kablink.teaming.web.util.NullServletResponse;
import org.kablink.util.servlet.DynamicServletRequest;


/**
 * This class serves as a bridge between the portal and the sitescape
 * portlet application and is deployed directly into the portal rather
 * than packaged with the sitescape app.
 * <p>
 * The primary utility of this class is to provide mechanism for managing
 * SSF sessions for users in such a manner that the lifecycle of SSF
 * sessions become entirely subordinate to that of portal sessions.  
 * (i.e., whan a portal session is created, corresponding SSF session is
 * created; when the portal session times out or gets invalidated, the
 * corresponding SSF session gets invalidated as well, etc.)
 *  
 * @author jong
 *
 */
public class SessionManager {
	
	public static void setupSession(HttpServletRequest request, 
			String portalSessionId, String zoneName, String userName) 
		throws ServletException, IOException {
		//System.out.println("### SessionManager [createSession]: ");
		//System.out.println("\tportal session id: " + portalSessionId);
		//System.out.println("\tzone name: " + zoneName);
		//System.out.println("\tuser name: " + userName);
		
		DynamicServletRequest req = new DynamicServletRequest(request);

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_SETUP_SESSION);
		req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		if(userName != null)
			req.setParameter(CrossContextConstants.USER_NAME, userName);
		
		NullServletResponse res = new NullServletResponse();
		
		BridgeClient.include(req, res);
	}
}
