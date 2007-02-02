package com.sitescape.team.web.portlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.InternalException;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class UserPreloadInterceptor implements HandlerInterceptor {

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
		boolean isRunByAdapter = PortletAdapterUtil.isRunByAdapter(request);
    	Long userId = (Long)ses.getAttribute(WebKeys.USER_ID, PortletSession.APPLICATION_SCOPE);
		if (userId == null) { 
			String zoneName = reqCxt.getZoneName();
			String userName = reqCxt.getUserName();
			try {
				user = getProfileDao().findUserByNameOnlyIfEnabled(userName, zoneName);
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
					Map userAttrs = (Map)request.getAttribute(javax.portlet.PortletRequest.USER_INFO);
					if(userAttrs == null) {
						// According to JSR-168 spec, this means that the user is un-authenticatecd.
						// However, this interceptor is designed to be invoked only if the user
						// is authenticated. This indicates some internal problem (which tends 
						// to occur under Liferay when it's automatic login facility is enabled).
						// We can not allow the user to proceed in this case.
						throw new InternalException("User must log off and log in again");
					}
					// The userAttrs map should be always non-null (althouth it may
					// be empty), since this code is always executed in the context of
					// an authenticated user.
					if(userAttrs.containsKey("user.name.given"))
						updates.put("firstName", userAttrs.get("user.name.given"));
					if(userAttrs.containsKey("user.name.family"))
						updates.put("lastName", userAttrs.get("user.name.family"));
					if(userAttrs.containsKey("user.name.middle"))
						updates.put("middleName", userAttrs.get("user.name.middle"));
					if(userAttrs.containsKey("user.business-info.online.email"))
						updates.put("emailAddress", userAttrs.get("user.business-info.online.email"));
					if(userAttrs.containsKey("user.business-info.postal.organization"))
						updates.put("organization", userAttrs.get("user.business-info.postal.organization"));
					updates.put("locale", request.getLocale());

	 				user = getProfileModule().addUserFromPortal(zoneName, userName, null, updates);
	 				
	 				if(!isRunByAdapter) {
	 					// This request is served by the regular portlet container.
	 					// In this case, no additional synch is needed during the
	 					// current user session.
	 					ses.setAttribute(WebKeys.PORTLET_USER_SYNC, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
	 				}
	 			} 		
	 			else
	 				throw e;
			}
			
			ses.setAttribute(WebKeys.USER_ID, user.getId(), PortletSession.APPLICATION_SCOPE);
		} 
		else {
			user = getProfileDao().loadUserOnlyIfEnabled(userId, reqCxt.getZoneName());
		}
		
		boolean userModify = 
			SPropsUtil.getBoolean("portal.user.auto.synchronize", false);
		
		Boolean sync = (Boolean)ses.getAttribute(WebKeys.PORTLET_USER_SYNC, PortletSession.APPLICATION_SCOPE);
		
		// If all of the following conditions hold, we update the user in Aspen
		// with the information from the portal.
		// 1. System is configured to permit such update
		// 2. This request is being served by the regular portlet container - 
		// Only the regular container has access to the portal user properties.
		// 3. The information hasn't already been synchronized during current 
		// session - there is no point in trying to synchronize the info more
		// than once per login. especially important for efficiency reason.
		if (userModify && !isRunByAdapter && (sync == null || sync.equals(Boolean.FALSE))) {
			Map updates = new HashMap();
			Map userAttrs = (Map)request.getAttribute(javax.portlet.PortletRequest.USER_INFO);
			if(userAttrs == null) {
				// According to JSR-168 spec, this means that the user is un-authenticatecd.
				// However, this interceptor is designed to be invoked only if the user
				// is authenticated. This indicates some internal problem (which tends 
				// to occur under Liferay when it's automatic login facility is enabled).
				// We can not allow the user to proceed in this case.
				throw new InternalException("User must log off and log in again");
			}
			String val = null;
			if(userAttrs.containsKey("user.name.given")) {
				val = (String) userAttrs.get("user.name.given");
				if(!identical(val, user.getFirstName()))
					updates.put("firstName", val);
			}
			if (userAttrs.containsKey("user.name.family")) {
				val = (String)userAttrs.get("user.name.family");
				if (!identical(val, user.getLastName())) 
					updates.put("lastName", val);
			}
			if (userAttrs.containsKey("user.name.middle")) {
				val = (String)userAttrs.get("user.name.middle");
				if (!identical(val, user.getMiddleName())) 
					updates.put("middleName", val);
			}
			if (userAttrs.containsKey("user.business-info.online.email")) {
				val = (String)userAttrs.get("user.business-info.online.email");
				if (!identical(val, user.getEmailAddress())) 
					updates.put("emailAddress", val);
			}
			if (userAttrs.containsKey("user.business-info.postal.organization")) {
				val = (String)userAttrs.get("user.business-info.postal.organization");
				if (!identical(val, user.getOrganization())) 
					updates.put("organization", val);
			}
			if (!request.getLocale().equals(user.getLocale())) 
				updates.put("locale", request.getLocale());

			if(!updates.isEmpty()) {
				getProfileModule().modifyUserFromPortal(user, updates);				
			}
			
			ses.setAttribute(WebKeys.PORTLET_USER_SYNC, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
		}
		
		reqCxt.setUser(user);
	}
	
	private boolean identical(String a, String b) {
		if(a == null) {
			if(b == null)
				return true;
			else
				return false;
		}
		else {
			if(b == null)
				return false;
			else
				return a.equals(b);
		}
	}
}
