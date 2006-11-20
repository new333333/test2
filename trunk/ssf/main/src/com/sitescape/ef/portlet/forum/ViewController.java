package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.DashboardPortlet;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.BinderHelper.TreeBuilder;
import com.sitescape.util.Validator;



/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public static final String FORUM_PORTLET="ss_forum";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";
	public static final String PROFILE_PORTLET="ss_profile";
	public static final String DASHBOARD_PORTLET="ss_dashboard";
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		PortletPreferences prefs = request.getPreferences();
		String displayType = (String)prefs.getValue(WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			PortletConfig pConfig = (PortletConfig)request.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);
			String pName = pConfig.getPortletName();
			//For liferay we use instances and the name will be changed slightly
			//That is why we check for the name with contains
			if (pName.contains(FORUM_PORTLET))
				displayType=FORUM_PORTLET;
			else if (pName.contains(WORKSPACE_PORTLET))
				displayType=WORKSPACE_PORTLET;
			else if (pName.contains(PRESENCE_PORTLET))
				displayType=PRESENCE_PORTLET;
			else if (pName.contains(PROFILE_PORTLET))
				displayType=PROFILE_PORTLET;
			else if (pName.contains(DASHBOARD_PORTLET)) {
				displayType=DASHBOARD_PORTLET;
			}
			//TODO temporary until we figure out why adding a portlet freezes
			model.put("ssf_support_files_loaded", "1");
		}
			
        User user = RequestContextHolder.getRequestContext().getUser();

//TODO: liferay has a configuration option that handles the title.  Don't know about other portals
///		String title = (String)prefs.getValue(WebKeys.PORTLET_PREF_TITLE, null);
//		if (!Validator.isNull(title)) response.setTitle(title);
		if (FORUM_PORTLET.equals(displayType)) {
			//Build the toolbar and add it to the model
			buildForumToolbar(response.getNamespace(), model);
		
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = prefs.getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			model.put(WebKeys.FOLDER_LIST, getFolderModule().getFolders(binderIds));
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			return new ModelAndView(WebKeys.VIEW_FORUM, model);
		} else if (PROFILE_PORTLET.equals(displayType)) {
			//Get the profile binder
			//If first time here, add a profile folder to the top workspace
			ProfileBinder binder = getProfileModule().getProfileBinder();
				
			model.put(WebKeys.BINDER, binder);
			Toolbar toolbar = new Toolbar();
			PortletURL url;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			url.setWindowState(WindowState.MAXIMIZED);
			toolbar.addToolbarMenu("listing", NLT.get("profile.list", "List users"), url);
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			
			return new ModelAndView(WebKeys.VIEW_PROFILE, model);
		} else if (WORKSPACE_PORTLET.equals(displayType)) {
			String id = prefs.getValue(WebKeys.WORKSPACE_PREF_ID, null);
			Workspace binder;
			try {
				binder = getWorkspaceModule().getWorkspace(Long.valueOf(id));
			} catch (Exception ex) {
				binder = getWorkspaceModule().getWorkspace();				
			}
			Document wsTree;
			//when at the top, don't expand
			if (request.getWindowState().equals(WindowState.NORMAL)) {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new TreeBuilder(null, true, getBinderModule()), 0);
			} else {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new TreeBuilder((Workspace)binder, true, getBinderModule()), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
		    return new ModelAndView("workspacetree/view", model);
		    
		} else if (PRESENCE_PORTLET.equals(displayType)) {
 			Set ids = new HashSet();		
 			ids.addAll(FindIdsHelper.getIdsAsLongSet(prefs.getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
 			ids.addAll(FindIdsHelper.getIdsAsLongSet(prefs.getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
 			//This is the portlet view; get the configured list of principals to show
 			model.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
 			//if we list groups, then we have issues when a user appears in multiple groups??
 			//how do we update the correct divs??
 			//so, explode the groups and just show members
 			//TODO: either deal with groups correctly or remove
  			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
  			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
  			Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
  			if ((binderId != null) && (entryId != null)) {
  				model.put(WebKeys.URL_BINDER_ID, binderId);
  				model.put(WebKeys.URL_ENTRY_ID, entryId);
  			}
 			return new ModelAndView(WebKeys.VIEW_PRESENCE, model);		
		} else if (DASHBOARD_PORTLET.equals(displayType)) {
			DashboardPortlet d=null;
			String id = prefs.getValue(WebKeys.PORTLET_PREF_DASHBOARD, null);
			if (id != null) {
				d = (DashboardPortlet)getDashboardModule().getDashboard(id);
			}
			Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
			model.put(WebKeys.USER_PROPERTIES, userProperties);
			DashboardHelper.getDashboardMap(d, userProperties, model);
			Toolbar toolbar = new Toolbar();
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			toolbar.addToolbarMenu("1_manageDashboard", NLT.get("toolbar.manageDashboard"));
			Map qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
			toolbar.addToolbarMenuItem("1_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);
			if (d != null) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				toolbar.addToolbarMenuItem("1_manageDashboard", "2dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);

				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
					response.getNamespace() + "_dashboardComponentCanvas', 'dashboardId="+d.getId()+"');return false;");
				if (DashboardHelper.checkIfShowingAllComponents(d)) {
					toolbar.addToolbarMenu("2_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					toolbar.addToolbarMenu("2_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
				model.put(WebKeys.DASHBOARD_ID, d.getId());
			}
			return new ModelAndView(WebKeys.VIEW_DASHBOARD, model);		
		}
		return null;
	}


	protected void buildForumToolbar(String prefix, Map<String,Object> model) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();

		//The "Show unseen" menu
		String url = "javascript: ;";
		Map<String,Object> qualifiers = new HashMap<String,Object>();
		String name= prefix + "_getUnseenCounts";
		qualifiers.put("onClick", "if (" + name+ ") {" + name + "()};return false;");
		toolbar.addToolbarMenu("1_showunseen", NLT.get("toolbar.showUnseen"), url, qualifiers);

		model.put(WebKeys.FORUM_TOOLBAR, toolbar.getToolbar());
	}


}
