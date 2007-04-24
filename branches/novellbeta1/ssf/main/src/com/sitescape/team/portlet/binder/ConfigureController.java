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
package com.sitescape.team.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 *
 */
public class ConfigureController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			List definitions = new ArrayList();
			Map workflowAssociations = new HashMap();
			getDefinitions(request, definitions, workflowAssociations);
			getBinderModule().setDefinitions(binderId, definitions, workflowAssociations);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		} else if (formData.containsKey("inheritanceBtn")) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getBinderModule().setDefinitions(binderId, inherit);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, binderType);
		} else
			response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));

		setupDefinitions(binder, model);
		//Build the navigation beans
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		BinderHelper.buildNavigationLinkBeans(this, binder, model);

		return new ModelAndView(WebKeys.VIEW_CONFIGURE, model);
	}
	protected void getDefinitions(ActionRequest request, List definitions, Map workflowAssociations) {
		//	Get the default binder view
		String defBinderId = PortletRequestUtils.getStringParameter(request, "binderDefinition", "");
		String[] defBinderIds = PortletRequestUtils.getStringParameters(request, "binderDefinitions");
		if (!Validator.isNull(defBinderId)) {
			//	The default binder view is always the first one in the list
			if (defBinderIds != null) {
				definitions.add(defBinderId);
			}
		}
			
		//Add the other allowed folder views
		if (defBinderIds != null) {
			for (int i = 0; i < defBinderIds.length; i++) {
				String defId = defBinderIds[i];
				if (!Validator.isNull(defId) && !defId.toString().equals(defBinderId.toString())) {
					definitions.add(defId);
				}
			}
		}
		//Add the allowed entry types
		// and the workflow associations
		String[] defEntryIds = PortletRequestUtils.getStringParameters(request, "entryDefinition");
		if (defEntryIds != null) {
			for (int i = 0; i < defEntryIds.length; i++) {
				String defId = defEntryIds[i];
				if (!Validator.isNull(defId)) {
					definitions.add(defId);
					String wfDefId = PortletRequestUtils.getStringParameter(request, "workflow_" + defId, "");
					if (!wfDefId.equals("")) workflowAssociations.put(defId,wfDefId);
				}
			}
		}

	}
	protected void setupDefinitions(Binder binder, Map model) {

		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		EntityType binderType = binder.getEntityType();
		if (binderType.equals(EntityType.workspace)) {
			if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				DefinitionHelper.getDefinitions(Definition.USER_WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			} else {
				DefinitionHelper.getDefinitions(Definition.WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			}
		} else if (binderType.equals(EntityType.profiles)) {
			DefinitionHelper.getDefinitions(Definition.PROFILE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			DefinitionHelper.getDefinitions(Definition.PROFILE_ENTRY_VIEW, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);			
		} else {
			DefinitionHelper.getDefinitions(Definition.FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);
		}
		DefinitionHelper.getDefinitions(binder, model);
		DefinitionHelper.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
		
	}

}
