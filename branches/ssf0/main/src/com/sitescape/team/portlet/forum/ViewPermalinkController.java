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
package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.runas.RunasCallback;
import com.sitescape.team.runas.RunasTemplate;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.web.util.WebUrlUtil;


/**
 * @author Peter Hurley
 *
 */
public class ViewPermalinkController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String newTab= PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		
		User user = null;
		Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			user = AccessUtils.getZoneGuestUser(zoneId);
			if (user == null || binderId.equals("") || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
	 			response.setRenderParameters(request.getParameterMap());
	 			return;
			}
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
			if (binderId.equals("") || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
	 			response.setRenderParameters(request.getParameterMap());
	 			return;
			}
		}
 		
 		//See if there is a portlet url to use for this request
        String url = BinderHelper.getBinderPermaLink(this);
 		if (url.equals("")) {
 			response.setRenderParameters(request.getParameterMap());
 			return;
 		}
 		
		if (!binderId.equals("")) url = url.replaceAll(WebKeys.URL_BINDER_ID_PLACE_HOLDER, binderId);
		if (!entryId.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_ID_PLACE_HOLDER, entryId);
		if (!entryTitle.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER, entryTitle);
		if (!entityType.equals("")) url = url.replaceAll(WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER, entityType);
		if (!newTab.equals("")) url = url.replaceAll(WebKeys.URL_NEW_TAB_PLACE_HOLDER, newTab);
		
		if (entityType.equals("") && entryId.equals("") && !binderId.equals("")) {
			Binder binder = getBinderModule().getBinder(new Long(binderId));
			entityType = binder.getEntityType().name();
		}
		if (entityType.equals(EntityIdentifier.EntityType.workspace.toString()) || entityType.equals(EntityIdentifier.EntityType.user.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_ws_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folder.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");
			} else {
				try {
					getBinderModule().getBinder(new Long(binderId));
					url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
				} catch (AccessControlException ac) {
					url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");					
				}
			}
		}
		if (!fileId.equals("") && !binderId.equals("") && !entityType.equals("")) {
			url = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?" +
				WebKeys.URL_BINDER_ID + "=" + binderId +
				"&" + WebKeys.URL_ENTITY_TYPE + "=" + entityType;
			if (!entryId.equals("")) url += "&" + WebKeys.URL_ENTRY_ID + "=" + entryId;
			url += "&" + WebKeys.URL_FILE_ID + "=" + fileId;
		}
 		
    	if(logger.isDebugEnabled()) {
    		logger.debug("Permalink followed: " + url);
    	}
    	response.sendRedirect(url);

	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		final String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		final String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String newTab= PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");

		Map<String,Object> model = new HashMap<String,Object>();
		User user = null;
		Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			if (!zoneId.equals("")) user = AccessUtils.getZoneGuestUser(new Long(zoneId));
			if (user == null || binderId.equals("") || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
				model.put("ss_portalLoginUrl", SPropsUtil.getString("permalink.login.url"));
				model.put("ss_screens_to_login_screen", 
						SPropsUtil.getString("permalink.login.screensToLoginScreen"));
				model.put("ss_screens_after_login_screen_to_logged_in", 
						SPropsUtil.getString("permalink.login.screensAfterLoginScreenToLoggedIn"));
	 	    	return new ModelAndView("forum/portal_login", model);
			} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
				String zoneName = WebHelper.getZoneNameByVirtualHost(request);
				try {
					RunasTemplate.runasGuest(new RunasCallback() {
						public Object doAs() {
							getFolderModule().getEntry(new Long(binderId), new Long(entryId));
							return null;
						}
					}, zoneName);
				} catch(AccessControlException ac) {
					model.put("ss_portalLoginUrl", SPropsUtil.getString("permalink.login.url"));
					model.put("ss_screens_to_login_screen", 
							SPropsUtil.getString("permalink.login.screensToLoginScreen"));
					model.put("ss_screens_after_login_screen_to_logged_in", 
							SPropsUtil.getString("permalink.login.screensAfterLoginScreenToLoggedIn"));
		 	    	return new ModelAndView("forum/portal_login", model);
				}
			}
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
	 		
			if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//See if the user has access to the item being requested
				if (!binderId.equals("")) {
					try {
						//See if this user can access the binder
						getBinderModule().getBinder(new Long(binderId));
						if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
							//See if the user can access the entry, too
							getFolderModule().getEntry(new Long(binderId), new Long(entryId));
						}
					} catch(AccessControlException ac) {
						model.put("ss_portalLoginUrl", SPropsUtil.getString("permalink.login.url"));
						model.put("ss_screens_to_login_screen", 
								SPropsUtil.getString("permalink.login.screensToLoginScreen"));
						model.put("ss_screens_after_login_screen_to_logged_in", 
								SPropsUtil.getString("permalink.login.screensAfterLoginScreenToLoggedIn"));
			 	    	return new ModelAndView("forum/portal_login", model);
					}
				}
			}
		}
		
 		String url = BinderHelper.getBinderPermaLink(this);
 		if (url.equals("")) {
 			url = SPropsUtil.getString("permalink.fallback.url", "");
 			model.put(WebKeys.PERMALINK, url);
 	    	return new ModelAndView("binder/view_permalink", model);
 		}
		
		if (!binderId.equals("")) url = url.replaceAll(WebKeys.URL_BINDER_ID_PLACE_HOLDER, binderId);
		if (!entryId.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_ID_PLACE_HOLDER, entryId);
		if (!entryTitle.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER, entryTitle);
		if (!entityType.equals("")) url = url.replaceAll(WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER, entityType);
		if (!newTab.equals("")) url = url.replaceAll(WebKeys.URL_NEW_TAB_PLACE_HOLDER, newTab);
		
		if (entityType.equals("") && entryId.equals("") && !binderId.equals("")) {
			Binder binder = getBinderModule().getBinder(new Long(binderId));
			entityType = binder.getEntityType().name();
		}
		if (entityType.equals(EntityIdentifier.EntityType.workspace.toString()) || entityType.equals(EntityIdentifier.EntityType.user.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_ws_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folder.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");
			} else {
				try {
					getBinderModule().getBinder(new Long(binderId));
					url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
				} catch (AccessControlException ac) {
					url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");					
				}
			}
		}
		if (!fileId.equals("") && !binderId.equals("") && !entityType.equals("")) {
			url = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?" +
				"&" + WebKeys.URL_BINDER_ID + "=" + binderId +
				"&" + WebKeys.URL_ENTITY_TYPE + "=" + entityType;
			if (!entryId.equals("")) url += "&" + WebKeys.URL_ENTRY_ID + "=" + entryId;
			url += "&" + WebKeys.URL_FILE_ID + "=" + fileId;
		}
 		
		model.put(WebKeys.PERMALINK, url);
			
    	if(logger.isDebugEnabled()) {
    		logger.debug("Permalink followed: " + url);
    	}

    	return new ModelAndView("binder/view_permalink", model);
	}

}
