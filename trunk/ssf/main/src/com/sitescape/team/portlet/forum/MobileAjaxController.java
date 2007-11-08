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
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
/**
 * @author Peter Hurley
 *
 */
public class MobileAjaxController  extends SAbstractControllerRetry {
	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (!WebHelper.isUserLoggedIn(request)) {
			if (op.equals(WebKeys.OPERATION_MOBILE_LOGIN)) {
				//Get the name and password and then login
				String name = PortletRequestUtils.getStringParameter(request, "name", "");
				String password = PortletRequestUtils.getStringParameter(request, "password", "");
			}
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			return ajaxMobileLogin(request, response);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FOLDER)) {
			return ajaxMobileShowFolder(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
			return ajaxMobileShowEntry(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_LOGIN)) {
			return ajaxMobileLogin(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
			return ajaxMobileFrontPage(request, response);
		}
		return ajaxMobileFrontPage(request, response);
	} 


	private ModelAndView ajaxMobileLogin(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		return new ModelAndView("mobile/show_login_form", model);
	}
	
	
	private ModelAndView ajaxMobileFrontPage(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		//This is the portlet view; get the configured list of folders to show
		String[] mobileBinderIds = (String[])userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS);

		//Build the jsp bean (sorted by folder title)
		List<Long> binderIds = new ArrayList<Long>();
		if (mobileBinderIds != null) {
			for (int i = 0; i < mobileBinderIds.length; i++) {
				binderIds.add(new Long(mobileBinderIds[i]));
			}
		}
		model.put(WebKeys.MOBILE_BINDER_LIST, getBinderModule().getBinders(binderIds));
		
		Map unseenCounts = new HashMap();
		unseenCounts = getFolderModule().getUnseenCounts(binderIds);
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
		
		return new ModelAndView("mobile/show_front_page", model);
	}

	private ModelAndView ajaxMobileShowFolder(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		Map options = new HashMap();		
		String viewType = "";
		Map folderEntries = null;
		
		String view = null;
		if (binder== null) {
			return ajaxMobileFrontPage(request, response);
		} 
		model.put(WebKeys.BINDER, binder);
		folderEntries = getFolderModule().getEntries(binderId, options);
		model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		if (folderEntries != null) {
			model.put(WebKeys.FOLDER_ENTRIES, (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
		}
		
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		return new ModelAndView("mobile/show_folder", model);
	}

	private ModelAndView ajaxMobileShowEntry(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		User user = RequestContextHolder.getRequestContext().getUser();
		
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
			Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
			HashMap entryAccessMap = BinderHelper.getEntryAccessMap(this, model, entry);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryBlogView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
			SeenMap seen = getProfileModule().getUserSeenMap(null);
			model.put(WebKeys.SEEN_MAP, seen);
			List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
			if (replies != null)  {
				accessControlMap.put(entry.getId(), entryAccessMap);
				for (int i=0; i<replies.size(); i++) {
					FolderEntry reply = (FolderEntry)replies.get(i);
					accessControlMap.put(reply.getId(), entryAccessMap);
				}
			}
			if (!seen.checkIfSeen(entry)) { 
				//only mark top entries as seen
				getProfileModule().setSeen(null, entry);
			}
		}
		return new ModelAndView("mobile/show_entry", model);
	}	
	
}
