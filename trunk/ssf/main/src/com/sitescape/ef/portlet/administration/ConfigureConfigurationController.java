package com.sitescape.ef.portlet.administration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

public class ConfigureConfigurationController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("addBtn")) {
			//Get the list of workAreaOperations to be added to this new role/function
			int type = PortletRequestUtils.getIntParameter(request, "cfgType");
			String configId = getBinderModule().addConfiguration(type, PortletRequestUtils.getRequiredStringParameter(request, "cfgTitle"));
			response.setRenderParameter(WebKeys.URL_OBJECT_ID, configId);
		} else if (formData.containsKey("okBtn")) {
			//Get the function id from the form
			String configId = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_OBJECT_ID);
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
			Map updates = new HashMap();
			updates.put("definitionIds", definitions);
			updates.put("workflowIds", workflowAssociations);
			getBinderModule().modifyConfiguration(configId, updates);
			response.setRenderParameter(WebKeys.URL_OBJECT_ID, configId);
		
		} else if (formData.containsKey("deleteBtn")) {
			//Get the function id from the form
			String configId = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_OBJECT_ID);
			getBinderModule().deleteConfiguration(configId);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		String configId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OBJECT_ID);				
		if (configId != null) {
			BinderConfig config = getBinderModule().getConfiguration(configId);
		
			model.put(WebKeys.BINDER_CONFIG, config);
		
			if (config.getDefinitionType() == Definition.WORKSPACE_VIEW) {
				DefinitionHelper.getDefinitions(Definition.WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);			
			} else if (config.getDefinitionType() == Definition.PROFILE_VIEW) {
				DefinitionHelper.getDefinitions(Definition.PROFILE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				DefinitionHelper.getDefinitions(Definition.PROFILE_ENTRY_VIEW, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);			
			} else if (config.getDefinitionType() == Definition.FILE_FOLDER_VIEW) {
					DefinitionHelper.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
					DefinitionHelper.getDefinitions(Definition.FILE_ENTRY_VIEW, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);				
			} else {
					DefinitionHelper.getDefinitions(Definition.FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
					DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);				 
			}
			Map defs = (Map)model.get(WebKeys.PUBLIC_BINDER_DEFINITIONS);
			Map defsMap = new HashMap();
			model.put(WebKeys.FOLDER_DEFINTION_MAP, defsMap);
			List defIds = config.getDefinitionIds();
			for (int i=0; i<defIds.size(); ++i) {
				String id = (String)defIds.get(i);
				if (defs.containsKey(id)) {
					defsMap.put(id, defs.get(id));
				}
			}
			if (!defIds.isEmpty()) {
				if (defsMap.containsKey(defIds.get(0))) {
					model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, defsMap.get(defIds.get(0)));
				}
			}
			defsMap = new HashMap();			
			model.put(WebKeys.ENTRY_DEFINTION_MAP, defsMap);
			defs = (Map)model.get(WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS);			
			for (int i=0; i<defIds.size(); ++i) {
				String id = (String)defIds.get(i);
				if (defs.containsKey(id)) {
					defsMap.put(id, defs.get(id));
				}
			}
			
			//DefinitionHelper.getDefinitions(config, model);
			DefinitionHelper.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
		} else {
			model.put(WebKeys.BINDER_CONFIGS, getBinderModule().getConfigurations());
		}
		return new ModelAndView("administration/configureConfiguration", model);
		
	}
}
