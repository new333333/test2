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
package org.kablink.teaming.portal.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
import org.kablink.teaming.asmodule.bridge.BridgeUtil;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.portal.CrossContextConstants;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebHelper;


public class DispatchServer extends GenericServlet {

	protected static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static final String PORTAL_CC_DISPATCHER = "portalCCDispatcher";
	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	private ProfileDao profileDao;
	private AccessTokenManager accessTokenManager;
	
	public void init(ServletConfig config) throws ServletException {
		RequestDispatcher rd = config.getServletContext().getNamedDispatcher(PORTAL_CC_DISPATCHER);
		BridgeUtil.setCCDispatcher(rd);
		String cxt = config.getInitParameter("ssfContextPath");
		if(cxt == null || cxt.length() == 0)
			cxt = SSF_CONTEXT_PATH_DEFAULT;
		BridgeUtil.setSSFContextPath(cxt);
		try {
			BridgeUtil.setClassLoader(Thread.currentThread().getContextClassLoader());
		}
		catch(Exception e) {
			new ServletException(e);
		}
		profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
		accessTokenManager = (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		// This method is invoked only once for each regular user after successful
		// authentication. However, unfortunately, the same is called for every
		// access through the guest account. This is because there is no "explicit"
		// login event for anonymous access. So, any modification to this method
		// should be done carefully with the understanding.
		String operation = req.getParameter(CrossContextConstants.OPERATION);

		if(operation.equals(CrossContextConstants.OPERATION_SETUP_SESSION)) {
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(zoneName != null) {
				if(!(zoneName.equals(SZoneConfig.getDefaultZoneName()) ||
						LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.zone.MultiZone")))
					return; // don't allow it; simply return					
			}
			else {
				zoneName = SZoneConfig.getDefaultZoneName();
			}
					
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			if(userName == null)
				userName = SZoneConfig.getGuestUserName(zoneName);
			
			final User user = profileDao.findUserByName(userName, zoneName);
			
			final HttpSession ses = ((HttpServletRequest) req).getSession();
			
			// need the following piece of data to detect the situation where Liferay  
			// upgrades a session from guest to regular user
			Long oldUserId = (Long) ses.getAttribute(WebKeys.USER_ID);

			WebHelper.putContext(ses, user);

			final String infoId = (String) ses.getAttribute(WebKeys.TOKEN_INFO_ID);
			if(infoId == null) { 
				if(!user.isShared() || 
						SPropsUtil.getBoolean("remoteapp.interactive.token.support.guest", true)) { // create a new info object
					// Make sure to run it in the user's context.			
					RunasTemplate.runas(new RunasCallback() {
						public Object doAs() {
							String infoId = accessTokenManager.createTokenInfoSession(user.getId(), ses.getId());
							ses.setAttribute(WebKeys.TOKEN_INFO_ID, infoId);
							return null;
						}
					}, user);						
				}
			}
			else if (!user.getId().equals(oldUserId)) {
				// The portal is re-using the same session while changing the owner(user). 
				if(!user.isShared() || 
						SPropsUtil.getBoolean("remoteapp.interactive.token.support.guest", true)) { // create a new info object
					RunasTemplate.runas(new RunasCallback() {
						public Object doAs() {
							accessTokenManager.updateTokenInfoSession(infoId, user.getId(), ses.getId());
							return null;
						}
					}, user);						
				}
				else {
					// The current user is guest and the configuration doesn't allow guest to use interactive tokens.
					// Run this in the old user's context.			
					RunasTemplate.runas(new RunasCallback() {
						public Object doAs() {
							accessTokenManager.destroyTokenInfoSession(infoId);
							return null;
						}
					}, user.getZoneId(), oldUserId);									
				}
			}
		}
		else {
			logger.error("Unrecognized operation [" + operation + "]");
		}
	}

}
