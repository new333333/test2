package com.sitescape.ef.portlet.administration;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.util.StringUtil;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.ScheduleHelper;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.jobs.ScheduleInfo;
public class ConfigureNotifyController extends  SAbstractController  {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
			Set userList = new HashSet();
			long [] gIds = PortletRequestUtils.getLongParameters(request, "sendToGroups");
			for (int i=0; i<gIds.length; ++i) {
				userList.add(new Long(gIds[i]));
			}
			long [] uIds = PortletRequestUtils.getLongParameters(request, "sendToUsers");
			for (int i=0; i<uIds.length; ++i) {
				userList.add(new Long(uIds[i]));
			}
			ScheduleInfo config = getAdminModule().getNotificationConfig(folderId);
			getScheduleData(request, config);
			getAdminModule().setNotificationConfig(folderId, config);
			
			getAdminModule().modifyNotification(folderId, getNotifyData(request), userList);
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		} else if (formData.containsKey("listUsers")) {
			response.setRenderParameters(formData);
			response.setRenderParameter("showUsers", "1");				
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		try {
			Map model = new HashMap();
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
			Folder folder = getFolderModule().getFolder(folderId);
			model.put(WebKeys.FOLDER, folder);

			List groups = getProfileModule().getGroups();
			model.put(WebKeys.GROUPS, groups);
			ScheduleInfo config = getAdminModule().getNotificationConfig(folderId);
			model.put(WebKeys.SCHEDULE_INFO, config);
			if (PortletRequestUtils.getStringParameter(request, "showUsers", "0").equals("1")) {
				//get any partially entered data
				NotificationDef notify = new NotificationDef();
		    	ObjectBuilder.updateObject(notify, getNotifyData(request));
				model.put(WebKeys.NOTIFICATION, notify); 
				
				getScheduleData(request, config);
				Map gList = new HashMap();
				Map uList = new HashMap();
				long [] gIds = PortletRequestUtils.getLongParameters(request, "sendToGroups");
				for (int i=0; i<gIds.length; ++i) {
					gList.put(new Long(gIds[i]),Boolean.TRUE);
				}
/*				List users = getProfileModule().getUsers();
				model.put(WebKeys.USERS, users );
				//Go through selected list and add only users .  Already have new groups accounted for from input form
				List defaultDistribution = folder.getNotificationDef().getDefaultDistribution();
				for (int i=0; i<defaultDistribution.size(); ++i) {
					Principal id = ((Notification)defaultDistribution.get(i)).getSendTo();
					if (users.contains(id)) uList.put(id.getId(), Boolean.TRUE);
				}
*/				model.put(WebKeys.SELECTED_USERS, uList);
				model.put(WebKeys.SELECTED_GROUPS, gList);
				
			} else {
				NotificationDef notify = folder.getNotificationDef();
				model.put(WebKeys.NOTIFICATION, notify); 
				List defaultDistribution = folder.getNotificationDef().getDefaultDistribution();
				Map gList = new HashMap();
				Map uList = new HashMap();
				for (int i=0; i<defaultDistribution.size(); ++i) {
					Principal id = ((Notification)defaultDistribution.get(i)).getSendTo();
					if (groups.contains(id)) gList.put(id.getId(), Boolean.TRUE); 
					else uList.put(id.getId(), Boolean.TRUE);
				}
			
				model.put(WebKeys.SELECTED_USERS, uList);
				model.put(WebKeys.SELECTED_GROUPS, gList);
			}
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_NOTIFICATION, model);		
			
		} catch (Exception e) {
			//assume not selected yet
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new TreeHelper(response));
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_NOTIFICATION, WebKeys.WORKSPACE_DOM_TREE, wsTree);		
		}
		
	}
	private Map getNotifyData(PortletRequest request) {
		Map input = new HashMap();
		
		String val = PortletRequestUtils.getStringParameter(request, "emailAddress", "");
		input.put("emailAddress", StringUtil.split(val, "\n"));
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
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", ws.getId().toString());
				element.addAttribute("image", "workspace");
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "forum");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "forum");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.NOTIFY_ACTION_CONFIGURE);
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
