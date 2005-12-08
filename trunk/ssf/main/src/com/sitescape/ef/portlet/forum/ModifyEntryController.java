package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


//import org.apache.commons.fileupload.FileUploadUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.portlet.PortletKeys;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			// Returns a map where key is form field name (String) and value is LiferayFileItem.
//				Map fileItems = FileUploadUtil.getFileItems(req);
			String entryId = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_ENTRY_ID);				
			getFolderModule().modifyEntry(folderId, Long.valueOf(entryId), formData, null);
			//Get the jsp objects again, but this time get the "view_forum" values
			response.setRenderParameter(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_FORUM);
			response.setRenderParameter(PortletKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			//Get the jsp objects again, but this time get the "view_forum" values
			response.setRenderParameter(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_FORUM);
			response.setRenderParameter(PortletKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else
			response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();

		Long folderId = ActionUtil.getForumId(formData, request);
		Map model;	
		try {
			model = getForumActionModule().getModifyEntry(formData, request, folderId);
		} catch (NoDefinitionByTheIdException nd) {
			return returnToViewForum(request, formData, folderId);
		}
			
		return new ModelAndView(PortletKeys.VIEW_MODIFY_ENTRY, model);
	}
}

