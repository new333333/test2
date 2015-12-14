/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.webdav.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.WindowsUtil;
import org.kablink.teaming.web.util.BuiltInUsersHelper;

/**
 * ?
 * 
 * @author jong
 */
public class RequestContextSetupFilter implements Filter {
	
	private static final String OPTIONS_METHOD = "OPTIONS";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		// Check if HTTP OPTIONS method
		if(isHttpOptionsMethod(req)) { // HTTP OPTIONS
			if(req.getUserPrincipal() == null) {
				// The client is not authenticated. This is allowed for OPTIONS method. 
				// Execute the request in the context of admin user in this case. This
				// should be safe since OPTIONS method can not do any harm to the system.
				doSetupAndExecute(request, response, chain, BuiltInUsersHelper.getAdminName());
			}
			else {
				// The client is authenticated. Proceed as normal.
				doSetupAndExecute(request, response, chain, req.getUserPrincipal().getName());
			}
		}
		else { // Something other than HTTP OPTIONS method. 
			if(req.getUserPrincipal() == null) {
				// Client authentication is required.
				throw new ServletException("Unauthorized access");
			}
			else {
				// Execute the request in the context of the authenticated user.
				doSetupAndExecute(request, response, chain, req.getUserPrincipal().getName());
			}
		}
	}

	private void doSetupAndExecute(ServletRequest request, ServletResponse response, FilterChain chain, String contextUserName) throws IOException, ServletException {
		// Set up request context
		setupRequestContext(contextUserName);
		
		try {
			// Resolve request context
			resolveRequestContext();
	
			chain.doFilter(request, response);
		}
		finally {
			// Clear request context
			clearRequestContext();			
		}
	}
	
	private void setupRequestContext(String userName) {
		RequestContextHolder.clear();
		
        Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
		
		RequestContextUtil.setThreadContext(zoneId, WindowsUtil.getSamaccountname(userName));
	}

	private void resolveRequestContext() {
		RequestContextHolder.getRequestContext().resolve();
	}
	
	private void clearRequestContext() {
		RequestContextHolder.clear();
	}
		
	private boolean isHttpOptionsMethod(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(OPTIONS_METHOD);
	}

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
}
