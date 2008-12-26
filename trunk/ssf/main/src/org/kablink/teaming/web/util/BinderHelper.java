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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.document.DateTools;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.ModelAndView;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.portlet.forum.ViewController;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilterRequestParser;
import org.kablink.teaming.search.filter.SearchFilterToMapConverter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.util.search.Restrictions;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.DomTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.domain.Definition;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

public class BinderHelper {
	public static final String RELEVANCE_DASHBOARD_PORTLET="ss_relevance_dashboard";
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
	public static final String WORKAREA_PORTLET="ss_workarea";
	public static final String WELCOME_PORTLET="ss_welcome";
	
	private static final String LOGOUT_SUCCESS_URL_MOBILE;
	private static final String AUTHENTICATION_FAILURE_URL_MOBILE;
	
	static {
		String url = SPropsUtil.getString("mobile.spring.security.logout.success.url", "");
		if(Validator.isNull(url)) {
			url = null;
		}
		else {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				url = null;
			}
		}
		LOGOUT_SUCCESS_URL_MOBILE = url;
		
		url = SPropsUtil.getString("mobile.spring.security.authentication.failure.url", "");
		if(Validator.isNull(url)) {
			url = null;
		}
		else {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				url = null;
			}
		}
		AUTHENTICATION_FAILURE_URL_MOBILE = url;
	}
	
	static public ModelAndView CommonPortletDispatch(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
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
		
		//Set up the standard beans
		setupStandardBeans(bs, request, response, model);
		
		String displayType = getDisplayType(request);
        User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_NAME, SPropsUtil.getString("product.conferencing.name", ObjectKeys.PRODUCT_CONFERENCING_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_TITLE, SPropsUtil.getString("product.conferencing.title", ObjectKeys.PRODUCT_CONFERENCING_TITLE_DEFAULT));
		model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		
		if (prefs != null) displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			displayType = getDisplayType(request);
		}
			
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
		} else if (WORKSPACE_PORTLET.equals(displayType) || WELCOME_PORTLET.equals(displayType)) {
			String id = null;
			if (prefs != null) id = PortletPreferencesUtil.getValue(prefs, WebKeys.WORKSPACE_PREF_ID, null);
			Workspace binder;
			try {
				binder = bs.getWorkspaceModule().getWorkspace(Long.valueOf(id));
			} catch (Exception ex) {
				binder = bs.getWorkspaceModule().getTopWorkspace();				
			}
			Document wsTree;
			//when at the top, don't expand
			if (request.getWindowState().equals(WindowState.NORMAL) &&
					!WELCOME_PORTLET.equals(displayType)) {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 0);
			} else {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
			if (WELCOME_PORTLET.equals(displayType)) {
				return new ModelAndView("help/welcome", model);
			} else {
				return new ModelAndView("workspacetree/view", model);
			}
		    
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
 	        Comparator c = new PrincipalComparator(user.getLocale());
 	       	SortedSet<User> users = new TreeSet(c);
 	       	users.add(user);
 			try {
 				users = bs.getProfileModule().getUsersFromPrincipals(ids);
 			} catch(Exception e) {}
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
			Workspace binder = bs.getWorkspaceModule().getTopWorkspace();
			Document wsTree;
			if (request.getWindowState().equals(WindowState.NORMAL)) {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 1);
			} else {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
 			return new ModelAndView(WebKeys.VIEW_TOOLBAR, model);		
		} else if (RELEVANCE_DASHBOARD_PORTLET.equals(displayType)) {
			model.put(WebKeys.NAMESPACE, response.getNamespace());
	        if (PortletAdapterUtil.isRunByAdapter(request)) {
	        	String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
	    		model.put(WebKeys.NAMESPACE, namespace);
	        }
	        Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			//Get the dashboard initial tab if one was passed in
			String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
	        RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, request, response, 
	        		binderId, type, model);
	    	return new ModelAndView(WebKeys.VIEW_RELEVANCE_DASHBOARD, model); 		
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
			return setupMobilePortlet(bs, request, response, prefs, model, WebKeys.VIEW_MOBILE);		
		} else if (WORKAREA_PORTLET.equals(displayType)) {
			return setupWorkareaPortlet(bs, request, response, prefs, model, WebKeys.VIEW_WORKAREA);		
		}

		return null;
	}
	
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model) {
		Long binderId = null;
		setupStandardBeans(bs, request, response, model, binderId);
		
	}
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model, Long binderId) {
		setupStandardBeans(bs, request, response, model, binderId, "ss_forum");
	}
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model, Long binderId, String portletName) {
		//Set up the standard beans
		//These have been documented, so don't delete any
		if (request != null) {
			String displayType = getDisplayType(request);
			model.put(WebKeys.DISPLAY_TYPE, displayType);
	 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
			if (!namespace.equals("")) {
				model.put(WebKeys.NAMESPACE, namespace);
			} else {
				model.put(WebKeys.NAMESPACE, response.getNamespace());
			}
			AdaptedPortletURL loginUrl = new AdaptedPortletURL(request, portletName, true);
			loginUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOGIN); 
			model.put(WebKeys.LOGIN_URL, loginUrl.toString());
			String logoutUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_LOGOUT;
			String loginPostUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_LOGIN;
			if(displayType.equals(MOBILE_PORTLET) || portletName.equals(MOBILE_PORTLET)) {
				if(LOGOUT_SUCCESS_URL_MOBILE != null)
					logoutUrl += "?logoutSuccessUrl=" + LOGOUT_SUCCESS_URL_MOBILE;
				if(AUTHENTICATION_FAILURE_URL_MOBILE != null)
					loginPostUrl += "?authenticationFailureUrl=" + AUTHENTICATION_FAILURE_URL_MOBILE;
			}
			model.put(WebKeys.LOGOUT_URL, logoutUrl);
			model.put(WebKeys.LOGIN_POST_URL, loginPostUrl);
			if (binderId == null) {
				BinderHelper.getBinderAccessibleUrl(bs, null, null, request, response, model);
			} else {
				try {
					Binder binder = bs.getBinderModule().getBinder(binderId);
					BinderHelper.getBinderAccessibleUrl(bs, binder, null, request, response, model);
				} catch(Exception e) {
					BinderHelper.getBinderAccessibleUrl(bs, null, null, request, response, model);
				}
			}
		}
		User user = null;
		if (RequestContextHolder.getRequestContext() != null) {
        	user = RequestContextHolder.getRequestContext().getUser();
    		if (user != null) {
    			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
        		model.put(WebKeys.USER_PRINCIPAL, user);
        		model.put(WebKeys.USER_PROPERTIES, userProperties.getProperties());
        		model.put(WebKeys.USER_PROPERTIES_OBJ, userProperties);
    		}
        }
		model.put(WebKeys.PORTAL_URL, BinderHelper.getPortalUrl(bs));
		if (binderId != null) {
			model.put(WebKeys.BINDER_ID, binderId.toString());
			if (user != null) {
				UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
				model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties.getProperties());
				model.put(WebKeys.USER_FOLDER_PROPERTIES_OBJ, userFolderProperties);
			}
		}
		if ("standalone".equals(SPropsUtil.getString("deployment.portal"))) {
			model.put((WebKeys.STAND_ALONE), true);
		} else {
			model.put((WebKeys.STAND_ALONE), false);
		}

	}
	public static Document getSearchFilter(AllModulesInjected bs, UserProperties userFolderProperties) {
		convertV1Filters(bs, userFolderProperties);  //make sure converted
		//Determine the Search Filter
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Document searchFilter = null;
		if (Validator.isNotNull(searchFilterName)) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			if (searchFilters != null) {
				String searchFilterStr = (String)searchFilters.get(searchFilterName);
				if (Validator.isNotNull(searchFilterStr)) {
					try {
						searchFilter = DocumentHelper.parseText(searchFilterStr);
					} catch (Exception ignore) {
						//get rid of it
						searchFilters.remove(searchFilterStr);
						bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
					};
				}
			}
		}		
		return searchFilter;
	}
	public static Map convertV1Filters(AllModulesInjected bs, UserProperties userFolderProperties) {
		//see if any v1 filters to convert. 
		//where stored as dom objects which cause unnecessary hibernate updates cause .equals doesn't work
		Map v2SearchFilters = (Map)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS_V1);
		if (v2SearchFilters == null) return new HashMap();
		if (v2SearchFilters.isEmpty()) return v2SearchFilters;
		Map searchFilters = new HashMap();
		for (Iterator iter=v2SearchFilters.entrySet().iterator(); iter.hasNext();) {
			try {
				Map.Entry me = (Map.Entry)iter.next();
				searchFilters.put(me.getKey(), ((Document)me.getValue()).asXML());				
			} catch (Exception ignore) {};
		}
		bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
		bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS_V1, null);
		return searchFilters;
	}
	protected static ModelAndView setupSummaryPortlets(AllModulesInjected bs, RenderRequest request, PortletPreferences prefs, Map model, String view) {
		String gId = null;
		if (prefs != null) gId = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (gId != null) {
			try {
				Map userProperties = (Map) model.get(WebKeys.USER_PROPERTIES);
				DashboardPortlet d = (DashboardPortlet)bs.getDashboardModule().getDashboard(gId);
				model.put(WebKeys.DASHBOARD_PORTLET, d);
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

	protected static ModelAndView setupMobilePortlet(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, PortletPreferences prefs, Map model, String view) {
        User user = RequestContextHolder.getRequestContext().getUser();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		//This is the portlet view; get the configured list of folders to show
		Map userProperties = (Map) model.get(WebKeys.USER_PROPERTIES);
		//where stored as string[] which causes unnecessary sql updates cause arrays always appear dirty to hibernate
		Object mobileBinderIds = userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS);
		Set<Long>binderIds = null;
		if (mobileBinderIds instanceof String) {
			binderIds = LongIdUtil.getIdsAsLongSet((String)mobileBinderIds);
		} else if (mobileBinderIds instanceof String[]) {
			binderIds = LongIdUtil.getIdsAsLongSet((String[])mobileBinderIds);
			bs.getProfileModule().setUserProperty(null,ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS, LongIdUtil.getIdsAsString((String[])mobileBinderIds));
		} else {
			binderIds = new HashSet();
		}

		model.put(WebKeys.MOBILE_BINDER_LIST, bs.getBinderModule().getBinders(binderIds));
		Map unseenCounts = new HashMap();
		unseenCounts = bs.getFolderModule().getUnseenCounts(binderIds);
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);

		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		ProfileBinder profileBinder = null;
		try {
			profileBinder = bs.getProfileModule().getProfileBinder();
		} catch(Exception e) {}
		if (profileBinder != null) {
			accessControlMap.put(WebKeys.CAN_VIEW_USER_PROFILES, true);
		}

		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		model.put("ss_UserQueries", userQueries);
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			adapterUrl.setParameter(WebKeys.OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
			model.put(WebKeys.URL, adapterUrl);
			return new ModelAndView("mobile/show_login_form", model);

		}
		return new ModelAndView(view, model);
		
	}

	protected static ModelAndView setupWorkareaPortlet(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, PortletPreferences prefs, Map model, String view) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		Long binderId = (Long) portletSession.getAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, PortletSession.APPLICATION_SCOPE);
		String entityType = (String) portletSession.getAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, PortletSession.APPLICATION_SCOPE);
		
		if (binderId != null) {
			if (entityType != null && entityType.equals(EntityType.folder.name()))
				return ListFolderHelper.BuildFolderBeans(bs, request, response, binderId);
			if (entityType != null && entityType.equals(EntityType.workspace.name()))
				return WorkspaceTreeHelper.setupWorkspaceBeans(bs, binderId, request, response);
		}

		//This is the default workarea view. Show the user's workspace
		//Set up the navigation beans
		binderId = user.getWorkspaceId();
		Binder binder = null;
		if (binderId != null) {
			try {
				binder = bs.getBinderModule().getBinder(binderId);
			}
			catch(AccessControlException e) {
				//Set up the standard beans
				setupStandardBeans(bs, request, response, model, binderId);
				if (WebHelper.isUserLoggedIn(request) && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//Access is not allowed
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
					return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
				} else {
					//Please log in
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
					return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
				}
			}
			catch(NoBinderByTheIdException e) {}
		}
		if (binder != null) {
			if (binder.getEntityType().name().equals(EntityType.folder.name()))
				return ListFolderHelper.BuildFolderBeans(bs, request, response, binderId);
			if (binder.getEntityType().name().equals(EntityType.workspace.name()))
				return WorkspaceTreeHelper.setupWorkspaceBeans(bs, binderId, request, response);
		}
		binder = bs.getWorkspaceModule().getTopWorkspace();
		Document tree = bs.getBinderModule().getDomBinderTree(binder.getId(), 
				new WsDomTreeBuilder(null, true, bs), 1);
		model.put(WebKeys.WORKSPACE_DOM_TREE, tree);

		return new ModelAndView(view, model);
		
	}

	protected static ModelAndView setupWorkareaNavigationPortlet(AllModulesInjected bs, 
			RenderRequest request, PortletPreferences prefs, Map model, String view) {
		//This is the workarea navigation view
		Binder binder = bs.getWorkspaceModule().getTopWorkspace();
		Document tree = bs.getBinderModule().getDomBinderTree(binder.getId(), 
				new WsDomTreeBuilder(null, true, bs), 1);

		model.put(WebKeys.WORKSPACE_DOM_TREE, tree);

		return new ModelAndView(view, model);
		
	}

	public static String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		boolean isWap = false;
		if (request instanceof RenderRequestImpl) {
			HttpServletRequest httpReq = ((RenderRequestImpl)request).getHttpServletRequest();
			isWap = BrowserSniffer.is_wap_xhtml(httpReq);
		}
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
			return ViewController.WORKAREA_PORTLET;
		else if (pName.contains(ViewController.WIKI_PORTLET))
			return ViewController.WIKI_PORTLET;
		else if (pName.contains(ViewController.MOBILE_PORTLET))
			return ViewController.MOBILE_PORTLET;
		else if (pName.contains(ViewController.WORKAREA_PORTLET))
			return ViewController.WORKAREA_PORTLET;
		else if (pName.contains(ViewController.WELCOME_PORTLET))
			return ViewController.WELCOME_PORTLET;
		else if (pName.contains(ViewController.RELEVANCE_DASHBOARD_PORTLET))
			return ViewController.RELEVANCE_DASHBOARD_PORTLET;
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
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_NEWPAGE;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_POPUP;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_ACCESSIBLE;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
				/** Vertical mode has been removed
				//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
				//only to the table folder view. For all other folder views we need to use the iframe view.
				if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_VERTICAL;
				} else {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
				}
				*/
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			}
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) {
			viewListingJspName = WebKeys.VIEW_LISTING_NEWPAGE;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
			viewListingJspName = WebKeys.VIEW_LISTING_POPUP;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			viewListingJspName = WebKeys.VIEW_LISTING_ACCESSIBLE;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
			/** Vertical mode has been removed
			//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
			//only to the table folder view. For all other folder views we need to use the iframe view.
			if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_VERTICAL;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
			}
			*/
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		}
		return viewListingJspName;
	}
	
	//Routine to save a generic portal url used to build a url to a binder or entry 
	//  This routine is callable only from a portlet controller
	static public void setBinderPermaLink(AllModulesInjected bs, 
			RenderRequest request, RenderResponse response) {
		if (request.getWindowState().equals(WindowState.MAXIMIZED) || getBinderPermaLink(bs, request).equals("")) {
			User user = RequestContextHolder.getRequestContext().getUser();
			PortletURL url = response.createActionURL();
			try {url.setWindowState(WindowState.MAXIMIZED);} catch(Exception e) {};
			url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_TITLE, WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER);
			if (!url.toString().equals(getBinderPermaLink(bs, request)))
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_PERMALINK_URL, url.toString());
		}
	}
	
	//Routine to get the user's portal url 
	//  This routine is callable from an adaptor controller
	static public String getPortalUrl(AllModulesInjected bs) {
		return SPropsUtil.getString("permalink.fallback.url");
	}
	
	//Routine to get a portal url that points to a binder or entry 
	//  This routine is callable from an adaptor controller
	static public String getBinderPermaLink(AllModulesInjected bs, PortletRequest request) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTRY_TITLE, WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER);
		return url.toString();
		/**
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
		*/
	}
	
	static public void getBinderAccessibleUrl(AllModulesInjected bs, Binder binder, Long entryId,
			RenderRequest request, RenderResponse response, Map model) {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("") || 
				(displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
				ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
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
		if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || 
				ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
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
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
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
					tree = bs.getBinderModule().getDomBinderTree(parentBinder.getId(), 
							new WsDomTreeBuilder(null, true, bs, helper),0);
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
    	entryElements.put(Constants.CREATOR_TITLE_FIELD, itemData);
    	
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
		Map sortedApplicationsMap = new TreeMap();
		Map sortedApplicationGroupsMap = new TreeMap();

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
				Map applicationGroups = new HashMap();
				Map applications = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				pMap.put(WebKeys.APPLICATIONS, applications);
				pMap.put(WebKeys.APPLICATION_GROUPS, applicationGroups);
				
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
							} else if (p instanceof User) {
								users.put(p.getId(), p);
							} else if (p instanceof Application) {
								applications.put(p.getId(), p);
							} else if (p instanceof ApplicationGroup) {
								applicationGroups.put(p.getId(), p);
							}
						}
					}
				}
			}
			//Populate the sorted users and groups maps 
			for (Iterator iter = membership.iterator();iter.hasNext();) {
				Long pId = (Long)iter.next();
				Principal p = (Principal)principalMap.get(pId);
				if(p == null) continue;
				if (p instanceof Group) {
					sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof User) {
					sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof Application) {
					sortedApplicationsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof ApplicationGroup) {
					sortedApplicationGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				}
			}

		} else {
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				Map applicationGroups = new HashMap();
				Map applications = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				pMap.put(WebKeys.APPLICATIONS, applications);
				pMap.put(WebKeys.APPLICATION_GROUPS, applicationGroups);
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
							if(p == null) continue;
							if (p instanceof Group) {
								groups.put(p.getId(), p);
								sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof User) {
								users.put(p.getId(), p);
								sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof Application) {
								applications.put(p.getId(), p);
								sortedApplicationsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof ApplicationGroup) {
								applicationGroups.put(p.getId(), p);
								sortedApplicationGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
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
			Map applications = (Map)pMap.get(WebKeys.APPLICATIONS);
			Map applicationGroups = (Map)pMap.get(WebKeys.APPLICATION_GROUPS);
			if (users.size() > 0 || 
					groups.size() > 0 || 
					applications.size() > 0 || 
					applicationGroups.size() > 0 || 
					pMap.containsKey(WebKeys.OWNER) || 
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
		
		List sortedApplicationGroups = new ArrayList(sortedApplicationGroupsMap.values());

		List sortedApplications = new ArrayList(sortedApplicationsMap.values());
		
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

		model.put(WebKeys.ACCESS_SORTED_APPLICATIONS_MAP, sortedApplicationsMap);
		model.put(WebKeys.ACCESS_SORTED_APPLICATIONS, sortedApplications);
		model.put(WebKeys.ACCESS_APPLICATIONS_COUNT, Integer.valueOf(sortedApplications.size()));
		model.put(WebKeys.ACCESS_SORTED_APPLICATION_GROUPS_MAP, sortedApplicationGroupsMap);
		model.put(WebKeys.ACCESS_SORTED_APPLICATION_GROUPS, sortedApplicationGroups);
		model.put(WebKeys.ACCESS_APPLICATION_GROUPS_COUNT, Integer.valueOf(sortedApplicationGroups.size()));
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
			String type = (String)entry.get(Constants.DOC_TYPE_FIELD);
			// if it's an entry, see if there's already an attachment in the list for it.
			String docId = (String)entry.get(Constants.DOCID_FIELD);
			String entityType = (String)entry.get(Constants.ENTITY_FIELD);
			if (type.equalsIgnoreCase(Constants.DOC_TYPE_ENTRY)) {
				int i = 0;
				for (i=0; i < count; i++) {
					String d = (String)((Map)entries.get(i)).get(Constants.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(Constants.ENTITY_FIELD);
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
			} else if (type.equalsIgnoreCase(Constants.DOC_TYPE_ATTACHMENT)) {

				for (int i = 0; i < count; i++) {
					String d = (String) ((Map) entries.get(i)).get(Constants.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(Constants.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then check if it's an
						// entry. If it is an entry, then add this attachment to
						// the entry, and delete this attachment from the list.
						// if it's an attachment, then if the attachment already
						// has an attachments map, add this to it. Otherwise,
						// create the attachments map, and add the attachment, and this
						// entry to it.
						Map ent = (Map) entries.get(i);
						String typ = (String) ent.get(Constants.DOC_TYPE_FIELD);

						// entry = (Map)entries.get(count);
						// see if this entry already has attachments
						List attachments = (List) ent.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments == null) {
							attachments = new ArrayList();
						}
						if (typ.equalsIgnoreCase(Constants.DOC_TYPE_ATTACHMENT)) {
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
			changeMap.put("changeLog", log);
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
				itAttr = ele.attributeIterator();
				while (itAttr.hasNext()) {
					Attribute attr = (Attribute) itAttr.next();
					attributeMap.put(attr.getName(), attr.getValue());
				}
				//Add the data
				if (Validator.isNull(name)) {
					eleMap.put("attributes", attributeMap);
					continue; //no way to add it
				}
				Map dataMap = new HashMap();
				dataMap.put("attributes", attributeMap);
				dataMap.put("value", ele.getData());
				eleMap.put(name, dataMap);
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
	
	public static void setupWhatsNewBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page) {	
		setupWhatsNewBinderBeans(bs, binder, model, page, "");
	}
	public static void setupWhatsNewBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page,
			String type) {		
		//Get the documents bean for the documents just created or modified
		Map options = new HashMap();
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Prepare for a standard search operation
		String entriesPerPage = SPropsUtil.getString("search.records.listed");
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = new Integer(entriesPerPage);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = new Integer(20);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List<String> trackedPlaces = new ArrayList<String>();
		if (type.equals(WebKeys.URL_WHATS_NEW_TRACKED)) {
			trackedPlaces = SearchUtils.getTrackedPlacesIds(bs, binder);
		} else {
			trackedPlaces.add(binder.getId().toString());
		}
		Criteria crit = SearchUtils.entriesForTrackedPlaces(bs, trackedPlaces);
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.WHATS_NEW_BINDER, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					if (!places.containsKey(id)) {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					}
				}
	    	}
    	}
    	model.put(WebKeys.WHATS_NEW_BINDER_FOLDERS, places);
	}
	
	public static void setupUnseenBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page) {		
		//Get a list of unseen entries in this binder tree
		Map options = new HashMap();
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
		int intEntriesPerPage = Integer.valueOf(SPropsUtil.getString("search.whatsNew.entriesPerPage"));
		int pageStart = pageNumber * intEntriesPerPage;
		
		//Prepare for a standard search operation
		String entriesPerPage = SPropsUtil.getString("search.unseen.maxEntries");
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = new Integer(entriesPerPage);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = new Integer(20);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(0));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List<String> trackedPlaces = new ArrayList<String>();
		trackedPlaces.add(binder.getId().toString());
	    //get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000);
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.entriesForTrackedPlaces(bs, trackedPlaces);
		crit.add(org.kablink.util.search.Restrictions.between(
				Constants.MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);
		List<Map> entries = (List<Map>) results.get(ObjectKeys.SEARCH_ENTRIES);
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
		List<Map> unseenEntries = new ArrayList();
		for (Map entry : entries) {
			//Only show the unseen entries
			if (!seen.checkIfSeen(entry)) unseenEntries.add(entry);
			if (unseenEntries.size() >= pageStart + intEntriesPerPage) break;
		}
		if (unseenEntries.size() > pageStart && unseenEntries.size() >= pageStart + intEntriesPerPage) {
			model.put(WebKeys.WHATS_NEW_BINDER, unseenEntries.subList(pageStart, pageStart + intEntriesPerPage));
		} else if (unseenEntries.size() > pageStart) {
			model.put(WebKeys.WHATS_NEW_BINDER, unseenEntries.subList(pageStart, unseenEntries.size()));
		}

		if (unseenEntries.size() > pageStart) {
			Map places = new HashMap();
	    	Iterator it = unseenEntries.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					if (!places.containsKey(id)) {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					}
				}
	    	}
	    	model.put(WebKeys.WHATS_NEW_BINDER_FOLDERS, places);
	    	model.put(WebKeys.WHATS_UNSEEN_TYPE, true);
		}
	}
	
	public static void updateUserStatus(AllModulesInjected bs, ActionRequest request, 
			Long folderId) {
		// Default to updating the user's status using the most recent
		// entry in their MiniBlog folder.
		updateUserStatus(bs, request, folderId, null);
	}
	
	public static void updateUserStatus(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
		Long miniBlogId = user.getMiniBlogId();

		// Does the folderId refer to the user's MiniBlog? 
		if ((null != miniBlogId) && (miniBlogId.longValue() == folderId.longValue())) {
			Folder	miniBlog;
			try {
				// Yes!  Can we access the MiniBlog folder?
				miniBlog = (Folder) bs.getBinderModule().getBinder(miniBlogId);
				if (miniBlog.isDeleted()) {
					//The miniblog folder doesn't exist anymore.
					miniBlog = null;
				}
			} catch(NoBinderByTheIdException e) {
				//The miniblog folder doesn't exist anymore,
				miniBlog = null;
			}
			if (null != miniBlog) {
				// Yes!  Were we given the entryId for the MiniBlog
				// entry to update the user's status with?
				String	text = null;
				if (null == entryId) {
					// No!  Use the most recent entry.  If there are no
					// entries, we'll just set the status text to an
					// empty string so that the string displayed
					// gets removed.
					Criteria crit = SearchUtils.entriesForTrackedMiniBlogs(new Long[]{user.getId()});
					crit.add(Restrictions.eq(Constants.BINDER_ID_FIELD, folderId.toString()));
					Map results   = bs.getBinderModule().executeSearchQuery(crit, 0, 1);
			    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
			    	boolean found = false;
			    	for (Map item: items) {
			    		text  = ((String) item.get(Constants.DESC_FIELD));
			    		found = (null != text);
			    		if (found) {
			    			text = text.trim();
			    			if (0 < text.length() && ('<' == text.charAt(0))) {
			    				text = text.replaceAll("\\<.*?\\>","");
			    			}
			    		}
			    		break;
			    	}
			    	if (!found) {
			    		text = "";
			    	}
				}
				else {
					// Yes, we were given the entryId for the MiniBlog
					// entry to update the user's status with!  Can we
					// access it?
					FolderEntry miniBlogEntry = bs.getFolderModule().getEntry(miniBlogId, entryId);
					if (null != miniBlogEntry) {
						// Yes!  Read the description from it.
						text = miniBlogEntry.getDescription().getStrippedText();
					}
				}
				
				// Do we have the text to update the user's status with?
				if (null != text) {
					// Yes!  Update it.
					bs.getProfileModule().setStatus(text);
					bs.getProfileModule().setStatusDate(new Date());
					bs.getReportModule().addStatusInfo(user);
				}
			}
		}
	}
	
	public static void sendMailOnEntryCreate(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
		MapInputData inputData = new MapInputData(request.getParameterMap());
		String[] toList       = getInputValues(inputData,"_sendMail_toList");
		String[] toListGroups = getInputValues(inputData,"_sendMail_toList_groups");
		String[] toListTeams  = getInputValues(inputData,"_sendMail_toList_teams");
		String toTeam = PortletRequestUtils.getStringParameter(request, "_sendMail_toTeam", "");
		if ((0 < toList.length) || (0 < toListGroups.length) || (0 < toListTeams.length) || !toTeam.equals("")) {
			FolderEntry entry = bs.getFolderModule().getEntry(folderId, entryId);
			ArrayList<Long> handledIds = new ArrayList<Long>();
			Set<Long> recipients = new HashSet<Long>();
			if (0 < toList.length)       handleEmailRecipients(handledIds, recipients, LongIdUtil.getIdsAsLongSet(toList));
			if (0 < toListGroups.length) handleEmailRecipients(handledIds, recipients, LongIdUtil.getIdsAsLongSet(toListGroups));
			if (0 < toListTeams.length)  handleTeamRecipients(handledIds,  recipients, LongIdUtil.getIdsAsLongSet(toListTeams), bs.getBinderModule());
			if (!toTeam.equals(""))      handleEmailRecipients(handledIds, recipients, entry.getParentFolder().getTeamMemberIds());
			
			if (!recipients.isEmpty()) {
				try {
					String title = PortletRequestUtils.getStringParameter(request, "title", "--no title--");
					String body = PortletRequestUtils.getStringParameter(request, "_sendMail_body", "");
					String subject = PortletRequestUtils.getStringParameter(request, "_sendMail_subject", "\"" + title + "\" entry notification");
					String includeAttachments = PortletRequestUtils.getStringParameter(request, "_sendMail_includeAttachments", "");
					boolean incAtt = (!includeAttachments.equals(""));
					bs.getAdminModule().sendMail(entry, recipients, null, null, null, null, subject, 
							new Description(body, Description.FORMAT_HTML), incAtt);
				} catch (Exception e) {
					//TODO Log that mail wasn't sent
				}
			}
		}
	}
	
	private static String[] getInputValues(InputDataAccessor inputData, String parameter) {
		String[] reply = inputData.getValues(parameter);
		if (null == reply) {
			reply = new String[0];
		}
		return reply;
	}
	
	private static void handleEmailRecipients(ArrayList<Long> handledIds, Set<Long> recipients, Set<Long> newRecipients) {
		// Scan the new recipients.
		for (Long id:newRecipients) {
			// Have we already handled this recipient?
			if ((-1) == handledIds.indexOf(id)) {
				// No!  Mark it has having been handled and add it to
				// the recipients list.
				handledIds.add(id);
				recipients.add(id);
			}
		}
	}
	
	private static void handleTeamRecipients(ArrayList<Long> handledIds, Set<Long> recipients, Set<Long> teamWSs, BinderModule bm) {
		// Scan the team workspaces.
		for (Long id:teamWSs) {
			// Have we already handled this team?
			if ((-1) == handledIds.lastIndexOf(id)) {
				// No!  Mark it has having been handled and handle
				// the team members.
				handledIds.add(id);
				Set<Long> teamMemberIds = bm.getTeamMemberIds(id, true);
				handleEmailRecipients(handledIds, recipients, teamMemberIds);
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
			bs.getFolderModule().setSubscription(folderId, entryId, styles);
			
		} else if (Validator.isNotNull(subscribeElementPresent)) {
			//The user turned off the subscription
			bs.getFolderModule().setSubscription(folderId, entryId, null);
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
		if (tab == null) {
			prepareSearchResultData(bs, request, tabs, model);
			return model;
		}
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
	
	public static void prepareSearchResultData(AllModulesInjected bs, RenderRequest request, Tabs tabs, Map model) {

		SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, bs.getDefinitionModule());
		Document searchQuery = requestParser.getSearchQuery();
		Map options = prepareSearchOptions(bs, request);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	}
	
	public static void prepareSearchResultPage (AllModulesInjected bs, Map results, Map model, Document query, 
			Map options, Tabs.TabEntry tab) {
		
		model.put(WebKeys.URL_TAB_ID, tab.getTabId());
		//save tab options
		tab.setData(options);
		SearchFilterToMapConverter searchFilterConverter = 
			new SearchFilterToMapConverter(bs, query);
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
		
		Workspace ws = bs.getWorkspaceModule().getTopWorkspace();
		Document tree = bs.getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, bs),1);
		model.put(WebKeys.DOM_TREE, tree);
		
		return model;
	}
	
	public static Map prepareSearchOptions(AllModulesInjected bs, RenderRequest request) {
		
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
		
		
		// it should be always 0, this method is(should be) used to only on first result page
		Integer searchUserOffset = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_USER_OFFSET, 0);
			
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_MAX_HITS, new Integer(entriesPerPage));
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS, new Integer(20));
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits + ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS;
		if (searchUserOffset > ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		options.put(Tabs.PAGE, pageNo);				
		
		Boolean quickSearch = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_QUICKSEARCH, Boolean.FALSE);
		options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, quickSearch);
		DateFormat fmt = DateFormat.getTimeInstance(DateFormat.SHORT, user.getLocale());
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
		int maxPageToSee = (ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS + maxOnPage) / maxOnPage; 
		if (pageNo > maxPageToSee) { // pageNo <= 21
			searchLuceneOffset += (pageNo - maxPageToSee) * maxOnPage;
			userOffset = ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS;
		}
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

		Map folders = prepareFolderList(placesWithCounters, true);
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

	public static List sortPlacesInEntriesSearchResults(BinderModule binderModule, List entries) {
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
	
	public static Map prepareFolderList(List placesWithCounters, Boolean includeParentBinderTitle) {
		Map folderMap = new HashMap();
		Iterator it = placesWithCounters.iterator();
		while (it.hasNext()) {
			Map place = (Map) it.next();
			Binder binder = (Binder)place.get(WebKeys.BINDER);
			if (binder == null) continue;
			Binder parentBinder = binder.getParentBinder();
			String parentBinderTitle = "";
			if (includeParentBinderTitle && parentBinder != null) 
				parentBinderTitle = parentBinder.getTitle() + " // ";
			folderMap.put(binder.getId(), parentBinderTitle + binder.getTitle());
		}
		return folderMap;
	}
	
	public static void extendEntriesInfo(List entries, Map folders) {
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
		int pageInterval = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options != null && options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) {
			pageInterval = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		}
		
		int pagesCount = (int)Math.ceil((double)totalRecordsFound / pageInterval);
		
		
		List allResultsList = (List) results.get(ObjectKeys.SEARCH_ENTRIES);  
		
		int userOffsetStart = 0;
		if (options != null && options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) {
			userOffsetStart = (Integer) options.get(ObjectKeys.SEARCH_USER_OFFSET);
		}
		if (userOffsetStart > allResultsList.size() || userOffsetStart < 0) {
			userOffsetStart = 0;
		}
		int userOffsetEnd = userOffsetStart + pageInterval;
		if ((allResultsList.size() - userOffsetStart) < pageInterval) {
			userOffsetEnd = userOffsetStart + (allResultsList.size() - userOffsetStart);
		}		
		
		List shownOnPage = allResultsList.subList(userOffsetStart, userOffsetEnd);
		
		int pageNo = 1;
		if (options != null && options.get(WebKeys.URL_PAGE_NUMBER) != null) {
			pageNo = (Integer) options.get(WebKeys.URL_PAGE_NUMBER);
		}
		int firstOnCurrentPage = (pageNo - 1) * pageInterval;;
		
		if (firstOnCurrentPage > totalRecordsFound || firstOnCurrentPage < 0) {
			firstOnCurrentPage = 0;
		}

		int currentPageNo = firstOnCurrentPage / pageInterval + 1;
		int lastOnCurrentPage = firstOnCurrentPage + pageInterval;
		if ((totalRecordsFound - firstOnCurrentPage) < pageInterval) {
			lastOnCurrentPage = firstOnCurrentPage + (totalRecordsFound - firstOnCurrentPage);
			if (firstOnCurrentPage < 0) firstOnCurrentPage = 0;
		}
		
		model.put(WebKeys.FOLDER_ENTRIES, shownOnPage);
		model.put(WebKeys.PAGE_NUMBER, currentPageNo);
		
		List pageNos = new ArrayList();
		int startFrom = 1;
		if (currentPageNo >= 7) {
			startFrom = currentPageNo - 3;
		}
		for (int i = startFrom; i <= currentPageNo; i++) {
			if (i > 0) {
				pageNos.add(i);
			}
		}
		
		for (int i = currentPageNo+1; i <= currentPageNo+3; i++) {
			if (i <= pagesCount) {
				pageNos.add(i);
			}
		}
		
		model.put(WebKeys.PAGE_COUNT, pagesCount);
		model.put(WebKeys.PAGE_NUMBERS, pageNos);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, totalRecordsFound);
		model.put(WebKeys.PAGE_START_INDEX, firstOnCurrentPage+1);
		model.put(WebKeys.PAGE_END_INDEX, lastOnCurrentPage);
		
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
			if (user == null) {
				continue;
			}
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
	
	public static void buildFolderActionsToolbar(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Toolbar folderActionsToolbar, String forumId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getDisplayStyle();
        if (userDisplayStyle == null) userDisplayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
        
		AdaptedPortletURL adapterUrl;
		Map qualifiers;
		PortletURL url;

        //Folder action menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			//Only show these options if not in accessible mode
			folderActionsToolbar.addToolbarMenu("4_display_styles", NLT.get("toolbar.folder_actions"));
			
			//iframe
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
			folderActionsToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_iframe"), url, qualifiers);
			//newpage
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE);
			folderActionsToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_newpage"), url, qualifiers);
			//popup
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
			folderActionsToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_popup"), url, qualifiers);
		}
	}

	public static Map getSearchAndPagingModels(Map entries, Map options) {
		Map model = new HashMap();
		
		if (entries == null) {
			// there is no paging to set
			return model;
		}
		
		String sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		Boolean sortDescend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		
		model.put(WebKeys.FOLDER_SORT_BY, sortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, sortDescend.toString());
		
		int totalRecordsFound = (Integer) entries.get(ObjectKeys.TOTAL_SEARCH_COUNT);
//		int totalRecordsReturned = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		//Start Point of the Record
		int searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		int searchPageIncrement = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
		int goBackSoManyPages = 2;
		int goFrontSoManyPages = 3;
		
		HashMap pagingInfo = getPagingLinks(totalRecordsFound, searchOffset, searchPageIncrement, 
				goBackSoManyPages, goFrontSoManyPages);
		
		HashMap prevPage = (HashMap) pagingInfo.get(WebKeys.PAGE_PREVIOUS);
		ArrayList pageNumbers = (ArrayList) pagingInfo.get(WebKeys.PAGE_NUMBERS);
		HashMap nextPage = (HashMap) pagingInfo.get(WebKeys.PAGE_NEXT);
		String pageStartIndex = (String) pagingInfo.get(WebKeys.PAGE_START_INDEX);
		String pageEndIndex = (String) pagingInfo.get(WebKeys.PAGE_END_INDEX);

		model.put(WebKeys.PAGE_CURRENT, pagingInfo.get(WebKeys.PAGE_CURRENT));
		model.put(WebKeys.PAGE_PREVIOUS, prevPage);
		model.put(WebKeys.PAGE_NUMBERS, pageNumbers);
		model.put(WebKeys.PAGE_NEXT, nextPage);
		model.put(WebKeys.PAGE_START_INDEX, pageStartIndex);
		model.put(WebKeys.PAGE_END_INDEX, pageEndIndex);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, ""+totalRecordsFound);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		model.put(WebKeys.PAGE_LAST, String.valueOf(Math.round(dblNoOfPages)));
		model.put(WebKeys.PAGE_LAST_STARTING_INDEX, String.valueOf((Math.round(dblNoOfPages) -1) * searchPageIncrement));
		model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		return model;
	}	
	
	//This method returns a HashMap with Keys referring to the Previous Page Keys,
	//Paging Number related Page Keys and the Next Page Keys.
	public static HashMap getPagingLinks(int intTotalRecordsFound, int intSearchOffset, 
			int intSearchPageIncrement, int intGoBackSoManyPages, int intGoFrontSoManyPages) {
		
		HashMap<String, Object> hmRet = new HashMap<String, Object>();
		ArrayList<HashMap> pagingInfo = new ArrayList<HashMap>(); 
		int currentDisplayValue = ( intSearchOffset + intSearchPageIncrement) / intSearchPageIncrement;
		hmRet.put(WebKeys.PAGE_CURRENT, String.valueOf(currentDisplayValue));

		//Adding Prev Page Link
		int prevInternalValue = intSearchOffset - intSearchPageIncrement;
		HashMap<String, Object> hmRetPrev = new HashMap<String, Object>();
		hmRetPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "<<");
		hmRetPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
		if (intSearchOffset == 0) {
			hmRetPrev.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_PREVIOUS, hmRetPrev);

		//Adding Links before Current Display
		if (intSearchOffset != 0) {
			//Code for generating the Numeric Paging Information previous to offset			
			int startPrevDisplayFrom = currentDisplayValue - intGoBackSoManyPages;
			
			int wentBackSoManyPages = intGoBackSoManyPages + 1;
			for (int i = startPrevDisplayFrom; i < currentDisplayValue; i++) {
				wentBackSoManyPages--;
				if (i < 1) continue;
				prevInternalValue = (intSearchOffset - (intSearchPageIncrement * wentBackSoManyPages));
				HashMap<String, Object> hmPrev = new HashMap<String, Object>();
				hmPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "" + i);
				hmPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
				pagingInfo.add(hmPrev);
			}
		}
		
		//Adding Links after Current Display
		for (int i = 0; i < intGoFrontSoManyPages; i++) {
			int nextInternalValue = intSearchOffset + (intSearchPageIncrement * i);
			int nextDisplayValue = (nextInternalValue + intSearchPageIncrement) / intSearchPageIncrement;  
			if ( !(nextInternalValue >= intTotalRecordsFound) ) {
				HashMap<String, Object> hmNext = new HashMap<String, Object>();
				hmNext.put(WebKeys.PAGE_DISPLAY_VALUE, "" + nextDisplayValue);
				hmNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
				if (nextDisplayValue == currentDisplayValue) hmNext.put(WebKeys.PAGE_IS_CURRENT, new Boolean(true));
				pagingInfo.add(hmNext);
			}
			else break;
		}
		hmRet.put(WebKeys.PAGE_NUMBERS, pagingInfo);
		
		//Adding Next Page Link
		int nextInternalValue = intSearchOffset + intSearchPageIncrement;
		HashMap<String, Object> hmRetNext = new HashMap<String, Object>();
		hmRetNext.put(WebKeys.PAGE_DISPLAY_VALUE, ">>");
		hmRetNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
		
		if ( (nextInternalValue >= intTotalRecordsFound) ) {
			hmRetNext.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_NEXT, hmRetNext);
		hmRet.put(WebKeys.PAGE_START_INDEX, "" + (intSearchOffset + 1));
		
		hmRet.put(WebKeys.PAGE_END_INDEX, "" + intTotalRecordsFound);
		
		return hmRet;
	}

	public static void addMiniBlogEntry(AllModulesInjected bs, String text) {
        User user = RequestContextHolder.getRequestContext().getUser();
		if (text.length() > ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH) {
			text = text.substring(0, ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH);
		}
		bs.getProfileModule().setStatus(text);
		bs.getProfileModule().setStatusDate(new Date());
		bs.getReportModule().addStatusInfo(user);
		if (0 < text.length()) {
			//Add this to the user's mini blog folder
			Long miniBlogId = user.getMiniBlogId();
			Folder miniBlog = null;
			if (miniBlogId == null) {
				//The miniblog folder doesn't exist, so create it
				miniBlog = bs.getProfileModule().addUserMiniBlog(user);
				
			} else {
				try {
					miniBlog = (Folder) bs.getBinderModule().getBinder(miniBlogId);
					if (miniBlog.isDeleted()) {
						//The miniblog folder doesn't exist anymore, so try create it again
						miniBlog = bs.getProfileModule().addUserMiniBlog(user);
					}
				} catch(NoBinderByTheIdException e) {
					//The miniblog folder doesn't exist anymore, so try create it again
					miniBlog = bs.getProfileModule().addUserMiniBlog(user);
				}
			}
			if (miniBlog != null) {
				//Found the mini blog folder, go add this new entry
		        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
		        		DateFormat.SHORT, user.getLocale());
		        dateFormat.setTimeZone(user.getTimeZone());
				String mbTitle = dateFormat.format(new Date());
				Map data = new HashMap(); // Input data
				data.put(ObjectKeys.FIELD_ENTITY_TITLE, mbTitle);
				data.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, text);
				Definition def = miniBlog.getDefaultEntryDef();
				if (def == null) {
					try {
						def = bs.getDefinitionModule().getDefinitionByReservedId(ObjectKeys.DEFAULT_ENTRY_MINIBLOG_DEF);
					} catch (Exception ex) {}
				}
				if (def != null) {
					FolderModule folderModule = bs.getFolderModule();
					Long entryId = null;
					
					miniBlogId = miniBlog.getId();
					try {
						entryId = folderModule.addEntry(miniBlogId, def.getId(), new MapInputData(data), null, null);
					} catch (Exception ex) {}
					if (null != entryId) {
						// Mark the entry as read
						bs.getProfileModule().setSeen(user.getId(),folderModule.getEntry(miniBlogId, entryId));
					}
				}
			}
		}
	}
	
	public static void buildWorkflowSupportBeans(AllModulesInjected bs, List entryList, Map model) {
		Map captionMap = new HashMap();
		Map questionsMap = new HashMap();
		Map transitionMap = new HashMap();
		for (int i=0; i<entryList.size(); i++) {
			FolderEntry entry = (FolderEntry)entryList.get(i);
			Set states = entry.getWorkflowStates();
			for (Iterator iter=states.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				//store the UI caption for each state
				captionMap.put(ws.getTokenId(), WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()));
				//See if user can transition out of this state
				if (bs.getFolderModule().testTransitionOutStateAllowed(entry, ws.getTokenId())) {
					//get all manual transitions
					Map trans = bs.getFolderModule().getManualTransitions(entry, ws.getTokenId());
					transitionMap.put(ws.getTokenId(), trans);
				} 
					
				Map qMap = bs.getFolderModule().getWorkflowQuestions(entry, ws.getTokenId());
				questionsMap.put(ws.getTokenId(), qMap);
			}
		}
		model.put(WebKeys.WORKFLOW_CAPTIONS, captionMap);
		model.put(WebKeys.WORKFLOW_QUESTIONS, questionsMap);
		model.put(WebKeys.WORKFLOW_TRANSITIONS, transitionMap);
	}
	
	public static List getAllApplications(AllModulesInjected bs) {
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);

		Document searchFilter = DocumentHelper.createDocument();
		Element field = searchFilter.addElement(Constants.FIELD_ELEMENT);
    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
    	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    	child.setText(Constants.ENTRY_TYPE_APPLICATION);
    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);
    	
		Map searchResults = bs.getProfileModule().getApplications(options);
		List applications = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		return applications;
	}
}
