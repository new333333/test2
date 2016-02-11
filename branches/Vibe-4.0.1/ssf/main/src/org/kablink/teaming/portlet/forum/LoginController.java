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
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.extuser.ExternalUserRespondingToInvitationException;
import org.kablink.teaming.extuser.ExternalUserRespondingToPwdResetException;
import org.kablink.teaming.extuser.ExternalUserRespondingToPwdResetVerificationException;
import org.kablink.teaming.extuser.ExternalUserRespondingToVerificationException;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.module.authentication.UserIdNotActiveException;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.kablink.util.api.ApiErrorCode;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
public class LoginController  extends SAbstractControllerRetry {
	private static final String LOGIN_STATUS_WEBACCESS_RESTRICTED = "webAccessRestricted";
	private static final String LOGIN_STATUS_AUTHENTICATION_FAILED = "authenticationFailed";
	private static final String LOGIN_STATUS_REGISTRATION_REQUIRED = "registrationRequired";
	private static final String LOGIN_STATUS_PROMPT_FOR_LOGIN = "promptForLogin";
	private static final String LOGIN_STATUS_PROMPT_FOR_PWD_RESET = "promptForPwdReset";
	private static final String LOGIN_STATUS_PWD_RESET_VERIFIED = "pwdResetVerified";
	private static final String LOGIN_STATUS_PASSWORD_EXPIRED = "passwordExpired";
	
	/**
	 * Set the given user's password and mark the user as verified.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void completePasswordReset( User extUser )
	{
		if ( extUser != null )
		{
			boolean pwdReset = false;
			
			// Change the user's password
			try
			{
				Map updates = new HashMap();
				String pwd = null;

				// The password is stored in the user's properties.
				{
					UserProperties userProperties;
					Object value;
					
			    	userProperties = getProfileModule().getUserProperties( extUser.getId() );
					value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_RESET_PWD );
					if ( value != null && value instanceof String )
						pwd = (String) value;
				}
				
				if ( pwd != null && pwd.length() > 0 )
				{
					updates.put( "password", pwd );
					getProfileModule().modifyUserFromPortal( extUser.getId(), updates, null );
					pwdReset = true;
				}
				else
					logger.error( "In completePasswordReset(), unable to get the reset password" );
			}
			catch ( Exception ex )
			{
				logger.error( "In completePasswordReset(), call to getProfileModule().modifyUserFromPortal() failed: " + ex.toString() );
			}

			try
			{
				if ( pwdReset )
				{
					// Mark the user as verified
					ExternalUserUtil.markAsVerified( extUser );

					// Remove the password from the user's properties 
					getProfileModule().setUserProperty(
													extUser.getId(),
													ObjectKeys.USER_PROPERTY_RESET_PWD,
													"" );
				}
			}
			catch ( Exception ex )
			{
				logger.error( "In completePasswordReset(), call to ExternalUserUtil.markAsVerified() failed: " + ex.toString() );
			}
		}
		else
			logger.error( "In completePasswordReset(), extUser is null" );
	}

	//caller will retry on OptimisiticLockExceptions
	@Override
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) 
			throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
			throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Object sessionObj;
		
		// Force the Vibe product that's running to be determined.
		// This will set the session captive state, ... into the
		// session cache as appropriate.
		GwtUIHelper.getVibeProduct(request);

		// This controller is used to display the sign-in form used for login. 
		// If form-based login is disallowed, this controller shouldn't display
		// the sign-in form. 
		if(SPropsUtil.getBoolean("form.login.auth.disallowed", false))
			return null;

        User user = RequestContextHolder.getRequestContext().getUser();
		Map<String,Object> model = new HashMap<String,Object>();
		
		Long binderId = user.getWorkspaceId();
		if (binderId != null) {
			try {
				//See if this user can access the binder
				Binder binder = getBinderModule().getBinder(new Long(binderId));
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}

		//The user is logged in, go to the user profile page
		//Set up the standard beans
		BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		String url = PortletRequestUtils.getStringParameter(request, WebKeys.URL_URL, "");
		if(Validator.isNotNull(url)) {
			model.put(WebKeys.URL, url);
		}
		
		// Look in the http session for an authentication exception
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
    	sessionObj = session.getAttribute( AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY );
    	if ( sessionObj != null )
    	{
    		if ( sessionObj instanceof ExternalUserRespondingToInvitationException )
    		{
    			{
	    			ExternalUserRespondingToInvitationException ex;
	    			String providerName;
	    			User extUser;
        			String invitationUrl;
	    			
	    			ex = (ExternalUserRespondingToInvitationException) sessionObj;
        			extUser = ex.getExternalUser();
	    			
	    			// Get the name of the opend id provider this user can use
	    			providerName = ex.getAllowedOpenidProviderName();
	    			if ( providerName != null && providerName.length() > 0 )
	    			{
	    				String providerUrl;
	    				
	    				// Get the url needed to authenticate using this provider
	    				providerUrl = getOpenIDProviderUrl( providerName );
	    				
	    				if ( providerUrl != null && providerUrl.length() > 0 )
	    				{
	    					// Tell the login dialog the name of the open id provider the user can use.
	    					model.put( WebKeys.LOGIN_OPEN_ID_PROVIDER_NAME, providerName );
	    					model.put( WebKeys.LOGIN_OPEN_ID_PROVIDER_URL, providerUrl );
	    				}
	    			}
	    			
        			// Get the original url the user used to hit Filr.
    				invitationUrl = ex.getInvitationLink();

    				if ( invitationUrl != null && invitationUrl.length() > 0 )
    				{
						model.put( WebKeys.LOGIN_INVITATION_URL, invitationUrl );
		
		    			// Tell the login dialog the id of the external user
		    			model.put( WebKeys.LOGIN_EXTERNAL_USER_ID, String.valueOf( extUser.getId() ) );
		    			
		    			// Tell the login dialog the user id of the external user.
		    			model.put( WebKeys.LOGIN_EXTERNAL_USER_NAME, extUser.getName() );
		    			
		    			// Tell the login dialog that an external user is responding to an invitation.
		    			model.put( WebKeys.LOGIN_STATUS, LOGIN_STATUS_REGISTRATION_REQUIRED );
    				}
    			}
    		}
    		else if(sessionObj instanceof ExternalUserRespondingToVerificationException) {
    			User extUser;
    			ExternalUserRespondingToVerificationException ex = (ExternalUserRespondingToVerificationException) sessionObj;

    			extUser = ex.getExternalUser();
    			
        		model.put( WebKeys.LOGIN_STATUS, LOGIN_STATUS_PROMPT_FOR_LOGIN );
        		model.put( WebKeys.LOGIN_EXTERNAL_USER_NAME, extUser.getName() );
        		String refererUrl = ex.getVerificationLink();
    			model.put(WebKeys.URL, refererUrl);
    			model.put( "loginRefererUrl", refererUrl );
    		}
    		else if ( sessionObj instanceof ExternalUserRespondingToPwdResetException )
    		{
    			User extUser;
    			ExternalUserRespondingToPwdResetException ex;
        		String refererUrl;

    			ex = (ExternalUserRespondingToPwdResetException) sessionObj;
    			extUser = ex.getExternalUser();
    			
        		model.put( WebKeys.LOGIN_STATUS, LOGIN_STATUS_PROMPT_FOR_PWD_RESET );
        		model.put( WebKeys.LOGIN_EXTERNAL_USER_NAME, extUser.getName() );
    			model.put( WebKeys.LOGIN_EXTERNAL_USER_ID, String.valueOf( extUser.getId() ) );
        		refererUrl = ex.getUrl();
    			model.put( WebKeys.URL, refererUrl );
    			model.put( "loginRefererUrl", refererUrl );
    		}
    		else if ( sessionObj instanceof ExternalUserRespondingToPwdResetVerificationException )
    		{
    			RunasCallback callback;
    			ExternalUserRespondingToPwdResetVerificationException ex;
    			final Long userId;
        		String refererUrl;
        		Object retVal;

    			ex = (ExternalUserRespondingToPwdResetVerificationException) sessionObj;
    			userId = ex.getExternalUserId();

    			callback = new RunasCallback()
    			{
    				@Override
    				public Object doAs()
    				{
    	    			User extUser;
    	    			String extUserName;

    	    			extUser = ((User) getProfileModule().getEntry( userId ));
    	    			extUserName = extUser.getName();

    	    			// Reset the user's password and mark the user as verified.
    	    			completePasswordReset( extUser );
    	    			
    	    			return extUserName;
    				}
    			};

    			// Do the necessary work as the admin user.
    			retVal = RunasTemplate.runasAdmin(
		    									callback,
		    									RequestContextHolder.getRequestContext().getZoneName() );

        		if ( retVal != null && retVal instanceof String )
        			model.put( WebKeys.LOGIN_EXTERNAL_USER_NAME, (String) retVal );
        		model.put( WebKeys.LOGIN_STATUS, LOGIN_STATUS_PWD_RESET_VERIFIED );
        		refererUrl = ex.getUrl();
    			model.put( WebKeys.URL, refererUrl );
    			model.put( "loginRefererUrl", refererUrl );
    		}
    		else if ( sessionObj instanceof AuthenticationException )
    		{
        		AuthenticationException ex;
        		String loginUserId = "";

        		// Authentication failed.
        		String exStatus;
        		ex = ( (AuthenticationException) sessionObj );
        		UserIdNotActiveException unaEx = ( ( ex instanceof UserIdNotActiveException ) ? ( ( UserIdNotActiveException ) ex ) : null );
        		ApiErrorCode exApiErrorCode = ( ( null != unaEx ) ? unaEx.getApiErrorCode() : null );
        		boolean hasApiErrorCode = ( null != exApiErrorCode );
        		if      ( hasApiErrorCode && ( ApiErrorCode.USERACCOUNT_WEBACCESS_BLOCKED.equals( exApiErrorCode ) ) ) exStatus = LOGIN_STATUS_WEBACCESS_RESTRICTED;
        		else if ( hasApiErrorCode && ( ApiErrorCode.PASSWORD_EXPIRED.equals(              exApiErrorCode ) ) ) exStatus = LOGIN_STATUS_PASSWORD_EXPIRED;
        		else                                                                                                   exStatus = LOGIN_STATUS_AUTHENTICATION_FAILED;
        		
        		if ( null != unaEx )
        		{
        			Long userId = unaEx.getUserId();
        			loginUserId = ( ( null == userId ) ? "" : String.valueOf( userId ) ); 
        		}
        		else
        		{
        			loginUserId = "";

        			if ( ex instanceof TextVerificationException )
            		{
    					// Either the user entered an invalid captcha response or we have detected
        				// a brute-force authentication attack.  Either way require captcha on the login dialog.
    					model.put( "ssDoTextVerification", "true" );
            		}
        		}

        		model.put( WebKeys.LOGIN_STATUS,  exStatus        );
        		model.put( WebKeys.LOGIN_ERROR,   ex.getMessage() );
        		model.put( WebKeys.LOGIN_USER_ID, loginUserId     );

        		HttpServletRequest req = ((HttpServletRequestReachable) request).getHttpServletRequest();
        		if (WebHelper.isMobileUI(req)) {
        			String view = "mobile/show_login_form";
        			
        			Cookie[] cookies = request.getCookies();
        			if(cookies != null) {
        				for(Cookie cookie:cookies) {
        					//if we found the native mobile and we have an error logging in then
        					if(cookie.getName().equals(WebKeys.URL_NATIVE_MOBILE_APP_COOKIE) && ex != null) {
        						String value = cookie.getValue();
        						model.put(WebKeys.URL_OPERATION2, value);
        						view = "mobile/redirected_login";
        						break;
        					}
        				}
        			}
        			
        			return new ModelAndView(view, model);
        		}
    		}

    		session.removeAttribute( AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY );
    	}
    	else
    	{
    		model.put( WebKeys.LOGIN_STATUS, LOGIN_STATUS_PROMPT_FOR_LOGIN );
    	}

    	// Is self registration permitted?
    	if ( MiscUtil.canDoSelfRegistration( this ) )
    	{
    		// Yes.
    		// Add the information needed to support the "Create new account" ui to the response.
    		MiscUtil.addCreateNewAccountDataToResponse( this, request, model );
    	}

		model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));	
		
		String refererUrl = request.getParameter("refererUrl");
		if(Validator.isNull(refererUrl))
			refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
		
		if (Validator.isNotNull(refererUrl)) {
			// Bugzilla 955017:  Only allow a refererUrl that
			// references this instance of Filr or Vibe.
			if (WebUrlUtil.doesUrlReferenceThisInstance(refererUrl, request)) {
				model.put(WebKeys.URL, refererUrl);
				model.put("loginRefererUrl", refererUrl);
			}
		}
		
		boolean durangoUI = GwtUIHelper.isGwtUIActive(request);
		if ( durangoUI )
		{
			// Store the common GWT UI request info data.
			GwtUIHelper.setCommonRequestInfoData( request, this, model );

			// Add the binder id to the response.
			model.put( WebKeys.URL_BINDER_ID, binderId );

			model.put( "adaptedUrl", "" );

			// Add the flag that tells us a user is not logged in.
			model.put( "isUserLoggedIn", false );
			
			// Add a flag that tells us if we should prompt for login.
			model.put( "promptForLogin", "true" );
			
			// Can the login dialog have a cancel button?
			if ( Validator.isNull( refererUrl ) && isGuestAccessAllowed() )
			{
				// Yes, guest access is allowed.
				model.put( WebKeys.LOGIN_CAN_CANCEL, "true" );
			}
			else
			{
				// No
				model.put( WebKeys.LOGIN_CAN_CANCEL, "false" );
			}
			
			// Add the user's name to the response.
			model.put( "userFullName", Utils.getUserTitle( user ) );
			
			// Add the "my workspace" url to the response.
			{
				String myWSUrl = PermaLinkUtil.getPermalink( request, user );
				model.put( "myWorkspaceUrl", (myWSUrl + "/seen_by_gwt/1") );
				
				Long userWSId = user.getWorkspaceId();
				Workspace userWS;
				try                  {userWS = getWorkspaceModule().getWorkspace(userWSId);}
				catch (Exception ex) {userWS = null;                                       }
				boolean userHasWSAccess = (null != userWS);
				if (userHasWSAccess) {
					userHasWSAccess = getBinderModule().testAccess(user, userWS, BinderOperation.readEntries, true);
				}
				model.put( "myWorkspaceAccessible", String.valueOf(userHasWSAccess));
			}
			
			return new ModelAndView( "forum/GwtMainPage", model );
		}
		
		return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
	}
	
	/**
	 * Return a list of OpenID Authentication providers supported by Vibe
	 */
	private String getOpenIDProviderUrl( String providerName )
	{
		List<OpenIDProvider> providers; 
		
		if ( providerName == null )
			return null;
		
		// Get a list of the OpenID providers
		providers = getAdminModule().getOpenIDProviders();
		if ( providers != null && providers.size() > 0 )
		{
			Iterator<OpenIDProvider> iterator;
			
			iterator = providers.iterator();
			while ( iterator.hasNext() )
			{
				OpenIDProvider provider;
				
				provider = iterator.next();
				
				if ( providerName.equalsIgnoreCase( provider.getName() ) )
					return provider.getUrl();
			}
		}
		
		// If we get here we did not find the given provider name
		return null;
	}

	/*
	 * See if the license allows for guest access.  If so see if guest access is turned on. 
	 */
	protected boolean isGuestAccessAllowed()
	{
		if ( ReleaseInfo.isLicenseRequiredEdition() )
		{
			if ( LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.GuestAccess" ) )
			{
				return isGuestAccessTurnedOn();
			}
			else
			{
				return false;
			}
		}
		else
		{
			return isGuestAccessTurnedOn();
		}
	}
	
	/**
	 * See if the administrator has turned on guest access.
	 */
	private boolean isGuestAccessTurnedOn()
	{
		Long zoneId;
		AuthenticationConfig config;

		zoneId = getZoneModule().getZoneIdByVirtualHost( ZoneContextHolder.getServerName() );
		config = getAuthenticationModule().getAuthenticationConfigForZone( zoneId );
		
		return config.isAllowAnonymousAccess();
	}
}
