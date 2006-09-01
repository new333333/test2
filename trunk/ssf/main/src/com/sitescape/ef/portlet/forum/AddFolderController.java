package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
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
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, "definitionId", "");
			String cfgType = PortletRequestUtils.getStringParameter(request, "binderConfigId", "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			} 
			MapInputData inputData = new MapInputData(formData);
			Long newId=null;
			if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
				newId = getFolderModule().addFolder(binderId, entryType, inputData, fileMap);
			} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
				newId = getWorkspaceModule().addFolder(binderId, entryType, inputData, fileMap);				
			} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
				newId = getWorkspaceModule().addWorkspace(binderId, entryType, inputData, fileMap);				
			}
			try {
				BinderConfig cfg = getBinderModule().getConfiguration(cfgType);
				getBinderModule().setConfiguration(newId, cfg.getDefinitionIds(), cfg.getWorkflowIds());
			} catch (Exception ex) {}
			
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
		if (operation.equals("")) operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String defId = PortletRequestUtils.getStringParameter(request, "binderConfigId", "");
		Binder binder = getBinderModule().getBinder(binderId);
    	model.put(WebKeys.URL_OPERATION, operation);
		model.put(WebKeys.BINDER, binder); 
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);

		String view = WebKeys.VIEW_ADD_BINDER_TYPE;
		if (Validator.isNull(defId)) {
			if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
				if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.FILE_FOLDER_VIEW)) {
					getFolderModule().checkAddFolderAllowed((Folder)binder);
					List result = getBinderModule().getConfigurations(Definition.FILE_FOLDER_VIEW);
					if (result.isEmpty()) {
						result.add(getBinderModule().createDefaultConfiguration(Definition.FILE_FOLDER_VIEW));
					}
					model.put(WebKeys.CONFIGURATIONS, result);
				} else {
					getFolderModule().checkAddFolderAllowed((Folder)binder);
					List result = getBinderModule().getConfigurations(Definition.FOLDER_VIEW);
					if (result.isEmpty()) {
						result.add(getBinderModule().createDefaultConfiguration(Definition.FOLDER_VIEW));
					}
					List result2 = getBinderModule().getConfigurations(Definition.FILE_FOLDER_VIEW);
					if (result2.isEmpty()) {
						result2.add(getBinderModule().createDefaultConfiguration(Definition.FILE_FOLDER_VIEW));
					}
					result.addAll(result2);	
					model.put(WebKeys.CONFIGURATIONS, result);
				}
			} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
				if (binder.getEntityIdentifier().getEntityType().name().equals(EntityType.folder)) {
					getFolderModule().checkAddFolderAllowed((Folder)binder);
				} else {
					getWorkspaceModule().checkAddFolderAllowed((Workspace)binder);
				}
				List result = getBinderModule().getConfigurations(Definition.FOLDER_VIEW);
				if (result.isEmpty()) {
					result.add(getBinderModule().createDefaultConfiguration(Definition.FOLDER_VIEW));
				}
				List result2 = getBinderModule().getConfigurations(Definition.FILE_FOLDER_VIEW);
				if (result2.isEmpty()) {
					result2.add(getBinderModule().createDefaultConfiguration(Definition.FILE_FOLDER_VIEW));
				}
				result.addAll(result2);	
				model.put(WebKeys.CONFIGURATIONS, result);
			} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
				getWorkspaceModule().checkAddWorkspaceAllowed((Workspace)binder);
				List result = getBinderModule().getConfigurations(Definition.WORKSPACE_VIEW);
				if (result.isEmpty()) {
					result.add(getBinderModule().createDefaultConfiguration(Definition.WORKSPACE_VIEW));	
				}
				model.put(WebKeys.CONFIGURATIONS, result);
			}
		} else {
			BinderConfig cfg = getBinderModule().getConfiguration(defId);
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			model.put(WebKeys.CONFIGURATION, cfg);
			List defs = cfg.getDefinitionIds();
			Definition def=null;
			if (!defs.isEmpty()) {
				try {
					 def = getDefinitionModule().getDefinition((String)defs.get(0));
				} catch (Exception ex) {}
			}
			//	Make sure the requested definition is legal
			if (def != null) {
				model.put(WebKeys.DEFINITION, def);
				DefinitionHelper.getDefinition(def, model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@type='form']");
			}
			view = WebKeys.VIEW_ADD_BINDER;
		}
	
		return new ModelAndView(view, model);
	}

	protected void setupViewBinder(ActionResponse response, Long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());		
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
		if (binder.getEntityIdentifier().getEntityType().name().equals(EntityType.folder.name())) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		} else {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		}
	}
}


