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
package com.sitescape.team.servlet.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.User;
import com.sitescape.team.portal.PortalLogin;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.team.web.util.WebHelper;

public class PortalLoginController extends SAbstractController {

	private static final String PORTAL_LOGIN_SUPPORTED_METHOD = "portal.login.supported.method";
	private static final String PORTAL_LOGIN_FORCENEW_ALLOWED = "portal.login.forcenew.allowed";
	
	private PortalLogin portalLogin;

	protected PortalLogin getPortalLogin() {
		return portalLogin;
	}

	public void setPortalLogin(PortalLogin portalLogin) {
		this.portalLogin = portalLogin;
	}

	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		Map<String,Object> model = new HashMap();
		String view = WebKeys.VIEW_LOGIN_RETURN;
		
		if(("/" + WebKeys.SERVLET_PORTAL_LOGIN).equalsIgnoreCase(request.getPathInfo())) { // login request
			if(!isInteractiveLoginAllowed(request)) {
				// Interactive portal login through the Teaming is not allowed. 
				model.put(WebKeys.LOGIN_ERROR, WebKeys.LOGIN_ERROR_LOGINS_NOT_ALLOWED);
				return new ModelAndView(view, model);
			}
			
			String username = RequestUtils.getStringParameter(request, "j_username", "");
			String password = RequestUtils.getStringParameter(request, "j_password", "");			
			String remember = RequestUtils.getStringParameter(request, "remember");
			String url = RequestUtils.getStringParameter(request, "spring-security-redirect", "");			
			boolean forceNew = RequestUtils.getBooleanParameter(request, "_forcenew", false);
			model.put(WebKeys.URL, url);

			if(!(forceNew && SPropsUtil.getBoolean(PORTAL_LOGIN_FORCENEW_ALLOWED, false))) {
				if(WebHelper.isUserLoggedIn(request) && username.equalsIgnoreCase(WebHelper.getRequiredUserName(request))) {
					// This code is executing in the context of a session that belogs to a
					// user with the same name as the specified username (case insensitively).
					// This doesn't necessarily mean that the specified password is valid.
					// But regardless, this code will not attempt to obtain a new session
					// in this case. Set _forcenew parameter to true to force creation of 
					// a new session.
					logger.info("The user " + username + " is already logged in.");
					model.put(WebKeys.LOGIN_ERROR, WebKeys.LOGIN_ERROR_USER_ALREADY_LOGGED_IN);
					return new ModelAndView(view, model);
				}
			}
			
			try {
				getPortalLogin().loginPortal(request, response, username, password,
						(remember != null && (remember.equalsIgnoreCase("on") || remember.equalsIgnoreCase("true")) ? true : false));
			}
			catch(Exception e) {
				model.put(WebKeys.LOGIN_ERROR, WebKeys.LOGIN_ERROR_LOGIN_FAILED);
				view = WebKeys.VIEW_LOGIN_RETRY;
				//slow this return down to throttle password guessers a little
				Thread.sleep(1000);
				return new ModelAndView(view, model);
			}
			//Get the user object for the newly logged in user
			User user = getProfileModule().findUserByName(username);
			model.put(WebKeys.USER_PRINCIPAL, user);
			String redirectUrl;
			//If there was a url passed in (e.g., from a permalink), use it
			if (!url.equals("")) { 
				redirectUrl = url;
				redirectUrl = redirectUrl.replace(WebKeys.URL_USER_ID_PLACE_HOLDER, user.getId().toString()); 
			}
			else {
				if(request.getQueryString() != null)
					redirectUrl = request.getRequestURL().append("?").append(request.getQueryString()).toString();
				else
					redirectUrl = request.getRequestURL().toString();			
			}
			response.sendRedirect(redirectUrl);
			return null;
		}
		else { // logout request
			Long userId = null;
			try {
				//Get the current user id before logging out
				userId = WebHelper.getRequiredUserId(request);
			} catch(Exception e) {}
			
			getPortalLogin().logoutPortal(request, response);

			view = WebKeys.VIEW_LOGOUT_RETURN;
			String url = RequestUtils.getStringParameter(request, "spring-security-redirect", "");
			if (url.equals("")) {
				if (userId != null) {
					url = PermaLinkUtil.getWorkspaceURL(request, userId.toString());
				}
			}
			model.put(WebKeys.URL, url);
		}
		
		return new ModelAndView(view, model);
	}
	
	protected boolean isInteractiveLoginAllowed(HttpServletRequest request) {
		String allowedMethod = SPropsUtil.getString(PORTAL_LOGIN_SUPPORTED_METHOD, "post");
		if(allowedMethod.equalsIgnoreCase("post")) {
			if("post".equalsIgnoreCase(request.getMethod()))
				return true;
			else
				return false;
		}
		else if(allowedMethod.equalsIgnoreCase("all")) {
			return true;
		}
		else if(allowedMethod.equalsIgnoreCase("none")) {
			return false;
		}
		else {
			logger.warn("Illegal value " + allowedMethod + " for " + PORTAL_LOGIN_SUPPORTED_METHOD + " property");
			return false;
		}		
	}
	
}
