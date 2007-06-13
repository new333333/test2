/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.profile;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.profile.ProfileModule.ProfileOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Toolbar;

public class ViewEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Map model = new HashMap();	
 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
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
		Principal entry = getProfileModule().getEntry(binderId, entryId);
		
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		//Get the definition used to view this entry
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		} else {
			//Set up the definition used to show this profile entry
			if (!DefinitionHelper.getDefinition(entryDef, model, "//item[@name='profileEntryView']")) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
		}
		//	Build the toolbar array
		Toolbar toolbar = new Toolbar();
		PortletURL url;
		if (getProfileModule().testAccess(entry, ProfileOperation.modifyEntry)) {
			//	The "Modify" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
		}
	
    
		//	The "Delete" menu
		if (getProfileModule().testAccess(entry, ProfileOperation.deleteEntry)) {
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
		}
    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
//		if (operation.equals("buddy")) 
//			return new ModelAndView("presence/view_entry", model);
		return new ModelAndView(viewPath, model);
	}	


	
} 