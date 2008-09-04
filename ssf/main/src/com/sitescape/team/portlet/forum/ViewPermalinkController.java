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
import com.sitescape.team.domain.NoBinderByTheIdException;
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
	public void handleActionRequestAfterValidation(final ActionRequest request, ActionResponse response) throws Exception {
		User user = null;
		String sUrl;
		try {
			if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
				Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
				user = AccessUtils.getZoneGuestUser(zoneId);
				if (user == null) {
				//	User must log in to see this
					response.setRenderParameters(request.getParameterMap());
					return;
				}
				sUrl = (String)RunasTemplate.runas(new RunasCallback() {
					public Object doAs() {
						return processRequest(request);
					}
				}, user);
			} else {
				user = RequestContextHolder.getRequestContext().getUser();
				sUrl = processRequest(request);
			}
			response.sendRedirect(sUrl);
		} catch  (AccessControlException ac) {
			//User must log in to see this
			response.setRenderParameters(request.getParameterMap());
			response.setRenderParameter("accessException", "true");
			return;
		} 
	}
	protected String processRequest(ActionRequest request) {
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.none;
		DefinableEntity entity = null;
		try {
			entityType = EntityIdentifier.EntityType.valueOf(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, ""));
		} catch(Exception ignore) {};
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);

		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			if (Validator.isNotNull(entryId)) {
				entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
		 		if (Validator.isNotNull(fileId)) return getFileUrl(request, entity, fileId);
				url.setParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			} else {
				entity = getBinderModule().getBinder(Long.valueOf(binderId));				
				url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
			}
			User user = RequestContextHolder.getRequestContext().getUser();
			String displayStyle = user.getDisplayStyle();
			if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) || 
					(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(displayStyle) &&
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
				url.setParameter(WebKeys.URL_ACTION, "view_folder_entry");
			} else {
				url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");
			}
		} else	if (entityType.isBinder() || entityType.equals(EntityIdentifier.EntityType.none)) {
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
	 		if (Validator.isNotNull(fileId)) return getFileUrl(request, entity, fileId);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			entityType = entity.getEntityType();
			if (entityType.equals(EntityIdentifier.EntityType.workspace)) {
				url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
			} else if (entityType.equals(EntityIdentifier.EntityType.profiles)) {
				url.setParameter(WebKeys.URL_ACTION, "view_profile_listing");
			} else {
				url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");				
			}
			
		} else if (entityType.isPrincipal()) {
	 		if (Validator.isNotNull(fileId)) {
	 			entity = getProfileModule().getEntry(Long.valueOf(entryId));
	 			return getFileUrl(request, entity, fileId);
	 		}
	 		if (entryId.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) {  
	 			entity = getBinderModule().getBinder(Long.valueOf(binderId));
	 			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
	 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
	 			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
	 		} else {
	 			Long workspaceId  = getProfileModule().getEntryWorkspaceId(Long.valueOf(entryId));
	 			entity = getBinderModule().getBinder(workspaceId);
	 			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
	 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
	 		}
		} 
		
		String sUrl = url.toString();
    	if(logger.isDebugEnabled()) {
    		logger.debug("Permalink followed: " + sUrl);
    	}
    	return sUrl;
	}
	protected String getFileUrl(ActionRequest request, DefinableEntity entity, String fileId) {
		FileAttachment attachment = (FileAttachment)entity.getAttachment(fileId);
		if (attachment != null) {
			return WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, attachment);
		} else {
			//use old v1 style
			Long binderId,entityId=null;
			if (entity.getEntityType().isBinder()) {
				binderId = entity.getId();
			} else {
				binderId = entity.getParentBinder().getId();
				entityId = entity.getId();
			}
			StringBuffer sUrl = new StringBuffer(WebUrlUtil.getServletRootURL(request)).append(WebKeys.SERVLET_VIEW_FILE).append("?");
			sUrl.append(WebKeys.URL_BINDER_ID).append("=").append(binderId);
			sUrl.append("&").append(WebKeys.URL_ENTITY_TYPE).append("=").append(entity.getEntityType().name());
			if (entityId!=null) sUrl.append("&").append(WebKeys.URL_ENTRY_ID).append("=").append(entityId);
			sUrl.append("&").append(WebKeys.URL_FILE_ID).append("=").append(fileId);
			return sUrl.toString();
		}
	
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		//The user is allowed to see this, go redirect to the url	

		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		if (!binderId.equals("")) url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		if (!entryId.equals("")) url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		if (!entryTitle.equals("")) url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
		if (!entityType.equals("")) url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
		if (!fileId.equals("")) url.setParameter(WebKeys.URL_FILE_ID, fileId);
	
		model.put(WebKeys.URL, url.toString());
		if (!"true".equals(PortletRequestUtils.getStringParameter(request, "accessException"))) {
			//this would be from a v1.0.3 permalink that didn't have actionUrl=1
			//send to action
			return new ModelAndView(WebKeys.VIEW_LOGIN_RETURN, model);
		}
		
		User user = null;
		Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			//User must log in to see this
			BinderHelper.setupStandardBeans(this, request, response, model);
			return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
	 		
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
