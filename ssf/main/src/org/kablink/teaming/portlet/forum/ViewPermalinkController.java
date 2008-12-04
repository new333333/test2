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
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


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
		//binderId is not longer required on all entries
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String fileName= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_NAME, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.none;
		DefinableEntity entity = null;
		try {
			entityType = EntityIdentifier.EntityType.valueOf(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, ""));
		} catch(Exception ignore) {};
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
 		if (Validator.isNotNull(fileId)) return getFileUrlById(request, entityType, binderId, entryId, fileId);
 		if (Validator.isNotNull(fileName)) return getFileUrlByName(request, entityType, binderId, entryId, fileName);

		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			if (Validator.isNotNull(entryId)) {
				entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
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
			url.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, "full");
		} else	if (entityType.isBinder() || entityType.equals(EntityIdentifier.EntityType.none)) {
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
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
			//permalinks are meant for the use workspace
	 		if (entryId.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER) || entryId.equals(WebKeys.URL_USER_ID_PLACE_HOLDER)) {  
	 			try {
	 				entity = getProfileModule().getProfileBinder();
	 			} catch  (AccessControlException ac) {
					Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
					User user = AccessUtils.getZoneSuperUser(zoneId);

	 				entity = (DefinableEntity)RunasTemplate.runas(new RunasCallback () {
	 					public Object doAs() {
	 						return getProfileModule().getProfileBinder();
	 					}
	 				}, user);
	 			}
	 			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
	 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
	 			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_USER_ID_PLACE_HOLDER);
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
	protected String getFileUrlById(ActionRequest request, EntityIdentifier.EntityType entityType, String binderId, String entryId, String fileId) {
		DefinableEntity entity;
		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
		} else if (entityType.isPrincipal()) {
 			entity = getProfileModule().getEntry(Long.valueOf(entryId));
		} else	{
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
 		} 
		FileAttachment attachment = (FileAttachment)entity.getAttachment(fileId);
		if (attachment != null) {
			return WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, attachment);
		} else {
			//use old v1 style
			Long entityId=null;
			if (entity.getEntityType().isBinder()) {
				binderId = entity.getId().toString();
			} else {
				binderId = entity.getParentBinder().getId().toString() ;
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
	protected String getFileUrlByName(ActionRequest request, EntityIdentifier.EntityType entityType, String binderId, String entryId, String fileName) {
		DefinableEntity entity;
		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
		} else if (entityType.isPrincipal()) {
 			entity = getProfileModule().getEntry(Long.valueOf(entryId));
		} else	{
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
 		} 
		return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(request), WebKeys.ACTION_READ_FILE, entity, fileName);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		String fileName= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_NAME, "");

		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		if (Validator.isNotNull(binderId)) url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		if (Validator.isNotNull(entryId)) url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		if (Validator.isNotNull(entryTitle)) url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
		if (Validator.isNotNull(entityType)) url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
		if (Validator.isNotNull(fileId)) url.setParameter(WebKeys.URL_FILE_ID, fileId);
		if (Validator.isNotNull(fileName)) url.setParameter(WebKeys.URL_FILE_NAME, fileName);
		model.put(WebKeys.URL, url.toString());
		if (!"true".equals(PortletRequestUtils.getStringParameter(request, "accessException"))) {
			return new ModelAndView(WebKeys.VIEW_LOGIN_RETURN, model);
		}
		
		User user = null;
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			//User must log in to see this
			BinderHelper.setupStandardBeans(this, request, response, model);
			return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
	 		
			//Set up the standard beans
			BinderHelper.setupStandardBeans(this, request, response, model);
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
