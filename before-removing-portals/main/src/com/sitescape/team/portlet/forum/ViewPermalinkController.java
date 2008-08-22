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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.runas.RunasCallback;
import com.sitescape.team.runas.RunasTemplate;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ViewPermalinkController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String newTab= PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.none;
		DefinableEntity entity = null;
		try {
			entityType = EntityIdentifier.EntityType.valueOf(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, ""));
		} catch(Exception ignore) {};
		
		User user = null;
		Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			user = AccessUtils.getZoneGuestUser(zoneId);
			if (user == null || Validator.isNull(binderId) || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
	 			response.setRenderParameters(request.getParameterMap());
	 			return;
			}
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
			if (Validator.isNull(binderId) || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
	 			response.setRenderParameters(request.getParameterMap());
	 			return;
			}
		}
 		if (Validator.isNotNull(fileId)) {
 			if (entityType.isBinder()) {
 				entity = getBinderModule().getBinder(Long.valueOf(binderId));
 			} if (entityType.isPrincipal()) {
 				entity = getProfileModule().getEntry(Long.valueOf(entryId));
 			} else {
 				entity = getFolderModule().getEntry(Long.valueOf(binderId), Long.valueOf(entryId));
 			}
 			FileAttachment attachment = (FileAttachment)entity.getAttachment(fileId);
 			if (attachment != null) {
 				response.sendRedirect(WebUrlUtil.getFileUrl(WebKeys.ACTION_READ_FILE, attachment));
 				return;
 			}
 			
 		}
 		//It is ok to see this request, get the url to use for this request
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		if (Validator.isNotNull(binderId)) url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		if (Validator.isNotNull(entryId)) url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		if (Validator.isNotNull(entryTitle)) url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
		url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType.name());
		if (Validator.isNotNull(newTab)) url.setParameter(WebKeys.URL_NEW_TAB, newTab);
		
		if (entityType.equals("") && !binderId.equals("")) {
			try {
				Binder binder = getBinderModule().getBinder(new Long(binderId));
				entityType = binder.getEntityType();
			} catch(Exception e) {}
		}
		if (entityType.equals(EntityIdentifier.EntityType.workspace) || 
				entityType.equals(EntityIdentifier.EntityType.user)) {
			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folder)) {
			url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) {
			String displayStyle = user.getDisplayStyle();
			if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) || 
					(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(displayStyle) &&
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
				url.setParameter(WebKeys.URL_ACTION, "view_folder_entry");
			} else {
				try {
					getBinderModule().getBinder(new Long(binderId));
					url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");
				} catch (AccessControlException ac) {
					url.setParameter(WebKeys.URL_ACTION, "view_folder_entry");					
				}
			}
		}
		String sUrl = url.toString();
		if (!fileId.equals("") && !binderId.equals("") && !entityType.equals("")) {
			sUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?" +
				WebKeys.URL_BINDER_ID + "=" + binderId +
				"&" + WebKeys.URL_ENTITY_TYPE + "=" + entityType;
			if (!entryId.equals("")) sUrl += "&" + WebKeys.URL_ENTRY_ID + "=" + entryId;
			sUrl += "&" + WebKeys.URL_FILE_ID + "=" + fileId;
		}
 		
    	if(logger.isDebugEnabled()) {
    		logger.debug("Permalink followed: " + sUrl);
    	}
    	response.sendRedirect(sUrl);

	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		final String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		final String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String newTab= PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");

		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		if (!binderId.equals("")) url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		if (!entryId.equals("")) url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		if (!entryTitle.equals("")) url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
		if (!entityType.equals("")) url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
		if (!fileId.equals("")) url.setParameter(WebKeys.URL_FILE_ID, fileId);
		if (!newTab.equals("")) url.setParameter(WebKeys.URL_NEW_TAB, newTab);
		
    	Map<String,Object> model = new HashMap<String,Object>();
		model.put(WebKeys.URL, url.toString());
		
		User user = null;
		Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			if (!zoneId.equals("")) user = AccessUtils.getZoneGuestUser(new Long(zoneId));
			if (user == null || binderId.equals("") || 
					!getBinderModule().checkAccess(new Long(binderId), user)) {
				//User must log in to see this
				BinderHelper.setupStandardBeans(this, request, response, model);
	 	    	return new ModelAndView("forum/login_please", model);
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
					BinderHelper.setupStandardBeans(this, request, response, model);
					return new ModelAndView("forum/login_please", model);
				}
			}
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
	 		
			//See if the user has access to the item being requested
			if (!binderId.equals("")) {
				try {
					//See if this user can access the binder
					Binder binder = getBinderModule().getBinder(new Long(binderId));
					model.put(WebKeys.BINDER, binder);
					if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
						//See if the user can access the entry, too
						getFolderModule().getEntry(new Long(binderId), new Long(entryId));
					}
				} catch(AccessControlException ac) {
					//Set up the standard beans
					BinderHelper.setupStandardBeans(this, request, response, model, new Long(binderId));
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						//Access is not allowed
						return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
					} else {
						//Please log in
						return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
					}
				}
			}
		}
		
		//The user is allowed to see this, go redirect to the url	
    	return new ModelAndView("forum/login_return", model);
	}

}
