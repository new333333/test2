/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.profile;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ProfilesBinderHelper;
import org.kablink.teaming.web.util.Tabs;
import org.springframework.web.portlet.ModelAndView;


public class ListProfilesController extends   SAbstractController {
	@SuppressWarnings("unchecked")
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
        User user = RequestContextHolder.getRequestContext().getUser();
		response.setRenderParameters(request.getParameterMap());
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put("displayStyle", PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getId(), new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
						PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,"", false));
				response.setRenderParameter(WebKeys.URL_NEW_TAB, "1");
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_PAGE_INFO)) {
			//Saves the folder page informaton when the user clicks on the page link			
			String pageStartIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_START_INDEX, "0");
			if (pageStartIndex.equals("")) pageStartIndex = "0";
			Tabs.TabEntry tab = Tabs.getTabs(request).getTab(binderId);
			Map tabData = tab.getData();
			Integer pageSI = Integer.valueOf(pageStartIndex);
			if (pageSI < 0) pageSI = 0;
			tabData.put(Tabs.PAGE, pageSI);			
			tab.setData(tabData);
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_GOTOPAGE_INFO)) {
			//Saves the folder page informaton when the user enters the page number in the go to page field
			String pageGoToIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_GOTOPAGE_INDEX, "");
			
			Tabs.TabEntry tab = Tabs.getTabs(request).getTab(binderId);
			Map tabData = tab.getData();
			Integer recordsPerPage = (Integer) tabData.get(Tabs.RECORDS_IN_PAGE);
			if (null == recordsPerPage) {
				recordsPerPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
				tabData.put(Tabs.RECORDS_IN_PAGE, recordsPerPage);
			}
					
			int intGoToPageIndex = new Integer(pageGoToIndex).intValue();
			int intRecordsPerPage = recordsPerPage.intValue();
			int intPageStartIndex = (intGoToPageIndex - 1) * intRecordsPerPage;
			tabData.put(Tabs.PAGE, new Integer(intPageStartIndex));			
			tab.setData(tabData);
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_PAGE)) {
			//Changes the number or records to be displayed in a page
			//Getting the new entries per page
			String newEntriesPerPage = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_ENTRIES_PER_PAGE, "");
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
			response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
		}
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return prepBeans(request, BinderHelper.CommonPortletDispatch(this, request, response));
		
 		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		HashMap model = new HashMap();

		if (op.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
			return prepBeans(request, new ModelAndView(BinderHelper.getViewListingJsp(this, BinderHelper.getViewType(this, binderId)), model));
		} 
		Binder binderObj = getBinderModule().getBinder(binderId);
		if (op.equals(WebKeys.OPERATION_VIEW_ENTRY)) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (!entryId.equals("")) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
				request.setAttribute("ssLoadEntryId", entryId);			
			}
		} else {
			getReportModule().addAuditTrail(AuditType.view, binderObj);
		}
		boolean showTrash = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_TRASH, false);
		return prepBeans(request, ProfilesBinderHelper.setupProfilesBinderBeans(this, binderId, request, response, showTrash));
	}
	
	/*
	 * Ensures the beans in the ModelAndView are ready to go.
	 */
	private static ModelAndView prepBeans(RenderRequest request, ModelAndView mv) {
		return GwtUIHelper.cacheToolbarBeans(request, mv);
	}
}
