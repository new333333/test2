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
package org.kablink.teaming.ssfs.web.crosscontext;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.ssfs.wck.Util;


public class DispatchClient {
	
	private static final String SSFS_CC_DISPATCHER = "ssfsCCDispatcher";
	
	private static ServletConfig ssfsServletConfig;
	private static ServletContext ssfContext;
	
	public static void init(ServletConfig ssfsServletCfg) {
		ssfsServletConfig = ssfsServletCfg;
	}
	
	public static void fini() {	
	}
	
	public static void doDispatch(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		// We use just-in-time initialization, rather than doing it fully at the 
		// system startup time, because the SSF web app may have not been loaded
		// at the time the SSFS is initialized. So we defer completion of this
		// initialization until very first time user attempts to log into the system.
		initInternal();
		
		RequestDispatcher dispatcher = ssfContext.getNamedDispatcher(SSFS_CC_DISPATCHER);
		
		if(dispatcher != null) {
			dispatcher.include(request, response);
		}
		else {
			// This facility is used primarily to pass a request from SSFS
			// to SSF. If everything is configured and deployed properly,
			// the dispatcher should be always non null. However, under the
			// shutdown scenario where SSF app is shutdown before the portal,
			// it is possible that this code is no longer able to obtain
			// the dispatcher for the SSF servlet. Unfortunately, there is
			// no standard way of obtaining a handle on the log facility 
			// associated with the app server within which this code is being
			// executed. Therefore, we will simply log something to the console
			// for the informational purpose only. Since the app server is
			// shutting down anyway, this shouldn't be a big deal. 
			System.out.println("Unable to obtain request dispatcher for " +
					SSFS_CC_DISPATCHER + " from SSF context");
		}
	}
	
	private static void initInternal() {
		synchronized(DispatchClient.class) {
			if(ssfContext == null) {				
				ssfContext = ssfsServletConfig.getServletContext().getContext(Util.getSsfContextPath());
			}
		}
	}
}
