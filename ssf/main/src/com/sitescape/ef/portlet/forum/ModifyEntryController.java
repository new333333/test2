package com.sitescape.ef.portlet.forum;

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
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(request);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_ENTRY_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.FORUM_ACTION_DELETE_ENTRY)) {
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
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
		}
		response.setRenderParameters(formData);
		response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
		response.setRenderParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId.toString());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(request);

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path;
		if (formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			if (action.equals(WebKeys.FORUM_ACTION_MODIFY_ENTRY)) {
				path = WebKeys.VIEW_FORUM;
				model.put(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
				request.setAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);
				try {
					Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_ENTRY_ID));				
					model.put(WebKeys.FORUM_URL_ENTRY_ID, entryId.toString());
					model = getForumActionModule().getShowEntry(entryId.toString(), model, request, response, folderId);
				} catch (NoDefinitionByTheIdException nd) {
					return returnToViewForum(request, response, formData, folderId);
				}
			} else return returnToViewForum(request, response, formData, folderId);
		} else	if (action.equals(WebKeys.FORUM_ACTION_MODIFY_ENTRY)) {
			try {
				model = getForumActionModule().getModifyEntry(formData, request, folderId);
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
		} else
			return returnToViewForum(request, response, formData, folderId);
			
		return new ModelAndView(path, model);
	}
}

