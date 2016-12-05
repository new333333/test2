/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.extuser.ExternalUserRespondingToInvitationException;
import org.kablink.teaming.extuser.ExternalUserRespondingToPwdResetException;
import org.kablink.teaming.extuser.ExternalUserRespondingToPwdResetVerificationException;
import org.kablink.teaming.extuser.ExternalUserRespondingToVerificationException;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portal.PortalLogin;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.LandingPageHelper;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Http;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @author ?
 */
public class LoginFilter  implements Filter {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Clear request context for the thread.
		RequestContextHolder.clear();
		
		try {
			if(isAtRoot(req) && req.getMethod().equalsIgnoreCase("get")) {
				if (WebHelper.isMobileUI(req)) {
					String landingPageUrl = getWapLandingPageURL(req);
					sendRedirect(res,landingPageUrl);
				} else {
					// Not a mobile device, or a mobile device in full UI mode
					String workspaceUrl = getWorkspaceURL(req);
					sendRedirect(res,workspaceUrl);
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
						sendRedirect(res, permalinkUrl );
						return;
					}
				}

				if(req.getQueryString() != null && req.getQueryString().contains(ExternalUserUtil.QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN + "=")) {
					// If we don't have a request context for this
					// thread...
					boolean hasRC = (null != RequestContextHolder.getRequestContext());
					if (!hasRC) {
						// ...create one so the URL construction below
						// ...works correctly.
						RequestContextUtil.setThreadContext(req);
					}
					try {
						// This might be a response from external user
						// to an invitation.  Should check and deal
						// with it if so.
						ExternalUserUtil.handleResponseToInvitationOrConfirmation(
							WebHelper.getRequiredSession(                req),
							WebUrlUtil.getCompleteURLFromRequestForEmail(req));
					}
					finally {
						// If we created a request context specifically
						// for this thread...
						if (!hasRC) {
							// ...clear it out so things revert to
							// ...where they stood when we started.
							RequestContextHolder.clear();
						}
					}
					
					// This might be a response from external user to
					// reset their password or verify that they reset
					// their password.
					String completeUrl = Http.getCompleteURL( req );
					completeUrl = StringCheckUtil.checkForQuotes(completeUrl, false);		//Prevent XSS attacks
					ExternalUserUtil.handleResponseToPwdReset(
															WebHelper.getRequiredSession( req ),
															completeUrl );
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
							sendRedirect(res,req.getRequestURI() + "?" + redirectUrl);
							return;
						}
					}
					req.setAttribute("referer", url);
					chain.doFilter(request, response);
				}
			}
		}
		catch(ExternalUserRespondingToInvitationException e) {
			// This is NOT an error. Just re-throw it.
			throw e;
		}
		catch(ExternalUserRespondingToVerificationException e) {
			// This is NOT an error. Just re-throw it.
			throw e;
		}
		catch( ExternalUserRespondingToPwdResetException e )
		{
			// This is NOT an error. Just re-throw it.
			throw e;
		}
		catch( ExternalUserRespondingToPwdResetVerificationException e )
		{
			// This is NOT an error. Just re-throw it.
			throw e;
		}
		catch(Exception e) {
			sendRedirect(res,getErrorUrl(req, MiscUtil.exToString(e)));
		}
	}
	
    private void sendRedirect(HttpServletResponse response, String location) throws IOException {
    	try {
    		response.sendRedirect(location);
    	}
    	catch(IllegalStateException e) {
    		logger.warn("Error sending redirect to '" + location + "'", e);
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
	
	@Override
	public void destroy() {
	}

	protected void handleGuestAccess(final HttpServletRequest req, final HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		Boolean readFileWithGuestAccessFlag = getReadFileWithGuestAccessFlag(req);
		boolean isReadFile                  = (null != readFileWithGuestAccessFlag);
		boolean isReadFileWithGuestAccess   = (isReadFile && readFileWithGuestAccessFlag);
		
		if (isReadFileWithGuestAccess || isPathPermittedUnauthenticated(req.getPathInfo()) || isActionPermittedUnauthenticated(req)) {
			String action;
			
			action = req.getParameter( "action" );
			
			// Is this a view permalink request?
			if ( action != null && action.equalsIgnoreCase( WebKeys.ACTION_VIEW_PERMALINK ) )
			{
				String currentURL;
				
				// Yes
				// Set the referrer url so we know where to go after the user logs in.
				currentURL = Http.getCompleteURL( req );
				currentURL = StringCheckUtil.checkForQuotes( currentURL, false );		//Prevent XSS attacks
				if ( Validator.isNotNull( currentURL ) )
					req.setAttribute( WebKeys.REFERER_URL, currentURL);
			}
			
			chain.doFilter(req, res);										
		}
		else {				
			String currentURL = Http.getCompleteURL(req);
			currentURL = StringCheckUtil.checkForQuotes(currentURL, false);		//Prevent XSS attacks
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
				if(Validator.isNotNull(refererURL)) {
					refererURL = StringCheckUtil.checkForQuotes(refererURL, false);		//Prevent XSS attacks
					req.setAttribute(WebKeys.REFERER_URL, refererURL);
				}
				chain.doFilter(req, res);										
			}
			else {
				// The guest is requesting a non-mobile page that isn't the login form.
				// We need to check whether we should allow this or not.
				if ((!isReadFile) && isValidGuestUrl(req, currentURL)) {
					// Guest is allowed to access this URL. Let it proceed as normal.
					req.setAttribute(WebKeys.REFERER_URL, currentURL);
					chain.doFilter(req, res);											
				}
				else if ((!isReadFile) && guestAccessAllowed()) {
					// Guest access allow, just not to that URL.
					// Send them to their personal workspace.
					currentURL = getUserPermalinkFromId(req, WebKeys.URL_USER_ID_PLACE_HOLDER);
					sendRedirect(res,currentURL);
				}
				else {
					// It's a readFile URL to an entry that the Guest
					// can't access or Guest access not allowed.
					// Redirect the Guest to the login page.
					sendRedirect(res,getLoginURL(req, currentURL));
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
			     reply += ("?" + WebKeys.URL_NOVL_ROOT_FLAG + "=1");
			else reply += ("/" + WebKeys.URL_NOVL_ROOT_FLAG + "/1");
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
							@Override
							public Object doAs()
							{
								@SuppressWarnings("unused")
								Binder binder;
								
								binder = getBinderModule().getBinder( defaultBinderId );
								
								// If we get here, guest as access to the default home page.
								return LandingPageHelper.getLandingPageUrlFromId( req, defaultBinderId );
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
				
				// If we get here, guest does not have access to the default home page.
			}

			// Do we have a default home page for the logged in user?
			binderId = homePageConfig.getDefaultHomePageId();
			if ( binderId != null ) 
			{
				// Yes
				return LandingPageHelper.getLandingPageUrlFromId( req, binderId );
			}
		} else if (!WebHelper.isGuestLoggedIn(req)) {
			//This user is logged in. Look for a default home page
			try {
				String url = (String)RunasTemplate.runas(new RunasCallback() {
					@Override
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
						@Override
						public Object doAs() {
							//See if this binder exists and is accessible. 
							//  If not, go to the user workspace page instead
							@SuppressWarnings("unused")
							Binder binder = getBinderModule().getBinder(binderId);
							return LandingPageHelper.getLandingPageUrlFromId( req, binderId );
						}
					}, WebHelper.getRequiredZoneName(req), WebHelper.getRequiredUserId(req));									
				} catch(Exception e) {}
			}
		}
		
		if (WebHelper.isGuestLoggedIn(req))
		     userId = WebKeys.URL_USER_ID_PLACE_HOLDER;
		else userId = WebHelper.getRequiredUserId(req).toString();
		return getUserPermalinkFromId(req, userId);
	}
	
	protected String getWapLandingPageURL(final HttpServletRequest req) {
		final String userId;
		if(WebHelper.isGuestLoggedIn(req))
			userId = WebKeys.URL_USER_ID_PLACE_HOLDER;
		else
			userId = WebHelper.getRequiredUserId(req).toString();
		
		return (String) RunasTemplate.runasAdmin(new RunasCallback() {
			@Override
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

	/*
	 * Returns an indicator of whether the request URL is a readFile
	 * URL and if it is, whether Guest has access to the entry.
	 * 
	 * Assumption:  Called as Guest.
	 * 
	 * Returns:
	 *    null          -> The request isn't a readFile URL.
	 *    Boolean.FALSE -> The request is    a readFile URL but Guest can't access it.
	 *    Boolean.TRUE  -> The request is    a readFile URL and Guest can   access it.
	 */
	protected Boolean getReadFileWithGuestAccessFlag(HttpServletRequest req) {
		// If we don't have a path...
		String path = req.getPathInfo();
		if (null == path) {
			// ...it can't be a readFile URL.
			return null;
		}
		
		// Are we looking at a readFile URL?
		Boolean reply;
		if (path.startsWith("/" + WebKeys.SERVLET_READ_FILE + "/" + WebKeys.URL_ENTITY_TYPE_SHARE)) {
			//This is probably a share with public link. Let it go through. It will get checked by the readFile controller
			reply = true;
		} else if (path.startsWith("/" + WebKeys.SERVLET_READ_FILE + "/")) {
			// Yes!  We'll need the zoneName and userId to validate
			// things.
			String zoneName = WebHelper.getRequiredZoneName(req);
			Long   userId   = WebHelper.getRequiredUserId(  req);
			
			// Can we find a folderEntry marker within it?
			String feIdMarker = (WebKeys.URL_FOLDER_ENTRY + "/");
			int    feIdPos    = path.indexOf(feIdMarker);
			
			// Can we find a folderId marker within it?
			String fIdMarker = (WebKeys.URL_FOLDER_ID + "/");
			int    fIdPos    = path.indexOf(fIdMarker);
			
			// Can we find a folderEntryList marker within it?
			String feListIdMarker = (WebKeys.URL_FOLDER_ENTRY_LIST + "/");
			int    feListIdPos    = path.indexOf(feListIdMarker);
			
			// Can we find a folderList marker within it?
			String fListIdMarker = (WebKeys.URL_FOLDER_LIST + "/");
			int    fListIdPos    = path.indexOf(fListIdMarker);

			// Did we find a folderEntry marker?
			if (0 < feIdPos) {
				FolderEntry fe;
				try {
					// Yes!  Try to access the entry as Guest.
					final String fePart = path.substring(feIdPos + feIdMarker.length());
					final Long   feId   = Long.parseLong(fePart.substring(0, fePart.indexOf('/')));
						fe = ((FolderEntry) RunasTemplate.runas(
							new RunasCallback() {
								@Override
								public Object doAs() {
									return getFolderModule().getEntry(null, feId);
								}
							},
							zoneName,
							userId));
				}
				catch (Exception ex) {
					// Failure possibilities:  AccessControlException,
					// NumericFormatException, ...
					fe = null;
				}
					
				// Return true if Guest can access the entry and false
				// otherwise.
				reply = new Boolean(null != fe);
			}
			
			// No, we didn't find a folderEntry marker!  Did we find a
			// folderId marker?
			else if (0 < fIdPos) {
				Folder	f;
				try {
					// Yes!  Try to access the folder as Guest.
					final String fPart = path.substring(fIdPos + fIdMarker.length());
					final Long   fId   = Long.parseLong(fPart.substring(0, fPart.indexOf('/')));
						f = ((Folder) RunasTemplate.runas(
							new RunasCallback() {
								@Override
								public Object doAs() {
									return getFolderModule().getFolder(fId);
								}
							},
							zoneName,
							userId));
				}
				catch (Exception ex) {
					// Failure possibilities:  AccessControlException,
					// NumericFormatException, ...
					f = null;
				}
					
				// Return true if Guest can access the entry and false
				// otherwise.
				reply = new Boolean(null != f);
			}
			
			// No, we didn't find a folderId marker either!  Did we
			// find both a folderEntryList and folderList marker?
			else if ((0 < feListIdPos) && (0 < fListIdPos)) {
				// Yes!  Validate the folderEntryList...
				String feListPart = path.substring(feListIdPos + feListIdMarker.length());
				feListPart = feListPart.substring(0, feListPart.indexOf('/'));
				boolean feListValid = ((0 == feListPart.length()) || "-".equals(feListPart));
				if (!feListValid) {
					feListValid = true;
					String[] feList = feListPart.split(":");
					for (String feId:  feList) {
						FolderEntry fe;
						try {
							// Yes!  Try to access the entry as Guest.
							final String feIdFinal = feId;
							fe = ((FolderEntry) RunasTemplate.runas(
								new RunasCallback() {
									@Override
									public Object doAs() {
										return getFolderModule().getEntry(null, Long.parseLong(feIdFinal));
									}
								},
								zoneName,
								userId));
							feListValid = (null != fe);
						}
						catch (Exception ex) {
							// Failure possibilities:  AccessControlException,
							// NumericFormatException, ...
							feListValid = false;
						}
						if (!feListValid) {
							break;
						}
					}
				}
				
				// ...and the folderList.
				String fListPart = path.substring(fListIdPos + fListIdMarker.length());
				fListPart = fListPart.substring(0, fListPart.indexOf('/'));
				boolean fListValid = ((0 == fListPart.length()) || "-".equals(fListPart));
				if (!fListValid) {
					fListValid = true;
					String[] fList = fListPart.split(":");
					for (String fId:  fList) {
						Folder	f;
						try {
							// Yes!  Try to access the folder as Guest.
							final String fIdFinal = fId;
							f = ((Folder) RunasTemplate.runas(
								new RunasCallback() {
									@Override
									public Object doAs() {
										return getFolderModule().getFolder(Long.parseLong(fIdFinal));
									}
								},
								zoneName,
								userId));
							fListValid = (null != f);
						}
						catch (Exception ex) {
							// Failure possibilities:  AccessControlException,
							// NumericFormatException, ...
							fListValid = false;
						}
						if (!fListValid) {
							break;
						}
					}
				}

				// Return true if Guest can access all the entries and
				// folders and false otherwise.
				reply = new Boolean(feListValid && fListValid);
			}
			
			else {
				String wsIdMarker;
				int wsIdPos;
				
				wsIdMarker = "workspace" + "/";
				wsIdPos = path.indexOf( wsIdMarker );
				
				// Did we find "workspace"?
				if ( wsIdPos >= 0 )
				{
					// Yes.
					// No need to check to see if the user has rights to the workspace.
					reply = new Boolean( true );
				}
				else
				{
					// No, we found readFile but couldn't find the
					// folderEntry or workspace marker.
					reply = Boolean.FALSE;
				}
			}
		}
		
		else {
			// No, this isn't a readFile URL.
			reply = null;
		}

		// If we get here, reply refers to the appropriate Boolean
		// value for the request's readFile URL analysis.  Return it.
		return reply;
	}
	
	protected boolean isPathPermittedUnauthenticated(String path) {
		return (path != null && 
				(path.equals("/"+WebKeys.SERVLET_PORTAL_LOGIN) || 
						path.equals("/"+WebKeys.SERVLET_PORTAL_LOGOUT) || 
//						path.startsWith("/"+WebKeys.SERVLET_READ_FILE+"/") ||	// readFile is now checked via call to getReadFileWithGuestAccessFlag(). 
						path.startsWith("/"+WebKeys.SERVLET_VIEW_CSS+"/") ||
						path.equals("/"+WebKeys.SERVLET_VIEW_CSS)));
	}
	
	/*
	 * Returns true if the action from the request is allowed when
	 * no user is authenticated and false otherwise.
	 */
	protected boolean isActionPermittedUnauthenticated(HttpServletRequest req) {
		// Is Guest access allowed?
		boolean reply = guestAccessAllowed();
		if (reply) {
			// Yes!  Does the request have an action value?
			String actionValue = req.getParameter("action");
			reply = (actionValue != null);
			if (reply) {
				// Yes!  If the action value starts with '__', it's
				// valid.  If it's 'view_permalink', it requires more
				// checking when we're Guest.  Do we need to do more
				// checking?
				boolean actionIsViewPermalink = actionValue.equals(WebKeys.ACTION_VIEW_PERMALINK);
				reply = (actionIsViewPermalink || (actionValue.startsWith("__")));
				if (actionIsViewPermalink && WebHelper.isGuestLoggedIn(req)) {
					// Yes!  Is there a Default Home Page defined?
					String homeUrl = LandingPageHelper.getDefaultLandingPageUrl(req);
					if (null != homeUrl) {
						// Yes!  If there's no Default Guest Home Page
						// defined or the Default Home Page and Default
						// Guest Home Page are not the same and we're
						// trying to navigate to the Default Home
						// Page...
						String guestHomeUrl = LandingPageHelper.getDefaultGuestLandingPageUrl(req);
						String requestUrl   = Http.getCompleteURL(                            req);
						if (((null == guestHomeUrl) || (!(homeUrl.equals(guestHomeUrl)))) && requestUrl.startsWith(homeUrl)) {
							// ...we disallow the action.
							reply = false;
						}
					}
				}
			}
		}
		
		// If we get here, reply is true if the action is permitted
		// when were not authenticated and false otherwise.  Return it.
		return reply;
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

	private FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
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
	 * and the url does NOT have the GWT URL parameter marking.
	 */
	@SuppressWarnings("deprecation")
	private boolean shouldUrlBeConvertedToAPermalink( HttpServletRequest req )
	{
		// Does the URL have the GWT URL parameter.
		if (MiscUtil.hasString( req.getParameter( WebKeys.URL_NOVL_URL_FLAG                  )) ||
		    MiscUtil.hasString( req.getParameter( WebKeys.URL_VIBE_URL_FLAG_DEPRECATED       )) ||
			MiscUtil.hasString( req.getParameter( WebKeys.URL_VIBEONPREM_URL_FLAG_DEPRECATED )))
		{
			// Yes, no need to convert it.
			return false;
		}
		
		String action = req.getParameter( "action" );
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

	/*
	 * Returns a permalink for a user based on a user ID.
	 */
	private static String getUserPermalinkFromId(final HttpServletRequest req, final String userId) {
		return ((String) RunasTemplate.runasAdmin(
			new RunasCallback() {
				@Override
				public Object doAs() {
					return
						PermaLinkUtil.getUserPermalink(
							req,
							userId,
							GwtUIHelper.isActivityStreamOnLogin(),
							Utils.checkIfFilr());
				}
			}, WebHelper.getRequiredZoneName(req)));									
	}
	
	/*
	 * Returns true if Guest can navigate to the given URL and false
	 * otherwise.
	 */
	private boolean isValidGuestUrl(HttpServletRequest req, String url) {
		// Is Guest access allowed?
		boolean reply = guestAccessAllowed();
		if (reply) {
			// Yes!  Is there a Default Home Page defined? 
			String homeUrl = LandingPageHelper.getDefaultLandingPageUrl(req);
			if (null != homeUrl) {
				// Yes!  Does the given URL refer to it?
				if (url.startsWith(homeUrl)) {
					// Yes!  If there's not a Default Guest Home Page
					// defined or the Default Guest Home Page is not
					// the same as the Default Home Page...
					String guestHomeUrl = LandingPageHelper.getDefaultGuestLandingPageUrl(req);
					if ((null == guestHomeUrl) || (!(guestHomeUrl.equals(homeUrl)))) {
						// ...we don't allow Guest to navigate to it.
						reply = false;
					}
				}
			}
		}
		
		// If we get here, reply is true if the given URL is a valid
		// for the Guest to navigate to and false otherwise.  Return
		// it.
		return reply;
	}
}
