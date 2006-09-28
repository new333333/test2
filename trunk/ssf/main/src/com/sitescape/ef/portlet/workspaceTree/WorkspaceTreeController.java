package com.sitescape.ef.portlet.workspaceTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.rss.util.UrlUtil;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Tabs;
import com.sitescape.ef.web.util.Toolbar;

/**
 * @author Peter Hurley
 *
 */
public class WorkspaceTreeController extends SAbstractController  {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
        User user = RequestContextHolder.getRequestContext().getUser();
		Map<String,Object> model = new HashMap<String,Object>();
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}

		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);						
		//see if it is a user workspace - can also get directly to user ws by a binderId
		//so don't assume anything here.  This just allows us to handle users without a workspace.
		Long entryId =  PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (entryId != null) {
			User entry = (User)getProfileModule().getEntry(binderId, entryId);
			//add one
			if (entry.getWorkspaceId() == null) {
				Map data = new HashMap();
				data.put("title", entry.getName());
				MapInputData inputData = new MapInputData(data);
				binderId = getProfileModule().addWorkspace(binderId, entryId, null, inputData, null);
			} else {
				binderId = entry.getWorkspaceId();
			}
		}


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
			tabs.setCurrentTab(tabs.addTab(binder));
		} else if (tabId != null) {
			tabs.setCurrentTab(tabs.setTab(tabId.intValue(), binder));
		} else {
			//Don't overwrite a search tab
			if (tabs.getTabType(tabs.getCurrentTab()).equals(Tabs.QUERY)) {
				tabs.setCurrentTab(tabs.addTab(binder));
			} else {
				tabs.setCurrentTab(tabs.setTab(binder));
			}
		}
		model.put(WebKeys.TABS, tabs.getTabs());

		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
	
		Map userProperties = getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, model);
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));

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
		getShowWorkspace(formData, request, response, (Workspace)binder, searchFilter, model);
			
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		
		return new ModelAndView(WebKeys.VIEW_WORKSPACE, model);
	}
	protected void getShowWorkspace(Map formData, RenderRequest req, RenderResponse response, Workspace ws, Document searchFilter, Map<String,Object>model) throws PortletRequestBindingException {
		Document wsTree;

//		if (searchFilter != null) {
//			wsEntries = getWorkspaceModule().getWorkspaceTree(wsId, searchFilter);
//		} else {
			Long top = PortletRequestUtils.getLongParameter(req, WebKeys.URL_OPERATION2);
			if ((top != null) && (ws.getParentBinder() != null)) {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(top, ws.getId(), new WsTreeBuilder(ws, true, getBinderModule()));
			} else {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsTreeBuilder(ws, true, getBinderModule()),1);
			}
//		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		buildWorkspaceToolbar(req, response, model, ws, ws.getId().toString());
		
	}  
	protected void buildWorkspaceToolbar(RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace, 
			String forumId) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu
		PortletURL url;
		boolean addMenuCreated=false;
		
		//Add Workspace
		try {
			getWorkspaceModule().checkAddWorkspaceAllowed(workspace);
			toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
			addMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			toolbar.addToolbarMenuItem("1_add", "workspace", NLT.get("toolbar.menu.addWorkspace"), url);
		} catch (AccessControlException ac) {};

		//Add Folder
		try {
			getWorkspaceModule().checkAddFolderAllowed(workspace);
			if (addMenuCreated == false) {
				toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
				addMenuCreated=true;
			}
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_add", "folders", NLT.get("toolbar.menu.addFolder"), url);
		} catch (AccessControlException ac) {};
		
		//The "Administration" menu
		toolbar.addToolbarMenu("3_administration", NLT.get("toolbar.administration"));
		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.accessControl"), url);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//Delete
		try {
			getBinderModule().checkDeleteBinderAllowed(workspace);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.delete_workspace"), url);
		} catch (AccessControlException ac) {};

		//Modify
		try {
			getBinderModule().checkModifyBinderAllowed(workspace);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.modify_workspace"), url, qualifiers);
		} catch (AccessControlException ac) {};
		
		//Move
		try {
			getBinderModule().checkMoveBinderAllowed(workspace);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			toolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.move_workspace"), url);
		} catch (AccessControlException ac) {};
		
		//	The "Add penlets" menu
		Map qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_addDashboardComponents();return false;");
		toolbar.addToolbarMenu("4_addPenlets", NLT.get("toolbar.addPenlets"), "#", qualifiers);

		//The "Footer" menu
		//RSS link 
		Toolbar footerToolbar = new Toolbar();
		footerToolbar.addToolbarMenu("subscribeToFolder", NLT.get("toolbar.menu.subscribeToFolder"), UrlUtil.getFeedURL(request, forumId));
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.permalink"), adapterUrl.toString());
		
		footerToolbar.addToolbarMenu("RSS", NLT.get("toolbar.menu.rss"), UrlUtil.getFeedURL(request, forumId));
		
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
	}
		
	public static class WsTreeBuilder implements DomTreeBuilder {
		Workspace bottom;
		boolean check;
		BinderModule binderModule;
		public WsTreeBuilder(Workspace ws, boolean checkChildren, BinderModule binderModule) {
			this.bottom = ws;
			this.check = checkChildren;
			this.binderModule = binderModule;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			Binder binder = (Binder) source;
			element.addAttribute("title", binder.getTitle());
			element.addAttribute("id", binder.getId().toString());

			//only need this information if this is the bottom of the tree
			if (check && bottom.equals(binder.getParentBinder())) {
				if (binderModule.hasBinders(binder)) {
					element.addAttribute("hasChildren", "true");
				} else {	
					element.addAttribute("hasChildren", "false");
				}
			}
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				String icon = ws.getIconName();
				String imageClass = "ss_twIcon";
				if (icon == null || icon.equals("")) {
					icon = "/icons/workspace.gif";
					imageClass = "ss_twImg";
				}
				element.addAttribute("type", "workspace");
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", imageClass);
				element.addAttribute("action", WebKeys.ACTION_VIEW_WS_LISTING);
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				String icon = f.getIconName();
				if (icon == null || icon.equals("")) icon = "/icons/folder.png";
				element.addAttribute("type", "folder");
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", "ss_twIcon");
				element.addAttribute("action", WebKeys.ACTION_VIEW_FOLDER_LISTING);
			} else return null;
			return element;
		}
	}
}