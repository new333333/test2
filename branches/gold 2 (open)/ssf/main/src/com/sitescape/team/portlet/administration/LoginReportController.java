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
package com.sitescape.team.portlet.administration;
import java.util.Map;

import javax.portlet.RenderRequest;

import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;

public class LoginReportController extends  AbstractReportController {
	
	protected void populateModel(RenderRequest request, Map model) {
		super.populateModel(request, model);
		
		//Initialize the acl bean
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		accessControlMap.put("generateLoginReport", getAdminModule().testAccess(AdminOperation.report));
	}

	protected String chooseView(Map formData) {
		return WebKeys.VIEW_LOGIN_REPORT;
	}
}
