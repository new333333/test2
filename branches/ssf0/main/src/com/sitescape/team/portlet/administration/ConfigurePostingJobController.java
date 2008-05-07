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
package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.ScheduleHelper;
import com.sitescape.util.Validator;

public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			ScheduleInfo config = getAdminModule().getPostingSchedule();
			config.setSchedule(ScheduleHelper.getSchedule(request, "post"));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request, "postenabled", false));
			getAdminModule().setPostingSchedule(config);

			int pos =0;
			Map updates = new HashMap();
			while (true) {
				if (!formData.containsKey("alias" + pos))
					break;
				String alias = PortletRequestUtils.getStringParameter(request, "alias" + pos, "").trim().toLowerCase();
				String password = PortletRequestUtils.getStringParameter(request, "password" + pos, null);
				String aliasId=null;
				try {
					aliasId = PortletRequestUtils.getStringParameter(request, "aliasId" + pos);
				} catch (Exception ex) {};
				
				if (!formData.containsKey("delete" + pos)) {
					if (!Validator.isNull(alias)) {
						updates.put("emailAddress", alias);
						updates.put("password", password);
						getAdminModule().modifyPosting(aliasId, updates);
					}
				} else if (!Validator.isNull(aliasId)) getAdminModule().deletePosting(aliasId);
				++pos;
				updates.clear();
			}
			config = getAdminModule().getNotificationSchedule();
			config.setSchedule(ScheduleHelper.getSchedule(request, "notify"));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "notifyenabled", false));
			getAdminModule().setNotificationSchedule(config);			

			response.setRenderParameters(formData);
	} else if (formData.containsKey("closeBtn") || (formData.containsKey("cancelBtn"))) {
		response.setRenderParameter("redirect", "true");
	} else
		response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		HashMap model = new HashMap();
		ScheduleInfo config = getAdminModule().getPostingSchedule();
		model.put(WebKeys.SCHEDULE_INFO + "post", config);	
		model.put(WebKeys.POSTINGS, getAdminModule().getPostings());
		model.put(WebKeys.MAIL_POSTING_USE_ALIASES, SPropsUtil.getString("mail.posting.useAliases", "false"));
		config = getAdminModule().getNotificationSchedule();
		model.put(WebKeys.SCHEDULE_INFO + "notify", config);
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model);
	}

}
