package com.sitescape.team.portlet.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class TeamMembersController extends SAbstractController  {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
 		Map<String,Object> model = new HashMap<String,Object>();

		User user = RequestContextHolder.getRequestContext().getUser();
		BinderHelper.setBinderPermaLink(this, request, response);

		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);						
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			String random = String.valueOf(new Random().nextInt(999999));
			reloadUrl.setParameter(WebKeys.URL_RANDOM, random);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, "noop");
			request.setAttribute("ssReloadUrl", reloadUrl.toString());
			return new ModelAndView(WebKeys.VIEW_WORKSPACE, model);
		}
		
		Map formData = request.getParameterMap();
		Binder binder = getBinderModule().getBinder(binderId);

 
 		//Check special options in the URL
		String[] debug = (String[])formData.get(WebKeys.URL_DEBUG);
		if (debug != null && (debug[0].equals(WebKeys.DEBUG_ON) || debug[0].equals(WebKeys.DEBUG_OFF))) {
			//The user is requesting debug mode to be turned on or off
			if (debug[0].equals(WebKeys.DEBUG_ON)) {
				getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(true));
			} else if (debug[0].equals(WebKeys.DEBUG_OFF)) {
				getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(false));
			}
		}
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);

		//Set up the tabs
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		if (newTab.equals("1")) {
			tabs.setCurrentTab(tabs.findTab(binder));
		} else if (newTab.equals("2")) {
			Map options = new HashMap();
			options.put(Tabs.TITLE, binder.getTitle() + ": " + NLT.get("tabs.teamMembers"));
			tabs.setCurrentTab(tabs.addTab(binder, options));
		} else if (tabId != null) {
			tabs.setCurrentTab(tabs.setTab(tabId.intValue(), binder));
		} else {
			//Don't overwrite a search tab
			if (tabs.getTabType(tabs.getCurrentTab()).equals(Tabs.QUERY)) {
				tabs.setCurrentTab(tabs.findTab(binder));
			} else {
				tabs.setCurrentTab(tabs.setTab(binder));
			}
		}
		model.put(WebKeys.TABS, tabs.getTabs());

		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, binder, model);
		
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		//See if this is a user workspace
		if ((binder.getDefinitionType() != null) && (binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW) &&
				binder.getOwner() != null) {
			Document profileDef = user.getEntryDef().getDefinition();
			model.put(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
			model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
					profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
			model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, "view");
			model.put(WebKeys.PROFILE_CONFIG_ENTRY, binder.getOwner());
		}
	
		Map userProperties = getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		DashboardHelper.getDashboardMap(binder, userProperties, model);
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Document searchFilter = null;
		if (searchFilterName != null && !searchFilterName.equals("")) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			searchFilter = (Document)searchFilters.get(searchFilterName);
		}
		//See if the user has selected a specific view to use
        UserProperties uProps = getProfileModule().getUserProperties(user.getId(), binderId);
		String userDefaultDef = (String)uProps.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		DefinitionHelper.getDefinitions(binder, model, userDefaultDef);
	

		getTeamMembers(formData, request, response, (Workspace)binder, model);
			
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		
		return new ModelAndView(WebKeys.VIEW_TEAM_MEMBERS, model);
	}
	protected void getTeamMembers(Map formData, RenderRequest req, RenderResponse response, Workspace ws, Map<String,Object>model) throws PortletRequestBindingException {
		List users = getBinderModule().getTeamMembers(ws.getId(), true);
		model.put(WebKeys.TEAM_MEMBERS, users);
		model.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
		buildWorkspaceToolbar(req, response, model, ws, ws.getId().toString());
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, ws.getId().toString());
		Map qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		model.put("send_mail_url", adapterUrl.toString());
		
	}
	
	protected void buildWorkspaceToolbar(RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace, 
			String forumId) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		//	The "Add" menu
		PortletURL url;
		boolean addMenuCreated=false;
		Binder parent = workspace.getParentBinder();
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		Map qualifiers = new HashMap();
//		qualifiers.put("popup", Boolean.TRUE);
		qualifiers.put("onClick", "appendUserIdsToURL(this); ss_toolbarPopupUrl(this.href);return false;");
		toolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
		
		//	The "Manage dashboard" menu
		if (DefinitionHelper.checkIfBinderShowingDashboard(workspace)) {
			boolean dashboardContentExists = false;
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			if (ssDashboard != null && ssDashboard.containsKey(WebKeys.DASHBOARD_COMPONENTS_LIST)) {
				Map dashboard = (Map)ssDashboard.get("dashboard");
				if (dashboard != null) {
					dashboardContentExists = DashboardHelper.checkIfContentExists(dashboard);
				}
			}
			dashboardToolbar.addToolbarMenu("5_manageDashboard", NLT.get("toolbar.manageDashboard"));
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
			dashboardToolbar.addToolbarMenuItem("5_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);
			
			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				dashboardToolbar.addToolbarMenuItem("5_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DASHBOARD_TITLE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter("_scope", "local");
				dashboardToolbar.addToolbarMenuItem("5_manageDashboard", "dashboard", NLT.get("dashboard.setTitle"), url);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter("_scope", "global");
				dashboardToolbar.addToolbarMenuItem("5_manageDashboard", "dashboard", NLT.get("dashboard.configure.global"), url);
	
				//Check the access rights of the user
				if (getBinderModule().testAccess(workspace, "setProperty")) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, forumId);
					url.setParameter("_scope", "binder");
					dashboardToolbar.addToolbarMenuItem("5_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				};
			
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', 'binderId=" +
						workspace.getId().toString()+"');return false;");
				
				if (DashboardHelper.checkIfShowingAllComponents(workspace)) {
					qualifiers.put("icon", "hideDashboard.gif");
					dashboardToolbar.addToolbarMenu("6_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					qualifiers.put("icon", "showDashboard.gif");
					dashboardToolbar.addToolbarMenu("6_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}

		//The "Footer" menu
		//RSS link 
		Toolbar footerToolbar = new Toolbar();
//		
//		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
//		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
//		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
//		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, workspace.getEntityType().toString());
//		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.workspacePermalink"), adapterUrl.toString());
//
//		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
//		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
//		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
//		Map qualifiers = new HashMap();
//		qualifiers.put("popup", Boolean.TRUE);
//		footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
//
//		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
//		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_START_MEETING);
//		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
//		adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, collectCreatorAndMoficationIds(workspace));
//		qualifiers = new HashMap();
//		qualifiers.put("popup", Boolean.TRUE);
//		footerToolbar.addToolbarMenu("startMeeting", NLT.get("toolbar.menu.startMeeting"), adapterUrl.toString(), qualifiers);
//
//		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
//		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SCHEDULE_MEETING);
//		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
//		adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, collectCreatorAndMoficationIds(workspace));
//		qualifiers = new HashMap();
//		qualifiers.put("popup", Boolean.TRUE);
//		footerToolbar.addToolbarMenu("scheduleMeeting", NLT.get("toolbar.menu.scheduleMeeting"), adapterUrl.toString(), qualifiers);

		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
	}
	private String[] collectCreatorAndMoficationIds(Workspace workspace) {
		Set principals = new HashSet();
		principals.add(workspace.getCreation().getPrincipal().getId().toString());
		principals.add(workspace.getModification().getPrincipal().getId().toString());
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

}
