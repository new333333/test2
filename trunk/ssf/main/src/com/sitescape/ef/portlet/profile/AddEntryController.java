package com.sitescape.ef.portlet.profile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		//See if the add entry form was submitted
		Long entryId=null;
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
			entryId= getProfileModule().addUser(binderId, entryType, inputData, fileMap);
			setupViewEntry(response, binderId, entryId);
			//flag reload of folder listing
			response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());				
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);

		}
			
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		Map model = new HashMap();
		ProfileBinder binder = getProfileModule().getProfileBinder();
		//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(binder);
		String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='profileEntryForm']");
		}
		return new ModelAndView(WebKeys.VIEW_ADD_ENTRY, model);
	}
}


