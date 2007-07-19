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
package com.sitescape.team.portlet.binder;
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

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.MailTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.ScheduleHelper;
import com.sitescape.util.Validator;

public class EmailConfigController extends  AbstractBinderController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (folderId != null) {
				Binder binder = getBinderModule().getBinder(folderId);
				setupViewBinder(response, binder);
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
			} else {
				response.setRenderParameter("redirect", "true");
			}
		} else  {
			if (formData.containsKey("alias")) {
				String alias = PortletRequestUtils.getStringParameter(request, "alias", null);
				getBinderModule().setPosting(folderId, alias);
			}
			//sub-folders don't have a schedule, use addresses to figure it out
			if (formData.containsKey("addresses")) {
				Set userList = new HashSet();
				if (formData.containsKey("users")) userList.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
				if (formData.containsKey("groups")) userList.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
				ScheduleInfo config = getBinderModule().getNotificationConfig(folderId);
				getScheduleData(request, config);
				getBinderModule().modifyNotification(folderId, getNotifyData(request), userList);
				getBinderModule().setNotificationConfig(folderId, config);			
			}
			response.setRenderParameters(formData);
		} 
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		try {
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
			Folder folder = getFolderModule().getFolder(folderId);
			model.put(WebKeys.BINDER, folder);
			model.put(WebKeys.DEFINITION_ENTRY, folder);
			//	Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(this, folder, model, new MailTreeHelper());

			if (folder.isTop()) {
				ScheduleInfo config = getBinderModule().getNotificationConfig(folderId);
				model.put(WebKeys.SCHEDULE_INFO, config);
				model.put(WebKeys.SCHEDULE, config.getSchedule());
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
			model.put(WebKeys.SCHEDULE_INFO2, getAdminModule().getPostingSchedule());
			return new ModelAndView(WebKeys.VIEW_BINDER_CONFIGURE_EMAIL, model);		
		} catch (Exception e) {
			//assume not selected yet - first time through from admin menu
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new MailTreeHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
			return new ModelAndView(WebKeys.VIEW_BINDER_CONFIGURE_EMAIL, model);
		}
			
		
	}
	private Map getNotifyData(PortletRequest request) {
		Map input = new HashMap();
		
		input.put("emailAddress", PortletRequestUtils.getStringParameter(request, "addresses", ""));
		input.put("teamOn", PortletRequestUtils.getBooleanParameter(request,  "teamMembers", false));
		return input;
		
	}
	private void getScheduleData(PortletRequest request, ScheduleInfo config) {
		config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));
		config.setSchedule(ScheduleHelper.getSchedule(request));

	}

}
