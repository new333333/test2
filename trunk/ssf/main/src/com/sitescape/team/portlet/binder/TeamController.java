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
package com.sitescape.team.portlet.binder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.User;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.StringUtil;

/**
 * @author Janet McCann
 * 
 */
public class TeamController extends AbstractBinderController {

	public void handleActionRequestAfterValidation(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Map formData = request.getParameterMap();

		if (formData.containsKey("okBtn")) {
			Set memberIds = new HashSet();
			if (formData.containsKey("users")) memberIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("users")));
			if (formData.containsKey("groups")) memberIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groups")));
			
			//Save the team members list
			// TODO - add code
			
			setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewBinder(response, binderId, binderType);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request,
				WebKeys.URL_BINDER_ID);
		if (binderId != null) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}

		List userIds = new ArrayList();
		model.put(WebKeys.USERS, getProfileModule().getUsers(new HashSet(userIds)));

		List groupIds = new ArrayList();
		model.put(WebKeys.GROUPS, getProfileModule().getUsers(new HashSet(userIds)));

		return new ModelAndView(WebKeys.VIEW_ADD_TEAM_MEMBERS, model);
	}

}
