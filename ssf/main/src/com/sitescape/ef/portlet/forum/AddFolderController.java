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
import com.sitescape.ef.domain.Description;
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
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
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
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			response.setRenderParameter("redirect", "true");
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			response.setRenderParameter("redirect", "true");
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		if (!Validator.isNull(request.getParameter("redirect"))) {
			model.put(WebKeys.BINDER_ID, binderId.toString());
			return new ModelAndView(WebKeys.VIEW_LISTING_REDIRECT, model);
		}
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Binder binder=null;
		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			binder = getFolderModule().getFolder(binderId);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			binder = getWorkspaceModule().getWorkspace(binderId);				
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			binder = getWorkspaceModule().getWorkspace(binderId);				
		}
		
    	model.put(WebKeys.URL_OPERATION, operation);
		model.put(WebKeys.BINDER, binder); 

		DefinitionUtils.getDefinitions(binder, model);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");

		return new ModelAndView(WebKeys.VIEW_ADD_FOLDER, model);
	}
}


