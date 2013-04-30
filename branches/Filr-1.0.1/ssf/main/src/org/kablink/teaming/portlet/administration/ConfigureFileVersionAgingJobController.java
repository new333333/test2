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
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ConfigureFileVersionAgingJobController extends  SAbstractController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
			throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			String s_versionAge;
			Long versionAge = null;
			try {
				s_versionAge = PortletRequestUtils.getStringParameter(request, "maxVersionAge", "").trim();
				if (!s_versionAge.equals("")) {
					versionAge = Long.valueOf(s_versionAge);
				}
				getAdminModule().setFileVersionsMaxAge(versionAge);
			} catch (Exception ex) {
				// The value entered by the user must not be valid, don't set it.
			}

			ScheduleInfo fileVersionAgingScheduleInfo = null;
			Long fileVersionMaxAge = getAdminModule().getFileVersionsMaxAge();
			if (fileVersionMaxAge != null && fileVersionMaxAge > 0) {
				fileVersionAgingScheduleInfo = getAdminModule().getFileVersionAgingSchedule();
				fileVersionAgingScheduleInfo.setSchedule(ScheduleHelper.getSchedule(request, "post"));
				fileVersionAgingScheduleInfo.setEnabled(Boolean.TRUE);
			} else {
				fileVersionAgingScheduleInfo = getAdminModule().getFileVersionAgingSchedule();
				fileVersionAgingScheduleInfo.setEnabled(Boolean.FALSE);
			}
			response.setRenderParameters(formData);
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		HashMap model = new HashMap();
		Long fileVersionMaxAge = getAdminModule().getFileVersionsMaxAge();
		model.put(WebKeys.FILE_VERSION_MAXIMUM_AGE, fileVersionMaxAge);
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_FILE_VERSION_AGING_JOB, model);
	}

}
