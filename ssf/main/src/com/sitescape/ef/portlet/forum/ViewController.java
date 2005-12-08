package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import com.sitescape.ef.util.PortletRequestUtils;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletSession;

import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;
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
			folderId = ActionUtil.getForumId(request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(WebKeys.VIEW);
		}
		if (request.getWindowState().equals(WindowState.NORMAL)) {
			return new ModelAndView(WebKeys.VIEW, WebKeys.FOLDER, getFolderModule().getFolder(folderId));
		}
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put("displayStyle", PortletRequestUtils.getStringParameter(request,WebKeys.FORUM_URL_VALUE,""));
			getProfileModule().modifyUser(user.getId(), updates);
		}
		if (op.equals(WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			ps.setAttribute(WebKeys.CALENDAR_VIEWMODE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.FORUM_URL_VALUE,""));
		}
		if (op.equals(WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			String urldate = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_NEWVIEWDATE, "");
			String urlviewmode = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_VIEWMODE, "");
			ps.setAttribute(WebKeys.CALENDAR_VIEWMODE, urlviewmode);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
			Date newdate = sdf.parse(urldate);
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, newdate);
		}

		return returnToViewForum(request, response, formData, folderId);
	} 
}
