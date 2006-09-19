package com.sitescape.ef.portlet.presence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;


/**
 * @author Janet McCann
 *
 * Handle Ajax request to update presence display
 */
public class UpdatePresenceController  extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
		//if action in the url, assume this is an ajax update call
		model.put(WebKeys.NAMING_PREFIX, PortletRequestUtils.getStringParameter(request, WebKeys.NAMING_PREFIX, ""));
		model.put(WebKeys.DASHBOARD_ID, PortletRequestUtils.getStringParameter(request, WebKeys.DASHBOARD_ID, ""));
		response.setContentType("text/xml");
		if (!WebHelper.isUserLoggedIn(request)) {
			Map statusMap = new HashMap();
			model.put(WebKeys.AJAX_STATUS, statusMap);	
	 				
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			return new ModelAndView(WebKeys.VIEW_PRESENCE_AJAX, model);
		} else {
			//refresh call
			Set p = FindIdsHelper.getIdsAsLongSet(request.getParameterValues("userList"));
			model.put(WebKeys.USERS, getProfileModule().getUsers(p));
			p = FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groupList"));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(p));
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			return new ModelAndView(WebKeys.VIEW_PRESENCE_AJAX, model);
		}
	}
}
