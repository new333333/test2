package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;


import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.User;
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
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long folderId=null;
		try {
			folderId = ActionUtil.getForumId(formData, request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(WebKeys.VIEW);
		}
		if (request.getWindowState().equals(WindowState.NORMAL)) {
			return new ModelAndView(WebKeys.VIEW, WebKeys.FOLDER, getFolderModule().getFolder(folderId));
		}
		String op = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_OPERATION);
		if (op.equals(WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put("displayStyle", ActionUtil.getStringValue(formData,WebKeys.FORUM_URL_VALUE));
			getProfileModule().modifyUser(user.getId(), updates);
		}

		return returnToViewForum(request, response, formData, folderId);
	} 
}
