/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.presence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;


/**
 * @author Janet McCann
 *
 * Handle Ajax request to update presence display
 */
public class UpdatePresenceController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
		//if action in the url, assume this is an ajax update call
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		String componentId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		if (!WebHelper.isUserLoggedIn(request)) {
			response.setContentType("text/xml");
			Map statusMap = new HashMap();
			model.put(WebKeys.AJAX_STATUS, statusMap);	
	 				
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			return new ModelAndView("forum/fetch_url_return", model);
		} else	if (Validator.isNotNull(componentId)) {
			String scope=null;
			if (componentId.contains("_")) scope = componentId.split("_")[0];
			if (Validator.isNull(scope)) scope = DashboardHelper.Local;
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			User user = RequestContextHolder.getRequestContext().getUser();
			DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId, false);
			return new ModelAndView("dashboard/buddy_list_view2", model);
		} else {
			//refresh call
			Set p = LongIdUtil.getIdsAsLongSet(request.getParameterValues("userList"));
			model.put(WebKeys.USERS, getProfileModule().getUsers(p));
			p = LongIdUtil.getIdsAsLongSet(request.getParameterValues("groupList"));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(p));
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			return new ModelAndView("dashboard/buddy_list_view2", model);
		}
	}
}
