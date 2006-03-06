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

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public abstract class AbstractConfigureController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
	    	List definitions = new ArrayList();
	    	//Get the default binder view
	    	String defBinderId = PortletRequestUtils.getStringParameter(request, "binderDefinition", "");
			String[] defBinderIds = PortletRequestUtils.getStringParameters(request, "binderDefinitions");
			if (!Validator.isNull(defBinderId)) {
				//The default binder view is always the first one in the list
				if (defBinderIds != null) {
					for (int i = 0; i < defBinderIds.length; i++) {
						String defId = defBinderIds[i];
						if (!Validator.isNull(defId) && defId.toString().equals(defBinderId.toString())) {
							definitions.add(defBinderId);
							break;
						}
					}
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
			getBinderModule().modifyConfiguration(binderId, definitions, workflowAssociations);
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setResponseOnClose(response, binderId);
		} else
			response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(binderId);
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER_WORKFLOW_ASSOCIATIONS, binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS));
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		DefinitionUtils.getDefinitions(model);
		DefinitionUtils.getDefinitions(binder, model);
		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
	
		return new ModelAndView(WebKeys.VIEW_CONFIGURE, model);
	}
	protected abstract void setResponseOnClose(ActionResponse responose, Long binderId);


}
