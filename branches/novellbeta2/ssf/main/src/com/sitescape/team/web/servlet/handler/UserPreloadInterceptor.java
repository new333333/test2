/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.servlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class UserPreloadInterceptor extends HandlerInterceptorAdapter {
	private ProfileDao profileDao;
	private ProfileModule profileModule;
	
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

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		if(requestContext.getUser() == null) {
			loadUser(request, requestContext);
		}

		return true;
	}
	    
	private void loadUser(HttpServletRequest request, RequestContext reqCxt) {
		User user;
		HttpSession ses = WebHelper.getRequiredSession(request);
		
	   	Long userId = (Long)ses.getAttribute(WebKeys.USER_ID);
		if (userId == null) { 
			String zoneName = reqCxt.getZoneName();
			String userName = reqCxt.getUserName();
			try {
				user = getProfileDao().findUserByName(
					userName, zoneName);
			}
			catch(NoUserByTheNameException e) {
				// The user doesn't exist in the Aspen user database. Since this
				// interceptor is never called unless the user is first authenticated,
				// this means that the user was authenticated against the portal
				// database, and yet the Aspen user database doesn't know about
				// the user. In this case, we create the user account in Aspen
				// if the system is configured to allow such.
				boolean userCreate = 
					SPropsUtil.getBoolean("portal.user.auto.create", false);
	 			if (userCreate) {
	 				Map updates = new HashMap();
	 				// Since we have no information about the user other than 
	 				// its login name, there's not much to store. However, we
	 				// will temporarily set the user's last name to the login 
	 				// name, to make it a bit more useful. The last name will
	 				// be properly updated when the same user accesses the
	 				// system through the regular portlet container if the
	 				// system is configured to support auto synch of user.
	 				// If not, the user can manually change the info later on. 
	 				updates.put("lastName", userName);
					updates.put("locale", request.getLocale());
					
	 				user = getProfileModule().addUserFromPortal(zoneName, userName, null, updates);
	 			}
	 			else
	 				throw e;
			}
			ses.setAttribute(WebKeys.USER_ID, user.getId());
		} else {
			user = getProfileDao().loadUser(userId, reqCxt.getZoneName());
		}

		reqCxt.setUser(user);
	}

}
