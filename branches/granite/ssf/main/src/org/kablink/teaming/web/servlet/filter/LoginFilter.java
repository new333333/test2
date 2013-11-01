/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.servlet.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portal.PortalLogin;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Http;
import org.kablink.util.Validator;

public class LoginFilter  implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Clear request context for the thread.
		RequestContextHolder.clear();
		
		try {
			if(isAtRoot(req) && req.getMethod().equalsIgnoreCase("get")) {
				HttpSession session = WebHelper.getRequiredSession(req);
				boolean mobileFullUI = false;
				if (session != null) {
					Boolean mfu = (Boolean) session.getAttribute(WebKeys.MOBILE_FULL_UI);
					if (mfu != null && mfu) {
						mobileFullUI = true;
					}
				}
				
				// We're at the root URL. Re-direct the client to its workspace.
				// Do this only if the request method is GET.
				String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
				String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
				Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
				if (BrowserSniffer.is_mobile(req, userAgents) && !mobileFullUI && 
						!BrowserSniffer.is_tablet(req, tabletUserAgents, testForAndroid)) {
					String landingPageUrl = getWapLandingPageURL(req);
					res.sendRedirect(landingPageUrl);
				} else {
					// Not a mobile device, or a mobile device in full UI mode
					String workspaceUrl = getWorkspaceURL(req);
					res.sendRedirect(workspaceUrl);
				}
			}
			else {
				// Do we need to convert the url to a permalink?
				if ( shouldUrlBeConvertedToAPermalink( req ) )
				{
					String permalinkUrl;
					
					// Yes
					// Create a permalink from the given url.
					permalinkUrl = convertUrlToPermalink( req );
					
					// Redirect to the permalink.
					if ( permalinkUrl != null )
					{
						res.sendRedirect( permalinkUrl );
						return;
					}
				}

				if(WebHelper.isGuestLoggedIn(req)) {
					// User is logged in as guest, which simply means that the user
					// is currently accessing Teaming without logging in as a regular 
					// user (yet).
					handleGuestAccess(req, res, chain);
				}
				else {
					// User is logged in as regular user. Proceed as normal.
					String url = req.getQueryString();
					String redirectUrl = url;
					if (url != null) { 
						redirectUrl = redirectUrl.replace(WebKeys.URL_USER_ID_PLACE_HOLDER, WebHelper.getRequiredUserId(req).toString());
						if (!redirectUrl.equals(url)) {
							res.sendRedirect(req.getRequestURI() + "?" + redirectUrl);
							return;
						}
					}
					req.setAttribute("referer", url);
					chain.doFilter(request, response);
					
				}
			}
		} catch(Exception e) {
			res.sendRedirect(getErrorUrl(req, e.getLocalizedMessage()));
		}
	}
	
	
	/**
	 * Convert the given url to a permalink.
	 */
	@SuppressWarnings("unchecked")
	private String convertUrlToPermalink( HttpServletRequest req )
	{
		String url;
		String paramValue;
		String action;
		boolean pAction;
		AdaptedPortletURL adaptedPortletUrl;
		
		action = req.getParameter( WebKeys.ACTION );
		
		pAction = false;
		paramValue = req.getParameter( "p_action" );
		if ( paramValue != null && paramValue.equalsIgnoreCase( "1" ) )
			pAction = true;

		adaptedPortletUrl = new AdaptedPortletURL( req, "ss_forum", pAction );
		adaptedPortletUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK );
	
		// Add the parameters from the original url to the permalink url.
		{
			Enumeration paramNames;
			
			paramNames = req.getParameterNames();
			if ( paramNames != null && paramNames.hasMoreElements() )
			{
				while ( paramNames.hasMoreElements() )
				{
					String paramName;
					
					// Get the next parameter name.
					paramName = (String) paramNames.nextElement();
					
					// Is the parameter named "action" or "p_name" or "p_action"?
					if ( paramName != null && paramName.equalsIgnoreCase( "action" ) == false &&
						 paramName.equalsIgnoreCase( "p_name" ) == false &&
						 paramName.equalsIgnoreCase( "p_action" ) == false )
					{
						String value;
						
						// No
						// Get the parameter value.
						value = (String) req.getParameter( paramName );
						
						// Add the parameter to the new url
						adaptedPortletUrl.setParameter( paramName, value );
					}
				}
			}
		}
		
		// Add the "entityType" parameter with the appropriate value.
		if ( action.equalsIgnoreCase( WebKeys.ACTION_VIEW_WS_LISTING ) )
		{
			adaptedPortletUrl.setParameter( WebKeys.URL_ENTITY_TYPE, "workspace" );
		}
		else if ( action.equalsIgnoreCase( WebKeys.ACTION_VIEW_FOLDER_LISTING ) )
		{
			adaptedPortletUrl.setParameter( WebKeys.URL_ENTITY_TYPE, "folder" );
		}
		else if ( action.equalsIgnoreCase( WebKeys.ACTION_VIEW_PROFILE_LISTING ) )
		{
			adaptedPortletUrl.setParameter( WebKeys.URL_ENTITY_TYPE, "profiles" );
		}
		else if ( action.equalsIgnoreCase( WebKeys.ACTION_VIEW_FOLDER_ENTRY ) )
		{
			adaptedPortletUrl.setParameter( WebKeys.URL_ENTITY_TYPE, "folderEntry" );
		}
		else if ( action.equalsIgnoreCase( WebKeys.ACTION_VIEW_PROFILE_ENTRY ) )
		{
		}
		
		url = adaptedPortletUrl.toString();
		return url;
	}
	
	public void destroy() {
	}

	protected void handleGuestAccess(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		if(isPathPermittedUnauthenticated(req.getPathInfo()) || isActionPermittedUnauthenticated(req.getParameter("action"))) {
			String currentURL = Http.getCompleteURL(req);

			if ( currentURL.contains( "action=__login" ) )
			{
				String workspaceUrl;

				// Redirect to the workspace url.  That way if the user cancels out of the
				// log in dialog, there will be something displayed.
				workspaceUrl = getWorkspaceURL( req );
				req.setAttribute( WebKeys.REFERER_URL, workspaceUrl );
				res.sendRedirect( workspaceUrl );
			}
			else
				chain.doFilter(req, res);										
		}
		else {				
			String currentURL = Http.getCompleteURL(req);
			if(currentURL.contains("p_name=ss_mobile")) {
				// Mobile interaction. Let it proceed as normal.
				req.setAttribute(WebKeys.REFERER_URL, currentURL);
				chain.doFilter(req, res);					
			}
			else if(currentURL.contains("action=__login") || 
					(currentURL.contains("action=__ajax_mobile") && 
							currentURL.contains("operation=mobile_login"))) {
				// Request for login form. Let it proceed as normal.
				String refererURL = req.getParameter("refererUrl");
				if(Validator.isNotNull(refererURL))
					req.setAttribute(WebKeys.REFERER_URL, refererURL);
				chain.doFilter(req, res);										
			}
/*			// Dead code.
			else if(1 == 0 && (BrowserSniffer.is_wap_xhtml(req) || 
					BrowserSniffer.is_blackberry(req) || 
					BrowserSniffer.is_iphone(req))) {
				// Mobile interaction. 
				// Guest access not allowed. Redirect the guest to the login page.
				res.sendRedirect(getMobileLoginURL(req, currentURL));
			}
*/
			else {
				// The guest is requesting a non-mobile page that isn't the login form.
				// We need to check whether we should allow this or not.
				if(guestAccessAllowed()) { 
					// Guest access allowed. Let it proceed as normal.
					req.setAttribute(WebKeys.REFERER_URL, currentURL);
					chain.doFilter(req, res);											
				}
				else {
					// Guest access not allowed. Redirect the guest to the login page.
					res.sendRedirect(getLoginURL(req, currentURL));
				}
			}
		}		
	}

	protected boolean guestAccessAllowed() {
		if(ReleaseInfo.isLicenseRequiredEdition()) {
			if(LicenseChecker.isAuthorizedByLicense("com.novell.teaming.GuestAccess")) {
				return authenticationConfigAllowsGuestAccess();
			}
			else {
				return false;
			}
		}
		else {
			return authenticationConfigAllowsGuestAccess();
		}
	}
	
	protected String getLoginURL(HttpServletRequest req, String currentURL) {
		// Should we disallow this if form.login.auth.disallowed is true?
		try {
			return req.getContextPath() + 
				SPropsUtil.getString("form.login.url", "/a/do?p_name=ss_forum&p_action=1&action=__login") +
				"&refererUrl=" + 
				URLEncoder.encode(currentURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	protected String getMobileLoginURL(HttpServletRequest req, String currentURL) {
		// Should we disallow this if form.login.auth.disallowed is true?
		try {
			return req.getContextPath() + 
				"/a/do?p_name=ss_mobile&p_action=1&action=__ajax_mobile&operation=mobile_login" +
				"&refererUrl=" + 
				URLEncoder.encode(currentURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	protected String getWorkspaceURL(final HttpServletRequest req) {
		String reply = getWorkspaceURLImpl(req);
		if (MiscUtil.hasString(reply)) {
			if (0 < reply.indexOf("/do?"))
			     reply += ("?" + WebKeys.URL_VIBEONPREM_ROOT_FLAG + "=1");
			else reply += ("/" + WebKeys.URL_VIBEONPREM_ROOT_FLAG + "/1");
		}
		return reply;
	}
	
	private String getWorkspaceURLImpl(final HttpServletRequest req) {
		final String userId;
		Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
		HomePageConfig homePageConfig = getZoneModule().getZoneConfig(zoneId).getHomePageConfig();
		if (WebHelper.isGuestLoggedIn(req) && homePageConfig != null) {
			// Do we have a guest default home page?
			Long binderId = homePageConfig.getDefaultGuestHomePageId();
			if ( binderId != null )
			{
				// Yes
				// Is guest access turned on?
				if ( guestAccessAllowed() == true )
				{
					// Yes, does guest have access to the guest default landing page?
					try
					{
						String url = null;
						RunasCallback callback;
						final Long defaultBinderId;
						
						defaultBinderId = binderId;
						callback = new RunasCallback()
						{
							public Object doAs()
							{
								@SuppressWarnings("unused")
								Binder binder;
								
								binder = getBinderModule().getBinder( defaultBinderId );
								
								// If we get here, guest as access to the default home page.
								return PermaLinkUtil.getPermalink( req, defaultBinderId, EntityType.folder);
							}
						};
						url = (String) RunasTemplate.runas(
														callback,
														WebHelper.getRequiredZoneName( req ),
														WebHelper.getRequiredUserId( req ) );
						
						// If we get here guest has access to the guest default home page.
						if ( url != null )
							return url;
					}
					catch (Exception ex)
					{
						// Nothing to do
					}
				}
				
				// If we get here, guest does not have access to the guest default home page.
			}

			// Do we have a default home page for the logged in user?
			binderId = homePageConfig.getDefaultHomePageId();
			if ( binderId != null ) 
			{
				// Yes
				return PermaLinkUtil.getPermalink( req, binderId, EntityType.folder );
			}
		} else if (!WebHelper.isGuestLoggedIn(req)) {
			//This user is logged in. Look for a default home page
			try {
				String url = (String)RunasTemplate.runas(new RunasCallback() {
					public Object doAs() {
						//See if this binder exists and is accessible. 
						//  If not, go to the user workspace page instead
						UserProperties userProperties = getProfileModule().getUserProperties(null);
						Long userHomePageId = (Long)userProperties.getProperty(ObjectKeys.USER_PROPERTY_DEFAULT_HOME_PAGE);
						if (userHomePageId != null) {
							//The user has defined a home page. See if it is accessible
							@SuppressWarnings("unused")
							Binder binder = getBinderModule().getBinder(userHomePageId);
							return PermaLinkUtil.getPermalink(req, userHomePageId, EntityType.folder);
						}
						return null;
					}
				}, WebHelper.getRequiredZoneName(req), WebHelper.getRequiredUserId(req));
				if (url != null) return url;
			} catch(Exception e) {}
			if (homePageConfig != null && homePageConfig.getDefaultHomePageId() != null) {
				//The admin has defined a default home page. See if it is accessible
				final Long binderId = homePageConfig.getDefaultHomePageId();
				try {
					return (String) RunasTemplate.runas(new RunasCallback() {
						public Object doAs() {
							//See if this binder exists and is accessible. 
							//  If not, go to the user workspace page instead
							@SuppressWarnings("unused")
							Binder binder = getBinderModule().getBinder(binderId);
							return PermaLinkUtil.getPermalink( req, binderId, EntityType.folder);
						}
					}, WebHelper.getRequiredZoneName(req), WebHelper.getRequiredUserId(req));									
				} catch(Exception e) {}
			}
		}
		
		if (WebHelper.isGuestLoggedIn(req)) {
			userId = WebKeys.URL_USER_ID_PLACE_HOLDER;
		} else {
			userId = WebHelper.getRequiredUserId(req).toString();
		}
		
		return (String) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return PermaLinkUtil.getUserPermalink(req, userId, GwtUIHelper.isActivityStreamOnLogin());
			}
		}, WebHelper.getRequiredZoneName(req));									
	}
	
	protected String getWapLandingPageURL(final HttpServletRequest req) {
		final String userId;
		if(WebHelper.isGuestLoggedIn(req))
			userId = WebKeys.URL_USER_ID_PLACE_HOLDER;
		else
			userId = WebHelper.getRequiredUserId(req).toString();
		
		return (String) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return WebUrlUtil.getWapLandingPage(req, userId);
			}
		}, WebHelper.getRequiredZoneName(req));									
	}
	
	protected boolean isAtRoot(HttpServletRequest req) {
		String path = req.getPathInfo();
		if(path == null || path.equals("/"))
			return true;
		else
			return false;
	}
	
	protected boolean isPathPermittedUnauthenticated(String path) {
		return (path != null && 
				(path.equals("/"+WebKeys.SERVLET_PORTAL_LOGIN) || 
						path.equals("/"+WebKeys.SERVLET_PORTAL_LOGOUT) || 
						path.startsWith("/"+WebKeys.SERVLET_READ_FILE+"/") || 
						path.startsWith("/"+WebKeys.SERVLET_VIEW_CSS+"/") ||
						path.equals("/"+WebKeys.SERVLET_VIEW_CSS)));
	}
	
	protected boolean isActionPermittedUnauthenticated(String actionValue) {
		return (actionValue != null && 
				(actionValue.startsWith("__") || 
						actionValue.equals(WebKeys.ACTION_VIEW_PERMALINK)));
	}
	
	@SuppressWarnings("unused")
	private PortalLogin getPortalLogin() {
		return (PortalLogin) SpringContextUtil.getBean("portalLoginBean");
	}
	
	private AuthenticationModule getAuthenticationModule() {
		return (AuthenticationModule) SpringContextUtil.getBean("authenticationModule");
	}	

	private ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}	

	@SuppressWarnings("unused")
	private AdminModule getAdminModule() {
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}	

	private BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}	

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}	

	private boolean authenticationConfigAllowsGuestAccess() {
		Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
		AuthenticationConfig config = getAuthenticationModule().getAuthenticationConfigForZone(zoneId);
		return config.isAllowAnonymousAccess();
	}
	
	public static String getErrorUrl(HttpServletRequest request, String errorMsg) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true, true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ERROR_MESSAGE);
		adapterUrl.setParameter(WebKeys.URL_VALUE, errorMsg);
		return adapterUrl.toString();
	}

	/**
	 * Look at the url in the request and determine if it should be converted to a permalink.
	 * The url needs to be converted to a permalink if the action parameter equals
	 * "view_ws_listing" or "view_folder_listing" or "view_profile_listing" or "view_folder_entry" or "view_profile_entry"
	 * and the url does NOT have the parameter "vibeonprem"
	 */
	private boolean shouldUrlBeConvertedToAPermalink( HttpServletRequest req )
	{
		String action;
		String param;
		
		// Does the url have the "vibeonprem" parameter.
		param = req.getParameter( WebKeys.URL_VIBEONPREM_URL_FLAG );
		if ( param != null && param.length() > 0 )
		{
			// Yes, no need to convert it.
			return false;
		}
		
		action = req.getParameter( "action" );
		if ( action != null &&
			 (action.equalsIgnoreCase( WebKeys.ACTION_VIEW_WS_LISTING ) ||
			  action.equalsIgnoreCase( WebKeys.ACTION_VIEW_FOLDER_LISTING ) ||
			  action.equalsIgnoreCase( WebKeys.ACTION_VIEW_PROFILE_LISTING ) ||
			  action.equalsIgnoreCase( WebKeys.ACTION_VIEW_FOLDER_ENTRY ) ||
			  action.equalsIgnoreCase( WebKeys.ACTION_VIEW_PROFILE_ENTRY )) )
		{
			return true;
		}
		
		return false;
	}
}
