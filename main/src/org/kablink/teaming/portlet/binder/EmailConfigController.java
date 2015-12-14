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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.MailTreeHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class EmailConfigController extends  AbstractBinderController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupCloseWindow(response);
		} else  if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//sub-folders don't have a schedule, use addresses to figure it out
			if (formData.containsKey("addresses")) {
				Set userList = new HashSet();
				if (formData.containsKey("users")) userList.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
				if (formData.containsKey("groups")) userList.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
				getBinderModule().modifyNotification(folderId, userList, getNotifyData(request));
			}
			//See if there is a notification schedule for this folder
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			ScheduleInfo config = getFolderModule().getNotificationSchedule(zoneId, folderId);			
			config.setSchedule(ScheduleHelper.getSchedule(request, "notify"));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request, "enabled", false));
			getFolderModule().setNotificationSchedule(config, folderId);			

			if (formData.containsKey("alias")) {
				String alias = PortletRequestUtils.getStringParameter(request, "alias", null, false);
				String password = PortletRequestUtils.getStringParameter(request, "password", null, false);
				try {
					getBinderModule().setPosting(folderId, alias, password);
				} catch (Exception ne) {
					if (ne.getCause() != null)
						response.setRenderParameter(WebKeys.EXCEPTION, ne.getCause().getLocalizedMessage() != null ? ne.getCause().getLocalizedMessage() : ne.getCause().getMessage());
					else 
						response.setRenderParameter(WebKeys.EXCEPTION, ne.getLocalizedMessage() != null ? ne.getLocalizedMessage() : ne.getMessage());
				}
			}
		} 
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
		Folder folder = getFolderModule().getFolder(folderId);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.DEFINITION_ENTRY, folder);
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		
		//	Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, folder, model, new MailTreeHelper());
		//get top level schedule

		if (folder.isTop()) {
			ScheduleInfo config = getFolderModule().getNotificationSchedule(zoneId, folderId);		
			model.put(WebKeys.NOTIFICATION_SCHEDULE_INFO, config);
			
			config = getAdminModule().getNotificationSchedule();
			model.put(WebKeys.SCHEDULE_INFO, config);
			List defaultDistribution = folder.getNotificationDef().getDistribution();
			Set gList = new HashSet();
			Set uList = new HashSet();
			for (int i=0; i<defaultDistribution.size(); ++i) {
				Principal id = ((Principal)defaultDistribution.get(i));
				if (id.getEntityType().name().equals(EntityType.group.name()))
				 		gList.add(id); 
				else uList.add(id);
			}

			model.put(WebKeys.USERS, uList);
			model.put(WebKeys.GROUPS, gList); 
		}
		MailConfig mConfig = getAdminModule().getMailConfig();
		if (mConfig.isPostingEnabled()) {
			model.put(WebKeys.SCHEDULE_INFO2, getAdminModule().getPostingSchedule());
			model.put(WebKeys.MAIL_POSTING_USE_ALIASES, SPropsUtil.getString("mail.posting.useAliases", "false"));
		}
		return new ModelAndView(WebKeys.VIEW_BINDER_CONFIGURE_EMAIL, model);		
			
		
	}
	private Map getNotifyData(PortletRequest request) {
		Map input = new HashMap();
		
		input.put("emailAddress", PortletRequestUtils.getStringParameter(request, "addresses", ""));
		input.put("teamOn", PortletRequestUtils.getBooleanParameter(request,  "teamMembers", false));
		input.put("style", PortletRequestUtils.getIntParameter(request,  "style", Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION));
		return input;
		
	}


}
