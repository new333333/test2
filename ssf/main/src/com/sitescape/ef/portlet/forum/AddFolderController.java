package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class AddFolderController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (operation.equals("") && formData.containsKey("_operation")) {
			operation = ((String[])formData.get("_operation"))[0];
		}
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			if (entryType.equals("") && formData.containsKey("_definitionId")) {
				entryType = ((String[])formData.get("_definitionId"))[0];
			}
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			} 
			MapInputData inputData = new MapInputData(formData);

			if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
				getFolderModule().addFolder(binderId, entryType, inputData, fileMap);
			} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
				getWorkspaceModule().addFolder(binderId, entryType, inputData, fileMap);				
			} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
				getWorkspaceModule().addWorkspace(binderId, entryType, inputData, fileMap);				
			}
			setupViewBinder(response, binderId, operation);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, operation);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map formData = request.getParameterMap();
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (operation.equals("") && formData.containsKey("_operation")) {
			operation = ((String[])formData.get("_operation"))[0];
		}
		String defId = "";
		if (formData.containsKey("binderDefinition")) {
			defId = ((String[])formData.get("binderDefinition"))[0];
		}
		Binder binder = null;
		Map publicBinderDefs = new HashMap();
		String itemFormPath = "//item[@name='folderForm']";
		DefinitionUtils.getDefinitions(model);
		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			binder = getFolderModule().getFolder(binderId);
			publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_FOLDER_DEFINITIONS);
			itemFormPath = "//item[@name='folderForm']";
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			binder = getWorkspaceModule().getWorkspace(binderId);				
			publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_FOLDER_DEFINITIONS);
			itemFormPath = "//item[@name='folderForm']";
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			binder = getWorkspaceModule().getWorkspace(binderId);				
			publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_WORKSPACE_DEFINITIONS);
			itemFormPath = "//item[@name='workspaceForm']";
		}
		
    	model.put(WebKeys.URL_OPERATION, operation);
		model.put(WebKeys.BINDER, binder); 
		model.put(WebKeys.PUBLIC_BINDER_DEFINITIONS, publicBinderDefs);

		DefinitionUtils.getDefinitions(binder, model);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		model.put(WebKeys.DEFINITION_ID, defId);
		
		String view = WebKeys.VIEW_ADD_BINDER_TYPE;
		if (!defId.equals("")) {
			//Make sure the requested definition is legal
			if (publicBinderDefs.containsKey(defId)) {
				DefinitionUtils.getDefinition(getDefinitionModule().getDefinition(defId), model, itemFormPath);
			} else {
				DefinitionUtils.getDefinition(null, model, itemFormPath);
			}
			view = WebKeys.VIEW_ADD_BINDER;
		}
		return new ModelAndView(view, model);
	}
	protected void setupViewBinder(ActionResponse response, Long binderId, String operation) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		} else {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		}
	}
}


