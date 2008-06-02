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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.User;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;

public abstract class PortalLoginController extends SAbstractController {

	private static final String PORTAL_LOGIN_OVERRIDE_SCHEME = "portal.login.override.scheme";
	private static final String PORTAL_LOGIN_OVERRIDE_HOST = "portal.login.override.host";
	private static final String PORTAL_LOGIN_OVERRIDE_PORT = "portal.login.override.port";
	private static final String PORTAL_LOGIN_SUPPORTED_METHOD = "portal.login.supported.method";
	private static final String PORTAL_LOGIN_FORCENEW_ALLOWED = "portal.login.forcenew.allowed";
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		javax.servlet.http.Cookie[] clientCookies = request.getCookies();
		
		org.apache.commons.httpclient.Cookie[] portalCookies = null;
		Map<String,Object> model = new HashMap();
		String view = WebKeys.VIEW_LOGIN_RETURN;
		
		if(("/" + WebKeys.SERVLET_PORTAL_LOGIN).equalsIgnoreCase(request.getPathInfo())) { // login request
			if(!isLoginAllowed(request)) {
				// Portal login is not allowed. Return silently, rather than throwing an exception.
				model.put(WebKeys.LOGIN_ERROR, WebKeys.LOGIN_ERROR_LOGINS_NOT_ALLOWED);
				return new ModelAndView(view, model);
			}
			
			String username = RequestUtils.getStringParameter(request, "username", "");
			String password = RequestUtils.getStringParameter(request, "password", "");			
			String url = RequestUtils.getStringParameter(request, "url", "");			
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
			
			HttpClient httpClient = getPortalHttpClient(request);
			if(clientCookies != null)
				copyClientCookies(request, clientCookies, httpClient);
			
			try {
				portalCookies = logIntoPortal(request, response, httpClient, username, password);
			}
			catch(Exception e) {
				model.put(WebKeys.LOGIN_ERROR, WebKeys.LOGIN_ERROR_LOGIN_FAILED);
				view = WebKeys.VIEW_LOGIN_RETRY;
				//slow this return down to throttle password guessers a little
				Thread.sleep(1000);
				return new ModelAndView(view, model);
			}
		}
		else { // logout request
			Long userId = null;
			try {
				//Get the current user id before logging out
				userId = WebHelper.getRequiredUserId(request);
			} catch(Exception e) {}
			
			HttpClient httpClient = getPortalHttpClient(request);
			if(clientCookies != null)
				copyClientCookies(request, clientCookies, httpClient);
			
			portalCookies = logOutFromPortal(request, response, httpClient);
			view = WebKeys.VIEW_LOGOUT_RETURN;
			String url = RequestUtils.getStringParameter(request, "url", "");
			if (url.equals("")) {
				if (userId != null) {
					Long profileBinderId = getProfileModule().getProfileBinder().getId();
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, profileBinderId.toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId.toString());
					url = adapterUrl.toString();
				}
			}
			model.put(WebKeys.URL, url);
		}
		
		copyPortalCookies(clientCookies, portalCookies, response);
		
		return new ModelAndView(view, model);
	}
	
	protected void copyClientCookies(HttpServletRequest request, javax.servlet.http.Cookie[] cookies, HttpClient httpClient) {
		javax.servlet.http.Cookie sourceCookie;
		org.apache.commons.httpclient.Cookie targetCookie;
		for(int i = 0; i < cookies.length; i++) {
			sourceCookie = cookies[i];
			// Usually when the client sends cookies back to the server, they only send
			// the name and the value pairs. All other information associated with the 
			// cookies (eg. expiration date, domain, path, etc.) are retained and used
			// only by the client. We need to take it into account as we make copies
			// of those cookies here. 
			
			// Apache HttpClient requires domain name to be set even for inbound cookies.
			String domain = sourceCookie.getDomain();
			if(domain == null)
				domain = getPortalHostname(request);
			targetCookie = new org.apache.commons.httpclient.Cookie(domain, sourceCookie.getName(), sourceCookie.getValue());

			// This is our internal mark that this particular cookie was copied from a client cookie.
			targetCookie.setComment("_client");

			int maxAge = sourceCookie.getMaxAge();
			if(maxAge < 0) {
				targetCookie.setExpiryDate(null);
			}
			else {
				targetCookie.setExpiryDate(new Date(System.currentTimeMillis() + maxAge*1000L));
			}
			
			String path = sourceCookie.getPath();
			if(path == null)
				path = getCookiePath();
			targetCookie.setPath(path);
			
			//targetCookie.setSecure(sourceCookie.getSecure());
			//targetCookie.setVersion(sourceCookie.getVersion());
			
			httpClient.getState().addCookie(targetCookie);				
		}
	}
	
	protected abstract String getCookiePath();
	
	protected HttpClient getPortalHttpClient(HttpServletRequest request) throws URIException {
		String portalBaseUrl = getPortalBaseUrl(request);
		
		return getHttpClient(portalBaseUrl);
	}
	
	protected HttpClient getHttpClient(String portalBaseUrl) throws URIException {
		HttpURL hrl = new HttpURL(portalBaseUrl);
		HttpClient httpClient = new HttpClient();
		HostConfiguration hostConfig = httpClient.getHostConfiguration();
		hostConfig.setHost(hrl);
		return httpClient;
	}
	
	protected boolean isLoginAllowed(HttpServletRequest request) {
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
	
	protected abstract org.apache.commons.httpclient.Cookie[] logIntoPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient, String username, String password) throws Exception;
			
	protected abstract org.apache.commons.httpclient.Cookie[] logOutFromPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient) throws Exception;
			
	protected String getPortalBaseUrl(HttpServletRequest request) {
		String scheme = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_SCHEME, "");
		if(scheme.equals(""))
			scheme = "http";
		String host = getPortalHostname(request);
		String port = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_PORT, "");
		if(port.equals(""))
			port = String.valueOf(request.getServerPort());
		
		return scheme + "://" + host + ":" + port;
	}
	
	private String getPortalHostname(HttpServletRequest request) {
		String host = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_HOST, "");
		if(host.equals(""))
			host = request.getServerName();
		return host;
	}
	
	protected void copyPortalCookies(javax.servlet.http.Cookie[] clientCookies, 
			org.apache.commons.httpclient.Cookie[] portalCookies, 
			HttpServletResponse response) {
		org.apache.commons.httpclient.Cookie portalCookie;
		javax.servlet.http.Cookie targetCookie;
		
		// Step 1: Copy portal cookies into the response. This accounts for both
		// newly added and modified cookies.
		if(portalCookies != null) {
			for(int i = 0; i < portalCookies.length; i++) {
				portalCookie = portalCookies[i];
				
				if(portalCookie.getComment() != null && portalCookie.getComment().equals("_client")) {
					// What this means is that, this particular cookie was sent by the client
					// to the portal AND the portal did not modify it. Therefore we must not
					// include this cookie in the response (ie, leave it as is on the client side).
					continue;
				}
				
				targetCookie = new javax.servlet.http.Cookie(portalCookie.getName(), portalCookie.getValue());
				
				// Copy everything from the portal cookie to the target cookie, EXCEPT
				// for the domain information.
				
				targetCookie.setComment(portalCookie.getComment());
				
				// Due to the lack of method on javax.servlet.http.Cookie that takes
				// absolute expiry date, we lose some precision while translating the
				// cookie expiration attribute between the source and target cookies.
				// But that discrepency should be no more than a few seconds.
				int maxAge = -1;
				if(portalCookie.getExpiryDate() != null) {
					maxAge = (int) ((portalCookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000L);
					if(maxAge < 0)
						maxAge = 0;
				}
				targetCookie.setMaxAge(maxAge);
				
				targetCookie.setPath(portalCookie.getPath());
				
				targetCookie.setSecure(portalCookie.getSecure());
				
				targetCookie.setVersion(portalCookie.getVersion());
				
				response.addCookie(targetCookie);				
			}
		}
		
		// Step 2: For each client cookie, check if it still exists in the list of portal
		// cookies. If not, it means that the particular cookie was destroyed as result
		// of the interaction with the portal, and we need to reflect that in the response.
		if(clientCookies != null) {
			javax.servlet.http.Cookie clientCookie;
			outer:
			for(int i = 0; i < clientCookies.length; i++) {
				clientCookie = clientCookies[i];
				// check if the client cookie is still found in the portal cookies.
				if(portalCookies != null) {
					for(int j = 0; j < portalCookies.length; j++) {
						if(clientCookie.getName().equalsIgnoreCase(portalCookies[j].getName()))
							continue outer; // found it
					}
				}
				// no match
				targetCookie = new javax.servlet.http.Cookie(clientCookie.getName(), "");
				targetCookie.setMaxAge(0);
				targetCookie.setPath(getCookiePath());
				response.addCookie(targetCookie);
			}
		}
	}
}
