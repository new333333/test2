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
import java.util.HashMap;
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
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.authentication.AuthenticationManager;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.crosscontext.CrossContextConstants;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static final String PORTAL_CC_DISPATCHER = "portalCCDispatcher";
	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	private static final String PORTAL_PROFILE_DELETE_USER_WORKSPACE = "portal.profile.deleteUserWorkspace";
	private static final boolean PORTAL_PROFILE_DELETE_USER_WORKSPACE_DEFAULT_VALUE = false;

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
				getAuthenticationManager().authenticate(zoneName, userName, password, createUser, passwordAutoSynch, ignorePassword, updates, authenticator);
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
		else if(operation.equals(CrossContextConstants.OPERATION_CREATE_SESSION)) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(zoneName == null)
				zoneName = SZoneConfig.getDefaultZoneName();
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			
			HttpSession ses = request.getSession();
			
			ses.setAttribute(WebKeys.ZONE_NAME, zoneName);
			ses.setAttribute(WebKeys.USER_NAME, userName);
			ses.setAttribute(WebKeys.SERVER_NAME, req.getServerName());
			ses.setAttribute(WebKeys.SERVER_PORT, Integer.valueOf(req.getServerPort()));
			if(logger.isDebugEnabled())
				logger.debug("Server name:port is " + req.getServerName() + ":" + req.getServerPort() + " for user " + userName + " at the time of login");
		}
		else if(operation.equals(CrossContextConstants.OPERATION_MODIFY_SCREEN_NAME)) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String contextZoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(contextZoneName == null)
				contextZoneName = SZoneConfig.getDefaultZoneName();
			// Use admin as the context user for this operation.
			String adminName = SZoneConfig.getAdminUserName(contextZoneName);
			
			String oldScreenName = req.getParameter(CrossContextConstants.OLD_SCREEN_NAME);
			String newScreenName = req.getParameter(CrossContextConstants.SCREEN_NAME);
			
			boolean closeSession = false;
			if (!SessionUtil.sessionActive()) {
				SessionUtil.sessionStartup();	
				closeSession = true;
			}
			try {
				User user = getProfileDao().findUserByName(oldScreenName, contextZoneName);
				
				RequestContext oldCtx = RequestContextHolder.getRequestContext();
				try {
					RequestContextUtil.setThreadContext(user);
					
					HashMap map = new HashMap();
					map.put("name", newScreenName);
					map.put("foreignName", newScreenName);
	
					getProfileModule().modifyEntry(user.getParentBinder().getId(), 
						user.getId(), new MapInputData(map));
				}
				finally {
					RequestContextHolder.setRequestContext(oldCtx); // Restore old context					
				}
			} catch (NoUserByTheNameException e) {
				// The user doesn't exist on the Teaming side.
				// This is possible, so don't throw an error.
				logger.warn(e.toString());
				return;
			} catch (WriteFilesException e) {
				logger.error(e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}
			finally {
				if (closeSession) 
					SessionUtil.sessionStop();
			}
		}
		else if(operation.equals(CrossContextConstants.OPERATION_DELETE_USER)) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String contextZoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(contextZoneName == null)
				contextZoneName = SZoneConfig.getDefaultZoneName();
			// Use admin (rather than the user being deleted) as the context user for this operation.
			String adminName = SZoneConfig.getAdminUserName(contextZoneName);
			
			String screenName = req.getParameter(CrossContextConstants.SCREEN_NAME);
			
			boolean closeSession = false;
			if (!SessionUtil.sessionActive()) {
				SessionUtil.sessionStartup();	
				closeSession = true;
			}
			try {
				User user = getProfileDao().findUserByName(screenName, contextZoneName);
				
				RequestContext oldCtx = RequestContextHolder.getRequestContext();
				
				try {
					RequestContextUtil.setThreadContext(user);
					
					boolean deleteWS = 
						SPropsUtil.getBoolean(PORTAL_PROFILE_DELETE_USER_WORKSPACE, 
							PORTAL_PROFILE_DELETE_USER_WORKSPACE_DEFAULT_VALUE);
	
					getProfileModule().deleteEntry(user.getParentBinder().getId(), user.getId(), deleteWS);
				}
				finally {
					RequestContextHolder.setRequestContext(oldCtx); // Restore old context	
				}
			} catch (NoUserByTheNameException e) {
				// The user doesn't exist on the Teaming side.
				// This is possible, so don't throw an error.
				logger.warn(e.toString());
				return;
			} catch (WriteFilesException e) {
				logger.error(e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}
			finally {
				if (closeSession) 
					SessionUtil.sessionStop();
			}
		}
		else {
			logger.error("Unrecognized operation [" + operation + "]");
		}
	}
	
	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
	private ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}
	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
}
