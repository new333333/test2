package com.sitescape.ef.portlet.profile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Principal;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractProfileController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.ACTION_DELETE_ENTRY)) {
			getProfileModule().deleteEntry(binderId, entryId);			
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
			response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("okBtn")) {
			//The modify form was submitted. Go process it
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			getProfileModule().modifyEntry(binderId, entryId, new MapInputData(formData), fileMap);
			setupViewEntry(response, binderId, entryId);
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewEntry(response, binderId, entryId);
		} else {
			response.setRenderParameters(formData);
		}
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {

		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (!action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {
			return returnToView(request, response);
		}
		Map model = new HashMap();	
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Principal entry  = getProfileModule().getEntry(binderId, entryId);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			DefinitionUtils.getDefaultEntryView(entry, model);
		} else {
			DefinitionUtils.getDefinition(entryDef, model, "//item[@type='form']");
		}
		
		return new ModelAndView(WebKeys.VIEW_MODIFY_ENTRY, model);
	}
}

