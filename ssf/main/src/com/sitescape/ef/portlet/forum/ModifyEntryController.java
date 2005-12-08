package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


//import org.apache.commons.fileupload.FileUploadUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.web.WebKeys;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
		String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);				
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			// Returns a map where key is form field name (String) and value is LiferayFileItem.
//				Map fileItems = FileUploadUtil.getFileItems(req);
			getFolderModule().modifyEntry(folderId, Long.valueOf(entryId), formData, null);
			//Get the jsp objects again, but this time get the "view_forum" values
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);
			response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
			response.setRenderParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId.toString());
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			//Get the jsp objects again, but this time get the "view_forum" values
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);
			response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
			response.setRenderParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId.toString());
		}
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);

		Map model = new HashMap();	
		String path = WebKeys.VIEW_MODIFY_ENTRY;
		if (formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			path = WebKeys.VIEW_FORUM;
			model.put(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			try {
				String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);				
				model.put(WebKeys.FORUM_URL_ENTRY_ID, entryId);
				model = getForumActionModule().getShowEntry(model, request, response, folderId);
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
		} else {
			try {
				model = getForumActionModule().getModifyEntry(formData, request, folderId);
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
		}
			
		return new ModelAndView(path, model);
	}
}

