package com.sitescape.ef.portlet.profile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
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
			getProfileModule().deletePrincipal(entryId);			
		} else if (formData.containsKey("okBtn")) {

			//See if the add entry form was submitted
			//The form was submitted. Go process it
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			getProfileModule().modifyPrincipal(entryId, formData, fileMap);
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
		}
		response.setRenderParameters(formData);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			if (action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {
				return returnToViewEntry(request, response);
			} else return returnToView(request, response);
		}
		if (!action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {
			return returnToView(request, response);
		}
		Map model = new HashMap();	
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Principal entry  = getProfileModule().getPrincipal(entryId);
		ProfileBinder binder = getProfileModule().getProfileBinder();
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		DefinitionUtils.getDefinition(entry.getEntryDef(), model, "//item[@name='entryForm']");
		
		return new ModelAndView(WebKeys.VIEW_MODIFY_ENTRY, model);
	}
}

