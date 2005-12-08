package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

//import org.apache.commons.fileupload.FileUploadUtil;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;


/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
		String action = ActionUtil.getStringValue(formData, PortletKeys.ACTION);
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			// Returns a map where key is form field name (String) and value is LiferayFileItem.
			Map fileItems=new HashMap(); // = FileUploadUtil.getFileItems(request);
			String entryType = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_ENTRY_TYPE);
			if (action.equals(PortletKeys.FORUM_ACTION_ADD_ENTRY)) {
				getFolderModule().addEntry(folderId, entryType, formData, fileItems);
			} else if (action.equals(PortletKeys.FORUM_ACTION_ADD_REPLY)) {
				String entryId = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_ENTRY_ID);				
				getFolderModule().addReply(folderId, Long.valueOf(entryId), entryType, formData, fileItems);
			}
			response.setRenderParameter(PortletKeys.ACTION, PortletKeys.FORUM_ACTION_VIEW_FORUM);
			response.setRenderParameter(PortletKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(PortletKeys.ACTION, PortletKeys.FORUM_ACTION_VIEW_FORUM);
			response.setRenderParameter(PortletKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else
			response.setRenderParameters(formData);
			
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
			
		String action = ActionUtil.getStringValue(formData, PortletKeys.ACTION);
		//See if the add entry form was submitted
		Map model;
		try {
			if (action.equals(PortletKeys.FORUM_ACTION_ADD_ENTRY)) {
				model = getForumActionModule().getAddEntry(formData, request, folderId);
			} else {
				model = getForumActionModule().getAddReply(formData, request, folderId);
			}
		} catch (NoDefinitionByTheIdException nd) {
			//Get the jsp objects again, but this time get the "view_forum" values
			return returnToViewForum(request, response, formData, folderId);
		}
							
		return new ModelAndView(PortletKeys.VIEW_ADD_ENTRY, model);
	}
}


