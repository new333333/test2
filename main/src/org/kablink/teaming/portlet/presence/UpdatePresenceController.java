/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.presence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;



/**
 * @author Janet McCann
 *
 * Handle Ajax request to update presence display
 */
public class UpdatePresenceController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
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
			model.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(p));
			return new ModelAndView("dashboard/buddy_list_view2", model);
		}
	}
}
