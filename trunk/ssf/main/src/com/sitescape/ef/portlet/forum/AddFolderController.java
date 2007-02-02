package com.sitescape.ef.portlet.forum;

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
						PortletRequestUtils.getStringParameter(request, "title", ""));
			
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

		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			getFolderModule().checkAccess((Folder)binder, "addFolder");
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().createDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			if (binder.getEntityIdentifier().getEntityType().name().equals(EntityType.folder)) {
				getFolderModule().checkAccess((Folder)binder, "addFolder");
			} else {
				getWorkspaceModule().checkAccess((Workspace)binder, "addFolder");
			}
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().createDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			getWorkspaceModule().checkAccess((Workspace)binder, "addWorkspace");
			List result = getAdminModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().createDefaultTemplate(Definition.WORKSPACE_VIEW));	
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


