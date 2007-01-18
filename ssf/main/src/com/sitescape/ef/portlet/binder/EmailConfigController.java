package com.sitescape.ef.portlet.binder;
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

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.ScheduleHelper;
import com.sitescape.ef.web.util.BinderHelper.TreeBuilder;
import com.sitescape.util.Validator;

public class EmailConfigController extends  AbstractBinderController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (formData.containsKey("okBtn")) {
			if (formData.containsKey("alias")) {
				String alias = PortletRequestUtils.getStringParameter(request, "alias", null);
				if (!Validator.isNull(alias)) getBinderModule().setPosting(folderId, alias);
				else getBinderModule().deletePosting(folderId);
			}
			//sub-folders don't have a schedule, use addresses to figure it out
			if (formData.containsKey("addresses")) {
				Set userList = new HashSet();
				if (formData.containsKey("users")) userList.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("users")));
				if (formData.containsKey("groups")) userList.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groups")));
				ScheduleInfo config = getBinderModule().getNotificationConfig(folderId);
				getScheduleData(request, config);
				getBinderModule().setNotificationConfig(folderId, config);			
				getBinderModule().modifyNotification(folderId, getNotifyData(request), userList);
			}
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (folderId != null) {
				Binder binder = getBinderModule().getBinder(folderId);
				setupViewBinder(response, binder);
				response.setRenderParameter("ssReloadUrl", "");
			} else {
				response.setRenderParameter("redirect", "true");
			}
		} else {
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
			BinderHelper.buildNavigationLinkBeans(this, folder, model, BinderHelper.TreeBuilder.EMAIL_KEY);

			if (folder.isTop()) {
				ScheduleInfo config = getBinderModule().getNotificationConfig(folderId);
				model.put(WebKeys.SCHEDULE_INFO, config);
				List defaultDistribution = folder.getNotificationDef().getDistribution();
				Set gList = new HashSet();
				Set uList = new HashSet();
				for (int i=0; i<defaultDistribution.size(); ++i) {
					Principal id = ((Principal)defaultDistribution.get(i));
					if (id.getEntityIdentifier().getEntityType().name().equals(EntityType.group.name()))
					 		gList.add(id); 
					else uList.add(id);
				}
	
				model.put(WebKeys.USERS, uList);
				model.put(WebKeys.GROUPS, gList);
			}
			model.put(WebKeys.POSTINGS, getAdminModule().getPostings());
			return new ModelAndView(WebKeys.VIEW_BINDER_CONFIGURE_EMAIL, model);		
		} catch (Exception e) {
			//assume not selected yet - first time through from admin menu
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new TreeBuilder(null, true, this, TreeBuilder.EMAIL_KEY),1);
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
