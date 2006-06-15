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
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
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
		
		Map<String,Object> model = new HashMap<String,Object>();
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}

		Long binderId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);						
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_RELOAD_LISTING)) {
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
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		Binder binder = getBinderModule().getBinder(binderId);

		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
	
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, userProperties);
		model.put(WebKeys.DASHBOARD, ssDashboard);

		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Document searchFilter = null;
		if (searchFilterName != null && !searchFilterName.equals("")) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			searchFilter = (Document)searchFilters.get(searchFilterName);
		}
		//See if the user has selected a specific view to use
        UserProperties uProps = getProfileModule().getUserProperties(user.getId(), binderId);
		String userDefaultDef = (String)uProps.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		DefinitionUtils.getDefinitions(binder, model, userDefaultDef);
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
		model.put(WebKeys.FOLDER_TOOLBAR, buildWorkspaceToolbar(response, ws, ws.getId().toString()).getToolbar());
		
	}  
	protected Toolbar buildWorkspaceToolbar(RenderResponse response, Workspace workspace, String forumId) {
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
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL("ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			toolbar.addToolbarMenuItem("1_add", "workspace", NLT.get("toolbar.menu.addWorkspace"), adapterUrl.toString(), qualifiers);
		} catch (AccessControlException ac) {};

		//Add Folder
		try {
			getWorkspaceModule().checkAddFolderAllowed(workspace);
			if (addMenuCreated == false) {
				toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
				addMenuCreated=true;
			}
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL("ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityIdentifier().getEntityType().name());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			toolbar.addToolbarMenuItem("1_add", "folders", NLT.get("toolbar.menu.addFolder"), adapterUrl.toString(), qualifiers);
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
		
		return toolbar;
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