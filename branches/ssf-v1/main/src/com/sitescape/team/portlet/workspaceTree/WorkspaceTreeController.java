/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.workspaceTree;

import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.profile.ProfileModule.ProfileOperation;
import com.sitescape.team.module.workspace.WorkspaceModule.WorkspaceOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
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
public class WorkspaceTreeController extends SAbstractController  {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
        User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
 		Map<String,Object> model = new HashMap<String,Object>();
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);

		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
			
		User user = RequestContextHolder.getRequestContext().getUser();
		BinderHelper.setBinderPermaLink(this, request, response);
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}

		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (operation.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			String random = String.valueOf(new Random().nextInt(999999));
			reloadUrl.setParameter(WebKeys.URL_RANDOM, random);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, "noop");
			request.setAttribute(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
			return new ModelAndView(WebKeys.VIEW_WORKSPACE, model);
		}

		Binder binder=null;
		String entryIdString =  PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Long entryId = null;
		if (Validator.isNotNull(entryIdString) && !entryIdString.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) {
			entryId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		}
		//see if it is a user workspace - can also get directly to user ws by a binderId
		//so don't assume anything here.  This just allows us to handle users without a workspace.
		if (entryId != null) {
			User entry = (User)getProfileModule().getEntry(binderId, entryId);
			if (entry.getWorkspaceId() == null) {
				binder = getProfileModule().addUserWorkspace(entry);
				if (binder == null) {
					// Redirect to profile list
					PortletURL reloadUrl = response.createRenderURL();
					reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
					reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
					reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
					reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryIdString);
					model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
					return new ModelAndView(WebKeys.VIEW_WORKSPACE, model);
				}
			} else {
				try {
					binder = getBinderModule().getBinder(entry.getWorkspaceId());
				} catch (NoBinderByTheIdException nb) {
					//reload entry
					entry = (User)getProfileModule().getEntry(binderId, entryId);
					binder = getProfileModule().addUserWorkspace(entry);	
				}
			}
			binderId = binder.getId();
			entryId = null;
		}

		
		Map formData = request.getParameterMap();
		try {
			if (binder == null) binder = getBinderModule().getBinder(binderId);
			getReportModule().addAuditTrail(AuditType.view, binder);
			BinderHelper.getBinderAccessibleUrl(this, binder, entryId, request, response, model);

 
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
			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(this, binder, model);
			BinderHelper.buildWorkspaceTreeBean(this, binder, model, null);
			
			//See if this is a user workspace
			if ((binder.getDefinitionType() != null) && 
					(binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				Principal owner = binder.getCreation().getPrincipal();  //get the user whose workspace this is
				if (owner != null) {
					//	turn creator into real object = not hibernate proxy
					try {
						User u = (User)getProfileModule().getEntry(owner.getParentBinder().getId(), owner.getId());
						model.put(WebKeys.PROFILE_CONFIG_ENTRY, u);							
						Document profileDef = u.getEntryDef().getDefinition();
						model.put(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
						model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
								profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
						model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, "view");
						model.put(WebKeys.USER_WORKSPACE, true);
					} catch (Exception ex) {} //user may have been deleted, but ws left around
				}
			}
		
			Map userProperties = getProfileModule().getUserProperties(user.getId()).getProperties();
			model.put(WebKeys.USER_PROPERTIES, userProperties);
			UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
			model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
			DashboardHelper.getDashboardMap(binder, userProperties, model);
//			model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
			String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
			Document searchFilter = null;
			if (searchFilterName != null && !searchFilterName.equals("")) {
				Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				searchFilter = (Document)searchFilters.get(searchFilterName);
			}
			//See if the user has selected a specific view to use
			String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			DefinitionHelper.getDefinitions(binder, model, userDefaultDef);
			
			
			if (operation.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				model.put(WebKeys.SHOW_TEAM_MEMBERS, true);
				getTeamMembers(formData, request, response, (Workspace)binder, model);
			} else {
				getShowWorkspace(formData, request, response, (Workspace)binder, searchFilter, model);
			}
			Map tagResults = TagUtil.uniqueTags(getBinderModule().getTags(binder));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		} catch(NoBinderByTheIdException e) {
		}
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);

		Tabs tabs = BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tabs.getTabs());		

		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		if(binder == null) {
			return new ModelAndView("binder/deleted_binder", model);
		}
		
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

		Long top = PortletRequestUtils.getLongParameter(req, WebKeys.URL_OPERATION2);
		if ((top != null) && (!ws.isRoot())) {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(top, ws.getId(), new WsDomTreeBuilder(ws, true, this));
		} else {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		
		//Get the info for the "add a team" button
		if (!ws.isRoot() && getWorkspaceModule().testAccess(ws, WorkspaceOperation.addWorkspace)) {
			Long cfgType = null;
			List result = getAdminModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			for (int i = 0; i < result.size(); i++) {
				TemplateBinder tb = (TemplateBinder) result.get(i);
				if (tb.getInternalId() != null && tb.getInternalId().toString().equals(ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG)) {
					//We have found the team workspace template, get its config id
					cfgType = tb.getId();
					break;
				}
			}
			if (cfgType != null) {
				PortletURL url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, ws.getId().toString());
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_TEAM_WORKSPACE);
				url.setParameter(WebKeys.URL_BINDER_CONFIG_ID, cfgType.toString());
				model.put(WebKeys.ADD_TEAM_WORKSPACE_URL, url);
			}
		}
		buildWorkspaceToolbar(req, response, model, ws, ws.getId().toString());
	}
	
	protected void getTeamMembers(Map formData, RenderRequest req, RenderResponse response, Workspace ws, Map<String,Object>model) throws PortletRequestBindingException {
		Collection users = getBinderModule().getTeamMembers(ws, true);
		model.put(WebKeys.TEAM_MEMBERS, users);
		model.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
		
		buildWorkspaceToolbar(req, response, model, ws, ws.getId().toString());
	}
	
	protected void buildWorkspaceToolbar(RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace, 
			String forumId) {
        User user = RequestContextHolder.getRequestContext().getUser();
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Map qualifiers;
		AdaptedPortletURL adapterUrl;

		
		//The "Administration" menu
		boolean adminMenuCreated=false;
		qualifiers = new HashMap();
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageWorkspaceMenu");
		toolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisWorkspace"), new HashMap(), qualifiers);

		//	The "Add" menu
		PortletURL url;
		//Add Workspace except to top or a user workspace
		if (!workspace.isRoot() && getWorkspaceModule().testAccess(workspace, WorkspaceOperation.addWorkspace)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			toolbar.addToolbarMenuItem("1_administration", "workspace", NLT.get("toolbar.menu.addWorkspace"), url);
		}
		//Add Folder except to top
		if (!workspace.isRoot() && getWorkspaceModule().testAccess(workspace, WorkspaceOperation.addFolder)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolder"), url);
		}
	
		//Configuration
		if (getBinderModule().testAccess(workspace, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.configuration"), url);
			
			//Modify
			
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_workspace"), 
					adapterUrl.toString(), qualifiers);
		}
		
		//Delete
		if (!workspace.isReserved()) {
			if (getBinderModule().testAccess(workspace, BinderOperation.deleteBinder)) {
				adminMenuCreated=true;
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.delete_workspace"), url, qualifiers);
			}
		}
		
		//Move
		if (!workspace.isReserved()) {
			if (getBinderModule().testAccess(workspace, BinderOperation.moveBinder)) {
				adminMenuCreated=true;
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.move_workspace"), url);
			}
		}
		//Reporting
		if (getBinderModule().testAccess(workspace, BinderOperation.report)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.report"), url);
		}
		
		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) toolbar.deleteToolbarMenu("1_administration");
		
		//Access control
		if (getAdminModule().testAccess(workspace, AdminOperation.manageFunctionMembership)) {
			qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//If this is a user workspace, add the "Manage this profile" menu
		if ((workspace.getDefinitionType() != null) && 
				(workspace.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {

			Principal owner = workspace.getCreation().getPrincipal();  //creator is the user
		
			boolean showModifyProfileMenu = false;
			boolean showDeleteProfileMenu = false;
			if (owner.isActive() && getProfileModule().testAccess(owner, ProfileOperation.modifyEntry)) {
				showModifyProfileMenu = true;
			}
		
			if (owner.isActive() && getProfileModule().testAccess(owner, ProfileOperation.deleteEntry)) {
				//Don't let a user delete his or her own account
				if (!owner.getId().equals(user.getId())) showDeleteProfileMenu = true;
			}
			
			if (showDeleteProfileMenu && showModifyProfileMenu) {
				qualifiers = new HashMap();
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
				toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.manageThisProfile"), new HashMap(), qualifiers);
				//	The "Modify" menu item
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
				adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
				toolbar.addToolbarMenuItem("4_manageProfile", "", NLT.get("toolbar.modify"), adapterUrl.toString(), qualifiers);
				//	The "Delete" menu item
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
				toolbar.addToolbarMenuItem("4_manageProfile", "", NLT.get("toolbar.delete"), url);
			}
			if (showModifyProfileMenu && !showDeleteProfileMenu) {
				//	The "Modify" menu item
				qualifiers = new HashMap();
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
				qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
				adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
				toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.menu.modify_profile"), adapterUrl.toString(), qualifiers);
			}
			if (!showModifyProfileMenu && showDeleteProfileMenu) {
				//	The "delete" menu item
				qualifiers = new HashMap();
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
				qualifiers.put("onClick", "return ss_confirmDeleteProfile();");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
				toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.delete"), url, qualifiers);
			}
		}
		
		// list team members
		qualifiers = new HashMap();
					
		// The "Teams" menu
		toolbar.addToolbarMenu("5_team", NLT.get("toolbar.teams"));
			
		//Add
		if (getBinderModule().testAccess(workspace, BinderOperation.manageTeamMembers)) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "500");
			qualifiers.put("popupHeight", "600");
			toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
		}
		// View
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
		url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
		toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
			
		// Sendmail
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.sendmail"), adapterUrl.toString(), qualifiers);
			
		// Meet
		if (getIcBrokerModule().isEnabled()) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.meet"), adapterUrl.toString(), qualifiers);
		}

		
		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, this, workspace, dashboardToolbar, model);

		//The "Footer" menu
		//RSS link 
		Toolbar footerToolbar = new Toolbar();
		String[] contributorIds = collectContributorIds(workspace);
		
		// permalink
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, workspace.getEntityType().toString());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.workspacePermalink"), 
				adapterUrl.toString(), qualifiers);

		// clipboard
		qualifiers = new HashMap();
		String contributorIdsAsJSString = "";
		for (int i = 0; i < contributorIds.length; i++) {
			contributorIdsAsJSString += contributorIds[i];
			if (i < (contributorIds.length -1)) {
				contributorIdsAsJSString += ", ";	
			}
		}
		qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "], '" + forumId + "');return false;");
		footerToolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "", qualifiers);

		// send mail
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);

		// start meeting
		if (getIcBrokerModule().isEnabled()) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
		}
		qualifiers = new HashMap();
		qualifiers.put("onClick", "javascript: ss_changeUITheme('" +
				NLT.get("ui.availableThemeIds") + "', '" +
				NLT.get("ui.availableThemeNames") + "'); return false;");
		footerToolbar.addToolbarMenu("themeChanger", NLT.get("toolbar.menu.changeUiTheme"), "javascript: ;", qualifiers);
		

		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
	}
	
	private String[] collectContributorIds(Workspace workspace) {
		Set principals = new HashSet();
		principals.add(workspace.getCreation().getPrincipal().getId().toString());
		principals.add(workspace.getOwner().getId().toString());
		principals.add(workspace.getModification().getPrincipal().getId().toString());
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

}
