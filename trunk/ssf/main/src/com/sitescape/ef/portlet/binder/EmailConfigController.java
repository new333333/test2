package com.sitescape.ef.portlet.binder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.ScheduleHelper;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

public class EmailConfigController extends  AbstractBinderController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		if (formData.containsKey("okBtn")) {
			if (formData.containsKey("alias")) {
				String alias = PortletRequestUtils.getStringParameter(request, "alias", null);
				if (!Validator.isNull(alias)) getBinderModule().setPosting(folderId, alias);
				else getBinderModule().deletePosting(folderId);
			}
			if (formData.containsKey("enabled")) {
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
			Binder binder = getBinderModule().getBinder(folderId);
			setupViewBinder(response, binder);
			response.setRenderParameter("ssReloadUrl", "");
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Folder folder = getFolderModule().getFolder(folderId);
		model.put(WebKeys.BINDER, folder);

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
			
		
	}
	private Map getNotifyData(PortletRequest request) {
		Map input = new HashMap();
		
		String val = PortletRequestUtils.getStringParameter(request, "emailAddress", "");
		input.put("emailAddress", StringUtil.split(val, "\n"));
		input.put("teamOn", PortletRequestUtils.getBooleanParameter(request,  "teamOn", false));
		return input;
		
	}
	private void getScheduleData(PortletRequest request, ScheduleInfo config) {
		config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));
		config.setSchedule(ScheduleHelper.getSchedule(request));

	}
	private class TreeHelper implements DomTreeBuilder {
		private RenderResponse response;
		public TreeHelper(RenderResponse response) {
			this.response = response;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			PortletURL url;
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				String icon = ws.getIconName();
				String imageClass = "ss_twIcon";
				if (icon == null || icon.equals("")) {
					icon = "/icons/workspace.gif";
					imageClass = "ss_twImg";
				}
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", ws.getId().toString());
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", imageClass);
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				String icon = f.getIconName();
				if (icon == null || icon.equals("")) icon = "/icons/folder.png";
				element.addAttribute("type", "folder");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", "ss_twIcon");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_NOTIFY_CONFIGURE);
				url.setParameter(WebKeys.URL_BINDER_ID, f.getId().toString());
				try {
					url.setWindowState(WindowState.MAXIMIZED);
				} catch (Exception e) {};
				try {
					url.setPortletMode(PortletMode.VIEW);
				} catch (Exception e) {};
				element.addAttribute("url", url.toString());

			} else return null;
			return element;
		}
	}
}
