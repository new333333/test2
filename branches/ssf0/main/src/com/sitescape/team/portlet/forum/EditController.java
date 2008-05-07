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
package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WorkspaceConfigHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
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
		String displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, "");
		if (Validator.isNull(displayType)) { 
			displayType = BinderHelper.getDisplayType(request);
			prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
		}
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
		}
		//see if type is being set
		if (formData.containsKey("applyBtn") || 
				formData.containsKey("okBtn")) {
			//	if not on form, must already be set.  
			if (ViewController.FORUM_PORTLET.equals(displayType) || 
					ViewController.MOBILE_PORTLET.equals(displayType)) {
				List forumPrefIdList = new ArrayList();
				List forumDelIdList = new ArrayList();
				String[] currentForumPrefIdList = PortletPreferencesUtil.getValues(prefs, 
						WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
				
				//	Build the jsp bean (sorted by folder title)
				for (int i = 0; i < currentForumPrefIdList.length; i++) {
					forumPrefIdList.add(currentForumPrefIdList[i]);
				}
				//	Get the forums to be deleted
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (((String)me.getKey()).startsWith("del_")) {
						String forumId = ((String)me.getKey()).substring(4);
						if (forumPrefIdList.contains(forumId)) forumPrefIdList.remove(forumId);
						forumDelIdList.add(forumId);
					}
				}
				//	Get the forums to be displayed
				itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (WebKeys.URL_ID_CHOICES.equals((String)me.getKey())) {
						String[] values = (String[])me.getValue();
						for (int i = 0; i < values.length; i++) {
							String[] valueSplited = values[i].split("\\s");
							for (int j = 0; j < valueSplited.length; j++) {
								if (valueSplited[j] != null && !"".equals(valueSplited[j])) {
									String forumId = valueSplited[j].substring(2);
									if (!forumPrefIdList.contains(forumId) && !forumDelIdList.contains(forumId)) forumPrefIdList.add(forumId);
								}
							}
						}
					}
				}
				if (ViewController.FORUM_PORTLET.equals(displayType)) {
					prefs.setValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, 
							(String[]) forumPrefIdList.toArray(new String[forumPrefIdList.size()]));
				} else if (ViewController.MOBILE_PORTLET.equals(displayType)) {
					User user = RequestContextHolder.getRequestContext().getUser();
					//don't store arrays in properties
					getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS, LongIdUtil.getIdsAsString(forumPrefIdList));
				}

			} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.TASK_SUMMARY_PORTLET.equals(displayType) ||
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
					DashboardHelper.addComponent(request, d, Dashboard.WIDE_TOP, DashboardHelper.Portlet);
					prefs.setValue(WebKeys.PORTLET_PREF_DASHBOARD, d.getId());
					prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
				}
				DashboardHelper.saveComponentData(request, d);

			} else if (ViewController.RELEVANCE_DASHBOARD_PORTLET.equals(displayType)) {
				//See if this portlet has been initialized yet. If not, add an empty dashboard
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
					prefs.setValue(WebKeys.PORTLET_PREF_DASHBOARD, d.getId());
					prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
				}
				DashboardHelper.saveComponentData(request, d);

			} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
				prefs.setValue(WebKeys.PRESENCE_PREF_USER_LIST, LongIdUtil.getIdsAsString(request.getParameterValues("users")));
				prefs.setValue(WebKeys.PRESENCE_PREF_GROUP_LIST, LongIdUtil.getIdsAsString(request.getParameterValues("groups"))); 			
			} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				String id = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ID_CHOICES, "");
				id = id.replaceAll("topWorkspace", "").trim();
				if (Validator.isNotNull(id)) {
					prefs.setValue(WebKeys.WORKSPACE_PREF_ID, id);
				}
			}
		} else if (ViewController.RELEVANCE_DASHBOARD_PORTLET.equals(displayType)) {
			//See if this portlet has been initialized yet. If not, add an empty dashboard
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
				prefs.setValue(WebKeys.PORTLET_PREF_DASHBOARD, d.getId());
				prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
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
			displayType = BinderHelper.getDisplayType(request);
		}
		if (ViewController.FORUM_PORTLET.equals(displayType)) {	
	    	Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);	
		
			String[] forumPrefIdList = PortletPreferencesUtil.getValues(prefs, WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
		
			//	Build the jsp bean (sorted by folder title)
			List folderIds = new ArrayList();
			for (int i = 0; i < forumPrefIdList.length; i++) {
				folderIds.add(Long.valueOf(forumPrefIdList[i]));
			}
			Collection folders = getBinderModule().getBinders(folderIds);
		
			model.put(WebKeys.FOLDER_LIST, folders);
			model.put(WebKeys.BINDER_ID_LIST, Arrays.asList(forumPrefIdList));
			return new ModelAndView(WebKeys.VIEW_FORUM_EDIT, model);
		} else if (ViewController.MOBILE_PORTLET.equals(displayType)) {	
	    	Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);	
		
	        User user = RequestContextHolder.getRequestContext().getUser();
			Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
			String mobileBinderIds = (String)userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS);
			Set<Long> binderIds = LongIdUtil.getIdsAsLongSet(mobileBinderIds);
			Collection binders = getBinderModule().getBinders(binderIds);
			model.put(WebKeys.FOLDER_LIST, binders);
			model.put(WebKeys.BINDER_ID_LIST, binderIds);
			return new ModelAndView(WebKeys.VIEW_MOBILE_EDIT, model);
		} else if (ViewController.RELEVANCE_DASHBOARD_PORTLET.equals(displayType)) {
			return setupRelevanceDashboardPortlet(request, prefs, model, "relevance_dashboard");
		} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_BLOG_EDIT, "blog");
		} else if (ViewController.GALLERY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_GALLERY_EDIT, "gallery");			
		} else if (ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_GUESTBOOK_EDIT, "guestbook");			
		} else if (ViewController.TASK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_TASK_EDIT, "task");			
		} else if (ViewController.WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_WIKI_EDIT, "wiki");
		} else if (ViewController.SEARCH_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_SEARCH_EDIT, "search");
		} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
			//This is the portlet view; get the configured list of principals to show
			Set<Long> userIds = new HashSet<Long>();
			Set<Long> groupIds = new HashSet<Long>();
			userIds.addAll(LongIdUtil.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
			groupIds.addAll(LongIdUtil.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));

			model.put(WebKeys.USERS, getProfileModule().getUsers(userIds));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(groupIds));			
			return new ModelAndView(WebKeys.VIEW_PRESENCE_EDIT, model);
		} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				
			Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new WorkspaceConfigHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
			
			String wsId = PortletPreferencesUtil.getValue(prefs, WebKeys.WORKSPACE_PREF_ID, null);
			try {
				Workspace ws;
				if (Validator.isNull(wsId)) ws = getWorkspaceModule().getTopWorkspace();	
				else ws = getWorkspaceModule().getWorkspace(Long.valueOf(wsId));				
				model.put(WebKeys.BINDER, ws);
			} catch (Exception ex) {};
			return new ModelAndView(WebKeys.VIEW_WORKSPACE_EDIT, model);
		}
		return null;
	}
	private ModelAndView setupRelevanceDashboardPortlet(RenderRequest request, PortletPreferences prefs, Map model, String componentName) {
		Map userProperties = (Map) getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		String id = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (id != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(id);
				DashboardHelper.getDashboardMap(d, userProperties, model, true);
			} catch (NoObjectByTheIdException no) {
				//setup dummy dashboard for config
				DashboardHelper.initDashboardComponent(userProperties, model, componentName);				
			}
		} else {
			//setup dummy dashboard for config
			DashboardHelper.initDashboardComponent(userProperties, model, componentName);
		}
		return new ModelAndView(WebKeys.VIEW_RELEVANCE_DASHBOARD_EDIT, model); 
		 
	}

	private ModelAndView setupSummaryPortlet(RenderRequest request, PortletPreferences prefs, Map model, String view, String componentName) {
		Map userProperties = (Map) getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		String id = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (id != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(id);
				DashboardHelper.getDashboardMap(d, userProperties, model, true);
			} catch (NoObjectByTheIdException no) {
				//setup dummy dashboard for config
				DashboardHelper.initDashboardComponent(userProperties, model, componentName);				
			}
		} else {
			//setup dummy dashboard for config
			DashboardHelper.initDashboardComponent(userProperties, model, componentName);
		}
		return new ModelAndView(view, model);
		
	}

}
