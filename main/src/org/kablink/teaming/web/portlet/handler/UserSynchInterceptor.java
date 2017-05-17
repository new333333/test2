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
package org.kablink.teaming.web.portlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.beans.factory.InitializingBean;


public class UserSynchInterceptor extends AbstractInterceptor implements InitializingBean {

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

	public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
		throws Exception {
		return preHandle(request, handler);
	}

	public boolean preHandleAction(ActionRequest request, ActionResponse response, Object handler)
		throws Exception {
		return preHandle(request, handler);
	}
	private boolean preHandle(PortletRequest request, Object handler)
		throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		if(requestContext == null)
			return true; // unauthenticated request
		
		synchUser(request, requestContext);
		
		return true;
	}


	private void synchUser(PortletRequest request, RequestContext reqCxt) {
		// If all of the following conditions hold, we update the user in Aspen
		// with the information from the portal.
		// 1. System is configured to permit such update
		// 2. This request is being served by the regular portlet container - 
		// Only the regular container has access to the portal user properties.
		// 3. The information hasn't already been synchronized during current 
		// session - there is no point in trying to synchronize the info more
		// than once per login. especially important for efficiency reason.
		
		boolean isRunByAdapter = PortletAdapterUtil.isRunByAdapter(request);

		if (!isRunByAdapter) {
			PortletSession ses = WebHelper.getRequiredPortletSession(request);
			
	 		Boolean sync = (Boolean)ses.getAttribute(WebKeys.PORTLET_USER_SYNC, PortletSession.APPLICATION_SCOPE);
			
	 		if(!Boolean.TRUE.equals(sync)) { 
				User user = RequestContextHolder.getRequestContext().getUser();
				Map updates = getStandardUserInfoFromPortal(request, user);
				
				updates = filterUpdates(updates);
				
				if(!updates.isEmpty()) {
					getProfileModule().modifyUserFromPortal(user.getId(), updates, null);				
				}
				
				ses.setAttribute(WebKeys.PORTLET_USER_SYNC, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
	 		}
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
		if(user.getName().equals(SZoneConfig.getGuestUserName(zoneName)))
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
