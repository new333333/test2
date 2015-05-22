/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.LimitUserVisibilityInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.util.AllModulesInjected;

/**
 * Helper methods for GWT photo album folder views.
 *
 * @author drfoster@novell.com
 */
public class GwtUserVisibilityHelper {
	protected static Log m_logger = LogFactory.getLog(GwtUserVisibilityHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtUserVisibilityHelper() {
		// Nothing to do.
	}
	
	/**
	 * Returns a LimitUserVisibilityInfoRpcResponseData containing
	 * information about the user visibility limitations currently in
	 * affect.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static LimitUserVisibilityInfoRpcResponseData getLimitUserVisibilityInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserVisibilityHelper.getLimitUserVisibilityInfo()");
		try {
			LimitUserVisibilityInfoRpcResponseData reply = new LimitUserVisibilityInfoRpcResponseData();
			
//!			...this needs to be implemented...
			
			// If we get here, reply contains a
			// LimitUserVisibilityInfoRpcResponseData containing the
			// user visibility limitations currently in affect.  Return
			// it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Sets the 'Can Only See Members of Group I'm In' and
	 * corresponding override flags on the given principal.
	 * 
	 * @param bs
	 * @param request
	 * @param principalId
	 * @param canOnlySeeMembersOfGroupsImIn
	 * @param overrideCanOnlySeeMembersOfGroupsImIn
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData setUserVisibility(AllModulesInjected bs, HttpServletRequest request, Long principalId, Boolean canOnlySeeMembersOfGroupsImIn, Boolean overrideCanOnlySeeMembersOfGroupsImIn) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserVisibilityHelper.setUserVisibility()");
		try {
			// If no flags need to be set...
			StringRpcResponseData reply = new StringRpcResponseData();
			if ((null == canOnlySeeMembersOfGroupsImIn) || (null == overrideCanOnlySeeMembersOfGroupsImIn)) {
				// ...bail.
				return reply;
			}
			
//!			...this needs to be implemented...
			
			// If we get here, reply contains a StringRpcResponseData
			// containing any error generated.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
}
