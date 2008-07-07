/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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

import org.springframework.web.portlet.ModelAndView;

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
