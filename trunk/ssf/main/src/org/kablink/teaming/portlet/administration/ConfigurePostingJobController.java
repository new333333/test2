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
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			MailConfig mailConfig = new MailConfig(getAdminModule().getMailConfig());
			//not present if totally disabled
			ScheduleInfo posting=null;
			if (mailConfig.isPostingEnabled()) { //if feature enabled
				posting = getAdminModule().getPostingSchedule();
				posting.setSchedule(ScheduleHelper.getSchedule(request, "post"));
				posting.setEnabled(PortletRequestUtils.getBooleanParameter(request, "postenabled", false));
			}
			mailConfig.setSimpleUrlPostingEnabled(PortletRequestUtils.getBooleanParameter(request, "simplepostenabled", false));
			Long outgoingAttachmentSizeLimit = PortletRequestUtils.getLongParameter(request, "outgoingAttachmentSizeLimit");
			Long outgoingAttachmentSumLimit = PortletRequestUtils.getLongParameter(request, "outgoingAttachmentSumLimit");
			if (outgoingAttachmentSizeLimit != null) {
				//Translate to actual bytes
				outgoingAttachmentSizeLimit = outgoingAttachmentSizeLimit * 1024;
			}
			if (outgoingAttachmentSumLimit != null) {
				//Translate to actual bytes
				outgoingAttachmentSumLimit = outgoingAttachmentSumLimit * 1024;
			}
			mailConfig.setOutgoingAttachmentSizeLimit(outgoingAttachmentSizeLimit);
			mailConfig.setOutgoingAttachmentSumLimit(outgoingAttachmentSumLimit);
			
			int pos =0;
			Map updates = new HashMap();
			while (true) {
				if (!formData.containsKey("alias" + pos))
					break;
				String alias = PortletRequestUtils.getStringParameter(request, "alias" + pos, "", false).trim().toLowerCase();
				String password = PortletRequestUtils.getStringParameter(request, "password" + pos, null, false);
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
			ScheduleInfo notify = getAdminModule().getNotificationSchedule();
			notify.setSchedule(ScheduleHelper.getSchedule(request, "notify"));
			notify.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "notifyenabled", false));
			mailConfig.setSendMailEnabled(notify.isEnabled());
			getAdminModule().setMailConfigAndSchedules(mailConfig, notify, posting);
			response.setRenderParameters(formData);
	} else
		response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		HashMap model = new HashMap();
		MailConfig mConfig = getAdminModule().getMailConfig();
		model.put(WebKeys.MAIL_CONFIG, mConfig);
		if (SPropsUtil.getBoolean("smtp.service.enable")) model.put(WebKeys.SMPT_ENABLED, Boolean.TRUE);
		ScheduleInfo config = getAdminModule().getNotificationSchedule();
		model.put(WebKeys.SCHEDULE_INFO + "notify", config);
		if (mConfig.isPostingEnabled()) {
			config = getAdminModule().getPostingSchedule();
			model.put(WebKeys.SCHEDULE_INFO + "post", config);	
			model.put(WebKeys.POSTINGS, getAdminModule().getPostings());
			model.put(WebKeys.MAIL_POSTING_USE_ALIASES, SPropsUtil.getString("mail.posting.useAliases", "false"));
		}
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model);
	}

}
