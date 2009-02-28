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
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.PortletRequestImpl;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebUrlUtil;
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
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		ProfileBinder	profileBinder;

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
        HttpSession session = ((PortletRequestImpl) request).getHttpServletRequest().getSession();
    	AuthenticationException ex = (AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	if(ex != null) {
    		model.put(WebKeys.LOGIN_ERROR, ex.getMessage());
    		session.removeAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	}

    	// Does the user have rights to add a user?
    	try
    	{
	    	profileBinder = getProfileModule().getProfileBinder();
			if ( getProfileModule().testAccess( profileBinder, ProfileOperation.addEntry ) )
			{
				List	defaultEntryDefinitions;

				// Yes
				model.put( WebKeys.ADD_USER_ALLOWED, "true" );

				// Build the url to invoke the "Add User" page.
				defaultEntryDefinitions = profileBinder.getEntryDefinitions();
				if ( !defaultEntryDefinitions.isEmpty() )
				{
					Definition			def;
					AdaptedPortletURL	adapterUrl;

					// There is only 1 entry definition for a Profile binder.  Get it.
					def = (Definition) defaultEntryDefinitions.get( 0 );
					
					// Create the url needed to invoke the "Add User" page.
					adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, profileBinder.getId().toString() );
					adapterUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
					model.put( WebKeys.ADD_USER_URL, adapterUrl.toString() );
				}
			}
    	}
    	catch (Exception e)
    	{
    		// Nothing to do.  It just means that the Guest user doesn't not have rights to the Profile binder.
    	}

		String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
		if(Validator.isNotNull(refererUrl))
			model.put(WebKeys.URL, refererUrl);
		return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
	} 
}
