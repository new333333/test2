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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * @author Janet McCann
 * 
 */
public class MeetingController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		String[] errors = request.getParameterValues(WebKeys.ERROR_LIST);
		Map model = new HashMap();
		if (errors != null) {
			model.put(WebKeys.ERROR_LIST, errors);
			return new ModelAndView(WebKeys.VIEW_BINDER_MEETING, model);
		}
		Long binderId = PortletRequestUtils.getLongParameter(request,
				WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request,
				WebKeys.URL_ENTRY_ID);
		List userIds = PortletRequestUtils.getLongListParameters(request,
				WebKeys.USER_IDS_TO_ADD);
		Boolean appendTeamMembers = PortletRequestUtils.getBooleanParameter(
				request, WebKeys.URL_APPEND_TEAM_MEMBERS, false);

		if (binderId != null) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}

		if (binderId != null && entryId != null) {
			Entry entry = getFolderModule().getEntry(binderId, entryId);
			model.put(WebKeys.ENTRY, entry);
		}

		
		Set users = new HashSet();
		users.addAll(getProfileModule().getUsers(new HashSet(userIds)));
		
		
		model.put(WebKeys.USERS, users);
		model.put(WebKeys.URL_APPEND_TEAM_MEMBERS, appendTeamMembers);

		return new ModelAndView(WebKeys.VIEW_BINDER_MEETING, model);
	}

}
