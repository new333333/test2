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
package com.sitescape.team.web.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portlet.forum.ViewController;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReleaseInfo;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.DomTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.domain.Definition;
import com.sitescape.util.BrowserSniffer;
import com.sitescape.util.Validator;
public class BinderHelper {
	public static final String BLOG_SUMMARY_PORTLET="ss_blog";
	public static final String FORUM_PORTLET="ss_forum";
	public static final String GALLERY_PORTLET="ss_gallery";
	public static final String GUESTBOOK_SUMMARY_PORTLET="ss_guestbook";
	public static final String TASK_SUMMARY_PORTLET="ss_task";
	public static final String MOBILE_PORTLET="ss_mobile";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String SEARCH_PORTLET="ss_search";
	public static final String TOOLBAR_PORTLET="ss_toolbar";
	public static final String WIKI_PORTLET="ss_wiki";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";

	static public ModelAndView CommonPortletDispatch(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) {
 		Map<String,Object> model = new HashMap<String,Object>();
 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
 		PortletPreferences prefs = null;
 		String ss_initialized = null;
 		try {
 			prefs = request.getPreferences();
 			ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
 		} catch(Exception e) {
 			ss_initialized = "true";
 		}
 		
		if (Validator.isNull(ss_initialized)) {
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			//need action URL to set initialized flag in preferences
			url = response.createActionURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
		}
		

		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_NICKNAME, SPropsUtil.getString("product.nickname", ObjectKeys.PRODUCT_NICKNAME_DEFAULT));
		model.put(WebKeys.PRODUCT_EDITION, SPropsUtil.getString("product.edition", ObjectKeys.PRODUCT_EDITION_DEFAULT));
		model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		
		String displayType = getDisplayType(request);
		if (prefs != null) displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			displayType = getDisplayType(request);
		}
			
        User user = RequestContextHolder.getRequestContext().getUser();
		BinderHelper.getBinderAccessibleUrl(bs, null, null, request, response, model);

		if (FORUM_PORTLET.equals(displayType)) {
		
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = new String[0];
			if (prefs != null) preferredBinderIds = PortletPreferencesUtil.getValues(prefs, WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			model.put(WebKeys.FOLDER_LIST, bs.getBinderModule().getBinders(binderIds));
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {}
			return new ModelAndView(WebKeys.VIEW_FORUM, model);
		} else if (WORKSPACE_PORTLET.equals(displayType)) {
			String id = null;
			if (prefs != null) id = PortletPreferencesUtil.getValue(prefs, WebKeys.WORKSPACE_PREF_ID, null);
			Workspace binder;
			try {
				binder = bs.getWorkspaceModule().getWorkspace(Long.valueOf(id));
			} catch (Exception ex) {
				binder = bs.getWorkspaceModule().getWorkspace();				
			}
			Document wsTree;
			//when at the top, don't expand
			if (request.getWindowState().equals(WindowState.NORMAL)) {
				wsTree = bs.getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 0);
			} else {
				wsTree = bs.getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
		    return new ModelAndView("workspacetree/view", model);
		    
		} else if (PRESENCE_PORTLET.equals(displayType)) {
 			Set ids = new HashSet();		
 			if (prefs != null) {
 				ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_USER_LIST, "")));
 	 			ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
 			}
 			if (ids.isEmpty()) {
 				//Initialize an empty presence list to have the current user as a buddy so there is always something to show
 				ids.add(user.getId());
 			}
 			//This is the portlet view; get the configured list of principals to show
 			SortedSet users = bs.getProfileModule().getUsersFromPrincipals(ids);
 			model.put(WebKeys.USERS, users);
 			//if we list groups, then we have issues when a user appears in multiple groups??
 			//how do we update the correct divs??
 			//so, explode the groups and just show members
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {}
  			model.put(WebKeys.USER_LIST, LongIdUtil.getIdsAsString(ids));
  			return new ModelAndView(WebKeys.VIEW_PRESENCE, model);				
		} else if (TOOLBAR_PORTLET.equals(displayType)) {
			Workspace binder = bs.getWorkspaceModule().getWorkspace();
			Document wsTree;
			if (request.getWindowState().equals(WindowState.NORMAL)) {
				wsTree = bs.getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 1);
			} else {
				wsTree = bs.getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
 			return new ModelAndView(WebKeys.VIEW_TOOLBAR, model);		
		} else if (BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_BLOG_SUMMARY);		
		} else if (WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_WIKI);		
		} else if (GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_GUESTBOOK_SUMMARY);		
		} else if (TASK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_TASK_SUMMARY);		
		} else if (SEARCH_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_SEARCH);		
		} else if (GALLERY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_GALLERY);		
		} else if (MOBILE_PORTLET.equals(displayType)) {
			return setupMobilePortlet(bs, request, prefs, model, WebKeys.VIEW_MOBILE);		
		}

		return null;
	}
	
	protected static ModelAndView setupSummaryPortlets(AllModulesInjected bs, RenderRequest request, PortletPreferences prefs, Map model, String view) {
		String gId = null;
		if (prefs != null) gId = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (gId != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)bs.getDashboardModule().getDashboard(gId);
				model.put(WebKeys.DASHBOARD_PORTLET, d);
				Map userProperties = (Map) bs.getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
				model.put(WebKeys.USER_PROPERTIES, userProperties);
				if (request.getWindowState().equals(WindowState.MAXIMIZED))
					model.put(WebKeys.PAGE_SIZE, "20");
				else
					model.put(WebKeys.PAGE_SIZE, "5");						
				DashboardHelper.getDashboardMap(d, userProperties, model, false);
				return new ModelAndView(view, model);		
			} catch (NoObjectByTheIdException no) {}
		}
		return new ModelAndView(WebKeys.VIEW_NOT_CONFIGURED);
		
	}

	protected static ModelAndView setupMobilePortlet(AllModulesInjected bs, RenderRequest request, PortletPreferences prefs, Map model, String view) {
		//This is the portlet view; get the configured list of folders to show
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) bs.getProfileModule().getUserProperties(user.getId()).getProperties();
		String[] mobileBinderIds = (String[])userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS);

		//Build the jsp bean (sorted by folder title)
		List<Long> binderIds = new ArrayList<Long>();
		if (mobileBinderIds != null) {
			for (int i = 0; i < mobileBinderIds.length; i++) {
				binderIds.add(new Long(mobileBinderIds[i]));
			}
		}
		model.put(WebKeys.MOBILE_BINDER_LIST, bs.getBinderModule().getBinders(binderIds));
		Map unseenCounts = new HashMap();
		unseenCounts = bs.getFolderModule().getUnseenCounts(binderIds);
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);

		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		model.put("ss_UserQueries", userQueries);
		return new ModelAndView(view, model);
		
	}

	public static String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		//For liferay we use instances and the name will be changed slightly
		//That is why we check for the name with contains
		if (pName.contains(ViewController.FORUM_PORTLET))
			return ViewController.FORUM_PORTLET;
		else if (pName.contains(ViewController.WORKSPACE_PORTLET))
			return ViewController.WORKSPACE_PORTLET;
		else if (pName.contains(ViewController.PRESENCE_PORTLET))
			return ViewController.PRESENCE_PORTLET;
		else if (pName.contains(ViewController.BLOG_SUMMARY_PORTLET))
			return ViewController.BLOG_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.GALLERY_PORTLET))
			return ViewController.GALLERY_PORTLET;
		else if (pName.contains(ViewController.GUESTBOOK_SUMMARY_PORTLET))
			return ViewController.GUESTBOOK_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.TASK_SUMMARY_PORTLET))
			return ViewController.TASK_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.SEARCH_PORTLET))
			return ViewController.SEARCH_PORTLET;
		else if (pName.contains(ViewController.TOOLBAR_PORTLET))
			return ViewController.TOOLBAR_PORTLET;
		else if (pName.contains(ViewController.WIKI_PORTLET))
			return ViewController.WIKI_PORTLET;
		else if (pName.contains(ViewController.MOBILE_PORTLET))
			return ViewController.MOBILE_PORTLET;
		return null;

	}

	static public String getViewType(AllModulesInjected bs, Long binderId) {
		//does read check
		Binder binder = bs.getBinderModule().getBinder(binderId);
		return getViewType(bs, binder);
	}
	static public String getViewType(AllModulesInjected bs, Binder binder) {

		User user = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId(), binder.getId()); 
		String displayDefId = (String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		Definition displayDef = binder.getDefaultViewDef();
		if (Validator.isNotNull(displayDefId)) {
			List<Definition> folderViewDefs = binder.getViewDefinitions();
			for (Definition def: folderViewDefs) {
				//Is this an allowed definition?
				if (displayDefId.equals(def.getId())) {
					//Ok, this definition is allowed
					displayDef = def;
					break;
				}
			}
		}
		String viewType = null;
		if (displayDef != null) viewType = DefinitionUtils.getViewType(displayDef.getDefinition());
		if (viewType == null) return "";
		return viewType;
	}

	static public String getViewListingJsp(AllModulesInjected bs) {
		return getViewListingJsp(bs, "");
	}

	//The getViewListingJSP function has been overloaded, to check if the displayDefinition is of type
	//search. For the 'search' display defintion, we should not have the display at bottom (vertical)
	//option. So when a user chooses display at bottom option, we will be showing the user a overlay display
	//Along with 'search', we have added 'blog' and 'guestbook' to above check 
	static public String getViewListingJsp(AllModulesInjected bs, String displayDefinition) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("")) {
			displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
		}
		String viewListingJspName;
		if (displayDefinition != null && displayDefinition.equalsIgnoreCase(ObjectKeys.SEARCH_RESULTS_DISPLAY)) {
			if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_POPUP;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_ACCESSIBLE;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
				//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
				//only to the table folder view. For all other folder views we need to use the iframe view.
				if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_VERTICAL;
				} else {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
				}
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			}
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
			viewListingJspName = WebKeys.VIEW_LISTING_POPUP;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			viewListingJspName = WebKeys.VIEW_LISTING_ACCESSIBLE;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
			//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
			//only to the table folder view. For all other folder views we need to use the iframe view.
			if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_VERTICAL;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
			}
		} else {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		}
		return viewListingJspName;
	}
	
	//Routine to save a generic portal url used to build a url to a binder or entry 
	//  This routine is callable only from a portlet controller
	static public void setBinderPermaLink(AllModulesInjected bs, 
			RenderRequest request, RenderResponse response) {
		if (request.getWindowState().equals(WindowState.MAXIMIZED) || getBinderPermaLink(bs).equals("")) {
			User user = RequestContextHolder.getRequestContext().getUser();
			PortletURL url = response.createActionURL();
			try {url.setWindowState(WindowState.MAXIMIZED);} catch(Exception e) {};
			url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_TITLE, WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER);
			if (!url.toString().equals(getBinderPermaLink(bs)))
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_PERMALINK_URL, url.toString());
		}
	}
	
	//Routine to get a portal url that points to a binder or entry 
	//  This routine is callable from an adaptor controller
	static public String getBinderPermaLink(AllModulesInjected bs) {
		User user = null;
		try {
			user = RequestContextHolder.getRequestContext().getUser();
		} catch(Exception e) {
			//TODO If there is no user, then get the permalink of the guest account
			return "";
		}
		UserProperties userProperties = (UserProperties) bs.getProfileModule().getUserProperties(user.getId());
		String url = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_PERMALINK_URL);
		if (url == null) url = "";
		return url;
	}
	
	static public void getBinderAccessibleUrl(AllModulesInjected bs, Binder binder, Long entryId,
			RenderRequest request, RenderResponse response, Map model) {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("")) {
			displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
		}
		model.put(WebKeys.DISPLAY_STYLE, displayStyle);
		
		PortletURL url = response.createActionURL();
		if (binder != null) {
			if (binder.getEntityType().equals(EntityType.folder)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			else if (binder.getEntityType().equals(EntityType.workspace)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			else if (binder.getEntityType().equals(EntityType.profiles)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			if (entryId != null) url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		}
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		} else {
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		}
		model.put(WebKeys.ACCESSIBLE_URL, url.toString());
	}

	static public Map getAccessControlMapBean(Map model) {
		//Initialize the acl bean
		if (!model.containsKey(WebKeys.ACCESS_CONTROL_MAP)) 
			model.put(WebKeys.ACCESS_CONTROL_MAP, new HashMap());
		return (Map)model.get(WebKeys.ACCESS_CONTROL_MAP);
	}
	
	static public Map getAccessControlEntityMapBean(Map model, DefinableEntity entity) {
		Map accessControlMap = getAccessControlMapBean(model);
		if (!accessControlMap.containsKey(entity.getId())) 
			accessControlMap.put(entity.getId(), new HashMap());
		return (Map)accessControlMap.get(entity.getId());
	}
	
	static public void buildWorkspaceTreeBean(AllModulesInjected bs, Binder binder, Map model, DomTreeHelper helper) {
		Binder workspaceBinder = binder;
		Document tree = null;
		try {
			if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
				tree = bs.getWorkspaceModule().getDomWorkspaceTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				tree = bs.getFolderModule().getDomFolderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
				tree = bs.getWorkspaceModule().getDomWorkspaceTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 0);
			}
		} catch (AccessControlException ac) {}

		model.put(WebKeys.SIDEBAR_WORKSPACE_TREE, tree);

		// Record the workspace passed in as the "current workspace" for
		// the sidebar.  This is used as the default workspace context
		// when adding a workspace from the sidebar.
		// It turns out that the calculation of this for the workspace
		// tree is exactly what is needed, so we piggyback this code
		// for this bean.
		
		model.put(WebKeys.SIDEBAR_CURRENT_WORKSPACE, workspaceBinder);

	}

	static public void buildNavigationLinkBeans(AllModulesInjected bs, Binder binder, Map model) {
		if (binder instanceof TemplateBinder)
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, new ConfigHelper(""));
		else
			buildNavigationLinkBeans(bs, binder, model, null);
	}
	static public void buildNavigationLinkBeans(AllModulesInjected bs, Binder binder, Map model, DomTreeHelper helper) {
		if (binder instanceof TemplateBinder) {
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, helper);
		} else {
			Binder parentBinder = binder;
			Map navigationLinkMap;
			if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
				navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
			else {
				navigationLinkMap = new HashMap();
				model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
			}
			while (parentBinder != null) {
				Document tree = null;
				try {
					if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
						tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
								new WsDomTreeBuilder(null, true, bs, helper),0);
					} else if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
						tree = bs.getFolderModule().getDomFolderTree(parentBinder.getId(), new WsDomTreeBuilder(null, true, bs, helper), 0);
					} else if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
						tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
								new WsDomTreeBuilder(null, true, bs, helper),0);
					}
				} catch (AccessControlException ac) {
					break;
				}
				navigationLinkMap.put(parentBinder.getId(), tree);
				parentBinder = ((Binder)parentBinder).getParentBinder();
			}
		}
	}

	static public void buildNavigationLinkBeans(AllModulesInjected bs, TemplateBinder config, Map model, DomTreeHelper helper) {
		TemplateBinder parentConfig = config;
		Map navigationLinkMap;
		if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
			navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
		else {
			navigationLinkMap = new HashMap();
			model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
		}
    	while (parentConfig != null) {
        	Document tree = buildTemplateTreeRoot(bs, parentConfig, helper);
 			navigationLinkMap.put(parentConfig.getId(), tree);
			parentConfig = (TemplateBinder)parentConfig.getParentBinder();
		}
	}
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllModulesInjected bs, TemplateBinder config, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
    	//only need this information if this is the bottom of the tree
    	buildTemplateChildren(element, config, helper);
    	return tree;
	}
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllModulesInjected bs, List configs, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
	   	element.addAttribute("title", NLT.get("administration.configure_cfg"));
    	element.addAttribute("displayOnly", "true");
    	if (!configs.isEmpty()) {
			element.addAttribute("hasChildren", "true");
			for (int i=0; i<configs.size(); ++i) {
				TemplateBinder child = (TemplateBinder)configs.get(i);
    			Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    			buildTemplateChildren(cElement, child, helper);
    		}
    	} else 	element.addAttribute("hasChildren", "false");

    	return tree;
	}
	static void buildTemplateChildren(Element element, TemplateBinder config, DomTreeHelper helper) {
		buildTemplateElement(element, config, helper);
    	List<TemplateBinder> children = config.getBinders();
    	for (TemplateBinder child: children) {
    		Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    		buildTemplateChildren(cElement, child, helper);
    	}
	}
	static void buildTemplateElement(Element element, TemplateBinder config, DomTreeHelper helper) {
	   	element.addAttribute("title", NLT.getDef(config.getTitle()));
    	element.addAttribute("id", config.getId().toString());
 		
    	if (!config.getBinders().isEmpty()) {
			element.addAttribute("hasChildren", "true");
		} else
			element.addAttribute("hasChildren", "false");
			
		if (config.getEntityType().equals(EntityType.workspace)) {
			String icon = config.getIconName();
			String imageClass = "ss_twImg";
			if (icon == null || icon.equals("")) {
				icon = "/icons/workspace.gif";
				imageClass = "ss_twImg";
			}
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_WORKSPACE);
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_TEMPLATE, config));
					
		} else {
			String icon = config.getIconName();
			String imageBrand = SPropsUtil.getString("branding.prefix");
			if (icon == null || icon.equals("")) icon = "/icons/folder.png";
			element.addAttribute("image", "/" + imageBrand + icon);
			element.addAttribute("imageClass", "ss_twIcon");
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_FOLDER);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_TEMPLATE, config));
		} 
		
	}

    public static Map getCommonEntryElements() {
    	Map entryElements = new HashMap();
    	Map itemData;
    	//Build a map of common elements for use in search filters
    	//  Each map has a "type" and a "caption". Types can be: title, text, user_list, or date.
    	
    	//title
    	itemData = new HashMap();
    	itemData.put("type", "title");
    	itemData.put("caption", NLT.get("filter.title"));
    	entryElements.put("title", itemData);
    	
    	//author
    	itemData = new HashMap();
    	itemData.put("type", "user_list");
    	itemData.put("caption", NLT.get("filter.author"));
    	// entryElements.put(EntityIndexUtils.CREATORID_FIELD, itemData);
    	// entryElements.put(EntityIndexUtils.CREATOR_NAME_FIELD, itemData);
    	entryElements.put(EntityIndexUtils.CREATOR_TITLE_FIELD, itemData);
    	
    	//creation date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.creationDate"));
    	entryElements.put("creation", itemData);
    	
    	//modification date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.modificationDate"));
    	entryElements.put("modification", itemData);
    	
    	return entryElements;
    }
       	
	// This method reads thru the results from a search, finds the tags, 
	// and places them into an array in a alphabetic order.
	public static List sortCommunityTags(List entries) {
		return sortCommunityTags(entries, "");
	}
	public static List sortCommunityTags(List entries, String wordRoot) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("\\s");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j];

		    	if (strTag.equals("")) continue;
		    	
		    	//See if this must match a specific word root
		    	if (!wordRoot.equals("") && !strTag.toLowerCase().startsWith(wordRoot.toLowerCase())) continue;
		    	
		    	Integer tagCount = (Integer) tagMap.get(strTag);
		    	if (tagCount == null) {
		    		tagMap.put(strTag, new Integer(1));
		    	}
		    	else {
		    		int intTagCount = tagCount.intValue();
		    		tagMap.put(strTag, new Integer(intTagCount+1));
		    	}
		    }
		}
		
		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}
	
	//This method rates the community tags
	public static List rateCommunityTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}
	
	//This method identifies if we need a + or - sign infront of the
	//tags being displayed in the tags tab in the search tab
	public static List determineSignBeforeTag(List entries, String tabTagTitle) {
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			String strTabTitle = tabTagTitle;
			Map tag = (Map) entries.get(i);
			String strTagName = (String) tag.get(WebKeys.TAG_NAME);
			if (strTabTitle != null && !strTabTitle.equals("")) {
				if ( (strTabTitle.indexOf(strTagName+ " ") != -1) || (strTabTitle.indexOf(" " + strTagName) != -1) ) {
					tag.put(WebKeys.TAG_SIGN, "-");
					
					int intFirstIndex = strTabTitle.indexOf(strTagName+ " ");
					int intFirstLength = (strTagName+ " ").length();
					
					if (intFirstIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intFirstIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intFirstIndex));
						}
						if ( strTabTitle.length() !=  (intFirstIndex+1+intFirstLength) ) {
							strLastPart = strTabTitle.substring(intFirstIndex+intFirstLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					
					int intLastIndex = strTabTitle.indexOf(" " + strTagName);
					int intLastLength = (" " + strTagName).length();

					if (intLastIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intLastIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intLastIndex));
						}
						if ( strTabTitle.length() !=  (intLastIndex+intLastLength) ) {
							strLastPart = strTabTitle.substring(intLastIndex+intLastLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle);					
				}
				else if (strTabTitle.equals(strTagName)) {
					tag.put(WebKeys.TAG_SIGN, "-");
					tag.put(WebKeys.TAG_SEARCH_TEXT, "");
				}
				else {
					tag.put(WebKeys.TAG_SIGN, "+");
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle + " " + strTagName);
				}
			}
			else {
				tag.put(WebKeys.TAG_SIGN, "+");
				tag.put(WebKeys.TAG_SEARCH_TEXT, strTagName);
			}
			tagList.add(tag);
		}
		return tagList;
	}

	// This method reads thru the results from a search, finds the personal tags, 
	// and places them into an array in a alphabetic order.
	public static List sortPersonalTags(List entries) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_ACL_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("ACL");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j].trim();
		    	if (strTag.equals("")) continue;
		    	
		    	String strFirstSixChars = "";
		    	if (strTag.length() >= 6) {
		    		strFirstSixChars = strTag.substring(0, 6);
		    	}
		    	//Ignore these entries as they refer to community entries.
		    	if (strFirstSixChars.equals("allTAG")) continue;

		    	User user = RequestContextHolder.getRequestContext().getUser();
		    	long userId = user.getId();
		    	
		    	String strUserIdTag = userId + "TAG";
		    	String strValueToCompare = strTag.substring(0, strUserIdTag.length());
		    	
		    	//We are going to get only the personal tags relating to the user
		    	if (strValueToCompare.equals(strUserIdTag)) {
		    		String strTagValues = strTag.substring(strUserIdTag.length());
				    String [] strIntTagArray = strTagValues.split("\\s");
				    for (int k = 0; k < strIntTagArray.length; k++) {
				    	String strIntTag = strIntTagArray[k].trim();
				    	if (strIntTag.equals("")) continue;
				    	
				    	Integer tagCount = (Integer) tagMap.get(strIntTag);
				    	if (tagCount == null) {
				    		tagMap.put(strIntTag, new Integer(1));
				    	} else {
				    		int intTagCount = tagCount.intValue();
				    		tagMap.put(strIntTag, new Integer(intTagCount+1));
				    	}
				    }
		    	}
		    	else continue;
		    }
		}

		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}

	//This method rates the personal tags
	public static List ratePersonalTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}	

	//This method provides ratings for the tags
	public static List rateTags(List entries, int intMaxHits) {
		List ratedList = new ArrayList();
		int intMaxHitsPerFolder = intMaxHits;
		/*
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		*/
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			int intResultCount = resultCount.intValue();
			Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
			int intRatingForFolder = DblRatingForFolder.intValue();
			tag.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
			if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largerprint");
			}
			else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largeprint");
			}
			else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_normalprint");
			}
			else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_smallprint");
			}
			else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_fineprint");
			}
			ratedList.add(tag);
		}
	
		return ratedList;		
	}
	
	public static int getMaxHitsPerTag(List entries) {
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		return intMaxHitsPerFolder;
	}
	
	public static void buildAccessControlTableBeans(RenderRequest request, RenderResponse response, 
			Binder binder, List functions, List membership, Map model, boolean ignoreFormData) {
		Map formData = request.getParameterMap();

		Set newRoleIds = new HashSet();
		String[] roleIds = new String[0];
		String[] principalIds = new String[0];
		String[] principalId = new String[0];
		
		Map functionMap = new HashMap();
		Map allowedFunctions = new HashMap();
		Map sortedGroupsMap = new TreeMap();
		Map sortedUsersMap = new TreeMap();

		String[] btnClicked = new String[] {""};
 		if (formData.containsKey("btnClicked")) btnClicked = (String[])formData.get("btnClicked");
		if (!ignoreFormData && (formData.containsKey("addRoleBtn") || 
				btnClicked[0].equals("addPrincipal") || btnClicked[0].equals("addRole"))) {
			if (formData.containsKey("roleIds")) {
				roleIds = (String[]) formData.get("roleIds");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("")) newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("roleIdToAdd")) {
				roleIds = (String[]) formData.get("roleIdToAdd");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("") && !newRoleIds.contains(Long.valueOf(roleIds[i]))) 
						newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("principalId")) {
				principalId = (String[]) formData.get("principalId");
			}

			if (formData.containsKey("principalIds")) {
				principalIds = (String[]) formData.get("principalIds");
			}

			//Get the role and user data from the form
			Map roleMembers = new HashMap();
			membership = new ArrayList();
						
			for (int i = 0; i < principalId.length; i++) {
				if (!principalId[i].equals("")) {
					Long id = Long.valueOf(principalId[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}			
						
			for (int i = 0; i < principalIds.length; i++) {
				if (!principalIds[i].equals("")) {
					Long id = Long.valueOf(principalIds[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry)itFormData.next();
				String key = (String)me.getKey();
				if (key.length() >= 8 && key.substring(0,7).equals("role_id")) {
					String[] s_roleId = key.substring(7).split("_");
					if (s_roleId.length == 2) {
						Long roleId = Long.valueOf(s_roleId[0]);
						Long memberId;
						if (s_roleId[1].equals("owner")) {
							memberId = ObjectKeys.OWNER_USER_ID;
						} else if (s_roleId[1].equals("teamMember")) {
							memberId = ObjectKeys.TEAM_MEMBER_ID;
						} else {
							memberId = Long.valueOf(s_roleId[1]);
						}
						if (!roleMembers.containsKey(roleId)) roleMembers.put(roleId, new ArrayList());
						List members = (List)roleMembers.get(roleId);
						if (!members.contains(memberId)) members.add(memberId);
						if (!membership.contains(memberId)) membership.add(memberId);
					}
				}
			}
			Collection ids = ResolveIds.getPrincipals(membership);
			Map principalMap = new HashMap();
    		for (Iterator iter=ids.iterator();iter.hasNext();) {
	    		Principal p = (Principal)iter.next();
				principalMap.put(p.getId(), p);
			}

			//Build the basic map structure
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				
				//Populate the map with data from the form instead of getting it from the database
				List members = (List)roleMembers.get(f.getId());
				if (members != null) {
					for (Iterator iter = members.iterator();iter.hasNext();) {
						Long pId = (Long)iter.next();
						if (pId.equals(ObjectKeys.OWNER_USER_ID)) {
							//The owner has this right
							pMap.put(WebKeys.OWNER, pId);
						} else if (pId.equals(ObjectKeys.TEAM_MEMBER_ID)) {
							//The team members have this right
							pMap.put(WebKeys.TEAM_MEMBER, pId);
						} else {
							Principal p = (Principal)principalMap.get(pId);
							if (p instanceof Group) {
								groups.put(p.getId(), p);
							} else {
								users.put(p.getId(), p);
							}
						}
					}
				}
			}
			//Populate the sorted users and groups maps 
			for (Iterator iter = membership.iterator();iter.hasNext();) {
				Long pId = (Long)iter.next();
				Principal p = (Principal)principalMap.get(pId);
				if (p != null && p instanceof Group) {
					sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p != null && p instanceof User) {
					sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				}
			}

		} else {
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				for (int j=0; j<membership.size(); ++j) {
					WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)membership.get(j);
					if (f.getId().equals(m.getFunctionId())) {
						if (m.getMemberIds().contains(ObjectKeys.OWNER_USER_ID)) {
							pMap.put(WebKeys.OWNER, ObjectKeys.OWNER_USER_ID);
						};
						if (m.getMemberIds().contains(ObjectKeys.TEAM_MEMBER_ID)) {
							pMap.put(WebKeys.TEAM_MEMBER, ObjectKeys.TEAM_MEMBER_ID);
						}
						Collection ids = ResolveIds.getPrincipals(m.getMemberIds());
						for (Iterator iter=ids.iterator(); iter.hasNext();) {
							Principal p = (Principal)iter.next();
							if (p != null && p instanceof Group) {
								groups.put(p.getId(), p);
								sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p != null && p instanceof User) {
								users.put(p.getId(), p);
								sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							}
						}
						break;
					}
				}
			}
		}
		
		//Build a sorted list of functions
		Map sortedFunctionsMap = new TreeMap();
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			Map pMap = (Map)functionMap.get(f);
			Map users = (Map)pMap.get(WebKeys.USERS);
			Map groups = (Map)pMap.get(WebKeys.GROUPS);
			if (users.size() > 0 || groups.size() > 0 || pMap.containsKey(WebKeys.OWNER) || 
					pMap.containsKey(WebKeys.TEAM_MEMBER) ||
					newRoleIds.contains(f.getId())) {
				//This function has some membership; add it to the sorted list
				sortedFunctionsMap.put(NLT.getDef(f.getName()).toLowerCase() + f.getId().toString(), f);
			}
		}
		//list of sorted functions
		List sortedFunctions = new ArrayList(sortedFunctionsMap.values());

		//Build the sorted lists of users and groups
		List sortedGroups = new ArrayList(sortedGroupsMap.values());

		List sortedUsers = new ArrayList(sortedUsersMap.values());
		
		//Build list of allowed roles
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			allowedFunctions.put(f.getId(), f);
		}
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FUNCTION_MAP, functionMap);
		model.put(WebKeys.FUNCTIONS_ALLOWED, allowedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS, sortedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP, sortedFunctionsMap);
		model.put(WebKeys.ACCESS_FUNCTIONS_COUNT, Integer.valueOf(functionMap.size()));
		model.put(WebKeys.ACCESS_SORTED_USERS_MAP, sortedUsersMap);
		model.put(WebKeys.ACCESS_SORTED_USERS, sortedUsers);
		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_SORTED_GROUPS_MAP, sortedGroupsMap);
		model.put(WebKeys.ACCESS_SORTED_GROUPS, sortedGroups);
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));
	}
	
	public static void mergeAccessControlTableBeans(Map model) {
		List sortedFunctions = (List)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS);
		Map sortedFunctionsMap = (Map)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		List sortedGroups = (List)model.get(WebKeys.ACCESS_SORTED_GROUPS);
		List sortedUsers = (List)model.get(WebKeys.ACCESS_SORTED_USERS);
		Map sortedGroupsMap = (Map)model.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map sortedUsersMap = (Map)model.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		Map parentModel = (Map)model.get(WebKeys.ACCESS_PARENT);
		Map parentSortedFunctionsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		Map parentSortedGroupsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map parentSortedUsersMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		for (Iterator i = parentSortedFunctionsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedFunctionsMap.put(me.getKey(), me.getValue());
		}
		Iterator itFunctions = sortedFunctionsMap.keySet().iterator();
		while (itFunctions.hasNext()) {
			Function f = (Function)sortedFunctionsMap.get((String) itFunctions.next());
			if (!sortedFunctions.contains(f)) sortedFunctions.add(f);
		}

		for (Iterator i = parentSortedGroupsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedGroupsMap.put(me.getKey(), me.getValue());
		}

		for (Iterator i = parentSortedUsersMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedUsersMap.put(me.getKey(), me.getValue());
		}

		//Merge the sorted lists of users and groups
		Iterator itGroups = sortedGroupsMap.keySet().iterator();
		while (itGroups.hasNext()) {
			Principal p = (Principal)sortedGroupsMap.get((String) itGroups.next());
			if (!sortedGroups.contains(p)) sortedGroups.add(p);
		}
		Iterator itUsers = sortedUsersMap.keySet().iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal)sortedUsersMap.get((String) itUsers.next());
			if (!sortedUsers.contains(p)) sortedUsers.add(p);
		}

		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));
	}
	public static class ConfigHelper implements DomTreeHelper {
		String action;
		String page;
		public ConfigHelper(String action) {
			this.action = action;
		}
		public ConfigHelper(String action, String page) {
			this.action = action;
			this.page = page;
		}
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_TEMPLATE) {return true;}
			return false;
		}
		public boolean hasChildren(AllModulesInjected bs, Object source, int type) {
			TemplateBinder config = (TemplateBinder)source;
			return !config.getBinders().isEmpty();
		}
	
		public String getAction(int type, Object source) {
			return action;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			return "false";
		}
		public String getTreeNameKey() {return null;}
		public String getPage() {return page;}
		public void customize(AllModulesInjected bs, Object source, int type, Element element) {};
		
	}
	public static void buildAccessControlRoleBeans(AllModulesInjected bs, Map model) {
		//Add the list of existing functions for this zone
		//since names are translatable, build our own function map
		Map functions = new TreeMap();
		List<Function> fs = bs.getAdminModule().getFunctions();
		for (Function f:fs) {
			functions.put(NLT.getDef(f.getName()).toLowerCase() + f.getId(), f);
		}
		model.put(WebKeys.FUNCTIONS, functions.values());
		
		//Add the list of workAreaOperations that can be added to each function
		//title must be map key to keep alphabetical
		Map operations = new TreeMap();
		Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
		while (itWorkAreaOperations.hasNext()) {
			String operationName = (String) ((WorkAreaOperation) itWorkAreaOperations.next()).toString();
			operations.put(NLT.get("workarea_operation." + operationName),operationName);
		}
		model.put(WebKeys.WORKAREA_OPERATIONS, operations);
	}
	
	// Walk the list of entries returned by the search engine.  If
	// an entry has doctype=attachment then see if it's entry is
	// already in the list, and add this attachment to it's map.
	// if not, see if there is an attachment for the same entry
	// already in the list.  If so, add this as an attachment to that 
	// attachment.
	//
	// Note that if an entry was on this list, then a new key/value pair of
	// (WebKeys.ENTRY_HAS_META_HIT, true) will be added to it's map.
	//
	// if attachments are found for an entry, then they will be taken off
	// the entry list, and added to the list of attachments for that entry
	// in the list. (key value - WebKeys.ENTRY_ATTACHMENTS
	//
	// if attachments are found, but not the entry they're associated
	// with, then leave an attachment on the list, and if there are 
	// mulitple attachments for the same entry, then the attachment
	// will contain a map entry (WebKeys.ENTRY_ATTACHMENTS), which
	// contains all the attachments for that entry.
	public static List filterEntryAttachmentResults(List entries) {

		for (int count = 0; count < entries.size(); count++) {
			Map entry = (Map)entries.get(count);
			String type = (String)entry.get(BasicIndexUtils.DOC_TYPE_FIELD);
			// if it's an entry, see if there's already an attachment in the list for it.
			String docId = (String)entry.get(EntityIndexUtils.DOCID_FIELD);
			String entityType = (String)entry.get(EntityIndexUtils.ENTITY_FIELD);
			if (type.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ENTRY)) {
				int i = 0;
				for (i=0; i < count; i++) {
					String d = (String)((Map)entries.get(i)).get(EntityIndexUtils.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(EntityIndexUtils.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then it's an attachment, 
						// so insert ourselves in here, add the attachment to 
						// the entry, and delete this attachment from the list.
						Map att = (Map)entries.get(i);
						// see if the attachment has other attachments added to it.
						// if it does, then add those to this entry.
						List attachments = (List)att.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments != null) 
							entry.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
						else
							entry.put(WebKeys.ENTRY_ATTACHMENTS, att);
						entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
						entries.remove(i);
						count--;
					}
				}
				if (i == count || count == 1) {
					entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
				}
			} else if (type.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ATTACHMENT)) {

				for (int i = 0; i < count; i++) {
					String d = (String) ((Map) entries.get(i)).get(EntityIndexUtils.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(EntityIndexUtils.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then check if it's an
						// entry. If it is an entry, then add this attachment to
						// the entry, and delete this attachment from the list.
						// if it's an attachment, then if the attachment already
						// has an attachments map, add this to it. Otherwise,
						// create the attachments map, and add the attachment, and this
						// entry to it.
						Map ent = (Map) entries.get(i);
						String typ = (String) ent.get(BasicIndexUtils.DOC_TYPE_FIELD);

						// entry = (Map)entries.get(count);
						// see if this entry already has attachments
						List attachments = (List) ent.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments == null) {
							attachments = new ArrayList();
						}
						if (typ.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ATTACHMENT)) {
							attachments.add(ent);
						}
						attachments.add(entry);
						ent.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
					
						entries.remove(count);
						count--;
						break;
					}
				}
			}
		}
		return entries;		
	}
	
	//Routine to build the beans for displaying entry versions
	//  Each ChangeLog document is exploaded into a map of values
	public static List BuildChangeLogBeans(List changeLogs) {
		List changeList = new ArrayList();
		if (changeLogs == null) return changeList;

		for (int i = 0; i < changeLogs.size(); i++) {
			ChangeLog log = (ChangeLog) changeLogs.get(i);
			Document doc = log.getDocument();
			Element root = doc.getRootElement();
			Map changeMap = new HashMap();
			changeList.add(changeMap);
			
			//Get name of rootElement (e.g., folderEntry) and build a map of its elements
			Map rootMap = new HashMap();
			changeMap.put(root.getName(), rootMap);
			Map attributeMap = new HashMap();
			rootMap.put("attributes", attributeMap);
			Iterator itAttr = root.attributeIterator();
			while (itAttr.hasNext()) {
				Attribute attr = (Attribute) itAttr.next();
				attributeMap.put(attr.getName(),attr.getValue());
			}

			Iterator itRoot = root.elementIterator();
			while (itRoot.hasNext()) {
				Element ele = (Element) itRoot.next();
				Map eleMap = (Map) rootMap.get(ele.getName());
				if (eleMap == null) {
					eleMap = new HashMap();
					rootMap.put(ele.getName(), eleMap);
				}
				//Add the attributes
				String name = ele.attributeValue("name");
				attributeMap = new HashMap();
				eleMap.put("attributes", attributeMap);
				itAttr = ele.attributeIterator();
				while (itAttr.hasNext()) {
					Attribute attr = (Attribute) itAttr.next();
					attributeMap.put(attr.getName(), attr.getValue());
				}
				//Add the data
				eleMap.put(name, ele.getData());
			}
		}
		return changeList;
	}
	
	public static Tabs.TabEntry initTabs(PortletRequest request, Binder binder) throws Exception {
		//Set up the tabs
		Tabs tabs = Tabs.getTabs(request);

		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "1");
		if ("1".equals(newTab)) {
			return tabs.findTab(binder, true);
		} else if ("0".equals(newTab)) {
			//	make sure params are set up
			return tabs.findTab(binder, false);
		} else  {
			return tabs.findTab(binder, true);			
		}
	}

	public static boolean isWebdavSupported(HttpServletRequest req) {
		//Is this ie7
		if (BrowserSniffer.is_ie_7(req)) return SPropsUtil.getBoolean("webdav.ie.7", false);
		
		//Is this ie6
		if (BrowserSniffer.is_ie_6(req)) return SPropsUtil.getBoolean("webdav.ie.6", false);
		
		//Is this moz5
		if (BrowserSniffer.is_mozilla_5(req)) return SPropsUtil.getBoolean("webdav.moz.5", false);
		
		return false;
	}
	
	public static void buildDashboardToolbar(RenderRequest request, RenderResponse response, 
			AllModulesInjected bs, Binder binder, Toolbar dashboardToolbar, Map model) {
		//	The "Manage dashboard" menu
		//See if the dashboard is being shown in the definition
		PortletURL url;
		boolean sharedUser = RequestContextHolder.getRequestContext().getUser().isShared();
		if (DefinitionHelper.checkIfBinderShowingDashboard(binder)) {
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			boolean dashboardContentExists = DashboardHelper.checkIfAnyContentExists(ssDashboard);
			
			//This folder is showing the dashboard
			Map qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageDashboard");
			qualifiers.put("linkclass", "ss_dashboard_config_control");
			dashboardToolbar.addToolbarMenu("3_manageDashboard", NLT.get("__dashboard_canvas"), "", qualifiers);
			if (!sharedUser || bs.getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
				dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);
			}
			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
				if (!(binder instanceof TemplateBinder) && !sharedUser) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DASHBOARD_TITLE);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "local");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.setTitle"), url);
	
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "global");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.global"), url);
				}
				//Check the access rights of the user
				if (bs.getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "binder");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				}
	
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', '" +
						binder.getId()+"');return false;");
				
				if (DashboardHelper.checkIfShowingAllComponents(binder)) {
					qualifiers.put("icon", "dashboard_hide.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					qualifiers.put("icon", "dashboard_show.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}		
	}
	
	public static void sendMailOnEntryCreate(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
		String title = PortletRequestUtils.getStringParameter(request, "title", "--no title--");
		String toList = PortletRequestUtils.getStringParameter(request, "_sendMail_toList", "");
		String toTeam = PortletRequestUtils.getStringParameter(request, "_sendMail_toTeam", "");
		String subject = PortletRequestUtils.getStringParameter(request, "_sendMail_subject", "\"" + title + "\" entry notification");
		String body = PortletRequestUtils.getStringParameter(request, "_sendMail_body", "");
		String includeAttachments = PortletRequestUtils.getStringParameter(request, "_sendMail_includeAttachments", "");
		if (!toList.equals("") || !toTeam.equals("")) {
			FolderEntry entry = bs.getFolderModule().getEntry(folderId, entryId);
			Set entrySet = new HashSet();
			entrySet.add(entry);
			Set users = new HashSet();
			users.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("_sendMail_toList")));
			
			if (!toTeam.equals("")) {
				Set teamMemberIds = entry.getParentFolder().getTeamMemberIds();
				if (!teamMemberIds.isEmpty()) users.addAll(teamMemberIds);
			}
			
			String messageBody = "<a href=\"";
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());
			messageBody += adapterUrl.toString();
			messageBody += "\">" + entry.getTitle() + "</a><br/><br/>";
			messageBody += body;
			
			boolean incAtt = false;
			if (!includeAttachments.equals("")) incAtt = true;

			if (!users.isEmpty()) {
				try {
					Map status = bs.getAdminModule().sendMail(users, null, subject, 
							new Description(messageBody, Description.FORMAT_HTML), 
							entrySet, incAtt);
				} catch (Exception e) {
					//TODO Log that mail wasn't sent
				}
			}
		}
	}
	
	public static void subscribeToThisEntry(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
		String subscribeElementPresent = PortletRequestUtils.getStringParameter(request, "_subscribe_element_present", "");
		//test attachments first for higher precedence
		Map<Integer,String[]> styles = new HashMap();
		for (int i=2; i<6; ++i) {
			if (i == 4) continue;
			String[] address = PortletRequestUtils.getStringParameters(request, "_subscribe"+i);
			if (address == null || address.length ==0) continue;
			else styles.put(Integer.valueOf(i), address);
		}
		if (!styles.isEmpty()) {
			bs.getFolderModule().addSubscription(folderId, entryId, styles);
			
		} else if (Validator.isNotNull(subscribeElementPresent)) {
			//The user turned off the subscription
			bs.getFolderModule().deleteSubscription(folderId, entryId);
		}
	}

	public static HashMap getEntryAccessMap(AllModulesInjected bs, Map model, FolderEntry entry) {
		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = new HashMap();
		if (accessControlMap.containsKey(entry.getId())) {
			entryAccessMap = (HashMap) accessControlMap.get(entry.getId());
		}
		return entryAccessMap;
	}
	
	public static void setAccessControlForAttachmentList(AllModulesInjected bs, 
			Map model, FolderEntry entry, User user) {

		Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, entry);

		boolean reserveAccessCheck = false;
		boolean isUserBinderAdministrator = false;
		boolean isEntryReserved = false;
		boolean isLockedByAndLoginUserSame = false;

		if (bs.getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
			reserveAccessCheck = true;
		}
		if (bs.getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
			isUserBinderAdministrator = true;
		}
		
		HistoryStamp historyStamp = entry.getReservation();
		if (historyStamp != null) isEntryReserved = true;

		if (isEntryReserved) {
			Principal lockedByUser = historyStamp.getPrincipal();
			if (lockedByUser.getId().equals(user.getId())) {
				isLockedByAndLoginUserSame = true;
			}
		}
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			accessControlEntryMap.put("addReply", new Boolean(true));
		}		
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("modifyEntry", new Boolean(true));
			}
		}
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("deleteEntry", new Boolean(true));
			}
		}		
	}

	public static Map prepareSearchResultPage(AllModulesInjected bs, RenderRequest request, Tabs tabs) {
		Map model = new HashMap();

		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
		//new search
		Tabs.TabEntry tab = tabs.findTab(Tabs.SEARCH, tabId);
		if (tab == null) return prepareSearchResultData(bs, request, tabs);
		// get query and options from tab
		Document searchQuery = tab.getQueryDoc();
		Map options = getOptionsFromTab(tab);
		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, -1);
		if (pageNo != -1) options.put(Tabs.PAGE, pageNo);				

		// get page no and actualize options
		// execute query
		// actualize tabs info
		actualizeOptions(options, request);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
		
		return model;
	}

	public static Map prepareSavedQueryResultData(AllModulesInjected bs, RenderRequest request, Tabs tabs) throws PortletRequestBindingException {
		Map model = new HashMap();

		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		// get query and options from tab		
		Document searchQuery = getSavedQuery(bs, queryName, bs.getProfileModule().getUserProperties(currentUser.getId()));
		
		// get page no and actualize options
		// execute query
		// actualize tabs info
		Map options = prepareSearchOptions(bs, request);
		actualizeOptions(options, request);

		options.put(Tabs.TITLE, queryName);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
		
		return model;
	}
	
	public static Map prepareSearchResultData(AllModulesInjected bs, RenderRequest request, Tabs tabs) {
		Map model = new HashMap();

		SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, bs.getDefinitionModule());
		Document searchQuery = requestParser.getSearchQuery();
		Map options = prepareSearchOptions(bs, request);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);

		return model;
	}
	
	public static void prepareSearchResultPage (AllModulesInjected bs, Map results, Map model, Document query, Map options, Tabs.TabEntry tab) {
		
		model.put(WebKeys.URL_TAB_ID, tab.getTabId());
		//save tab options
		tab.setData(options);
		SearchFilterToMapConverter searchFilterConverter = 
			new SearchFilterToMapConverter(query, bs.getDefinitionModule(), bs.getProfileModule(), bs.getBinderModule());
		model.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		// SearchUtils.filterEntryAttachmentResults(results);
		prepareRatingsAndFolders(bs, model, (List) results.get(ObjectKeys.SEARCH_ENTRIES));
		model.putAll(prepareSavedQueries(bs));

		// this function puts also proper part of entries list into a model
		preparePagination(model, results, options, tab);
		
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("summaryWordCount", (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));

		model.put("quickSearch", options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		
	}
	public static Map prepareSearchFormData(AllModulesInjected bs, RenderRequest request) throws PortletRequestBindingException {
		Map options = prepareSearchOptions(bs, request);
		Map model = new HashMap();
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("quickSearch", false);
		
		model.putAll(prepareSavedQueries(bs));
		
		Workspace ws = bs.getWorkspaceModule().getWorkspace();
		Document tree = bs.getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, bs),1);
		model.put(WebKeys.DOM_TREE, tree);
		
		return model;
	}
	
	private static Map prepareSearchOptions(AllModulesInjected bs, RenderRequest request) {
		
		Map options = new HashMap();
		
		//If the entries per page is not present in the user properties, then it means the
		//number of records per page is obtained from the ssf properties file, so we do not have 
		//to worry about checking the old and new number or records per page.
		
		//Getting the entries per page from the user properties
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProp = bs.getProfileModule().getUserProperties(user.getId());
		String entriesPerPage = (String) userProp.getProperty(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE);
		if (entriesPerPage == null || "".equals(entriesPerPage)) {
			entriesPerPage = SPropsUtil.getString("search.records.listed");
		}
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_USER_OFFSET, 0);
			
		// TODO - implement it better(?) this stuff is to get from lucene proper entries,  
		// to get the ~ proper rankings we get from lucene MoreEntriesCounter more hits as max on page
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_MAX_HITS, new Integer(entriesPerPage));
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS, new Integer(20));
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits + 200;
		if (searchUserOffset > 200) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		options.put(Tabs.PAGE, pageNo);				
		
		Boolean quickSearch = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_QUICKSEARCH, Boolean.FALSE);
		options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, quickSearch);
		DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
		fmt.setTimeZone(user.getTimeZone());
		if (quickSearch) {
			options.put(Tabs.TITLE, NLT.get("searchForm.quicksearch.Title") + " " + fmt.format(new Date()));
		} else {
			options.put(Tabs.TITLE, NLT.get("searchForm.advanced.Title") + " " + fmt.format(new Date()));
		} 
	
		return options;
	}

	private static void actualizeOptions(Map options, RenderRequest request) {
		Integer pageNo = (Integer)options.get(Tabs.PAGE);
		if ((pageNo == null) || pageNo < 1) {
			pageNo = 1;
			
		}
		int defaultMaxOnPage = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) defaultMaxOnPage = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		int[] maxOnPageArr = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_MAX_HITS);
		int maxOnPage = defaultMaxOnPage;
		if (maxOnPageArr.length >0) maxOnPage = maxOnPageArr[0];
		int userOffset = (pageNo - 1) * maxOnPage;
		int[] summaryWords = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS);
		int summaryWordsCount = 20;
		if (options.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) { summaryWordsCount = (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS);}
		if (summaryWords.length > 0) {summaryWordsCount = summaryWords[0];}
		
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, userOffset);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxOnPage);
		options.put(WebKeys.URL_PAGE_NUMBER, pageNo);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWordsCount);
		
	}
	private static Map getOptionsFromTab(Tabs.TabEntry tab) {
		Map options = new HashMap();
		Map tabData = tab.getData();
		if (tabData.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, tabData.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		if (tabData.containsKey(ObjectKeys.SEARCH_OFFSET)) options.put(ObjectKeys.SEARCH_OFFSET, tabData.get(ObjectKeys.SEARCH_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) options.put(ObjectKeys.SEARCH_USER_OFFSET, tabData.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_MAX_HITS)) options.put(ObjectKeys.SEARCH_MAX_HITS, tabData.get(ObjectKeys.SEARCH_MAX_HITS));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) options.put(ObjectKeys.SEARCH_USER_MAX_HITS, tabData.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		if (tabData.containsKey(Tabs.TITLE)) options.put(Tabs.TITLE, tabData.get(Tabs.TITLE));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, tabData.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_QUICKSEARCH)) options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, tabData.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		if (tabData.containsKey(Tabs.PAGE)) options.put(Tabs.PAGE, tabData.get(Tabs.PAGE));
		return options;
	}
	
	public static Document getSavedQuery(AllModulesInjected bs, String queryName, UserProperties userProperties) {
		
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			Object q = queries.get(queryName);
			if (q == null) return null;
			if (q instanceof String) {
				try {
					return DocumentHelper.parseText((String)q);
				} catch (Exception ex) {
					queries.remove(queryName);
					bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				};
			}
			//In v1 these are stored as documents; shouldn't be because the hibernate dirty check always fails causing updates
			if (q instanceof Document) {
				queries.put(queryName, ((Document)q).asXML());
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				return (Document)q;
			}
		
		}
		return null;
	}


	
	private static Map prepareSavedQueries(AllModulesInjected bs) {
		Map result = new HashMap();
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = bs.getProfileModule().getUserProperties(currentUser.getId());
		if (userProperties != null) {
			Map properties = userProperties.getProperties();
			if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
				Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
				result.put(WebKeys.SEARCH_SAVED_QUERIES, queries.keySet());
			}
		}
		return result;
	}
	//This method rates the people
	public static List ratePeople(List entries) {
		//The same logic and naming has been followed for both people and placess
		return ratePlaces(entries);
	}

	private static void prepareRatingsAndFolders(AllModulesInjected bs, Map model, List entries) {
		List peoplesWithCounters = sortPeopleInEntriesSearchResults(entries);
		List placesWithCounters = sortPlacesInEntriesSearchResults(bs.getBinderModule(), entries);
		
		List peoplesRating = ratePeople(peoplesWithCounters);
		model.put(WebKeys.FOLDER_ENTRYPEOPLE + "_all", peoplesRating);
		
		List peoplesRatingToShow = new ArrayList();
		if (peoplesRating.size() > 20) {
			peoplesRatingToShow.addAll(peoplesRating.subList(0,20));
		} else {
			peoplesRatingToShow.addAll(peoplesRating);
		}
		List placesRating = ratePlaces(placesWithCounters);
		if (placesRating.size() > 20) {
			placesRating = placesRating.subList(0,20);
		}
		model.put(WebKeys.FOLDER_ENTRYPEOPLE, peoplesRatingToShow);
		model.put(WebKeys.FOLDER_ENTRYPLACES, placesRating);

		Map folders = prepareFolderList(placesWithCounters);
		extendEntriesInfo(entries, folders);

		// TODO check and make it better, copied from SearchController
		List entryCommunityTags = BinderHelper.sortCommunityTags(entries);
		List entryPersonalTags = BinderHelper.sortPersonalTags(entries);
		int intMaxHitsForCommunityTags = BinderHelper.getMaxHitsPerTag(entryCommunityTags);
		int intMaxHitsForPersonalTags = BinderHelper.getMaxHitsPerTag(entryPersonalTags);
		int intMaxHits = intMaxHitsForCommunityTags;
		if (intMaxHitsForPersonalTags > intMaxHitsForCommunityTags) intMaxHits = intMaxHitsForPersonalTags;
		entryCommunityTags = BinderHelper.rateCommunityTags(entryCommunityTags, intMaxHits);
		entryPersonalTags = BinderHelper.ratePersonalTags(entryPersonalTags, intMaxHits);

		model.put(WebKeys.FOLDER_ENTRYTAGS, entryCommunityTags);
		model.put(WebKeys.FOLDER_ENTRYPERSONALTAGS, entryPersonalTags);
	}

	protected static List sortPlacesInEntriesSearchResults(BinderModule binderModule, List entries) {
		HashMap placeMap = new HashMap();
		ArrayList placeList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique places.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String id = (String)entry.get("_binderId");
			if (id == null) continue;
			Long bId = new Long(id);
			if (placeMap.get(bId) == null) {
				placeMap.put(bId, new Place(bId,1));
			} else {
				Place p = (Place)placeMap.remove(bId);
				p = new Place(p.getId(),p.getCount()+1);
				placeMap.put(bId,p);
			}
		}
		//sort the hits
		Collection collection = placeMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			Binder binder=null;
			try {
				binder = binderModule.getBinder(((Place)array[j]).getId());
			} catch (Exception ex) {
				//not access or doesn't exist?
			}
			int count = ((Place)array[j]).getCount();
			Map place = new HashMap();
			place.put(WebKeys.BINDER, binder);
			place.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(count));
			placeList.add(place);
		}
		return placeList;

	}

	//This method rates the places
	public static List ratePlaces(List entries) {
		List ratedList = new ArrayList();
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map place = (Map) entries.get(i);
			Integer resultCount = (Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (i == 0) {
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(100));
				place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				intMaxHitsPerFolder = resultCount;
			}
			else {
				int intResultCount = resultCount.intValue();
				Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
				int intRatingForFolder = DblRatingForFolder.intValue();
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
				if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				}
				else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brighter");
				}
				else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_bright");
				}
				else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_dim");
				}
				else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_very_dim");
				}
			}
			ratedList.add(place);
		}
		return ratedList;
	}
	
	private static Map prepareFolderList(List placesWithCounters) {
		Map folderMap = new HashMap();
		Iterator it = placesWithCounters.iterator();
		while (it.hasNext()) {
			Map place = (Map) it.next();
			Binder binder = (Binder)place.get(WebKeys.BINDER);
			if (binder == null) continue;
			Binder parentBinder = binder.getParentBinder();
			String parentBinderTitle = "";
			if (parentBinder != null) parentBinderTitle = parentBinder.getTitle() + " // ";
			folderMap.put(binder.getId(), parentBinderTitle + binder.getTitle());
		}
		return folderMap;
	}
	
	private static void extendEntriesInfo(List entries, Map folders) {
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map) it.next();
			if (entry.get(WebKeys.SEARCH_BINDER_ID) != null) {
				entry.put(WebKeys.BINDER_TITLE, folders.get(Long.parseLong((String)entry.get(WebKeys.SEARCH_BINDER_ID))));
			}
		}
	}
	
	private static void preparePagination(Map model, Map results, Map options, Tabs.TabEntry tab) {
		int totalRecordsFound = (Integer) results.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		int countReturned = (Integer) results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		int pageInterval = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options != null && options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) {
			pageInterval = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		}
		
		int pagesCount = (int)Math.ceil((double)totalRecordsFound / pageInterval);
		
		int firstOnCurrentPage = 0;
		if (options != null && options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) {
			firstOnCurrentPage = (Integer) options.get(ObjectKeys.SEARCH_USER_OFFSET);
		}
		
		if (firstOnCurrentPage > totalRecordsFound || firstOnCurrentPage < 0) {
			firstOnCurrentPage = 0;
		}

		int currentPageNo = firstOnCurrentPage / pageInterval + 1;
		int lastOnCurrentPage = firstOnCurrentPage + pageInterval;
		if ((countReturned - firstOnCurrentPage)<pageInterval) {
			//If asking for more than search results returned, don't go beyond the ammount returned
			//TODO Make the search request ask for the proper results (i.e., set the "starting count" properly on the search)
			lastOnCurrentPage = firstOnCurrentPage + (countReturned-firstOnCurrentPage);
			if (firstOnCurrentPage < 0) firstOnCurrentPage = 0;
		}
		
		List shownOnPage = ((List) results.get(ObjectKeys.SEARCH_ENTRIES)).subList(firstOnCurrentPage, lastOnCurrentPage);
		checkFileIds(shownOnPage);
		model.put(WebKeys.FOLDER_ENTRIES, shownOnPage);
		model.put(WebKeys.PAGE_NUMBER, currentPageNo);
		
		List pageNos = new ArrayList();
		for (int i = currentPageNo - 2; i <= currentPageNo; i++) {
			if (i > 0) {
				pageNos.add(i);
			}
		}
		
		for (int i = currentPageNo+1; i <= currentPageNo+2; i++) {
			if (i <= pagesCount) {
				pageNos.add(i);
			}
		}
		model.put(WebKeys.PAGE_NUMBERS, pageNos);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, totalRecordsFound);
		model.put(WebKeys.PAGE_START_INDEX, firstOnCurrentPage+1);
		model.put(WebKeys.PAGE_END_INDEX, lastOnCurrentPage);
		
	}
	
	public static void checkFileIds(List entries) {
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map)it.next();
			if (entry.containsKey(ObjectKeys.FIELD_FILE_ID)) {
				try{ 
					Set files = ((SearchFieldResult)entry.get(ObjectKeys.FIELD_FILE_ID)).getValueSet();
					if (!files.isEmpty()) {
						entry.put(ObjectKeys.FIELD_FILE_ID, files.iterator().next());
					}
				} catch (Exception e) {
					// do nothing, if not set - it is only one or no _fileID
				}
			}
		}
	}
	
	// This method reads thru the results from a search, finds the principals, 
	// and places them into an array that is ordered by the number of times
	// they show up in the results list.
	public static List sortPeopleInEntriesSearchResults(List entries) {
		HashMap userMap = new HashMap();
		ArrayList userList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			Principal user = (Principal)entry.get(WebKeys.PRINCIPAL);
			if (userMap.get(user.getId()) == null) {
				userMap.put(user.getId(), new Person(user.getId(),user,1));
			} else {
				Person p = (Person)userMap.remove(user.getId());
				p.incrCount();
				userMap.put(user.getId(),p);
			}
		}
		//sort the hits
		Collection collection = userMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap person = new HashMap();
			Principal user = (Principal) ((Person)array[j]).getUser();
			int intUserCount = ((Person)array[j]).getCount();
			person.put(WebKeys.USER_PRINCIPAL, user);
			person.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(intUserCount));
			userList.add(person);
		}
		return userList;
	}
	
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Person implements Comparable {
		long id;
		int count;
		Principal user;

		public Person (long id, Principal p, int count) {
			this.id = id;
			this.user = p;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public Principal getUser() {
			return this.user;
		}
		
		public int compareTo(Object o) {
			Person p = (Person) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Place implements Comparable {
		long id;
		int count;

		public Place (long id, int count) {
			this.id = id;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public long getId() {
			return this.id;
		}
		
		public int compareTo(Object o) {
			Place p = (Place) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}	

}
