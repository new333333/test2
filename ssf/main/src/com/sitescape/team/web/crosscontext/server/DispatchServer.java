/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.web.crosscontext.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.crosscontext.CrossContextConstants;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static final String PORTAL_CC_DISPATCHER = "portalCCDispatcher";
	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	public void init(ServletConfig config) throws ServletException {
		RequestDispatcher rd = config.getServletContext().getNamedDispatcher(PORTAL_CC_DISPATCHER);
		SiteScapeBridgeUtil.setCCDispatcher(rd);
		String cxt = config.getInitParameter("ssfContextPath");
		if(cxt == null || cxt.length() == 0)
			cxt = SSF_CONTEXT_PATH_DEFAULT;
		SiteScapeBridgeUtil.setSSFContextPath(cxt);
		try {
			SiteScapeBridgeUtil.setClassLoader(Thread.currentThread().getContextClassLoader());
		}
		catch(Exception e) {
			new ServletException(e);
		}
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = req.getParameter(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) {
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(zoneName == null)
				zoneName = SZoneConfig.getDefaultZoneName();
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			String password = req.getParameter(CrossContextConstants.PASSWORD);
			Map updates = (Map)req.getAttribute(CrossContextConstants.USER_INFO);
			String authenticator = req.getParameter(CrossContextConstants.AUTHENTICATOR);

			// Authenticate the user against SSF user database.
			try {
				boolean passwordAutoSynch = 
					SPropsUtil.getBoolean("portal.password.auto.synchronize", false);
				boolean ignorePassword = 
					SPropsUtil.getBoolean("portal.password.ignore", false);
				boolean createUser = 
					SPropsUtil.getBoolean("portal.user.auto.create", false);
				AuthenticationManagerUtil.authenticate(zoneName, userName, password, createUser, passwordAutoSynch, ignorePassword, updates, authenticator);
			}
			catch(UserDoesNotExistException e) {
				logger.warn(e.getLocalizedMessage(), e);
				// Throw ServletException with cause's error message rather
				// then the cause itself. This is because the class loader
			    // of the calling app does not have access to the class of 
				// the cause exception. 
				throw new ServletException(e.getLocalizedMessage());
			}
			catch(PasswordDoesNotMatchException e) {
				logger.warn(e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}	
			catch(Exception e) {
				logger.warn(e.getLocalizedMessage(), e);
				if(e instanceof IOException)
					throw (IOException) e;
				else
					throw new ServletException(e.getLocalizedMessage());
			}
		}
		else if(operation.equals(CrossContextConstants.OPERATION_SETUP_SESSION)) {
			HttpServletRequest request = (HttpServletRequest) req;			
			HttpSession ses = request.getSession();
			
			if(ses.getAttribute(WebKeys.ZONE_NAME) == null) {		
				String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
				if(zoneName == null)
					zoneName = SZoneConfig.getDefaultZoneName();
				String userName = req.getParameter(CrossContextConstants.USER_NAME);
				if(userName == null)
					userName = SZoneConfig.getGuestUserName(zoneName);
				
				ses.setAttribute(WebKeys.ZONE_NAME, zoneName);
				ses.setAttribute(WebKeys.USER_NAME, userName);
				ses.setAttribute(WebKeys.SERVER_NAME, req.getServerName().toLowerCase());
				ses.setAttribute(WebKeys.SERVER_PORT, Integer.valueOf(req.getServerPort()));
				if(logger.isDebugEnabled())
					logger.debug("Server name:port is " + req.getServerName().toLowerCase() + ":" + req.getServerPort() + " for user " + userName + " at the time of login");
			}
		}
		else {
			logger.error("Unrecognized operation [" + operation + "]");
		}
	}
}
