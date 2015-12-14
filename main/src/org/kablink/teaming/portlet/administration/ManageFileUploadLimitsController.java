/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


@SuppressWarnings({"unchecked","unused"})
public class ManageFileUploadLimitsController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Binder topBinder = getWorkspaceModule().getTopWorkspace();
		getAdminModule().checkAccess(AdminOperation.manageFunction);
		if ((formData.containsKey("okBtn") || formData.containsKey("deleteBtn")) && WebHelper.isMethodPost(request)) {
			if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
				boolean okBtnDelete = false;
				if (formData.containsKey("deleteBtn")) okBtnDelete = true;
				Long allUsersGroupId = Utils.getAllUsersGroupId();

				//Now check file size limits
				String s_defaultFileSizeLimit;
				Long defaultFileSizeLimit = null;
				
				// Get the default file size limit entered by the user.
				s_defaultFileSizeLimit = PortletRequestUtils.getStringParameter(request, "defaultFileSizeLimit", "");
				if (!s_defaultFileSizeLimit.equals("")) {
					try {
						defaultFileSizeLimit = Long.valueOf(s_defaultFileSizeLimit);
					} catch(Exception e) {}
				}
				
				if (defaultFileSizeLimit != null && defaultFileSizeLimit < 0) {
					defaultFileSizeLimit = 0L;
				}
				
				getAdminModule().setFileSizeLimitUserDefault(defaultFileSizeLimit);
				
				Set<Long> groupFSLIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addFSLGroups"));
				if (allUsersGroupId != null && groupFSLIds.contains(allUsersGroupId)) {
					//Trying to set a limit for all users by using the All Users group is prohibited
					groupFSLIds.remove(allUsersGroupId);
				}
				Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
				if (allExtUsersGroupId != null && groupFSLIds.contains(allExtUsersGroupId)) {
					//Trying to set a quota for all external users by using the All External Users group is prohibited
					groupFSLIds.remove(allExtUsersGroupId);
				}
				Set<Long> userFSLIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addFSLUsers"));
				String s_userFSLLimit = PortletRequestUtils.getStringParameter(request, "addFSLUserLimit", "");
				Long userFileSizeLimit = null;
				try {
					if (!s_userFSLLimit.equals("")) {
						userFileSizeLimit = Long.valueOf(s_userFSLLimit);
					}
					if (!userFSLIds.isEmpty()) {
						getProfileModule().setUserFileSizeLimits(userFSLIds, userFileSizeLimit);
					}
				} catch(Exception e) {}
				
				Long groupFileSizeLimit = null;
				String s_groupFileSizeLimit = PortletRequestUtils.getStringParameter(request, "addFSLGroupLimit", "");
				try {
					if (!s_groupFileSizeLimit.equals("")) {
						groupFileSizeLimit = Long.valueOf(s_groupFileSizeLimit);
					}
					if (!groupFSLIds.isEmpty()) {
						getProfileModule().adjustGroupFileSizeLimits(groupFSLIds, groupFileSizeLimit);
					}
				} catch(Exception e) {}
				
				//Check for individual group and user changes
				userFSLIds = new HashSet<Long>();
				groupFSLIds = new HashSet<Long>();
				Map<String, Long> fileSizeLimitValues = new HashMap<String, Long>();
				Iterator itFormData = formData.keySet().iterator();
				while (itFormData.hasNext()) {
					String key = (String)itFormData.next();
					if (okBtnDelete && (key.indexOf("deleteFSLUser_") == 0)) {
						String s_userId = key.substring(14, key.length());
						userFSLIds.add(Long.valueOf(s_userId));
						fileSizeLimitValues.put(s_userId, null);
					}
					if (okBtnDelete && (key.indexOf("deleteFSLGroup_") == 0)) {
						String groupId = key.substring(15, key.length());
						groupFSLIds.add(Long.valueOf(groupId));
						fileSizeLimitValues.put(groupId, null);
					}
					if (key.indexOf("modifyFSLId") == 0) {
						String s_id = PortletRequestUtils.getStringParameter(request, "modifyFSLId", "");
						String s_newGroupFileSizeLimit = PortletRequestUtils.getStringParameter(request, "newFSLGroupLimit_"+s_id, "");
						Long newGroupFileSizeLimit = null;
						if (formData.containsKey("newFSLGroupLimit_"+s_id)) {
							try {
								if (!s_newGroupFileSizeLimit.equals("")) {
									newGroupFileSizeLimit = Long.valueOf(s_newGroupFileSizeLimit);
								}
								groupFSLIds.add(Long.valueOf(s_id));
								fileSizeLimitValues.put(s_id, newGroupFileSizeLimit);
							} catch(Exception e) {}
						}
						
						String s_newUserFileSizeLimit = PortletRequestUtils.getStringParameter(request, "newFSLUserLimit_"+s_id, "");
						Long newUserFileSizeLimit = null;
						if (formData.containsKey("newFSLUserLimit_"+s_id)) {
							try {
								if (!s_newUserFileSizeLimit.equals("")) {
									newUserFileSizeLimit = Long.valueOf(s_newUserFileSizeLimit);
								}
								userFSLIds.add(Long.valueOf(s_id));
								fileSizeLimitValues.put(s_id, newUserFileSizeLimit);
							} catch(Exception e) {}
						}
					}
				}
				for (Long id : groupFSLIds) {
					List ids = new ArrayList();
					ids.add(id);
					if (id != null && fileSizeLimitValues.containsKey(id.toString())) 
						getProfileModule().adjustGroupFileSizeLimits(ids, fileSizeLimitValues.get(id.toString()));
				}
				for (Long id : userFSLIds) {
					List ids = new ArrayList();
					ids.add(id);
					if (id != null && fileSizeLimitValues.containsKey(id.toString())) 
						getProfileModule().setUserFileSizeLimits(ids, fileSizeLimitValues.get(id.toString()));
				}

			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageFunction);

		if (formData.containsKey("okBtn")) {
			//return new ModelAndView("forum/close_window", model);
		}

		model.put(WebKeys.FILE_SIZE_LIMIT_USER_DEFAULT, getAdminModule().getFileSizeLimitUserDefault());
		List users = getProfileModule().getNonDefaultFileSizeLimits(ObjectKeys.PRINCIPAL_TYPE_USER);
		List groups = getProfileModule().getNonDefaultFileSizeLimits(ObjectKeys.PRINCIPAL_TYPE_GROUP);
		
		SortedSet<Principal> principals = getProfileModule().getPrincipals(users);
		SortedSet<Principal> group_principals = getProfileModule().getPrincipals(groups);

		model.put(WebKeys.ALL_USERS_GROUP_ID, String.valueOf(Utils.getAllUsersGroupId()));
		model.put(WebKeys.ALL_EXTERNAL_USERS_GROUP_ID, String.valueOf(Utils.getAllExtUsersGroupId()));
		model.put(WebKeys.FILE_SIZE_LIMITS_USERS, principals);
		model.put(WebKeys.FILE_SIZE_LIMITS_GROUPS, group_principals);
		
		model.put( "isFilr", Utils.checkIfFilr() );
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_FILE_UPLOAD_LIMITS, model);

	}

}
