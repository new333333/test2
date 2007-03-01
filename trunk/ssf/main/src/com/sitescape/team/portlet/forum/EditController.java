package com.sitescape.team.portlet.forum;

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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.FolderConfigHelper;
import com.sitescape.team.web.tree.WorkspaceConfigHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletPreferencesUtil;
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
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
		}
		//see if type is being set
		if (formData.containsKey("applyBtn") || 
				formData.containsKey("okBtn")) {
			String displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, "");
			//	if not on form, must already be set.  
			if (Validator.isNull(displayType)) { 
				displayType = ViewController.getDisplayType(request);
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
				if (forumPrefIdList.size() > 0) {
					prefs.setValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, (String[]) forumPrefIdList.toArray(new String[forumPrefIdList.size()]));
				}

			} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.WIKI_PORTLET.equals(displayType) ||
					ViewController.SEARCH_PORTLET.equals(displayType) ||
					ViewController.GALLERY_PORTLET.equals(displayType)) {
				String id = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
				DashboardPortlet d=null;
				if (id != null) {
					try {
						d = (DashboardPortlet)getDashboardModule().getDashboard(id);
					} catch (NoObjectByTheIdException no) {}
				}
				if (d == null) {
					PortletConfig pConfig = (PortletConfig)request.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);
					d = getDashboardModule().createDashboardPortlet( pConfig.getPortletName(), DashboardHelper.getNewDashboardMap());
					DashboardHelper.addComponent(request, d, DashboardHelper.Wide_Top, DashboardHelper.Portlet);
					prefs.setValue(WebKeys.PORTLET_PREF_DASHBOARD, d.getId());
					prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
				}
				DashboardHelper.saveComponentData(request, d, DashboardHelper.PORTLET_COMPONENT_ID);

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
		String displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, "");
		if (Validator.isNull(displayType)) {
			displayType = ViewController.getDisplayType(request);
			
		}
		if (ViewController.FORUM_PORTLET.equals(displayType)) {	
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new FolderConfigHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);	
		
			String[] forumPrefIdList = PortletPreferencesUtil.getValues(prefs, WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
		
			//	Build the jsp bean (sorted by folder title)
			List folderIds = new ArrayList();
			for (int i = 0; i < forumPrefIdList.length; i++) {
				folderIds.add(Long.valueOf(forumPrefIdList[i]));
			}
			Collection folders = getFolderModule().getFolders(folderIds);
		
			model.put(WebKeys.FOLDER_LIST, folders);
			model.put(WebKeys.BINDER_ID_LIST, folderIds);
			return new ModelAndView(WebKeys.VIEW_FORUM_EDIT, model);
		} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_BLOG_EDIT, "blog");
		} else if (ViewController.GALLERY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_GALLERY_EDIT, "gallery");			
		} else if (ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_GUESTBOOK_EDIT, "guestbook");			
		} else if (ViewController.WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_WIKI_EDIT, "wiki");
		} else if (ViewController.SEARCH_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_SEARCH_EDIT, "search");
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
					new WsDomTreeBuilder(null, true, this, new WorkspaceConfigHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
			
			String wsId = PortletPreferencesUtil.getValue(prefs, WebKeys.WORKSPACE_PREF_ID, null);
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
	private ModelAndView setupSummaryPortlet(RenderRequest request, PortletPreferences prefs, Map model, String view, String componentName) {
		Map userProperties = (Map) getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		String id = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (id != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(id);
				DashboardHelper.getDashboardMap(d, userProperties, model, true);
			} catch (Exception no) {}
		} else {
			//setup dummy dashboard for config
			DashboardHelper.initDashboardComponent(userProperties, model, componentName);
		}
		return new ModelAndView(view, model);
		
	}

}
