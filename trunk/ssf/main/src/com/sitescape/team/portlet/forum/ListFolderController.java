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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.ListFolderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WorkspaceTreeHelper;

/**
 * @author Peter Hurley
 *
 */
public class ListFolderController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		response.setRenderParameters(request.getParameterMap());
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getId(), new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
		} else if (op.equals(WebKeys.OPERATION_SET_DISPLAY_DEFINITION)) {
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
					PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,""));
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_COLUMNS)) {
			if (formData.containsKey("okBtn")) {
				Map columns = new LinkedHashMap();
				String[] columnNames = new String[] {"number", "title", "comments", "size", "download", "html", "state", "author", "date", "rating"};
				for (int i = 0; i < columnNames.length; i++) {
					columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
				}
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (me.getKey().toString().startsWith("customCol_", 0)) {
						String colName = me.getKey().toString().substring(10, me.getKey().toString().length());
						columns.put(colName, "on");
					}
				}
				
				//See if this request was to set the folder default
				if (formData.containsKey("setFolderDefaultColumns")) {
					Binder binder = getBinderModule().getBinder(binderId);
					if (getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
						binder.setProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS, columns);
					}
				}
				
				Map values = new HashMap();
				values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, columns);
				//Reset the column positions to the default
			   	values.put(WebKeys.FOLDER_COLUMN_POSITIONS, "");
				getProfileModule().setUserProperties(user.getId(), binderId, values);
				response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
			} else if (formData.containsKey("defaultBtn")) {
				Map values = new HashMap();
				values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, null);
				//Reset the column positions to the default
				values.put(WebKeys.FOLDER_COLUMN_POSITIONS, "");
				//Reset the Sort Order information in the User Properties
				values.put(ObjectKeys.SEARCH_SORT_BY, "");
				values.put(ObjectKeys.SEARCH_SORT_DESCEND, "");
				getProfileModule().setUserProperties(user.getId(), binderId, values);
				response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
			}
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO)) {
			//Saves the folder sort information

			String folderSortBy = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_BY, "");
			String folderSortDescend = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_DESCEND, "");
			
			//Saving the Sort Order information in the User Properties
			Map values = new HashMap();
			values.put(ObjectKeys.SEARCH_SORT_BY, folderSortBy);
			values.put(ObjectKeys.SEARCH_SORT_DESCEND, folderSortDescend);
			getProfileModule().setUserProperties(user.getId(), binderId, values);
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_PAGE_INFO)) {
			//Saves the folder page informaton when the user clicks on the page link			
			String pageStartIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_START_INDEX, "");
			Tabs.TabEntry tab = Tabs.getTabs(request).getTab(binderId);
			if (tab != null) {
				Map tabData = tab.getData();
				tabData.put(Tabs.PAGE, new Integer(pageStartIndex));			
				tab.setData(tabData);
				response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
			}
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_GOTOPAGE_INFO)) {
			//Saves the folder page informaton when the user enters the page number in the go to page field
			String pageGoToIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_GOTOPAGE_INDEX, "");
			
			Tabs.TabEntry tab = Tabs.getTabs(request).getTab(binderId);
			if (tab != null) {
				Map tabData = tab.getData();
				Integer recordsPerPage = (Integer) tabData.get(Tabs.RECORDS_IN_PAGE);
						
				int intGoToPageIndex = new Integer(pageGoToIndex).intValue();
				int intRecordsPerPage = recordsPerPage.intValue();
				int intPageStartIndex = (intGoToPageIndex - 1) * intRecordsPerPage;
				tabData.put(Tabs.PAGE, new Integer(intPageStartIndex));			
				tab.setData(tabData);
				response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
			}
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_PAGE)) {
			//Changes the number or records to be displayed in a page
			//Getting the new entries per page
			String newEntriesPerPage = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_ENTRIES_PER_PAGE, "");
			//Saving the Sort Order information in the User Properties
			//Changing the user folder paging information from folder/binder level to the user level 
			//getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
		} else if (op.equals(WebKeys.OPERATION_GO_TO_ENTRY)) {
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
			String entryNumber = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_GOTOENTRY, "");
			//Get the entryId from the entry number and add it to the request
			Long entryId = null;
			if (entryId != null) response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		}

		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		String displayType = BinderHelper.getDisplayType(request);
		if (request.getWindowState().equals(WindowState.NORMAL) &&
				!BinderHelper.WORKAREA_PORTLET.equals(displayType)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		//If no binder, Default to the user's workspace
		if (binderId == null) binderId = user.getWorkspaceId();
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		Long parentBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_PARENT_ID);
		if (parentBinderId != null && binderId != null) {
			try {
				getBinderModule().getBinder(binderId);
			} catch(NoBinderByTheIdException e) {
				//The binder no longer exists, show the parent binder instead
				request.setAttribute(WebKeys.URL_BINDER_ID, parentBinderId);
				Binder parentBinder = getBinderModule().getBinder(parentBinderId);
				if (parentBinder.getEntityType().name().equals(EntityIdentifier.EntityType.workspace.name())) {
					return WorkspaceTreeHelper.setupWorkspaceBeans(this, parentBinderId, request, response);
				}
				binderId = parentBinderId;
			}
			
		} else if (binderId == null) {
			binderId = (Long) portletSession.getAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, PortletSession.APPLICATION_SCOPE);
		}
		if (binderId == null) {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_PARENT_ID);
		}

		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				if (binder.getEntityType().name().equals(EntityIdentifier.EntityType.workspace.name())) {
					return WorkspaceTreeHelper.setupWorkspaceBeans(this, binderId, request, response);					}
			} catch(NoBinderByTheIdException e) {
			}
			
		} else {
			binderId = (Long) portletSession.getAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, PortletSession.APPLICATION_SCOPE);
		}
		return ListFolderHelper.BuildFolderBeans(this, request, response, binderId);
		
	}
}

