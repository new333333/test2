/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.profile.widgets.GwtProfilePage;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used by the GWT RPC controller and GWT RPC service
 * methods to pass around the HttpServletRequest that invoked an
 * RPC call.
 * 
 * @author drfoster@novell.com
 */
public class HttpRequestInfo implements IsSerializable {
	private transient Object m_requestObj;
	private           String m_userLoginId;	// The login ID of the user the client thinks we are dealing with.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public HttpRequestInfo() {
		// Nothing to do.
	}
	
	/**
	 * This method should be used by the client to construct an
	 * HttpRequestInfo. 
	 */
	public static HttpRequestInfo createHttpRequestInfo()
	{
		String userLoginId;
		try {
			// Can we access the user's login ID from GwtMainPage?
			userLoginId = GwtMainPage.m_requestInfo.getUserLoginId();
		}
		catch (Exception eMain) {
			try {
				// No!  Can we access it from GwtProfilePage?
				userLoginId = GwtProfilePage.profileRequestInfo.getUserLoginId();
			}
			catch (Exception eProfile) {
				// No!  Then we don't know where to get it.
				userLoginId = null;
			}
		}

		// Construct, initialize and return an HttpRequestInfo object
		HttpRequestInfo reply = new HttpRequestInfo();
		if (GwtClientHelper.hasString(userLoginId)) {
			reply.setUserLoginId(userLoginId);
		}		
		return reply;
	}
	
	/**
	 * Get'er/Set'er methods.
	 */
	public Object getRequestObj()                    {return m_requestObj;        }
	public String getUserLoginId()                   {return m_userLoginId;       }
	public void   setRequestObj( Object requestObj ) {m_requestObj  = requestObj; }
	public void   setUserLoginId(String userLoginId) {m_userLoginId = userLoginId;}	
}
