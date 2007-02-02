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
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.DomTreeHelper;
import com.sitescape.ef.module.shared.WsDomTreeBuilder;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {

        //Make the prefs available to the jsp
		Map formData = request.getParameterMap();
		PortletPreferences prefs= request.getPreferences();
		String title = PortletRequestUtils.getStringParameter(request, "title", null);
		if (title != null) prefs.setValue(WebKeys.PORTLET_PREF_TITLE, title); 
		//see if type is being set
		if (formData.containsKey("applyBtn") || 
				formData.containsKey("okBtn")) {
			String displayType = prefs.getValue(WebKeys.PORTLET_PREF_TYPE, "");
			//	if not on form, must already be set.  
			if (Validator.isNull(displayType)) { 
				displayType = getDisplayType(request);
				prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
			}
			if (ViewController.FORUM_PORTLET.equals(displayType)) {
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
			} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
				prefs.setValue(WebKeys.PRESENCE_PREF_USER_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("users")));
				prefs.setValue(WebKeys.PRESENCE_PREF_GROUP_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("groups"))); 			
			} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				String id = PortletRequestUtils.getStringParameter(request, "topWorkspace"); 
				if (Validator.isNotNull(id)) {
					prefs.setValue(WebKeys.WORKSPACE_PREF_ID, id);
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
		if (Validator.isNull(displayType)) {
			displayType = getDisplayType(request);
			
		}
		if (ViewController.FORUM_PORTLET.equals(displayType)) {
		
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new folderTree()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
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
		} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
			//This is the portlet view; get the configured list of principals to show
			Set<Long> userIds = new HashSet<Long>();
			Set<Long> groupIds = new HashSet<Long>();
			userIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
			groupIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));

			model.put(WebKeys.USERS, getProfileModule().getUsers(userIds));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(groupIds));			
			return new ModelAndView(WebKeys.VIEW_PRESENCE_EDIT, model);
		} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new wsTree()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
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
	private String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		if (pName.contains(ViewController.FORUM_PORTLET))
			return ViewController.FORUM_PORTLET;
		else if (pName.contains(ViewController.WORKSPACE_PORTLET))
			return ViewController.WORKSPACE_PORTLET;
		else if (pName.contains(ViewController.PRESENCE_PORTLET))
			return ViewController.PRESENCE_PORTLET;
		return null;

	}
	public static class wsTree implements DomTreeHelper {
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			return bs.getBinderModule().hasBinders((Binder)source, EntityType.workspace);
		}
		
	
		public String getAction(int type, Object source) {
			return null;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			return "false";
		}
		public String getTreeNameKey() {return "editWs";}
		
	}	
	public static class folderTree implements DomTreeHelper {
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
			if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			return bs.getBinderModule().hasBinders((Binder)source);
		}
	
		public String getAction(int type, Object source) {
			return null;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_FOLDER) return "false";
			return "true";
		}
		public String getTreeNameKey() {return "editForum";}
		
	}	
}
