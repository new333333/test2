package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.portlet.Constants;
import com.sitescape.util.Validator;


/**
 * @author Peter Hurley
 *
 */
public class ConfigureController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
	    	List definitions = new ArrayList();
	    	String defId = ActionUtil.getStringValue(formData, Constants.FOLDER_DEFINITION);
			if (!Validator.isNull(defId)) {
				definitions.add(defId);
			}
				
			String[] defIds = (String[]) formData.get(Constants.ENTRY_DEFINITION);
			for (int i = 0; i < defIds.length; i++) {
				defId = defIds[i];
				if (!Validator.isNull(defId)) {
					definitions.add(defId);
				}
			}				
			getFolderModule().modifyFolderConfiguration(folderId, definitions);
		}
		response.setRenderParameter(Constants.ACTION, Constants.FORUM_OPERATION_VIEW_FORUM);
		response.setRenderParameter(Constants.FORUM_URL_FORUM_ID, folderId.toString());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(formData, request);
	
		Map model = getForumActionModule().getConfigureForum(formData, request, folderId);
		return new ModelAndView(Constants.VIEW_CONFIGURE, model);
	}
}
