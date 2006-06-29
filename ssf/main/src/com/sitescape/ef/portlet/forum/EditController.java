package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController implements DomTreeBuilder {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

        //Make the prefs available to the jsp
        Map model = new HashMap();
		Map formData = request.getParameterMap();
		PortletPreferences prefs= request.getPreferences();
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		if ("ss_forum".equals(pName) || Validator.isNull(pName)) {
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
		} else if ("ss_presence".equals(pName)) {
			if (formData.containsKey("applyBtn")) {
				Set userIds = new HashSet();
				Set groupIds = new HashSet();
				if (formData.containsKey("users")) {
					String ids[] = (String[])formData.get("users");
					if (ids != null) {
						for (int i = 0; i < ids.length; i++) {
							String[] uIds = ids[i].split(" ");
							for (int j = 0; j < uIds.length; j++) {
								if (uIds[j].length() > 0) userIds.add(uIds[j].trim());
							}
						}
						
					}
				}
				if (formData.containsKey("groups")) {
					String ids[] = (String[])formData.get("groups");
					if (ids != null) {
						for (int i = 0; i < ids.length; i++) {
							String[] uIds = ids[i].split(" ");
							for (int j = 0; j < uIds.length; j++) {
								if (uIds[j].length() > 0) groupIds.add(uIds[j].trim());
							}
						}
						
					}
				}
		
				prefs.setValues(WebKeys.PRESENCE_PREF_USER_LIST, (String[]) userIds.toArray(new String[userIds.size()]));
				prefs.setValues(WebKeys.PRESENCE_PREF_GROUP_LIST, (String[]) groupIds.toArray(new String[groupIds.size()]));
			} 			
		}
		prefs.store();
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
        Map model = new HashMap();
		PortletPreferences prefs= request.getPreferences();
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		if ("ss_forum".equals(pName) || Validator.isNull(pName)) {
		
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(this);
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
			model.put(WebKeys.FOLDER_ID_LIST, folderIds);
			model.put(WebKeys.FORUM_ID_LIST, forumIdList);
			return new ModelAndView(WebKeys.VIEW_EDIT, model);
		} else if ("ss_presence".equals(pName)) {
			//This is the portlet view; get the configured list of principals to show
			String[] uIds = request.getPreferences().getValues(WebKeys.PRESENCE_PREF_USER_LIST, new String[0]);
			String[] gIds = request.getPreferences().getValues(WebKeys.PRESENCE_PREF_GROUP_LIST, new String[0]);

			Set<Long> userIds = new HashSet<Long>();
			for (int i = 0; i < uIds.length; i++) {
				userIds.add(new Long(uIds[i]));
			}
			for (int i = 0; i < gIds.length; i++) {
				userIds.add(new Long(gIds[i]));
			}

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
		}
		return null;
	}
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
