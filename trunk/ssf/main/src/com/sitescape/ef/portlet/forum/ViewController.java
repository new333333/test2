package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;


import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.NoFolderByTheIdException;
/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId=null;
		try {
			folderId = ActionUtil.getForumId(formData, request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(PortletKeys.VIEW);
		}
		if (request.getWindowState().equals(WindowState.NORMAL)) {
			return new ModelAndView(PortletKeys.VIEW, PortletKeys.FOLDER, getFolderModule().getFolder(folderId));
		}
		String op = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_OPERATION);
		if (op.equals(PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
			String displayStyle = ActionUtil.getStringValue(formData,PortletKeys.FORUM_URL_VALUE);
			getProfileModule().setUserProperty(null,ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, displayStyle);
		}

		return returnToViewForum(request, response, formData, folderId);
	} 
}
