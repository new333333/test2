package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.Folder;

/**
 * @author Janet McCann
 *
 */
public class AddFolderController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		if (formData.containsKey("okBtn")) {
			Map input = new HashMap();
			input.put("title",PortletRequestUtils.getStringParameter(request, "title", ""));
			input.put("name",PortletRequestUtils.getStringParameter(request, "name", ""));
			input.put("description", new Description(PortletRequestUtils.getStringParameter(request, "description", ""), Description.FORMAT_HTML));
			getFolderModule().addFolder(folderId, input);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		
		Folder folder = getFolderModule().getFolder(folderId);
    	model.put(WebKeys.FOLDER, folder); 

		return new ModelAndView(WebKeys.VIEW_ADD_FOLDER, model);
	}
}


