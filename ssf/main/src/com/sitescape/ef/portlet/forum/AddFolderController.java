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
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
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
			Long newId;
			if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
				newId = getFolderModule().addFolder(binderId, entryType, inputData, fileMap);
				setupConfigBinder(response, newId, operation);
			} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
				newId = getWorkspaceModule().addFolder(binderId, entryType, inputData, fileMap);				
				setupConfigBinder(response, newId, operation);
			} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
				newId = getWorkspaceModule().addWorkspace(binderId, entryType, inputData, fileMap);				
				setupViewBinder(response, newId, operation);
			}
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, operation);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (operation.equals("")) operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String defId = PortletRequestUtils.getStringParameter(request, "binderDefinition", "");
		Integer binderType = PortletRequestUtils.getIntParameter(request, "binderDefinitionType");
		Binder binder = getBinderModule().getBinder(binderId);
    	model.put(WebKeys.URL_OPERATION, operation);
		model.put(WebKeys.BINDER, binder); 
		if (binderType != null) model.put(WebKeys.BINDER_DEFINITION_TYPE, binderType.toString());

		String view = WebKeys.VIEW_ADD_BINDER_DEFINITION;
		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.FILE_FOLDER_VIEW)) {
				getFolderModule().checkAddFolderAllowed((Folder)binder);
				DefinitionHelper.getDefinitions(Definition.FILE_FOLDER_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				model.put(WebKeys.BINDER_DEFINITION_TYPE, String.valueOf(Definition.FILE_FOLDER_VIEW));
			} else {
				if (binderType == null)
					view = WebKeys.VIEW_ADD_BINDER_TYPE;
				else {  
					getFolderModule().checkAddFolderAllowed((Folder)binder);
					DefinitionHelper.getDefinitions(binderType.intValue(), WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
				}
				
			}
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			if (binderType == null)
				view = WebKeys.VIEW_ADD_BINDER_TYPE;
			else {
				if (binder instanceof Folder)
					getFolderModule().checkAddFolderAllowed((Folder)binder);
				else 
					getWorkspaceModule().checkAddWorkspaceAllowed((Workspace)binder);
					
				DefinitionHelper.getDefinitions(binderType.intValue(), WebKeys.PUBLIC_BINDER_DEFINITIONS, model);
			}
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			getWorkspaceModule().checkAddWorkspaceAllowed((Workspace)binder);
			DefinitionHelper.getDefinitions(Definition.WORKSPACE_VIEW, WebKeys.PUBLIC_BINDER_DEFINITIONS, model);			
			model.put(WebKeys.BINDER_DEFINITION_TYPE, String.valueOf(Definition.WORKSPACE_VIEW));
		}
		
		
		boolean ajax = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_AJAX, false);
		if (ajax) {
			view = WebKeys.VIEW_ADD_BINDER_DEFINITION_AJAX;
			Map statusMap = new HashMap();
			model.put(WebKeys.AJAX_STATUS, statusMap);		
			response.setContentType("text/xml");
 			if (!WebHelper.isUserLoggedIn(request)) {
 				//Signal that the user is not logged in. 
 				//  The code on the calling page will output the proper translated message.
 				statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
 			} 
 		} else if (!Validator.isNull(defId)) {
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			model.put(WebKeys.DEFINITION_ID, defId);
			Map publicBinderDefs = (Map)model.get(WebKeys.PUBLIC_BINDER_DEFINITIONS);
			//Make sure the requested definition is legal
			if (publicBinderDefs.containsKey(defId)) {
				Definition def = (Definition)publicBinderDefs.get(defId);
				DefinitionHelper.getDefinition(def, model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@type='form']");
			}
			view = WebKeys.VIEW_ADD_BINDER;
		} 
		
		return new ModelAndView(view, model);
	}
	protected void setupConfigBinder(ActionResponse response, Long binderId, String operation) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_FORUM);	
		if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			response.setRenderParameter(WebKeys.URL_BINDER_TYPE, EntityType.workspace.name());
		} else {
			response.setRenderParameter(WebKeys.URL_BINDER_TYPE, EntityType.folder.name());
		}
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


