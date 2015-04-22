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
package org.kablink.teaming.remoting.ws.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;


public class SecurityPropagatingAxisServlet extends org.apache.axis.transport.http.AxisServlet {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String TOKEN_SERVICES_PATH = "token.servicesPath";
	private static final String TOKEN_SERVICES_PATH_DEFAULT_VALUE = "/token/";

	protected String tokenServicesPath;
	
    public void init() throws javax.servlet.ServletException {
    	super.init();
    	tokenServicesPath = getServletConfig().getServletContext().getInitParameter(TOKEN_SERVICES_PATH);
    	if(Validator.isNull(tokenServicesPath))
    		tokenServicesPath = TOKEN_SERVICES_PATH_DEFAULT_VALUE;
    }
    
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		String servletPath = req.getServletPath();
		if(servletPath.startsWith(tokenServicesPath)) {
			// Service request with token-based authentication.
			// In this case, authentication has NOT happened yet. 
			// It will be handled in the downstream.
			
			if(SPropsUtil.getBoolean("remoting.token.enable", true)) {
				try {
					Long zoneId = getZoneModule().getZoneIdByVirtualHost(
							req.getServerName().toLowerCase());
					// Set a temporary context with fake user ID. 
					// Pass the zone and the authenticator information down the call stack.
					RequestContextUtil.setThreadContext(zoneId, Long.valueOf(0));
					AuthenticationContextHolder.setAuthenticationContext(LoginAudit.AUTHENTICATOR_REMOTING_T, null);
					
					super.service(req, res);
				}
				finally {
					RequestContextHolder.clear();
					AuthenticationContextHolder.clear();
				}		
			}
			else {
				if(logger.isDebugEnabled())
					logger.debug("Denying " + req.getRemoteAddr() + " access to token-based remote services: It is disabled");
				throw new ServletException("The service is disabled");
			}
		}
		else {
			// Non-secured request (ie, request requiring no authentication)
			// or secured request that has already passed authentication.
			// For these two scenarios, no additional authentication is needed.
			String remoteUser = WebHelper.getRemoteUserName(req);
	
			String zoneName = getZoneModule().getZoneNameByVirtualHost(
					req.getServerName().toLowerCase());
	
			String userName;
			if (remoteUser != null) { // authenticated user
				userName = remoteUser;
				if (logger.isDebugEnabled()) {
					logger.debug("Remote user " + remoteUser);
				}
			} else { // unauthenticated user (anonymous access) - assume guest identity
				if(SPropsUtil.getBoolean("remoting.anonymous.enable", true)) {
					userName = SZoneConfig.getGuestUserName(zoneName);
					if (logger.isDebugEnabled()) {
						logger.debug("Anonymous access: assuming " + userName);
					}
				}
				else {
					if(logger.isDebugEnabled())
						logger.debug("Denying " + req.getRemoteAddr() + " anonymous access to remote services: It is disabled");
					throw new ServletException("The service is disabled");		
				}
			}
	
			try {
				RequestContextUtil.setThreadContext(zoneName, userName);
	
				super.service(req, res);
			} finally {
				RequestContextHolder.clear();
			}
		}
	}

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
