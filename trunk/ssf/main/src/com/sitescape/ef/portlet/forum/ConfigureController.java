package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ConfigureController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(request);
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
	    	List definitions = new ArrayList();
	    	//Get the default folder view
	    	String defFolderId = PortletRequestUtils.getStringParameter(request, "folderDefinition");
			String[] defFolderIds = PortletRequestUtils.getStringParameters(request, "folderDefinitions");
			if (!Validator.isNull(defFolderId)) {
				//The default folder view is always the first one in the list
				if (defFolderIds != null) {
					for (int i = 0; i < defFolderIds.length; i++) {
						String defId = defFolderIds[i];
						if (!Validator.isNull(defId) && defId == defFolderId) {
							definitions.add(defFolderId);
							break;
						}
					}
				}
			}
				
			//Add the other allowed folder views
			if (defFolderIds != null) {
				for (int i = 0; i < defFolderIds.length; i++) {
					String defId = defFolderIds[i];
					if (!Validator.isNull(defId) && defId != defFolderId) {
						definitions.add(defId);
					}
				}
			}

			//Add the allowed entry types
			// and the workflow associations
			String[] defEntryIds = PortletRequestUtils.getStringParameters(request, "entryDefinition");
			Map workflowAssociations = new HashMap();
			if (defEntryIds != null) {
				for (int i = 0; i < defEntryIds.length; i++) {
					String defId = defEntryIds[i];
					if (!Validator.isNull(defId)) {
						definitions.add(defId);
						String wfDefId = PortletRequestUtils.getStringParameter(request, "workflow_" + defId, "");
						if (!wfDefId.equals("")) workflowAssociations.put(defId,wfDefId);
					}
				}
			}
			getFolderModule().modifyFolderConfiguration(folderId, definitions, workflowAssociations);
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
			response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else
			response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(request);
	
		Map model = getForumActionModule().getConfigureForum(formData, request, folderId);
		return new ModelAndView(WebKeys.VIEW_CONFIGURE, model);
	}
}
