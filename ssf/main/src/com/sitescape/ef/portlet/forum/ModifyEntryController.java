package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.ACTION_DELETE_ENTRY)) {
			getFolderModule().deleteEntry(folderId, entryId);
		} else if (formData.containsKey("okBtn")) {

			//See if the add entry form was submitted
			//The form was submitted. Go process it
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			getFolderModule().modifyEntry(folderId, entryId, formData, fileMap);
			setupViewEntry(response, folderId, entryId);
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewEntry(response, folderId, entryId);
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
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path;
		if (action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {
			try {
				FolderEntry entry=null;
				Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
				entry  = getFolderModule().getEntry(folderId, entryId);
				
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.FOLDER, entry.getParentFolder());
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				DefinitionUtils.getDefinition(entry.getEntryDef(), model, "//item[@name='entryForm']");
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
		} else
			return returnToViewForum(request, response, formData, folderId);
			
		return new ModelAndView(path, model);
	}
}

