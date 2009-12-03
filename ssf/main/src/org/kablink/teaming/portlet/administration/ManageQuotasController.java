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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;




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
			Integer defaultQuota;
			Integer highWaterMark;
			
			// Get the default data quota size entered by the user.
			try
			{
				defaultQuota = PortletRequestUtils.getIntParameter(request, "defaultQuota");
			}
			catch (Exception ex)
			{
				// The value entered by the user is not valid.  Use default value.
				defaultQuota = getAdminModule().getQuotaDefault();
			}
			
			// Get the highwater mark entered by the user.
			try
			{
				highWaterMark = PortletRequestUtils.getIntParameter(request, "highWaterMark");
			}
			catch (Exception ex)
			{
				// The value entered by the user is not valid.  Use default value.
				highWaterMark = getAdminModule().getQuotaHighWaterMark();
			}
			
			getAdminModule().setQuotaDefault(defaultQuota);
			getAdminModule().setQuotaHighWaterMark(highWaterMark);
			
			Set<Long> groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addGroups"));
			Set<Long> userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addUsers"));
			String s_userQuota = PortletRequestUtils.getStringParameter(request, "addUserQuota", "");
			if (!s_userQuota.equals("")) {
				Long userQuota = Long.valueOf(s_userQuota);
				if (!userIds.isEmpty() && userQuota != null) {
					getProfileModule().setUserDiskQuotas(userIds, userQuota);
				}
			}
			String s_groupQuota = PortletRequestUtils.getStringParameter(request, "addGroupQuota", "");
			if (!s_groupQuota.equals("")) {
				Long groupQuota = Long.valueOf(s_groupQuota);
				if (!groupIds.isEmpty() && groupQuota != null) {
					getProfileModule().setGroupDiskQuotas(groupIds, groupQuota);
				}
			}
			//Check for individual group and user changes
			userIds = new HashSet<Long>();
			groupIds = new HashSet<Long>();
			Map<String, Long> quotaValues = new HashMap<String, Long>();
			Iterator itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String)itFormData.next();
				if (key.indexOf("deleteUser_") == 0) {
					String userId = key.substring(11, key.length());
					userIds.add(Long.valueOf(userId));
					quotaValues.put(userId, Long.valueOf(0));
				}
				if (key.indexOf("deleteGroup_") == 0) {
					String groupId = key.substring(12, key.length());
					groupIds.add(Long.valueOf(groupId));
					quotaValues.put(groupId, Long.valueOf(0));
				}
				if (key.indexOf("modifyId") == 0) {
					String id = PortletRequestUtils.getStringParameter(request, "modifyId", "");
					String newGroupQuota = PortletRequestUtils.getStringParameter(request, "newGroupQuota_"+id, "");
					if (!newGroupQuota.equals("")) {
						groupIds.add(Long.valueOf(id));
						quotaValues.put(id, Long.valueOf(newGroupQuota));
					}
					String newUserQuota = PortletRequestUtils.getStringParameter(request, "newUserQuota_"+id, "");
					if (!newUserQuota.equals("")) {
						userIds.add(Long.valueOf(id));
						quotaValues.put(id, Long.valueOf(newUserQuota));
					}
				}
			}
			for (Long id : groupIds) {
				List ids = new ArrayList();
				ids.add(id);
				getProfileModule().setGroupDiskQuotas(ids, quotaValues.get(id.toString()));
			}
			for (Long id : userIds) {
				List ids = new ArrayList();
				ids.add(id);
				getProfileModule().setUserDiskQuotas(ids, quotaValues.get(id.toString()));
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
		
		SortedSet<Principal> principals = getProfileModule().getPrincipals(users);
		model.put(WebKeys.QUOTAS_USERS, principals);
		SortedSet<Principal> group_principals = getProfileModule().getPrincipals(groups);
		model.put(WebKeys.QUOTAS_GROUPS, group_principals);

		model.put(WebKeys.QUOTAS_DEFAULT, getAdminModule().getQuotaDefault());
		model.put(WebKeys.QUOTAS_ENABLED, getAdminModule().isQuotaEnabled());
		model.put(WebKeys.QUOTAS_HIGH_WATER_MARK, getAdminModule().getQuotaHighWaterMark());
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_QUOTAS, model);

	}

}
