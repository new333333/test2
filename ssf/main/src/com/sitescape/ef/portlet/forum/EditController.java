package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

        //Make the prefs available to the jsp
		Map formData = request.getParameterMap();
		PortletPreferences prefs= request.getPreferences();
		String title = PortletRequestUtils.getStringParameter(request, "title", null);
		if (title != null) prefs.setValue(WebKeys.PORTLET_PREF_TITLE, title); 
		//see if type is being set
		if (formData.containsKey("applyBtn") || 
				formData.containsKey("okBtn")) {
			String displayType = (String)PortletRequestUtils.getStringParameter(request, "displayType");
			//	if not on form, must already be set.  
			if (Validator.isNull(displayType)) { 
				displayType = prefs.getValue(WebKeys.PORTLET_PREF_TYPE, "");
				if ("ss_forum".equals(displayType)) {
					List forumPrefIdList = new ArrayList();
		
					//	Get the forums to be displayed
					Iterator itFormData = formData.entrySet().iterator();
					while (itFormData.hasNext()) {
						Map.Entry me = (Map.Entry) itFormData.next();
						if (((String)me.getKey()).startsWith("id_")) {
							String forumId = ((String)me.getKey()).substring(3);
							forumPrefIdList.add(forumId);
						}
					}
		
					prefs.setValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, (String[]) forumPrefIdList.toArray(new String[forumPrefIdList.size()]));
				} else if ("ss_presence".equals(displayType)) {
					prefs.setValue(WebKeys.PRESENCE_PREF_USER_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("users")));
					prefs.setValue(WebKeys.PRESENCE_PREF_GROUP_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("groups"))); 			
				} else if ("ss_workspace".equals(displayType)) {
					String id = PortletRequestUtils.getStringParameter(request, "topWorkspace"); 
					if (Validator.isNotNull(id)) {
						prefs.setValue(WebKeys.WORKSPACE_PREF_ID, id);
					}
				}
			} else {
				//first time through - just set type
				prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
				//return to view
				if ("ss_profile".equals(displayType)) {
					response.setPortletMode(PortletMode.VIEW);
					response.setWindowState(WindowState.NORMAL);
				}
			}
		}
		prefs.store();
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
		PortletPreferences prefs= request.getPreferences();
        Map model = new HashMap();
		String title = (String)prefs.getValue(WebKeys.PORTLET_PREF_TITLE, null);
		if (title != null) response.setTitle(title);
		else title="";
		model.put("portletTitle", prefs.getValue(WebKeys.PORTLET_PREF_TITLE, ""));
		String displayType = prefs.getValue(WebKeys.PORTLET_PREF_TYPE, "");
		if ("ss_forum".equals(displayType)) {
		
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new BuildWsFolder());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		
			String[] forumPrefIdList = prefs.getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
		
			//	Build the jsp bean (sorted by folder title)
			List forumIdList = new ArrayList();
			List folderIds = new ArrayList();
			for (int i = 0; i < forumPrefIdList.length; i++) {
				forumIdList.add(forumPrefIdList[i]);
				folderIds.add(new Long(forumPrefIdList[i]));
			}
			Collection folders = getFolderModule().getFolders(folderIds);
		
			model.put(WebKeys.FOLDER_LIST, folders);
			model.put(WebKeys.BINDER_ID_LIST, folderIds);
			model.put(WebKeys.FORUM_ID_LIST, forumIdList);
			return new ModelAndView(WebKeys.VIEW_FORUM_EDIT, model);
		} else if ("ss_presence".equals(displayType)) {
			//This is the portlet view; get the configured list of principals to show
			Set<Long> userIds = new HashSet<Long>();
			userIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
			userIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));

			model.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(userIds));
			//Build the jsp bean (sorted by folder title)
//			List<Long> userIds = new ArrayList<Long>();
//			for (int i = 0; i < uIds.length; i++) {
//				userIds.add(new Long(uIds[i]));
//			}
//			List<Long> groupIds = new ArrayList<Long>();
//			for (int i = 0; i < gIds.length; i++) {
//				groupIds.add(new Long(gIds[i]));
//			}
//			model.put(WebKeys.USERS, getProfileModule().getUsers(userIds));
//			model.put(WebKeys.GROUPS, getProfileModule().getGroups(groupIds));
			
			return new ModelAndView(WebKeys.VIEW_PRESENCE_EDIT, model);
		} else if ("ss_workspace".equals(displayType)) {
				
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new BuildWs());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			
			String wsId = prefs.getValue(WebKeys.WORKSPACE_PREF_ID, null);
			try {
				Workspace ws;
				if (Validator.isNull(wsId)) ws = getWorkspaceModule().getWorkspace();	
				else ws = getWorkspaceModule().getWorkspace(Long.valueOf(wsId));				
				model.put(WebKeys.BINDER, ws);
			} catch (Exception ex) {};
			return new ModelAndView(WebKeys.VIEW_WORKSPACE_EDIT, model);
		}
		return null;
	}

	private static class BuildWsFolder implements DomTreeBuilder {
		public Element setupDomElement(String type, Object source, Element element) {
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
				element.addAttribute("id", "");
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
				element.addAttribute("url", "");
			} else return null;
			return element;
		}
	}
	private static class BuildWs implements DomTreeBuilder {
		public Element setupDomElement(String type, Object source, Element element) {
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
				element.addAttribute("url", "");
			} else return null;
			return element;
		}
	}
}
