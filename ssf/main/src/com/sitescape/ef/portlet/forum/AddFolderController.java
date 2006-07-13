package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
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
		if (operation.equals("")) operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String defId = PortletRequestUtils.getStringParameter(request, "binderDefinition", "");
		Binder binder = getBinderModule().getBinder(binderId);
    	model.put(WebKeys.URL_OPERATION, operation);
		model.put(WebKeys.BINDER, binder); 

		DefinitionUtils.getDefinitions(binder, model);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		model.put(WebKeys.DEFINITION_ID, defId);
		
		String itemFormPath = "//item[@name='folderForm']";
		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.FILE_FOLDER_VIEW)) {
				DefinitionUtils.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			} else {
				DefinitionUtils.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				DefinitionUtils.getDefinitions(Definition.FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			}
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			DefinitionUtils.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			DefinitionUtils.getDefinitions(Definition.FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			DefinitionUtils.getDefinitions(Definition.WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);			
			itemFormPath = "//item[@name='workspaceForm']";					
		}
		
		
		String view = WebKeys.VIEW_ADD_BINDER_TYPE;
		if (!Validator.isNull(defId)) {
			Map publicBinderDefs = (Map)model.get(WebKeys.PUBLIC_BINDER_DEFINITIONS);
			//Make sure the requested definition is legal
			if (publicBinderDefs.containsKey(defId)) {
				Definition def = (Definition)publicBinderDefs.get(defId);
				if (def.getType() == Definition.FILE_FOLDER_VIEW)
					itemFormPath = "//item[@name='fileFolderForm']";
				DefinitionUtils.getDefinition(def, model, itemFormPath);
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


