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
package com.sitescape.team.liferay.enterpriseadmin;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;

import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringUtil;
import com.liferay.util.Validator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portlet.admin.util.AdminUtil;
import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;
import com.sitescape.team.liferay.util.Util;
import com.sitescape.team.portalmodule.web.security.AuthenticationManager;
import com.sitescape.team.web.crosscontext.CrossContextConstants;
import com.sitescape.team.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.team.web.util.NullServletResponse;

public class EditUserAction extends com.liferay.portlet.enterpriseadmin.action.EditUserAction {

	protected Object[] updateUser(ActionRequest req) throws Exception {
		Object[] returnValue = super.updateUser(req);
		
		User user = (User) returnValue[0];
		String oldScreenName = ((String)returnValue[1]);
		
		if (Validator.isNotNull(oldScreenName)) {
			// This means that the screen name has changed.
			// Update user screen name first.
			modifyScreenName(PortalUtil.getCompany(req).getWebId(), oldScreenName, user.getScreenName());
		}

		// Update user attributes.
		String password = AdminUtil.getUpdateUserPassword(req, user.getUserId());
		
		if(password == null)
			password = ""; // temporary...only until user specifies real password
					
		synchAddOrUpdateUser(PortalUtil.getCompany(req).getWebId(), 
				user.getScreenName(), password, Util.getUpdatesMap(user));
		
		return returnValue;
	}

	protected User updateDisplay(ActionRequest req) throws Exception {
		User user = super.updateDisplay(req);
		
		// The user object returned from above method contains states as of prior 
		// to the modification, which appears to be a bug in the code.
		// To workaround this problem, we need to retrieve user object again.
		User updatedUser = UserLocalServiceUtil.getUserById(user.getUserId());
		
		Map updates = new HashMap();
		if(user.getLocale() != null)
			updates.put("locale", updatedUser.getLocale());
		if(user.getTimeZone() != null)
			updates.put("timeZone", updatedUser.getTimeZone());

		synchAddOrUpdateUser(PortalUtil.getCompany(req).getWebId(), 
				user.getScreenName(), null, updates);
		
		// Return old user object so as not to change the old behavior (whether
		// it was right or wrong).
		return user;
	}
	
	protected User updatePassword(ActionRequest req) throws Exception {
		User user = super.updatePassword(req);
				
		// If we're still here, it means that the password update was successful.
		synchAddOrUpdateUser(PortalUtil.getCompany(req).getWebId(),
				user.getScreenName(), ParamUtil.getString(req, "password1"), null);
		
		return user;
	}
	
	protected void deleteUsers(ActionRequest req) throws Exception {
		String cmd = ParamUtil.getString(req, Constants.CMD);

		long[] deleteUserIds = StringUtil.split(
			ParamUtil.getString(req, "deleteUserIds"), 0L);

		String companyWebId = PortalUtil.getCompany(req).getWebId();
		User user;
		for (int i = 0; i < deleteUserIds.length; i++) {
			if (cmd.equals(Constants.DEACTIVATE) ||
				cmd.equals(Constants.RESTORE)) {

				boolean active = !cmd.equals(Constants.DEACTIVATE);

				UserServiceUtil.updateActive(deleteUserIds[i], active);
			}
			else {
				user = UserLocalServiceUtil.getUserById(deleteUserIds[i]);
				
				// In ideal world, software will never fail. Since that's not where
				// we live, we need to think about damage control in the rare
				// cases of failure. If we successfully delete an user from Liferay
				// but fails to do it in ICEcore, we have an entry in ICEcore that
				// we can never re-try to delete again (that's because the current
				// version of Liferay provides no separate management UI for deleting 
				// users - it can only be done through portal UI). So attempting to
				// delete the user from ICEcore side first seems a bit more desirable.
				synchDeleteUser(companyWebId, user.getScreenName());
				
				UserServiceUtil.deleteUser(deleteUserIds[i]);
			}
		}
	}

	protected void synchAddOrUpdateUser(String contextCompanyWebId, 
			String userScreenName, String userPassword, Map updates) throws Exception {
		AuthenticationManager.authenticate(contextCompanyWebId, userScreenName, userPassword, updates);
	}
	
	protected void synchDeleteUser(String contextCompanyWebId, String userScreenName)
	throws Exception {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(SiteScapeBridgeUtil.getSSFContextPath());

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_DELETE_USER);
		if(contextCompanyWebId != null)
			req.setParameter(CrossContextConstants.ZONE_NAME, contextCompanyWebId);
		req.setParameter(CrossContextConstants.SCREEN_NAME, userScreenName);
		
		NullServletResponse res = new NullServletResponse();
		
		SiteScapeBridgeUtil.include(req, res);
	}
	
	protected void modifyScreenName(String contextCompanyWebId, 
			String oldScreenName, String newScreenName) throws Exception {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(SiteScapeBridgeUtil.getSSFContextPath());

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_MODIFY_SCREEN_NAME);
		if(contextCompanyWebId != null)
			req.setParameter(CrossContextConstants.ZONE_NAME, contextCompanyWebId);
		req.setParameter(CrossContextConstants.OLD_SCREEN_NAME, oldScreenName);
		req.setParameter(CrossContextConstants.SCREEN_NAME, newScreenName);
		
		NullServletResponse res = new NullServletResponse();
		
		SiteScapeBridgeUtil.include(req, res);
	}

}
