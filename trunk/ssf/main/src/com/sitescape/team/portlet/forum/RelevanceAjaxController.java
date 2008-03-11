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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.RelevanceDashboardHelper;
import com.sitescape.team.web.util.WebHelper;
/**
 * @author Peter Hurley
 *
 */
public class RelevanceAjaxController  extends SAbstractControllerRetry {
	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER)) {
				ajaxSaveTrackThisBinder(request, response, "add");
			} else if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER_DELETE)) {
				ajaxSaveTrackThisBinder(request, response, "delete");
			} else if (op.equals(WebKeys.OPERATION_SHARE_THIS_BINDER)) {
				ajaxSaveShareThisBinder(request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);

			return new ModelAndView("forum/fetch_url_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_GET_RELEVANCE_DASHBOARD)) {
			return ajaxGetRelevanceDashboard(request, response);
		} else if (op.equals(WebKeys.OPERATION_SHARE_THIS_BINDER)) {
			if (formData.containsKey("okBtn")) {
				Map model = new HashMap();
				return new ModelAndView("forum/close_window", model);
			} else {
				return ajaxShareThisBinder(this, request, response);
			}
		} else if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER)) {
			response.setContentType("text/xml");
			Map model = new HashMap();
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
			Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
			model.put(WebKeys.NAMESPACE, namespace);
			if (entryId==null) {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				return new ModelAndView("forum/relevance_dashboard/track_this_item_return", model);			
			} else {
				FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
				model.put(WebKeys.ENTRY, entry);
				return new ModelAndView("forum/relevance_dashboard/track_this_item_return", model);
			}
		}

		return new ModelAndView("forum/fetch_url_return");
	} 	
	
	private void ajaxSaveTrackThisBinder(ActionRequest request, 
			ActionResponse response, String type) throws Exception {
		//The list of tracked binders and shared binders are kept in the user' user workspace user folder properties
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		Long userWorkspaceId = user.getWorkspaceId();
		if (binder != null && userWorkspaceId != null) {
			UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), userWorkspaceId);
			Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
			if (relevanceMap == null) relevanceMap = new HashMap();
			List trackedBinders = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
			if (trackedBinders == null) {
				trackedBinders = new ArrayList();
				relevanceMap.put(ObjectKeys.RELEVANCE_TRACKED_BINDERS, trackedBinders);
			}
			List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
			if (trackedPeople == null) {
				trackedPeople = new ArrayList();
				relevanceMap.put(ObjectKeys.RELEVANCE_TRACKED_PEOPLE, trackedPeople);
			}
			if (type.equals("add")) {
				if (!trackedBinders.contains(binderId)) trackedBinders.add(binderId);
				if (binder.getEntityType().equals(EntityType.workspace) && 
						binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
					//This is a user workspace, so also track this user
					if (!trackedPeople.contains(binder.getOwnerId())) trackedPeople.add(binder.getOwnerId());
				}
			} else if (type.equals("delete")) {
				if (trackedBinders.contains(binderId)) trackedBinders.remove(binderId);
				if (binder.getEntityType().equals(EntityType.workspace) && 
						binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
					//This is a user workspace, so also track this user
					if (trackedPeople.contains(binder.getOwnerId())) trackedPeople.remove(binder.getOwnerId());
				}
			}
			//Save the updated list
			getProfileModule().setUserProperty(user.getId(), userWorkspaceId, 
					ObjectKeys.USER_PROPERTY_RELEVANCE_MAP, relevanceMap);
		}
	}
	
	private void ajaxSaveShareThisBinder(ActionRequest request, 
			ActionResponse response) throws Exception {
		//TODO Add more code to store the share request
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		DefinableEntity entity;
		if (entryId == null) {
			entity = getBinderModule().getBinder(binderId);
		} else {
			entity = getFolderModule().getEntry(binderId, entryId);
		}
		Set<Long> ids = new HashSet();
		ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
		ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
		Set<Long>teams = new HashSet();
		for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
			Map.Entry e = (Map.Entry)iter.next();
			String key = (String)e.getKey();
			if (key.startsWith("cb_")) {
				try {
					teams.add(Long.valueOf(key.substring(3)));
				} catch (Exception ignoreIt) {}
			}
		}

		getProfileModule().setShares(entity, ids, teams);
	}
	
	private ModelAndView ajaxGetRelevanceDashboard(RenderRequest request, 
			RenderResponse response) throws Exception {
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
		Map model = new HashMap();
		model.put(WebKeys.TYPE, type);
		setupDashboardBeans(this, type, request, response, model);
		return new ModelAndView("forum/relevance_dashboard/ajax", model);
	}
	
	private ModelAndView ajaxShareThisBinder(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (binderId != null) model.put(WebKeys.BINDER_ID, binderId.toString());
		if (entryId != null) model.put(WebKeys.ENTRY_ID, entryId.toString());
		RelevanceDashboardHelper.setupMyTeamsBeans(bs, model);
		return new ModelAndView("forum/relevance_dashboard/share_this_item", model);
	}
	
	private void setupDashboardBeans(AllModulesInjected bs, String type, RenderRequest request, 
			RenderResponse response, Map model) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
		model.put(WebKeys.NAMESPACE, namespace);
        Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, binderId, type, model);
	}
	
}
