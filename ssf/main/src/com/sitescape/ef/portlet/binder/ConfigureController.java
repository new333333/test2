package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ConfigureController extends AbstractBinderController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			List definitions = new ArrayList();
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
			Map workflowAssociations = new HashMap();
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
			getBinderModule().setConfiguration(binderId, definitions, workflowAssociations);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		} else if (formData.containsKey("inheritanceBtn")) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getBinderModule().setConfiguration(binderId, inherit);
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
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		if (binder.getEntityIdentifier().getEntityType().getValue() == EntityType.workspace.getValue()) {
			DefinitionUtils.getDefinitions(Definition.WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);			
		} else if (binder.getEntityIdentifier().getEntityType().getValue() == EntityType.profiles.getValue()) {
			DefinitionUtils.getDefinitions(Definition.PROFILE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			DefinitionUtils.getDefinitions(Definition.PROFILE_ENTRY_VIEW, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);			
		} else {
			if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.FILE_FOLDER_VIEW)) {
				DefinitionUtils.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				DefinitionUtils.getDefinitions(Definition.FILE_ENTRY_VIEW, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);				
			} else {
				DefinitionUtils.getDefinitions(Definition.FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				DefinitionUtils.getDefinitions(Definition.COMMAND, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);				
			} 
		}
		DefinitionUtils.getDefinitions(binder, model);
		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);

		return new ModelAndView(WebKeys.VIEW_CONFIGURE, model);
	}

}
