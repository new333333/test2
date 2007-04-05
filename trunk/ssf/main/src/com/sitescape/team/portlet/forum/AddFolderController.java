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
package com.sitescape.team.portlet.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
/**
 * @author Janet McCann
 *
 */
public class AddFolderController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			Long cfgType = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
			Long newId = getAdminModule().addBinderFromTemplate(cfgType, binderId, 
						PortletRequestUtils.getStringParameter(request, "title", ""), null);
			
			setupViewBinder(response, newId);
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder); 
		model.put(WebKeys.OPERATION, operation);

		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			List result = getAdminModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		}
	
		return new ModelAndView(WebKeys.VIEW_ADD_BINDER, model);
	}

	protected void setupViewBinder(ActionResponse response, Long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());		
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		} else {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		}
	}
}


