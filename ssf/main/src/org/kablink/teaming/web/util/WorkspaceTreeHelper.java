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
package org.kablink.teaming.web.util;

import static org.kablink.util.search.Constants.BINDERS_PARENT_ID_FIELD;
import static org.kablink.util.search.Constants.DOCID_FIELD;
import static org.kablink.util.search.Constants.ENTITY_FIELD;
import static org.kablink.util.search.Constants.ENTRY_ANCESTRY;
import static org.kablink.util.search.Constants.MODIFICATION_DATE_FIELD;
import static org.kablink.util.search.Constants.TITLE_FIELD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.util.Validator;
import org.kablink.util.search.Criteria;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;


public class WorkspaceTreeHelper {
	public static ModelAndView setupWorkspaceBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		String view = setupWorkspaceBeans(bs, binderId, request, response, model);
 		return new ModelAndView(view, model);
	}
	public static String setupWorkspaceBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response, Map model) throws Exception {
		model.put(WebKeys.WORKSPACE_BEANS_SETUP, true);
        User user = RequestContextHolder.getRequestContext().getUser();

		BinderHelper.setBinderPermaLink(bs, request, response);
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}

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
			return WebKeys.VIEW_WORKSPACE;
		}

		Binder binder = null;
		String entryIdString =  PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Long entryId = null;
		if (Validator.isNotNull(entryIdString) && entryIdString.equals(WebKeys.URL_USER_ID_PLACE_HOLDER)) {
			entryId= user.getId();
		} else if (Validator.isNotNull(entryIdString) && !entryIdString.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) {
			entryId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		}
		//see if it is a user workspace - can also get directly to user ws by a binderId
		//so don't assume anything here.  This just allows us to handle users without a workspace.
		if (entryId != null) {
			Long workspaceId = bs.getProfileModule().getEntryWorkspaceId(entryId);
			if (workspaceId == null && user.getId().equals(entryId)) {
				//This is the user trying to access his or her own workspace; try to create it
				binder = bs.getProfileModule().addUserWorkspace(user, null);
				if (binder == null) {
					// Redirect to profile list
					PortletURL reloadUrl = response.createRenderURL();
					reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
					reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
					reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
					reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryIdString);
					model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
					return WebKeys.VIEW_WORKSPACE;
				}
				workspaceId = binder.getId(); 
			} else if (workspaceId != null) {
				try {
					binder = bs.getBinderModule().getBinder(workspaceId);
				} catch (NoBinderByTheIdException nb) {
					//User workspace does not yet exist
					User entry = null;
					entry = (User)bs.getProfileModule().getEntry(entryId);
					model.put(WebKeys.USER_OBJECT, entry);
					return WebKeys.VIEW_NO_USER_WORKSPACE;
				} catch(AccessControlException e) {
					BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						//Access is not allowed
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return WebKeys.VIEW_ACCESS_DENIED;
					} else {
						//Please log in
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return WebKeys.VIEW_LOGIN_PLEASE;
					}
				}
			} else {
				User entry = null;
				entry = (User)bs.getProfileModule().getEntry(entryId);
				model.put(WebKeys.USER_OBJECT, entry);
				return WebKeys.VIEW_NO_USER_WORKSPACE;
			}
			binderId = workspaceId;
			entryId = null;
		}
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

 		//Remember the last binder viewed
		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		portletSession.setAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, binderId, PortletSession.APPLICATION_SCOPE);
		portletSession.setAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, EntityType.workspace.name(), PortletSession.APPLICATION_SCOPE);
		
		Map formData = request.getParameterMap();
		try {
			if (binder == null) binder = bs.getBinderModule().getBinder(binderId);
			bs.getReportModule().addAuditTrail(AuditType.view, binder);
			BinderHelper.getBinderAccessibleUrl(bs, binder, entryId, request, response, model);

	 		//Check special options in the URL
			String[] debug = (String[])formData.get(WebKeys.URL_DEBUG);
			if (debug != null && (debug[0].equals(WebKeys.DEBUG_ON) || debug[0].equals(WebKeys.DEBUG_OFF))) {
				//The user is requesting debug mode to be turned on or off
				if (debug[0].equals(WebKeys.DEBUG_ON)) {
					bs.getProfileModule().setUserProperty(user.getId(), 
							ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(true));
				} else if (debug[0].equals(WebKeys.DEBUG_OFF)) {
					bs.getProfileModule().setUserProperty(user.getId(), 
							ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(false));
				}
			}
			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(bs, binder, model);
			BinderHelper.buildWorkspaceTreeBean(bs, binder, model, null);
			
			//See if this is a user workspace
			if ((binder.getDefinitionType() != null) && 
					(binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				Principal owner = binder.getCreation().getPrincipal(); //creator is user
				if (owner != null) {
					//	turn owner into real object = not hibernate proxy
					try {
						User u = user;
						if (!user.getId().equals(owner.getId())) {
							u = (User)bs.getProfileModule().getEntry(owner.getId());
						}
						model.put(WebKeys.PROFILE_CONFIG_ENTRY, u);							
						Document profileDef = u.getEntryDef().getDefinition();
						model.put(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
						model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
								profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
						model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
						model.put(WebKeys.USER_WORKSPACE, true);

						//Get the dashboard initial tab if one was passed in
						String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
				        RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, request, response, 
				        		binder.getId(), type, model);
					} catch (Exception ex) {} //user may have been deleted, but ws left around
				}
			}

			//Set up more standard beans
			DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
//			model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
			//See if the user has selected a specific view to use
			String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			DefinitionHelper.getDefinitions(binder, model, userDefaultDef);
			
			
			if (operation.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				model.put(WebKeys.SHOW_TEAM_MEMBERS, true);
				getTeamMembers(bs, formData, request, response, (Workspace)binder, model);
			} else {
				Document searchFilter = BinderHelper.getSearchFilter(bs, userFolderProperties);
				Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
				String viewType = DefinitionUtils.getViewType(configDocument);
				if (viewType == null) viewType = "";
				if (viewType.equals(Definition.VIEW_STYLE_DISCUSSION_WORKSPACE)) {
					getShowDiscussionWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model);					
				} else {
					getShowWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model);
				}
			}
			Map tagResults = TagUtil.uniqueTags(bs.getBinderModule().getTags(binder));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
			
			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(bs, binder, configDocument, model);
			
			String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
			model.put(WebKeys.TYPE, type);
			String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "0");
			model.put(WebKeys.PAGE_NUMBER, page);
			if (type.equals(WebKeys.URL_WHATS_NEW)) 
				BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, page);
			if (type.equals(WebKeys.URL_UNSEEN)) 
				BinderHelper.setupUnseenBinderBeans(bs, binder, model, page);
			
		} catch(NoBinderByTheIdException e) {
		} catch(OperationAccessControlExceptionNoName e) {
			//Access is not allowed
			if (WebHelper.isUserLoggedIn(request) && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Access is not allowed
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_ACCESS_DENIED;
			} else {
				//Please log in
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_LOGIN_PLEASE;
			}
		}
		
		//Set up the standard beans
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
		
		Tabs.TabEntry tab = BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tab.getTabs());		

		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		if(binder == null) {
			return "binder/deleted_binder";
		}
		
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) {
			buildWorkspaceToolbar(bs, request, response, model, (Workspace)binder, binder.getId().toString());
			return WebKeys.VIEW_NO_DEFINITION;
		}
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) {
			buildWorkspaceToolbar(bs, request, response, model, (Workspace)binder, binder.getId().toString());
			return WebKeys.VIEW_NO_DEFINITION;
		}
		
		return WebKeys.VIEW_WORKSPACE;
	}
	
	protected static void getShowWorkspace(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Document searchFilter, Map<String,Object>model) throws PortletRequestBindingException {
		Document wsTree;

		Long top = PortletRequestUtils.getLongParameter(req, WebKeys.URL_OPERATION2);
		if ((top != null) && (!ws.isRoot())) {
			wsTree = bs.getBinderModule().getDomBinderTree(top, ws.getId(), new WsDomTreeBuilder(ws, true, bs));
		} else {
			wsTree = bs.getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, bs),1);
		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		
		//Get the info for the "add a team" button
		buildAddTeamButton(bs, req, response, ws, model);
		buildWorkspaceToolbar(bs, req, response, model, ws, ws.getId().toString());
	}
	
	protected static void getShowDiscussionWorkspace(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Document searchFilter, Map<String,Object>model) throws PortletRequestBindingException {
		
    	Map<String, Counter> unseenCounts = new HashMap();

    	//Get the sorted list of child binders
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
		options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
		Map searchResults = bs.getBinderModule().getBinders(ws, options);
		List<Map> binders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		model.put(WebKeys.BINDERS, binders); 
		
		//Now get the next level of binders below the workspaces in "binders"
		List binderIdList = new ArrayList();
		for (Map binder:binders) {
			String binderIdString = (String) binder.get(DOCID_FIELD);
			String binderEntityType = (String) binder.get(ENTITY_FIELD);
			if (binderIdString != null) {
				if (binderEntityType != null && (binderEntityType.equals(EntityIdentifier.EntityType.workspace.name()) ||
						binderEntityType.equals(EntityIdentifier.EntityType.profiles.name()))) {
					binderIdList.add(binderIdString);
				}
				unseenCounts.put(binderIdString, new WorkspaceTreeHelper.Counter());
			}
		}
		if (!binderIdList.isEmpty()) {
			//Now search for the next level of binders
			options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
			options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
			Map searchResults2 = bs.getBinderModule().getBinders(ws, binderIdList, options);
			List<Map> binders2 = (List)searchResults2.get(ObjectKeys.SEARCH_ENTRIES);
			Map subBinders = new HashMap();
			for (Map binder : binders2) {
				String binderId = (String) binder.get(BINDERS_PARENT_ID_FIELD);
				if (binderId != null) {
					if (!subBinders.containsKey(binderId)) subBinders.put(binderId, new ArrayList());
					List binderList = (List) subBinders.get(binderId);
					binderList.add(binder);
					unseenCounts.put((String) binder.get(DOCID_FIELD), 
							new WorkspaceTreeHelper.Counter());
				}
			}
			model.put(WebKeys.BINDERS_SUB_BINDERS, subBinders);
		}

		//Get the recent entries anywhere in this workspace
		options = new HashMap();
		List binderIds = new ArrayList();
		binderIds.add(ws.getId().toString());
	    
		//get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000);
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.newEntriesDescendants(binderIds);
		crit.add(org.kablink.util.search.Restrictions.between(
				MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(crit, 0, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
    	List<Map> entries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);

		//Get the count of unseen entries
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
    	for (Map entry:entries) {
    		SearchFieldResult entryAncestors = (SearchFieldResult) entry.get(ENTRY_ANCESTRY);
			if (entryAncestors == null) continue;
			String entryIdString = (String) entry.get(DOCID_FIELD);
			if (entryIdString == null || (seen.checkIfSeen(entry))) continue;
			
			//Count up the unseen counts for all ancestor binders
			Iterator itAncestors = entryAncestors.getValueSet().iterator();
			while (itAncestors.hasNext()) {
				String binderIdString = (String)itAncestors.next();
				if (binderIdString.equals("")) continue;
				Counter cnt = unseenCounts.get(binderIdString);
				if (cnt == null) {
					cnt = new WorkspaceTreeHelper.Counter();
					unseenCounts.put(binderIdString, cnt);
				}
				cnt.increment();
			}
    	}
    	model.put(WebKeys.BINDER_UNSEEN_COUNTS, unseenCounts);
		
      	//Get the info for the "add a team" button
		buildAddTeamButton(bs, req, response, ws, model);
		buildWorkspaceToolbar(bs, req, response, model, ws, ws.getId().toString());
	}
	
	protected static void buildAddTeamButton(AllModulesInjected bs, 
			RenderRequest req, RenderResponse response, Workspace ws, Map<String,Object>model) {
		//Get the info for the "add a team" button
		if (!ws.isRoot() && bs.getBinderModule().testAccess(ws, BinderOperation.addWorkspace)) {
			Long cfgType = null;
			List result = bs.getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(bs.getTemplateModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
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
	}
	protected static void getTeamMembers(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Map<String,Object>model) throws PortletRequestBindingException {
		Collection users = bs.getBinderModule().getTeamMembers(ws, true);
		model.put(WebKeys.TEAM_MEMBERS, users);
		model.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
		
		buildWorkspaceToolbar(bs, req, response, model, ws, ws.getId().toString());
	}
	
	protected static void buildWorkspaceToolbar(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace, 
			String forumId) {
        User user = RequestContextHolder.getRequestContext().getUser();
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar folderActionsToolbar = new Toolbar();
		Toolbar whatsNewToolbar = new Toolbar();
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
		if (!workspace.isRoot() && bs.getBinderModule().testAccess(workspace, BinderOperation.addWorkspace)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			toolbar.addToolbarMenuItem("1_administration", "workspace", 
					NLT.get("toolbar.menu.addWorkspace"), url, qualifiers);
		}
		//Add Folder except to top
		if (!workspace.isRoot() && bs.getBinderModule().testAccess(workspace, BinderOperation.addFolder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_administration", "folders", 
					NLT.get("toolbar.menu.addFolder"), url, qualifiers);
		}
	
		//Configuration
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.configuration"), url, qualifiers);
			
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
		
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.manageConfiguration)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("administration.definition_builder_designers"), url, qualifiers);
		}
		//Delete
		if (!workspace.isReserved()) {
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.deleteBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "", 
						NLT.get("toolbar.menu.delete_workspace"), url, qualifiers);
			}
		}
		
		//Move
		if (!workspace.isReserved()) {
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.moveBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "", 
						NLT.get("toolbar.menu.move_workspace"), url, qualifiers);
			}
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.copyBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.copy_workspace"), url, qualifiers);
			}

		}
		//Reporting
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.report)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.report"), url, qualifiers);
		}
		
		//Site administration
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) {
			adminMenuCreated=true;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			toolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.siteAdministration"), url);
		}
		
		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) toolbar.deleteToolbarMenu("1_administration");
		
		//Access control
		if (bs.getAdminModule().testAccess(workspace, AdminOperation.manageFunctionMembership)) {
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//If this is a user workspace, add the "Manage this profile" menu
		if ((workspace.getDefinitionType() != null) && 
				(workspace.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
			Principal owner = workspace.getCreation().getPrincipal(); //creator is user
		
			boolean showModifyProfileMenu = false;
			boolean showDeleteProfileMenu = false;
			if (owner.isActive() && bs.getProfileModule().testAccess(owner, ProfileOperation.modifyEntry)) {
				showModifyProfileMenu = true;
			}
		
			if (owner.isActive() && bs.getProfileModule().testAccess(owner, ProfileOperation.deleteEntry)) {
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
		//toolbar.addToolbarMenu("5_team", NLT.get("toolbar.teams"));
			
		//Add
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.manageTeamMembers)) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "500");
			qualifiers.put("popupHeight", "600");
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_ADD_URL, adapterUrl.toString());
		}
		// View
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
		url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
		//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
		model.put(WebKeys.TOOLBAR_TEAM_VIEW_URL, url.toString());
			
		// Sendmail
		if (Validator.isNotNull(user.getEmailAddress()) && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.sendmail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_SENDMAIL_URL, adapterUrl.toString());
		}
		
		// Meet
		if (bs.getIcBrokerModule().isEnabled() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.meet"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_MEET_URL, adapterUrl.toString());
		}

		
		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, bs, workspace, dashboardToolbar, model);

		//The "Footer" menu
		//RSS link 
		Toolbar footerToolbar = new Toolbar();
		String[] contributorIds = collectContributorIds(workspace);
		
		// permalink
		String permaLink = PermaLinkUtil.getPermalink(request, workspace);
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.workspacePermalink"), 
				permaLink, qualifiers);
		
		model.put(WebKeys.PERMALINK, permaLink);

		// clipboard
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			String contributorIdsAsJSString = "";
			for (int i = 0; i < contributorIds.length; i++) {
				contributorIdsAsJSString += contributorIds[i];
				if (i < (contributorIds.length -1)) {
					contributorIdsAsJSString += ", ";	
				}
			}
			qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "], '" + forumId + "');return false;");
			//footerToolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "", qualifiers);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS, contributorIds);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS_AS_JS_STRING, contributorIdsAsJSString);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_SHOW, Boolean.TRUE);
		}
		
		// send mail
		if (user.getEmailAddress() != null && !user.getEmailAddress().equals("") && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_SENDMAIL_URL, adapterUrl.toString());
		}

		// start meeting
		if (bs.getIcBrokerModule().isEnabled() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_MEETING_URL, adapterUrl.toString());
		}
		
		//Theme
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "javascript: ss_changeUITheme('" +
					NLT.get("ui.availableThemeIds") + "', '" +
					NLT.get("ui.availableThemeNames") + "', '" +
					NLT.get("sidebar.themeChange") + "'); return false;");
			//footerToolbar.addToolbarMenu("themeChanger", NLT.get("toolbar.menu.changeUiTheme"), "javascript: ;", qualifiers);
			model.put(WebKeys.TOOLBAR_THEME_IDS, NLT.get("ui.availableThemeIds"));
			model.put(WebKeys.TOOLBAR_THEME_NAMES, NLT.get("ui.availableThemeNames"));
		}

		//Set up the whatsNewToolbar links
		//What's new
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_TYPE, "whatsNew");
		adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
		adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'whatsNew', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
		whatsNewToolbar.addToolbarMenu("whatsnew", NLT.get("toolbar.menu.whatsNew"), 
				adapterUrl.toString(), qualifiers);
		
		// What's unseen
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_TYPE, "unseen");
		adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
		adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'unseen', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
		whatsNewToolbar.addToolbarMenu("unseen", NLT.get("toolbar.menu.whatsUnseen"), 
				adapterUrl.toString(), qualifiers);

		//Build the folder actions toolbar
		BinderHelper.buildFolderActionsToolbar(bs, request, response, folderActionsToolbar, forumId);

		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.WHATS_NEW_TOOLBAR,  whatsNewToolbar.getToolbar());
		model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
	}
	
	private static String[] collectContributorIds(Workspace workspace) {
		Set principals = new HashSet();
		principals.add(workspace.getCreation().getPrincipal().getId().toString());
		principals.add(workspace.getOwner().getId().toString());
		principals.add(workspace.getModification().getPrincipal().getId().toString());
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

    /**
     * Helper classs to return folder unseen counts as an objects
     * @author Janet McCann
     *
     */
     public static class Counter {
    	private long count=0;
    	protected Counter() {	
    	}
    	protected void increment() {
    		++count;
    	}
    	public Long getCount() {
    		return count;
    	}    	
    }
    
}
