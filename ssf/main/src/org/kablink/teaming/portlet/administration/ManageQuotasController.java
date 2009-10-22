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
package org.kablink.teaming.portlet.administration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestUtils;




public class ManageQuotasController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			if (formData.containsKey("enableQuotas")) {
				getAdminModule().setQuotaEnabled(true);
			} else {
				getAdminModule().setQuotaEnabled(false);
			}
			Integer defaultQuota = PortletRequestUtils.getIntParameter(request, "defaultQuota");
			Integer highWaterMark = PortletRequestUtils.getIntParameter(request, "highWaterMark");
			getAdminModule().setQuotaDefault(defaultQuota);
			getAdminModule().setQuotaHighWaterMark(highWaterMark);
			
			Set groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addGroups"));
			Set userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addUsers"));
			Long userQuota = PortletRequestUtils.getLongParameter(request, "addUserQuota");
			Long groupQuota = PortletRequestUtils.getLongParameter(request, "addGroupQuota");
			if (!groupIds.isEmpty() && groupQuota != null) {
				getProfileModule().setUserDiskQuotas(groupIds, groupQuota);
			}
			if (!userIds.isEmpty() && userQuota != null) {
				getProfileModule().setUserDiskQuotas(userIds, userQuota);
			}
			//Check for individual group and user changes
			Iterator itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String)itFormData.next();
				
			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Map formData = request.getParameterMap();

		if (formData.containsKey("okBtn")) {
			//return new ModelAndView("forum/close_window", model);
		}

		List users = getProfileModule().getNonDefaultQuotas(ObjectKeys.PRINCIPAL_TYPE_USER);
		List groups = getProfileModule().getNonDefaultQuotas(ObjectKeys.PRINCIPAL_TYPE_GROUP);
		users.add(user.getId());
		
		SortedSet<Principal> principals = getProfileModule().getPrincipals(users);
		model.put(WebKeys.QUOTAS_USERS, principals);
		model.put(WebKeys.QUOTAS_GROUPS, groups);

		model.put(WebKeys.QUOTAS_DEFAULT, getAdminModule().getQuotaDefault());
		model.put(WebKeys.QUOTAS_ENABLED, getAdminModule().isQuotaEnabled());
		model.put(WebKeys.QUOTAS_HIGH_WATER_MARK, getAdminModule().getQuotaHighWaterMark());
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_QUOTAS, model);

	}

}
