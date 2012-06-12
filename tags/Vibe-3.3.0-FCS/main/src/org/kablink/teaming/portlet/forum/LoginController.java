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
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.ssfs.util.SsfsUtil;
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
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.web.portlet.ModelAndView;

/**
 * @author Peter Hurley
 *
 */
public class LoginController  extends SAbstractControllerRetry {
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) 
			throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
			throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
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
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
    	AuthenticationException ex = (AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	if(ex != null) {
    		model.put(WebKeys.LOGIN_ERROR, ex.getMessage());
    		session.removeAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
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
		
		if ( Validator.isNotNull( refererUrl ) )
		{
			model.put(WebKeys.URL, refererUrl);
			model.put( "loginRefererUrl", refererUrl );
		}
		
		HttpServletRequest req = WebHelper.getHttpServletRequest(request);
		if(BrowserSniffer.is_wap_xhtml(req) || 
				BrowserSniffer.is_blackberry(req) || 
				BrowserSniffer.is_iphone(req) ||
				BrowserSniffer.is_droid(req)) {
			
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
			
			// Add the user's name to the response.
			model.put( "userFullName", Utils.getUserTitle( user ) );
			
			// Add the "my workspace" url to the response.
			{
				String myWSUrl = PermaLinkUtil.getPermalink( request, user );
				model.put( "myWorkspaceUrl", (myWSUrl + "/seen_by_gwt/1") );
			}
			
			return new ModelAndView( "forum/GwtMainPage", model );
		}
		
		return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
	} 
}
