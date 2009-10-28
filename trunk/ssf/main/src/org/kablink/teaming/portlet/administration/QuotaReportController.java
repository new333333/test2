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
import java.util.Map;

import javax.portlet.RenderRequest;

import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;


public class QuotaReportController extends  AbstractReportController {
	
	protected void populateModel(RenderRequest request, Map model) {
		super.populateModel(request, model);
		
		//Initialize the acl bean
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		accessControlMap.put("generateQuotaReport", getAdminModule().testAccess(AdminOperation.report));
	}

	protected String chooseView(Map formData) {
		String[] actions = (String[])formData.get("action");
		if (actions[0].equals("quota_report")) {
			 return WebKeys.VIEW_QUOTA_REPORT;
		} else if (actions[0].equals("quota_exceeded_report")) {
			return WebKeys.VIEW_QUOTA_EXCEEDED_REPORT;
		} else if (actions[0].equals("quota_highwater_exceeded_report")) {
			return WebKeys.VIEW_QUOTA_HIGHWATER_EXCEEDED_REPORT;
		}
		return WebKeys.VIEW_QUOTA_REPORT;
	}
}
