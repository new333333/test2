/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portlet.forum.ViewController;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.Toolbar;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 *  
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class ViewEntryController extends SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		Map model = new HashMap();	
		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		if (binderId == null) binderId = getProfileModule().getProfileBinderId();
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String entryViewType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_TYPE, "entryView");
		String entryViewStyle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE, "");
		String entryViewStyle2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE2, "");
		String displayType = BinderHelper.getDisplayType(request);
		if (entryViewStyle.equals("")) {
			if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) &&
					!ViewController.WIKI_PORTLET.equals(displayType)) entryViewStyle = WebKeys.URL_ENTRY_VIEW_STYLE_FULL;
		}
		BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		Map formData = request.getParameterMap();
		String viewType = BinderHelper.getViewType(this, binderId);
		String viewPath = BinderHelper.getViewListingJsp(this, viewType);
		if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			model = new HashMap();
			model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
			return new ModelAndView(viewPath, model);
		}
		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
		Principal entry = getProfileModule().getEntry(entryId);
		
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());

 		model.put(WebKeys.TABS, Tabs.getTabs(request));

		//Let the jsp know what style to show the entry in 
		//  (popup has no navbar header, inline has no navbar and no script tags, full has a navbar header)
		model.put(WebKeys.ENTRY_VIEW_STYLE, entryViewStyle);
		model.put(WebKeys.ENTRY_VIEW_STYLE2, entryViewStyle2);
		
		//Get the definition used to view this entry
		if (entry.getEntryDefId() == null) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		} else {
			//Set up the definition used to show this profile entry
			if (!DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='profileEntryView' or @name='groupEntryView']")) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
		}
		//	Build the toolbar array
		Toolbar toolbar = new Toolbar();
		PortletURL url;
		if (getProfileModule().testAccess(entry, ProfileOperation.modifyEntry)) {
			//	The "Modify" menu
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url, qualifiers);
		}
	
		//	The "Disable/Enable" menu
		if (getProfileModule().testAccess(entry, ProfileOperation.deleteEntry)) {
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			if (entry.isDisabled()) {
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ENABLE);
			} else {
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DISABLE);
			}
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			if (entry.isDisabled()) {
				//toolbar.addToolbarMenu("3_disable", NLT.get("toolbar.enable"), url, qualifiers);
			} else {
				//toolbar.addToolbarMenu("3_disable", NLT.get("toolbar.disable"), url, qualifiers);
			}
		}
    
		//	The "Delete" menu
		if (getProfileModule().testAccess(entry, ProfileOperation.deleteEntry)) {
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			// DRF (20150105):  With the GWT rewrite of the profile and
			//    user management pages, we no longer allow user profiles
			//    to be deleted from this page.
			//toolbar.addToolbarMenu("4_delete", NLT.get("toolbar.delete"), url, qualifiers);
		}
    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
//		if (operation.equals("buddy")) 
//			return new ModelAndView("presence/view_entry", model);
		return new ModelAndView(viewPath, model);
	}	
} 
