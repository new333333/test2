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
package com.sitescape.team.web.portlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.InternalException;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class UserPreloadInterceptor implements HandlerInterceptor,InitializingBean {

	private ProfileDao profileDao;
	private ProfileModule profileModule;
	private String[] userModify;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}

 	public void afterPropertiesSet() {
		userModify = SPropsUtil.getStringArray("portal.user.auto.synchronize", ",");
 	}

	public boolean preHandle(PortletRequest request, PortletResponse response, Object handler)
    	throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		if(requestContext == null)
			return true; // unauthenticated request
		
		// Load user only if it hasn't already been done for the current
		// requesting thread. Since this handler is called once per SSF portlet, 
		// it's possible that we end up loading the same user object multiple 
		// times when the single user interaction involves invocation of 
		// multiple SSF portlets (e.g. re-drawing of a portal page). 
		// This checking will prevent the inefficiency from happening. 
		if(requestContext.getUser() == null) {
			loadUser(request, requestContext);
		}
		
		return true;
	}

	public void postHandle(
			RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
	}

	public void afterCompletion(
			PortletRequest request, PortletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	private void loadUser(PortletRequest request, RequestContext reqCxt) {
    	User user;
		PortletSession ses = WebHelper.getRequiredPortletSession(request);
    	Long userId = (Long)ses.getAttribute(WebKeys.USER_ID, PortletSession.APPLICATION_SCOPE);
		if (userId == null) { 
			user = RequestContextUtil.resolveToUser();
			
			ses.setAttribute(WebKeys.USER_ID, user.getId(), PortletSession.APPLICATION_SCOPE);
		} 
		else {
			reqCxt.setUserId(userId);
			user = RequestContextUtil.resolveToUser();
		}
		
		Boolean sync = (Boolean)ses.getAttribute(WebKeys.PORTLET_USER_SYNC, PortletSession.APPLICATION_SCOPE);
		
		// If all of the following conditions hold, we update the user in Aspen
		// with the information from the portal.
		// 1. System is configured to permit such update
		// 2. This request is being served by the regular portlet container - 
		// Only the regular container has access to the portal user properties.
		// 3. The information hasn't already been synchronized during current 
		// session - there is no point in trying to synchronize the info more
		// than once per login. especially important for efficiency reason.
		
		boolean isRunByAdapter = PortletAdapterUtil.isRunByAdapter(request);

		if (!isRunByAdapter && !Boolean.TRUE.equals(sync)) { 
			Map updates = getStandardUserInfoFromPortal(request, user);
			
			updates = filterUpdates(updates);
			
			if(!updates.isEmpty()) {
				getProfileModule().modifyUserFromPortal(user, updates);				
			}
			
			ses.setAttribute(WebKeys.PORTLET_USER_SYNC, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
		}
	}
	
	private Map filterUpdates(Map updates) {
		if(updates.isEmpty()) {
			return updates;
		}
		else {
			Map mods = new HashMap();
			for (int i = 0; i<userModify.length; ++i) {
				Object val = updates.get(userModify[i]);
				if (val != null) {
					mods.put(userModify[i], val);
				}					
			}
			return mods;
		}
	}
	
	private Map getStandardUserInfoFromPortal(PortletRequest request, User user) {
		Map updates = new HashMap();
		
		String zoneName = user.getParentBinder().getParentBinder().getName();
		if(user.getName().equals(SZoneConfig.getGuestUserName(zoneName))) {
			return updates; // guest access - don't update account	
			
		Map userAttrs = (Map)request.getAttribute(javax.portlet.PortletRequest.USER_INFO);
		if(userAttrs == null) {
			// According to JSR-168 spec, this means that the user is un-authenticatecd.
			// However, this interceptor is designed to be invoked only if the user
			// is already authenticated by the portal. For any user other than the
			// default user, this indicates some internal problem (which tended to 
			// occur under older version of Liferay when it's automatic login facility 
			// is enabled). We can not allow the user to proceed in that case.
			throw new InternalException("User must log off and log in again");
		}
		
		String val = null;
		if(userAttrs.containsKey("user.name.given")) {
			val = (String) userAttrs.get("user.name.given");
			if(val != null && !val.equals(user.getFirstName()))
				updates.put("firstName", val);
		}
		if (userAttrs.containsKey("user.name.family")) {
			val = (String)userAttrs.get("user.name.family");
			if(val != null && !val.equals(user.getLastName()))
				updates.put("lastName", val);
		}
		if (userAttrs.containsKey("user.name.middle")) {
			val = (String)userAttrs.get("user.name.middle");
			if(val != null && !val.equals(user.getMiddleName()))
				updates.put("middleName", val);
		}
		if (userAttrs.containsKey("user.business-info.online.email")) {
			val = (String)userAttrs.get("user.business-info.online.email");
			if(val != null && !val.equals(user.getEmailAddress()))
				updates.put("emailAddress", val);
		}
		if (userAttrs.containsKey("user.business-info.postal.organization")) {
			val = (String)userAttrs.get("user.business-info.postal.organization");
			if(val != null && !val.equals(user.getOrganization()))
				updates.put("organization", val);
		}
		if (!request.getLocale().equals(user.getLocale())) 
			updates.put("locale", request.getLocale());

		return updates;
	}

}
