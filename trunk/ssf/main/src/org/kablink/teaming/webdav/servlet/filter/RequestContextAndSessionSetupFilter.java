/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.WindowsUtil;

/**
 * @author jong
 *
 */
public class RequestContextAndSessionSetupFilter implements Filter {

	private Log logger = LogFactory.getLog(getClass());
	
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		// Check authentication
		checkAuthentication(req);

		// Set up request context
		setupRequestContext(req);
		
		try {
			// Set up Hibernate session
			setupHibernateSession(req);
			
			try {
				// Resolve request context
				resolveRequestContext(req);
		
				chain.doFilter(request, response);
			}
			finally {
				// Tear down Hibernate session
				teardownHibernateSession(req);				
			}
		}
		finally {
			// Clear request context
			clearRequestContext(req);			
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	private void checkAuthentication(HttpServletRequest request) throws ServletException {
		if(request.getUserPrincipal() == null)
			throw new ServletException("Unauthorized access");
	}
	
	private void setupRequestContext(HttpServletRequest request) {
		RequestContextHolder.clear();
		
        Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());

        String userName = request.getUserPrincipal().getName();
		
		RequestContextUtil.setThreadContext(zoneId, WindowsUtil.getSamaccountname(userName));
	}

	private void resolveRequestContext(HttpServletRequest request) {
		RequestContextHolder.getRequestContext().resolve();
	}
	
	private void clearRequestContext(HttpServletRequest request) {
		RequestContextHolder.clear();
	}
	
	private void setupHibernateSession(HttpServletRequest request) {
		// NOTE: This could be problematic if a single request from client ever results in a chained 
		// invocation of more than one methods on resource(s). If such case is a possibility, we need
		// to create session conditionally (i.e., only when SessionUtil.sessionActive() returns false) 
		// so that the thread of execution can share a single Hibernate session. 
		if(SessionUtil.sessionActive())
			logger.warn("We've got an active Hibernate session for request " + request.getPathInfo());
		else 
			SessionUtil.sessionStartup();
	}
	
	private void teardownHibernateSession(HttpServletRequest request) {
		SessionUtil.sessionStop();
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
