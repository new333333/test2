/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked","unused"})
public class ManageQuotasController extends SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Binder topBinder = getWorkspaceModule().getTopWorkspace();
		getAdminModule().checkAccess(AdminOperation.manageFunction);
		if ((formData.containsKey("okBtn") || formData.containsKey("deleteBtn")) && WebHelper.isMethodPost(request)) {
			if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
				if (Utils.checkIfFilr() && formData.containsKey("okBtn")) {
					boolean allowPersonalStorage = formData.containsKey("allowPersonalStorage");
					getAdminModule().setAdHocFoldersEnabled(allowPersonalStorage);
				}
				
				boolean okBtnDelete = false;
				if (formData.containsKey("deleteBtn")) okBtnDelete = true;
				if (formData.containsKey("enableQuotas")) {
					getAdminModule().setQuotaEnabled(true);
				} else {
					getAdminModule().setQuotaEnabled(false);
				}
				if (formData.containsKey("enableBinderQuotas")) {
					boolean allowBinderOwner = false;
					if (formData.containsKey("allowBinderQuotasByOwner")) allowBinderOwner = true;
					getAdminModule().setBinderQuotasEnabled(true, allowBinderOwner);
				} else {
					getAdminModule().setBinderQuotasEnabled(false, false);
				}
				Integer defaultQuota;
				Integer highWaterMark;
				
				// Get the default data quota size entered by the user.
				try
				{
					defaultQuota = PortletRequestUtils.getIntParameter(request, "defaultQuota", getAdminModule().getQuotaDefault());
				}
				catch (Exception ex)
				{
					// The value entered by the user is not valid.  Use default value.
					defaultQuota = getAdminModule().getQuotaDefault();
				}
				
				// Get the highwater mark entered by the user.
				try
				{
					highWaterMark = PortletRequestUtils.getIntParameter(request, "highWaterMark", getAdminModule().getQuotaHighWaterMark());
				}
				catch (Exception ex)
				{
					// The value entered by the user is not valid.  Use default value.
					highWaterMark = getAdminModule().getQuotaHighWaterMark();
				}
				if (defaultQuota < 0) defaultQuota = 0;
				if (highWaterMark > 100) highWaterMark = 100;
				if (highWaterMark < 0) highWaterMark = 0;
				
				getAdminModule().setQuotaDefault(defaultQuota);
				getAdminModule().setQuotaHighWaterMark(highWaterMark);
				
				Set<Long> groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addGroups"));
				Long allUsersGroupId = Utils.getAllUsersGroupId();
				if (allUsersGroupId != null && groupIds.contains(allUsersGroupId)) {
					//Trying to set a quota for all users by using the All Users group is prohibited
					groupIds.remove(allUsersGroupId);
				}
				Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
				if (allExtUsersGroupId != null && groupIds.contains(allExtUsersGroupId)) {
					//Trying to set a quota for all external users by using the All External Users group is prohibited
					groupIds.remove(allExtUsersGroupId);
				}
				Set<Long> userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addUsers"));
				String s_userQuota = PortletRequestUtils.getStringParameter(request, "addUserQuota", "");
				if (!s_userQuota.equals("")) {
					try {
						Long userQuota = Long.valueOf(s_userQuota);
						if (!userIds.isEmpty() && userQuota != null) {
							getProfileModule().setUserDiskQuotas(userIds, userQuota);
						}
					} catch(Exception e) {}
				}
				String s_groupQuota = PortletRequestUtils.getStringParameter(request, "addGroupQuota", "");
				if (!s_groupQuota.equals("")) {
					try {
						Long groupQuota = Long.valueOf(s_groupQuota);
						if (!groupIds.isEmpty() && groupQuota != null) {
							getProfileModule().adjustGroupDiskQuotas(groupIds, groupQuota);
						}
					} catch(Exception e) {}
				}
				//Check for individual group and user changes
				userIds = new HashSet<Long>();
				groupIds = new HashSet<Long>();
				Map<String, Long> quotaValues = new HashMap<String, Long>();
				Iterator itFormData = formData.keySet().iterator();
				while (itFormData.hasNext()) {
					String key = (String)itFormData.next();
					if (okBtnDelete && (key.indexOf("deleteUser_") == 0)) {
						String userId = key.substring(11, key.length());
						userIds.add(Long.valueOf(userId));
						quotaValues.put(userId, Long.valueOf(0));
					}
					if (okBtnDelete && (key.indexOf("deleteGroup_") == 0)) {
						String groupId = key.substring(12, key.length());
						groupIds.add(Long.valueOf(groupId));
						quotaValues.put(groupId, Long.valueOf(0));
					}
					if (key.indexOf("modifyId") == 0) {
						String id = PortletRequestUtils.getStringParameter(request, "modifyId", "");
						String newGroupQuota = PortletRequestUtils.getStringParameter(request, "newGroupQuota_"+id, "");
						if (!newGroupQuota.equals("")) {
							try {
								groupIds.add(Long.valueOf(id));
								quotaValues.put(id, Long.valueOf(newGroupQuota));
							} catch(Exception e) {}
						}
						String newUserQuota = PortletRequestUtils.getStringParameter(request, "newUserQuota_"+id, "");
						if (!newUserQuota.equals("")) {
							try {
								userIds.add(Long.valueOf(id));
								quotaValues.put(id, Long.valueOf(newUserQuota));
							} catch(Exception e) {}
						}
					}
				}
				for (Long id : groupIds) {
					List ids = new ArrayList();
					ids.add(id);
					if (id != null && quotaValues.get(id.toString()) != null) 
						getProfileModule().adjustGroupDiskQuotas(ids, quotaValues.get(id.toString()));
				}
				for (Long id : userIds) {
					List ids = new ArrayList();
					ids.add(id);
					if (id != null && quotaValues.get(id.toString()) != null) 
						getProfileModule().setUserDiskQuotas(ids, quotaValues.get(id.toString()));
				}
				
			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageFunction);

		if (formData.containsKey("okBtn")) {
			//return new ModelAndView("forum/close_window", model);
		}

		List users = getProfileModule().getNonDefaultQuotas(ObjectKeys.PRINCIPAL_TYPE_USER);
		List groups = getProfileModule().getNonDefaultQuotas(ObjectKeys.PRINCIPAL_TYPE_GROUP);
		
		SortedSet<Principal> principals = getProfileModule().getPrincipals(users);
		model.put(WebKeys.QUOTAS_USERS, principals);
		SortedSet<Principal> group_principals = getProfileModule().getPrincipals(groups);
		model.put(WebKeys.QUOTAS_GROUPS, group_principals);
		model.put(WebKeys.ALL_USERS_GROUP_ID, String.valueOf(Utils.getAllUsersGroupId()));
		model.put(WebKeys.ALL_EXTERNAL_USERS_GROUP_ID, String.valueOf(Utils.getAllExtUsersGroupId()));

		model.put(WebKeys.QUOTAS_DEFAULT, getAdminModule().getQuotaDefault());
		model.put(WebKeys.QUOTAS_ENABLED, getAdminModule().isQuotaEnabled());
		model.put(WebKeys.QUOTAS_HIGH_WATER_MARK, getAdminModule().getQuotaHighWaterMark());
		model.put(WebKeys.BINDER_QUOTAS_INITIALIZED, getAdminModule().isBinderQuotaInitialized());
		model.put(WebKeys.BINDER_QUOTAS_ENABLED, getAdminModule().isBinderQuotaEnabled());
		model.put(WebKeys.BINDER_QUOTAS_ALLOW_BINDER_OWNER_ENABLED, getAdminModule().isBinderQuotaAllowBinderOwnerEnabled());
		
		if (Utils.checkIfFilr()) {
			model.put(WebKeys.ALLOW_PERSONAL_STORAGE, GwtUIHelper.getAdhocFolderSettingFromZone(this));
		}
		
		model.put( "isFilr", Utils.checkIfFilr() );
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_QUOTAS, model);
	}
}
