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
package org.kablink.teaming.portlet.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Janet McCann
 * 
 */
public class ReportsController extends AbstractBinderController {

	public void handleActionRequestAfterValidation(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request,
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request,
				WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		
		if (getBinderModule().testAccess(binder, BinderOperation.report)) {
			PortletURL url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
			model.put("ss_activityReportUrl", url.toString());
			
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ENTRY_ACL_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
			model.put("ss_entryAclReportUrl", url.toString());
		}

		return new ModelAndView(WebKeys.VIEW_BINDER_REPORTS, model);
	}

}
