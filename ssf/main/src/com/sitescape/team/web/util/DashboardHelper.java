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

import java.awt.font.NumericShaper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;

import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.portlet.binder.AdvancedSearchController;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeHelper;
import com.sitescape.team.web.tree.FolderConfigHelper;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.tree.WorkspaceConfigHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;

public class DashboardHelper extends AbstractAllModulesInjected {
	private static DashboardHelper instance; // A singleton instance


	public final static String[] ComponentLists = {Dashboard.WIDE_TOP, Dashboard.NARROW_FIXED, Dashboard.NARROW_VARIABLE, Dashboard.WIDE_BOTTOM};
	 
	public final static String DisplayStyleDefault = "border";

	
	//Component data keys
	public final static String SearchFormSavedSearchQuery = "__savedSearchQuery";
	public final static String SearchFormSavedFolderIdList = "__savedFolderIdList";
	public final static String Users= "users";
	public final static String Groups= "groups";
	public final static String TeamOn= "teamOn";
    public static final String Workspace_topId="topId";
	//key in component data used to resolve binders by type
	public final static String ChooseType = "chooseViewType";
	public final static String AssignedTo = "assignedTo";
	public final static String AssignedToGroups = "assignedToGroups";
	public final static String AssignedToTeams = "assignedToTeams";	
	
	//Scopes
	public final static String Local = "local";
	public final static String Binder = "binder";
	public final static String Global = "global";
	public final static String Portlet = "portlet";
	
	//only has one
	public static final String PORTLET_COMPONENT_ID =Portlet+"_0";

	//Form keys
	public final static String ElementNamePrefix = "data_";
	
	public DashboardHelper() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionHelper.class);
		
		instance = this;
	}
    public static DashboardHelper getInstance() {
    	return instance;
    }
		
    protected static void getDashboardBeans(Binder binder, Map ssDashboard, Map model, boolean isConfig) {
		//Go through each list and build the needed beans
    	List componentList = new ArrayList();
    	for (int i = 0; i < ComponentLists.length; i++) {
			String scope = (String)ssDashboard.get(WebKeys.DASHBOARD_SCOPE);
			if (scope.equals(DashboardHelper.Local)) {
				componentList = (List) ssDashboard.get(ComponentLists[i]);
			} else if (scope.equals(DashboardHelper.Global)) {
				componentList = (List) ((Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP)).get(ComponentLists[i]);
			} else if (scope.equals(DashboardHelper.Binder)) {
				componentList = (List) ((Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP)).get(ComponentLists[i]);
			}
			for (int j = 0; j < componentList.size(); j++) {
				Map component = (Map) componentList.get(j);
				if ((Boolean)component.get(Dashboard.VISIBLE)) {
					//Set up the bean for this component
					getDashboardBean(binder, ssDashboard, model, (String)component.get(Dashboard.ID), isConfig);
				} else if (!isConfig) {
					//the workspace needs to setup the tree even when not visible inorder to load the javascript
					getInvisibleDashboardBean(binder, ssDashboard, model, (String)component.get(Dashboard.ID), isConfig);
				}
			}
		}
    }
    

    //used by Penlets
    protected static void getDashboardBean(Binder binder, Map ssDashboard, Map model, String id, boolean isConfig) {
		String componentScope = "";
		if (id.contains("_")) componentScope = id.split("_")[0];
		if (!componentScope.equals("")) {
			//Get the component from the appropriate scope
			Map dashboard = new HashMap();
			if (componentScope.equals(DashboardHelper.Local)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Global)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Binder)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
			}
			if (isConfig)
				doComponentConfigSetup(ssDashboard, dashboard, binder, model, id);
			else
				doComponentSetup(ssDashboard, dashboard, binder, model, id);
		}
    }
	private static void getInvisibleDashboardBean(Binder binder, Map ssDashboard, Map model, String id, boolean isConfig) {
		//workspace tree has setup even when invisible
		String componentScope = "";
		if (id.contains("_")) componentScope = id.split("_")[0];
		if (!componentScope.equals("")) {
			//Get the component from the appropriate scope
			Map dashboard = new HashMap();
			if (componentScope.equals(DashboardHelper.Local)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Global)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Binder)) {
				dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
			}
			if (dashboard.containsKey(Dashboard.COMPONENTS)) {
				Map components = (Map) dashboard.get(Dashboard.COMPONENTS);
				if (components.containsKey(id)) {
					Map component = (Map) components.get(id);
					String componentName = (String)component.get(Dashboard.NAME);
					 if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
							//Set up the workspace tree bean
								getInstance().getWorkspaceTreeBean(binder, 
									ssDashboard, model, id, component, false);
					 } else if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY)) {
						 getInstance().doCalendarSetup(ssDashboard, id, component);
					 }
				}
			}
		}
		
	}
	
	protected void doCalendarSetup(Map ssDashboard, String id, Map component) {
		Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
	 
 		Map searchSearchFormData = new HashMap();      	
 		idData.put(WebKeys.SEARCH_FORM_DATA,searchSearchFormData);
 		
    	Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	
		Set <Long> folderIds = new HashSet();
		if (data.get(SearchFormSavedFolderIdList) instanceof String) 
			folderIds = LongIdUtil.getIdsAsLongSet((String)data.get(SearchFormSavedFolderIdList));
		searchSearchFormData.put(WebKeys.BINDER_ID_LIST, folderIds);

		Collection folders=null;
		if (folderIds != null && !folderIds.isEmpty()) {
			folders = getBinderModule().getBinders(folderIds);		//may include have templates		
			idData.put(WebKeys.FOLDER_LIST, folders);
		}
		
	}
	
	
	
    private static void doComponentSetup(Map ssDashboard, Map dashboard, Binder binder, Map model, String id) {
		if (dashboard.containsKey(Dashboard.COMPONENTS)) {
			Map components = (Map) dashboard.get(Dashboard.COMPONENTS);
			if (components.containsKey(id)) {
				Map component = (Map) components.get(id);
				String componentName = (String)component.get(Dashboard.NAME);
				//See if this component needs a bean
				if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
					//Set up the buddy list bean
					getInstance().getBuddyListBean(binder, ssDashboard, 
							id, component, false);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST)) {
					//Set up the team members bean
					getInstance().getTeamMembersBean(binder, 
							ssDashboard, model, id, component, false);					
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
					//Set up the workspace tree bean
						getInstance().getWorkspaceTreeBean(binder, 
							ssDashboard, model, id, component, false);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY)) {
					getInstance().getWikiHomepageEntryBean(null, ssDashboard, model, id, component, false);
				} else if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY)){
					getInstance().getTasksBean(binder, ssDashboard, model, id, component, false);
				} else if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_REMOTE_APPLICATION)){
					getInstance().getRemoteApplicationBean(ssDashboard, id, component);
				} else if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_SEARCH) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_GALLERY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
					//Set up the search results bean
					getInstance().getSearchResultsBean(binder, ssDashboard, 
							model, id, component, false);
				} 
			}
		}
    }
    private void getTasksBean(Binder binder, Map ssDashboard, Map model, String id, Map component, boolean b) {
    	getInstance().getSearchResultsBean(binder, ssDashboard, model, id, component, b);
    	
    	Map beanMap = (Map)ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beanMap == null || beanMap.get(id) == null) return;
    	Map searchFormData = (Map) ((Map) beanMap.get(id)).get(WebKeys.SEARCH_FORM_DATA);
    	if (searchFormData == null) return;
    	List items = (List) searchFormData.get(WebKeys.SEARCH_FORM_RESULTS);
    	if (items == null) return;
    	Iterator it = items.iterator();
    	while (it.hasNext()) {
    		Map entry = (Map)it.next();
    		String entryDefId = (String)entry.get(Constants.COMMAND_DEFINITION_FIELD);
    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, getDefinitionModule().getEntryDefinitionElements(entryDefId));
    	}
    	
	}
    
	private static void doComponentConfigSetup(Map ssDashboard, Map dashboard, Binder binder, Map model, String id) {
		if (dashboard.containsKey(Dashboard.COMPONENTS)) {
			Map components = (Map) dashboard.get(Dashboard.COMPONENTS);
			if (components.containsKey(id)) {
				Map component = (Map) components.get(id);
				String componentName = (String)component.get(Dashboard.NAME);
				//See if this component needs a bean
				if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
					//Set up the buddy list bean
					getInstance().getBuddyListBean(binder, ssDashboard, 
							id, component, true);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST)) {
					//Set up the team members list bean,
					getInstance().getTeamMembersBean(binder, 
							ssDashboard, model, id, component, true);					
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
					//Set up the workspace tree bean,
					getInstance().getWorkspaceTreeBean(binder, 
							ssDashboard, model, id, component, true);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
					//Set up the search results bean
					getInstance().getSearchResultsBean(binder, ssDashboard, 
							model, id, component, true);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY)) {
					getInstance().getWikiHomepageEntryBean(null, ssDashboard, model, id, component, true);
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_REMOTE_APPLICATION)) {
					getInstance().getRemoteApplicationBean(ssDashboard, id, component);
				} else if (componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_GALLERY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY) ||
						componentName.equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
					//Set up the search results bean
					getInstance().getSummaryConfigBean(binder, ssDashboard, 
							model, id, component);
				}
			}
		}
   	
    }


    public static Map getNewDashboardMap() {
    	Map dashboard =  new HashMap();
		dashboard.put(Dashboard.TITLE, "");
		dashboard.put(Dashboard.INCLUDEBINDERTITLE, new Boolean(false));
		dashboard.put(Dashboard.COMPONENTS, new HashMap());
		dashboard.put(Dashboard.WIDE_TOP, new ArrayList());
		dashboard.put(Dashboard.NARROW_FIXED, new ArrayList());
		dashboard.put(Dashboard.NARROW_VARIABLE, new ArrayList());
		dashboard.put(Dashboard.WIDE_BOTTOM, new ArrayList());
		
		return dashboard;
	}
	//penlets
	static public Map getDashboardMap(Binder binder, Map userProperties, Map model) {
		return getDashboardMap(binder, userProperties, model, DashboardHelper.Local, "", false);
	}
	//penlets
	static public Map getDashboardMap(Binder binder, Map userProperties, Map model, String scope, String componentId, boolean isConfig) {
		//Users dashboard settings for this binder		
		Map dashboard = getInstance().getDashboard(binder, DashboardHelper.Local);
		//Users global dashboard settings
		Map dashboard_g = getInstance().getDashboard(binder, DashboardHelper.Global);
		//Binder global dashboard settings
		Map dashboard_b = getInstance().getDashboard(binder, DashboardHelper.Binder);

		Map ssDashboard = new HashMap();
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		
		if (scope.equals(DashboardHelper.Local)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, new HashMap(dashboard));
		} else if (scope.equals(DashboardHelper.Global)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, new HashMap(dashboard_g));
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, new HashMap(dashboard_b));
		}
		ssDashboard.put(WebKeys.DASHBOARD_LOCAL_MAP, dashboard);
		ssDashboard.put(WebKeys.DASHBOARD_GLOBAL_MAP, dashboard_g);
		ssDashboard.put(WebKeys.DASHBOARD_BINDER_MAP, dashboard_b);
		int narrowFixedWidth = new Integer(SPropsUtil.getString("dashboard.size.narrowFixedWidth"));
		ssDashboard.put(WebKeys.DASHBOARD_NARROW_FIXED_WIDTH, 
				String.valueOf(narrowFixedWidth));
		ssDashboard.put(WebKeys.DASHBOARD_NARROW_FIXED_WIDTH2, 
				String.valueOf(narrowFixedWidth / 2));

		//Get the title for this page
		String title = (String) dashboard_b.get(Dashboard.TITLE);
		Boolean includeBinderTitle = (Boolean) dashboard_b.get(Dashboard.INCLUDEBINDERTITLE);
		if (includeBinderTitle==null) includeBinderTitle=Boolean.FALSE;
		Boolean includeBinderTitle_l = (Boolean) dashboard.get(Dashboard.INCLUDEBINDERTITLE);
		if (includeBinderTitle_l==null) includeBinderTitle_l=Boolean.FALSE;
		if (includeBinderTitle_l || !dashboard.get(Dashboard.TITLE).equals("")) {
			title = (String) dashboard.get(Dashboard.TITLE);
			includeBinderTitle = includeBinderTitle_l;
		}
		if (Validator.isNull(title) && !includeBinderTitle) {
			title = (String) dashboard_g.get(Dashboard.TITLE);
			includeBinderTitle = (Boolean) dashboard_g.get(Dashboard.INCLUDEBINDERTITLE);
		}
		ssDashboard.put(WebKeys.DASHBOARD_TITLE, title);
		ssDashboard.put(WebKeys.DASHBOARD_INCLUDE_BINDER_TITLE, includeBinderTitle);

		//Build the lists of components
		if (scope.equals(DashboardHelper.Local)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, getInstance().buildLocalDashboardList(Dashboard.WIDE_TOP, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, getInstance().buildLocalDashboardList(Dashboard.NARROW_FIXED, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, getInstance().buildLocalDashboardList(Dashboard.NARROW_VARIABLE, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, getInstance().buildLocalDashboardList(Dashboard.WIDE_BOTTOM, ssDashboard));
		} else if (scope.equals(DashboardHelper.Global)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_g.get(Dashboard.WIDE_TOP)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_g.get(Dashboard.NARROW_FIXED)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_g.get(Dashboard.NARROW_VARIABLE)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_g.get(Dashboard.WIDE_BOTTOM)));
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_b.get(Dashboard.WIDE_TOP)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_b.get(Dashboard.NARROW_FIXED)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_b.get(Dashboard.NARROW_VARIABLE)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_b.get(Dashboard.WIDE_BOTTOM)));
		}
		
		//Get the lists of dashboard components that are supported
		String[] components_list = SPropsUtil.getCombinedPropertyList(
				"dashboard.components.list", ObjectKeys.CUSTOM_PROPERTY_PREFIX);
		
		List cw = new ArrayList();
		Map componentTitles = new HashMap();
		for (int i = 0; i < components_list.length; i++) {
			if (!components_list[i].trim().equals("")) {
				String component = components_list[i].trim();
				cw.add(component);
				String componentTitle = SPropsUtil.getString("dashboard.title." + component, component);
				componentTitles.put(component, componentTitle);
			}
		}
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_LIST, cw);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_TITLES, componentTitles);

		//Set up the beans
		if (componentId.equals("")) {
			getDashboardBeans(binder, ssDashboard, model, isConfig);
		} else {
			getDashboardBean(binder, ssDashboard, model, componentId, isConfig);
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		}
		
		//Check the access rights of the user
		if (getInstance().getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
			ssDashboard.put(WebKeys.DASHBOARD_SHARED_MODIFICATION_ALLOWED, new Boolean(true));
		} else {
			ssDashboard.put(WebKeys.DASHBOARD_SHARED_MODIFICATION_ALLOWED, new Boolean(false));			
		};
		
		model.put(WebKeys.DASHBOARD, ssDashboard);

		//See if the components are shown or hidden
		Boolean showAllComponents = Boolean.FALSE;
		if (checkIfShowingAllComponents(binder)) showAllComponents = Boolean.TRUE;
		model.put(WebKeys.DASHBOARD_SHOW_ALL, showAllComponents);

		return ssDashboard;
	}
	//setup for portlet where only 1 element is allowed.  Don't need all the other stuff
	static public Map getDashboardMap(DashboardPortlet dashboard, Map userProperties, Map model, boolean isConfig) {
		if (dashboard.getVersion() == null) getInstance().fixupDashboard(dashboard);
		Map ssDashboard = new HashMap();
		model.put(WebKeys.DASHBOARD, ssDashboard);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, DashboardHelper.Portlet);
		Map dashboardProps = new HashMap(dashboard.getProperties());
		ssDashboard.put(WebKeys.DASHBOARD_MAP, dashboardProps);	
		
		Map<String, Map> components = (Map) dashboardProps.get(Dashboard.COMPONENTS);
		//should only be one
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();			
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, me.getKey());
			model.put(WebKeys.DASHBOARD_COMPONENT_ID, me.getKey());
			if (isConfig)
				doComponentConfigSetup(ssDashboard, dashboardProps, null, model, (String)me.getKey());
			else	
				doComponentSetup(ssDashboard, dashboardProps, null, model, (String)me.getKey());
		}
		return ssDashboard;
	}
	private List buildLocalDashboardList(String listName, Map ssDashboard) {
		Map localDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP);
		if (localDashboard != null) localDashboard = new HashMap(localDashboard);
		Map globalDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
		if (globalDashboard != null) globalDashboard = new HashMap(globalDashboard);
		Map binderDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
		if (binderDashboard != null) binderDashboard = new HashMap(binderDashboard);
		
		//Start with a copy of the local list
		List components = new ArrayList((List)localDashboard.get(listName));
		
		List seenList = new ArrayList();
		for (int i = 0; i < components.size(); i++) {
			String id = (String) ((Map)components.get(i)).get(Dashboard.ID);
			if (seenList.contains(id) || !checkIfComponentExists(id, ssDashboard)) {
				//Remove duplicates
				components.remove(i);
				i--;
			} else {
				seenList.add(id);
			}
		}
		
		//Then merge in the global and binder lists
		List globalAndBinderComponents = new ArrayList((List)globalDashboard.get(listName));
		globalAndBinderComponents.addAll((List)binderDashboard.get(listName));
		for (int i = 0; i < globalAndBinderComponents.size(); i++) {
			String id = (String) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.ID);
			String scope = (String) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.SCOPE);
			Boolean visible = (Boolean) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.VISIBLE);
			if (!seenList.contains(id) && checkIfComponentExists(id, ssDashboard) && 
					!checkIfComponentOnLocalList(id, localDashboard)) {
				Map newComponent = new HashMap();
				newComponent.put(Dashboard.ID, id);
				newComponent.put(Dashboard.SCOPE, scope);
				newComponent.put(Dashboard.VISIBLE, visible);
				components.add(newComponent);
			}
			seenList.add(id);
		}
		
		return components;
	}
	
	public static void checkDashboardLists(Binder binder, Map ssDashboard) {
		//DASHBOARD_BINDER_MAP
		Dashboard d;
		d = getInstance().getDashboardModule().getEntityDashboard(binder.getEntityIdentifier());
		if (d != null) {
			for (int i = 0; i < ComponentLists.length; i++) {
			if (getInstance().checkDashboardList(ssDashboard, d.getProperties(), ComponentLists[i])) 
				getInstance().getDashboardModule().setProperty(d.getId(), ComponentLists[i], d.getProperty(ComponentLists[i]));
			}
		}

		//DASHBOARD_LOCAL_MAP
		User user = RequestContextHolder.getRequestContext().getUser();
		d = getInstance().getDashboardModule().getUserDashboard(user.getEntityIdentifier(), binder.getId());
		if (d != null) {
			for (int i = 0; i < ComponentLists.length; i++) {
				if (getInstance().checkDashboardList(ssDashboard, d.getProperties(), ComponentLists[i]))
					getInstance().getDashboardModule().setProperty(d.getId(), ComponentLists[i], d.getProperty(ComponentLists[i]));
			}
		}
		//DASHBOARD_GLOBAL_MAP
		d = getInstance().getDashboardModule().getEntityDashboard(user.getEntityIdentifier());
		if (d != null) {
			for (int i = 0; i < ComponentLists.length; i++) {
				if (getInstance().checkDashboardList(ssDashboard, d.getProperties(), ComponentLists[i]))
					getInstance().getDashboardModule().setProperty(d.getId(), ComponentLists[i], d.getProperty(ComponentLists[i]));
			}
		}
	}
	

	protected void getBuddyListBean(Binder binder, Map ssDashboard, String id, Map component, boolean isConfig) {
		//make sure data is reflected in toXml
		Map data = (Map)component.get(Dashboard.DATA);
	   	if (data != null) {
	    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
	    	if (beans == null) {
	    		beans = new HashMap();
	    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
	    	}
	    	Map idData = new HashMap();
	    	beans.put(id, idData);
	    	Set ids = getIds(data.get(Users));

	    	if (data.containsKey(Groups)) {
	    		if (isConfig) {
	    			//keep separate for config
		    		Set gIds = getIds(data.get(Groups));
		    		idData.put(WebKeys.GROUPS, getProfileModule().getGroups(gIds));
	    		} else {
	    			//merge into user list if not config
	    			ids.addAll(getIds(data.get(Groups)));
	    		}
	    	}
	    	if (data.containsKey(TeamOn)) {
	    		if (!isConfig) {
	    			//merge into user list if not config
	    			try {
	    				ids.addAll(getBinderModule().getTeamMemberIds(binder.getId(), false));
	    			} catch (Exception ex) {};  //skip if don't have access
	    		}
	    	}
			idData.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
 	   	}
	}
	
	private Set getIds(Object ids) {
		//handle bad data
		if (ids instanceof String) {
			return LongIdUtil.getIdsAsLongSet((String)ids);
		} else if (Collection.class.isAssignableFrom(ids.getClass())) {
			Set result = new HashSet<Long>();
			Iterator it = ((Collection)ids).iterator();
			while (it.hasNext()) {
				result.addAll(getIds(it.next()));
			}
			return result;
		} else {
			return new HashSet<Long>();
		}
	}
	protected void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
	    		String id, Map component, boolean isConfig) {
		//make sure data is reflected in toXml
		Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
    	Document tree = null;
    	String idString = "";
    	if (data.containsKey(Workspace_topId) && data.get(Workspace_topId) instanceof String) {
    		idString = (String)data.get(Workspace_topId);
    	} else if (data.containsKey(Workspace_topId) && data.get(Workspace_topId) instanceof List) {
    		idString = (String)((List)data.get(Workspace_topId)).get(0);
    	}
		Long topId = null;
		try {
			if (!idString.equals("")) topId = Long.valueOf(idString);
		} catch (Exception ex) {};
       	String startPoint = "";
       	if (data.containsKey("start") && data.get("start") instanceof String) {
       		startPoint = (String)data.get("start");
       	} else if (data.containsKey("start") && data.get("start") instanceof List) {
       		startPoint = (String)((List)data.get("start")).get(0);
       	}
   		if (!(binder instanceof TemplateBinder)) {
   	       	Workspace topWs = null;
   			if ("this".equals(startPoint)) {
   	  			if (binder instanceof Workspace) {
   	  				topWs = (Workspace)binder;   				
   	  			} else  {
   	  				Folder topFolder = ((Folder)binder).getTopFolder();
   	  				if (topFolder == null) topFolder = (Folder)binder;
   	  				topWs = (Workspace)topFolder.getParentBinder();
    			} 				
   			} else if (topId != null) {
   				try {
   					topWs = getWorkspaceModule().getWorkspace(topId);
   	   				idData.put(WebKeys.BINDER, topWs);
   				} catch (Exception ex) {}; //ignore error and continue
   			}
   			if (isConfig) {
   				if (topWs != null && !topWs.isZone()) topWs = null;
   			}
   			if (topWs == null) topWs = getWorkspaceModule().getTopWorkspace();
   			idData.put(Workspace_topId, topWs.getId());
   			if (isConfig)
   				tree = getBinderModule().getDomBinderTree(topWs.getId(), new WsDomTreeBuilder(topWs, true, this, new WorkspaceConfigHelper()),1);
   			else
  				tree = getBinderModule().getDomBinderTree(topWs.getId(), new WsDomTreeBuilder(topWs, true, this),1);
   		   				
   		} else {
   			Binder topWs = null;
  			if ("this".equals(startPoint)) {
				topWs = (TemplateBinder)binder;
				if (!binder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
					topWs = (TemplateBinder)binder;
					while (!topWs.isRoot()) {
						topWs = (TemplateBinder)topWs.getParentBinder();
					}
					if (!topWs.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
						topWs = null;
					}
				}
			} else if (topId != null) {
   				try {
   					topWs = getWorkspaceModule().getWorkspace(topId);
   	   				idData.put(WebKeys.BINDER, topWs);
   				} catch (Exception ex) {}; //ignore error and continue
   			}
  			if (isConfig) {
   				if (topWs != null && !topWs.isZone()) topWs = null;
   			}
   			if (topWs == null) topWs = getWorkspaceModule().getTopWorkspace();
  			idData.put(Workspace_topId, topWs.getId());
   			if (topWs instanceof TemplateBinder) {
   				tree = BinderHelper.buildTemplateTreeRoot(this, (TemplateBinder)topWs, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION));
   			} else {
   				if (isConfig)
   					tree = getBinderModule().getDomBinderTree(topWs.getId(), new WsDomTreeBuilder(topWs, true, this, new WorkspaceConfigHelper()),1);
   				else
  					tree = getBinderModule().getDomBinderTree(topWs.getId(), new WsDomTreeBuilder(topWs, true, this),1);
   			}
   		}
		idData.put(WebKeys.DASHBOARD_WORKSPACE_TREE, tree);
    }

	
	protected void getTeamMembersBean(Binder binder, Map ssDashboard, Map model, String id, Map component, boolean isConfig) {
    	Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
    	if (!isConfig) {
    		int pageSize = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
    		if (data.containsKey(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT)) {
    			try {
    				String resultsCount = (String)data.get(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT);
    				pageSize = Integer.valueOf(resultsCount);
    				model.put(WebKeys.PAGE_SIZE, resultsCount);
    			} catch (Exception e) {}
    		}
    		int pageNumber = 0;
    		if (model.containsKey(WebKeys.PAGE_SIZE)) {
    			pageSize = Integer.valueOf((String)model.get(WebKeys.PAGE_SIZE));
    		}
    		if (model.containsKey(WebKeys.PAGE_NUMBER)) {
    			pageNumber = Integer.valueOf((String)model.get(WebKeys.PAGE_NUMBER));
    		}
    		idData.put(WebKeys.PAGE_SIZE, String.valueOf(pageSize));
    		idData.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
   			SortedSet<Principal> users = getBinderModule().getTeamMembers(binder, true);
   			Object[] usersSet = users.toArray();
   			List usersPage = new ArrayList(); //use a list so results remain ordered
   			int iEnd = pageSize*pageNumber + pageSize;
   			if (iEnd > usersSet.length) iEnd = usersSet.length;
   			for (int i = pageSize*pageNumber; i < iEnd; i++) {
   				usersPage.add(usersSet[i]);
   			}
   			idData.put(WebKeys.TEAM_MEMBERS, usersPage);
   			idData.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
    	}
	}
	
    protected void getWikiHomepageEntryBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component, boolean isConfig) {
    	Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
    	if (isConfig) {
    		Workspace ws = getWorkspaceModule().getTopWorkspace();
    		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()), 1);
    		idData.put(WebKeys.DASHBOARD_WORKSPACE_TREE, tree);
    	}

    	try {
    		String binderId = null;
   			Set <String> savedFolderIds = LongIdUtil.getIdsAsStringSet((String)data.get(SearchFormSavedFolderIdList));
   			if (!savedFolderIds.isEmpty()) binderId = savedFolderIds.iterator().next();
       		if (Validator.isNotNull(binderId)) {
       			Binder fBinder = getBinderModule().getBinder(Long.valueOf(binderId));				
    			idData.put(WebKeys.BINDER, fBinder);
    			if (!isConfig) {
    				String entryId = (String) fBinder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
    				if (entryId != null) {
    					Entry entry = getFolderModule().getEntry(fBinder.getId(), Long.valueOf(entryId));
    					idData.put(WebKeys.DASHBOARD_WIKI_HOMEPAGE_ENTRY, entry);
    				}
				}
			}
    	
		} catch (Exception ex) {
			//just skip = assume binder or entry doesn't exist
		}
		
    }
    //reduce work
    protected void getSummaryConfigBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component) {
    	Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
		
		Collection folders=null;
		Set <Long> folderIds = null;
		try {
			folderIds = LongIdUtil.getIdsAsLongSet((String)data.get(SearchFormSavedFolderIdList));
		} catch(Exception e) {}
		if (!folderIds.isEmpty()) {
			folders = getBinderModule().getBinders(folderIds);		//may have templates		
			idData.put(WebKeys.FOLDER_LIST, folders);
		}
		idData.put(WebKeys.BINDER_ID_LIST, folderIds); //longs

		if (component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
			if ((folders != null) && !folders.isEmpty()) {
				idData.put(WebKeys.BINDER, folders.iterator().next());					
			}
		}

    	if (data.get(AssignedTo) != null && data.get(AssignedTo) instanceof String) {
    		// convert from v1.0
			Set userIds = new HashSet();
			if (data.get(AssignedTo) instanceof String) {
				try {
					userIds.add(Long.parseLong((String)data.get(AssignedTo)));
				} catch (NumberFormatException e) {
					// ignore
				}
			}
			data.put(AssignedTo, userIds);
	    }
    	
//    	if (data.get(AssignedTo) != null) {
//    		Set userIds = null;
//    		if (data.get(AssignedTo) instanceof String) {
//    			userIds = Collections.singleton((String)data.get(AssignedTo));
//    		} else {
//    			userIds = (Set)data.get(AssignedTo);
//    		}
//    		String userTitle = null;
//    		if (SearchFilterKeys.CurrentUserId.equals(userId)) {
//    			userTitle = NLT.get("searchForm.currentUserTitle");
//    		} else {
//    			Iterator users = getProfileModule().getUsers(Collections.singleton(Long.parseLong(userId))).iterator();
//    			if (users.hasNext()) {
//    				userTitle = ((Principal)users.next()).getTitle();
//    			}
//    		}
//    		data.put(AssignedToName, userTitle);
//    	}
		
		
    	Workspace ws = getWorkspaceModule().getTopWorkspace();
    	Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()), 1);
    	idData.put(WebKeys.DASHBOARD_WORKSPACE_TREE, tree);

    }
    protected void getSearchResultsBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component, boolean isConfig) {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(user.getId()));

		Map data = (Map)component.get(Dashboard.DATA);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData;
    	if (beans.containsKey(id)) {
    		idData = (Map)beans.get(id);
    	} else {
    		idData = new HashMap();
        	beans.put(id, idData);
    	}
 		Map searchSearchFormData = new HashMap();      	
 		idData.put(WebKeys.SEARCH_FORM_DATA,searchSearchFormData);

		searchSearchFormData.put("searchFormTermCount", new Integer(0));
		Document searchQuery = null;
		if (data.containsKey(DashboardHelper.SearchFormSavedSearchQuery)) {
			// Retrieve and parse the saved search query.  If it fails for some
			// reason failover with a empty search query.
			try {
				searchQuery = DocumentHelper.parseText((String)data.get(DashboardHelper.SearchFormSavedSearchQuery));
			} catch (Exception e) {
				searchQuery = DocumentHelper.createDocument();
				searchQuery.addElement(SearchFilterKeys.FilterRootName);		
			}
		}
		
		SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(this, searchQuery);
		searchSearchFormData.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		Workspace ws = getWorkspaceModule().getTopWorkspace();
		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
		
		//Do the search and store the search results in the bean
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.MODIFICATION_DATE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
		if (data.containsKey(WebKeys.SEARCH_FORM_MAX_HITS)) {
			try {
				String maxHitsStr = (String)data.get(WebKeys.SEARCH_FORM_MAX_HITS);
				options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxHitsStr));
			} catch (Exception e) {}
		}
		int pageSize = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (data.containsKey(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT)) {
			try {
				String resultsCount = (String)data.get(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT);
				pageSize = Integer.valueOf(resultsCount);
				model.put(WebKeys.PAGE_SIZE, resultsCount);
			} catch (Exception e) {}
		}
		int pageNumber = 0;
		if (model.containsKey(WebKeys.PAGE_SIZE)) {
			pageSize = Integer.valueOf((String)model.get(WebKeys.PAGE_SIZE));
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(pageSize));
		}
		if (model.containsKey(WebKeys.PAGE_NUMBER)) {
			pageNumber = Integer.valueOf((String)model.get(WebKeys.PAGE_NUMBER));
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageNumber * pageSize));
		}
		searchSearchFormData.put(WebKeys.PAGE_SIZE, String.valueOf(pageSize));
		searchSearchFormData.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
		Set <Long> folderIds = new HashSet();
		if (data.get(SearchFormSavedFolderIdList) instanceof String) 
			folderIds = LongIdUtil.getIdsAsLongSet((String)data.get(SearchFormSavedFolderIdList));
		searchSearchFormData.put(WebKeys.BINDER_ID_LIST, folderIds);

		boolean doSearch = true;		
		Collection folders=null;
		if (folderIds != null && !folderIds.isEmpty()) {
			folders = getBinderModule().getBinders(folderIds);		//may include have templates		
			idData.put(WebKeys.FOLDER_LIST, folders);
		}
		if (binder instanceof TemplateBinder) {
			//don't do the search - cannot support links between portlets
			doSearch = false;
			searchSearchFormData.put(WebKeys.SEARCH_FORM_RESULTS, new ArrayList());
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_COUNT, Integer.valueOf(0));
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED, Integer.valueOf(0));
		}  else {
			if (component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY) || 
				component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY) ||
				component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY) ||
				component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY) ||
				component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_GALLERY)) {
				
				idData.put(WebKeys.BINDER_ID_LIST, folderIds);  //longs

				if (component.get(Dashboard.NAME).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
					if ((folders != null) && !folders.isEmpty()) {
						idData.put(WebKeys.BINDER, folders.iterator().next());					
					}
				}

				if (folderIds.isEmpty()) doSearch = false; //don't search everything
				else {
					//	Limit the search to entries only
					Document searchFilter2 = DocumentHelper.createDocument();
					Element rootElement = searchFilter2.addElement(Constants.AND_ELEMENT);
					Element field = rootElement.addElement(Constants.FIELD_ELEMENT);
					field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD);
					Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
					child.setText(Constants.DOC_TYPE_ENTRY);
					field = rootElement.addElement(Constants.FIELD_ELEMENT);
					field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
					child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
					child.setText(Constants.ENTRY_TYPE_ENTRY);
					options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter2);
				}
			}
		}	

		if (doSearch) {
			if (binder != null) options.put(ObjectKeys.SEARCH_DASHBOARD_CURRENT_BINDER_ID, binder.getId().toString());
			Map retMap = getInstance().getBinderModule().executeSearchQuery(searchQuery, options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			searchSearchFormData.put(WebKeys.SEARCH_FORM_RESULTS, entries);
			Integer searchCount = (Integer)retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL);
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_COUNT, searchCount);
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED, (Integer)retMap.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED));
			//Also get the folder titles
			Set ids = new HashSet();
			Iterator itEntries = entries.iterator();
			while (itEntries.hasNext()) {
				Map r = (Map) itEntries.next();
				String entityType = (String) r.get("_entityType");
				if (entityType != null && r.containsKey("_docId") && 
					(entityType.equals(EntityType.folder.toString()) || entityType.equals(EntityType.workspace.toString()))) {
					ids.add(Long.valueOf((String)r.get("_docId")));
				} else if (r.containsKey("_binderId")) {
				ids.add(Long.valueOf((String)r.get("_binderId")));				
				}
			}
			searchSearchFormData.put(WebKeys.BINDER_DATA, ResolveIds.getBinderTitlesAndIcons(ids));
		}
	
			
	
    }
    
    public static void setTitle(ActionRequest request, Binder binder, String scope) {
		Dashboard dashboard = getInstance().getDashboardObj(binder, scope);
		Map updates = new HashMap();
		updates.put(Dashboard.TITLE, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		updates.put(Dashboard.INCLUDEBINDERTITLE, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		getInstance().getDashboardModule().modifyDashboard(dashboard.getId(), updates);
	}
	
	public static String addComponent(ActionRequest request, Binder binder, 
			String listName, String scope) {
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
			Dashboard dashboard = getInstance().getDashboardObj(binder, scope);
			return DashboardHelper.addComponent(request, dashboard, listName, scope);
		}
		//User isn't privileged to do this
		return "";
	}
	
	public static String addComponent(ActionRequest request, Dashboard dashboard, 
			String listName, String scope) {
		String id = "";
		//Get the name of the component to be added
		//cannot use "name" as the name of a form element,  IE gets confused if you later want the form.name attribute
		String componentName = PortletRequestUtils.getStringParameter(request, "componentName", "");
		if (Validator.isNotNull(componentName)) {
			Map component = new HashMap();
			component.put(Dashboard.NAME, componentName);
			component.put(Dashboard.ROLES, 
					PortletRequestUtils.getStringParameter(request, "roles", ""));
						
			id = getInstance().getDashboardModule().addComponent(dashboard.getId(), scope, listName, component);
		}
		return id;
	}
	//add empty component for initial portlet configuration
	public static Map initDashboardComponent(Map userProperties, Map model, String name) {
		Map ssDashboard = new HashMap();
		model.put(WebKeys.DASHBOARD, ssDashboard);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, DashboardHelper.Portlet);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, PORTLET_COMPONENT_ID);
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, PORTLET_COMPONENT_ID);
		Map dashboard = getNewDashboardMap();
		ssDashboard.put(WebKeys.DASHBOARD_MAP, getNewDashboardMap());
		Map components = (Map)dashboard.get(Dashboard.COMPONENTS);
		Map component = new HashMap();
		component.put(Dashboard.NAME, name);
		component.put(Dashboard.ROLES, "");
		components.put(PORTLET_COMPONENT_ID, component);
		doComponentConfigSetup(ssDashboard, dashboard, null, model, PORTLET_COMPONENT_ID);
		return ssDashboard;
	}
	public static void saveComponentData(ActionRequest request, Binder binder, String scope) {
		//Get the dashboard component
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
			String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
			String componentScope = "";
			if (componentId.contains("_")) componentScope = componentId.split("_")[0];
			if (!componentScope.equals("")) {
				Dashboard d = getInstance().getDashboardObj(binder, componentScope);
				getInstance().internSaveComponentData(request, binder, d, componentId, scope);
			}
		}
	}

	public static void saveComponentData(ActionRequest request, Dashboard d) {
		//Get the dashboard component
		getInstance().internSaveComponentData(request, null, d, PORTLET_COMPONENT_ID, DashboardHelper.Portlet);
	}

	/*
	 * No static version because needs a DefinitionModule.
	 */
	private void internSaveComponentData(ActionRequest request, Binder binder, Dashboard d, String componentId, String scope) {

		//Get the generic data elements that start with the ElementNamePrefix
		Map formData = request.getParameterMap();
		Map componentData = new HashMap();
		Iterator itKeys = formData.keySet().iterator();
		while (itKeys.hasNext()) {
			String key = (String) itKeys.next();
			if (key.startsWith(DashboardHelper.ElementNamePrefix)) {
				String elementName = key.substring(DashboardHelper.ElementNamePrefix.length());
				//Save this value as a string for use when displaying the component
				List stringParams = Arrays.asList(PortletRequestUtils.getStringParameters(request, key));
				if (stringParams != null && stringParams.size() == 1) {
					componentData.put(elementName, stringParams.iterator().next());
				} else {
					componentData.put(elementName, stringParams);
				}
			}
		}
			
		//Get the component title
		String componentTitle = PortletRequestUtils.getStringParameter(request, 
				Dashboard.COMPONENT_TITLE, "");
		String displayStyle = PortletRequestUtils.getStringParameter(request, 
				Dashboard.DISPLAYSTYLE, DisplayStyleDefault);
		
		//Get the component config data map
		Map components = (Map)d.getProperty(Dashboard.COMPONENTS);
		if (components != null) {
			Map componentMap = (Map) components.get(componentId);
			if (componentMap != null) {
				Map originalComponentData = new HashMap();
				if (componentMap.containsKey(Dashboard.DATA)) originalComponentData = (Map) componentMap.get(Dashboard.DATA);
				String cName = (String)componentMap.get(Dashboard.NAME);
				//Get any component specific data
				if (ObjectKeys.DASHBOARD_COMPONENT_SEARCH.equals(cName)) {
					//Get the search query
					SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
					Document query = requestParser.getSearchQuery();
					componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query.asXML());
				} else if (ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_GALLERY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY.equals(cName)) {
					
					//multi-select	
					Set <String> folderIds = new HashSet();
					folderIds.addAll(TreeHelper.getSelectedStringIds(formData, "ss_folder_id"));

					//	Get the forums to be deleted
					Iterator itFormData = formData.entrySet().iterator();
					while (itFormData.hasNext()) {
						Map.Entry me = (Map.Entry) itFormData.next();
						if (((String)me.getKey()).startsWith("del_")) {
							String forumId = ((String)me.getKey()).substring(4);
							folderIds.remove(forumId);
						}
					}
					boolean chooseFolder = GetterUtil.getBoolean(PortletRequestUtils.getStringParameter(request, "chooseFolder", "false"), false);
					if (chooseFolder && binder != null) {
						String type = resolveFolderType(cName);
						if (binder instanceof TemplateBinder) {
							//save - resolve later
							componentData.put(ChooseType, type);
						} else {
							//resolve binder now
							if (DashboardHelper.Binder.equals(scope) || DashboardHelper.Local.equals(scope)) {
								String id = resolveBinder(binder, type);
								if (Validator.isNotNull(id)) folderIds.add(id);
							}
						}
					}
					SearchFilter searchFilter = new SearchFilter(true);
					String filterName = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterNameField, "");
					searchFilter.addFilterName(filterName);

					if (!folderIds.isEmpty()) {
						searchFilter.addFolderIds(folderIds);
						componentData.put(SearchFormSavedFolderIdList, LongIdUtil.getIdsAsString(folderIds));
					}
					
					
					if (ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY.equals(cName)) {
						List<SearchFilter.Entry> entries = new ArrayList(); 
						String assignedTo = PortletRequestUtils.getStringParameter(request, "assignedTo", "");
						String[] assignedToSplited = new String[0];
						if (!"".equals(assignedTo)) {
							assignedToSplited = assignedTo.trim().split("\\s");
							Set ids = new HashSet();
							for (int i = 0; i < assignedToSplited.length; i++) {
								try {
									if (SearchFilterKeys.CurrentUserId.equals(assignedToSplited[i])) {
										ids.add(SearchFilterKeys.CurrentUserId);
									} else {
										ids.add(Long.parseLong(assignedToSplited[i]));
									}
								} catch (NumberFormatException e) {
									// ignore
								}
							}
							componentData.put(AssignedTo, ids);
						}
						entries.add(new SearchFilter.Entry(null, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME, assignedToSplited, "user_list"));
						
						String assignedToGroup = PortletRequestUtils.getStringParameter(request, "assignedToGroup", "");
						String[] assignedToGroupSplited = new String[0];
						if (!"".equals(assignedToGroup)) {
							assignedToGroupSplited = assignedToGroup.trim().split("\\s");
							Set ids = new HashSet();
							for (int i = 0; i < assignedToGroupSplited.length; i++) {
								try {
									ids.add(Long.parseLong(assignedToGroupSplited[i]));
								} catch (NumberFormatException e) {
									// ignore
								}
							}
							componentData.put(AssignedToGroups, ids);
						}
						entries.add(new SearchFilter.Entry(null, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME, assignedToGroupSplited, "group_list"));
						
						String assignedToTeam = PortletRequestUtils.getStringParameter(request, "assignedToTeam", "");
						String[] assignedToTeamSplited = new String[0];
						if (!"".equals(assignedToTeam)) {
							assignedToTeamSplited = assignedToTeam.trim().split("\\s");
							Set ids = new HashSet();
							for (int i = 0; i < assignedToTeamSplited.length; i++) {
								try {
									ids.add(Long.parseLong(assignedToTeamSplited[i]));
								} catch (NumberFormatException e) {
									// ignore
								}
							}
							componentData.put(AssignedToTeams, ids);
						}
						entries.add(new SearchFilter.Entry(null, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME, assignedToTeamSplited, "team_list"));					
						searchFilter.addEntries(entries, "userGroupTeam");
					}
					
					componentData.put(SearchFormSavedSearchQuery, searchFilter
							.getFilter().asXML());
					
				} else if (ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY.equals(cName)) {

					//single select
					Set<String> folderIds = new HashSet();
					boolean chooseFolder = GetterUtil.getBoolean(PortletRequestUtils.getStringParameter(request, "chooseFolder", "false"), false);
					if (chooseFolder && binder != null) {
						String type = resolveFolderType(cName);
						if (binder instanceof TemplateBinder) {
							//save - resolve later
							componentData.put(ChooseType, type);
						} else {
							//resolve binder now
							if (DashboardHelper.Binder.equals(scope) || DashboardHelper.Local.equals(scope)) {
								String id = resolveBinder(binder, type);
								if (Validator.isNotNull(id)) folderIds.add(id);
							}
						}
					} else {
						folderIds.addAll(TreeHelper.getSelectedStringIds(request.getParameterMap(), "ss_folder_id"));
					}
					
					SearchFilter searchFilter = new SearchFilter(true);
					String filterName = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterNameField, "");
					searchFilter.addFilterName(filterName);

					if (!folderIds.isEmpty()) {
						searchFilter.addFolderIds(folderIds);
						componentData.put(SearchFormSavedFolderIdList, LongIdUtil.getIdsAsString(folderIds));
					}
					componentData.put(SearchFormSavedSearchQuery, searchFilter
							.getFilter().asXML());
		
				} else if (ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST.equals(cName)) {
					String users = LongIdUtil.getIdsAsString(request.getParameterValues(DashboardHelper.ElementNamePrefix+"users"));
					String groups = LongIdUtil.getIdsAsString(request.getParameterValues(DashboardHelper.ElementNamePrefix+"groups"));
					if (users != null && !users.equals("")) componentData.put("users", users);
					if (groups != null && !groups.equals("")) componentData.put("groups", groups);
				} else if (ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE.equals(cName)) {
					Long topId = TreeHelper.getSelectedId(request.getParameterMap());
					if (topId != null) {
						componentData.put("topId", topId.toString());
					}
				}
				//Save the title and data map
				componentMap.put(Dashboard.COMPONENT_TITLE, componentTitle);
				componentMap.put(Dashboard.DISPLAYSTYLE, displayStyle);
				componentMap.put(Dashboard.DATA, componentData);
				//Save the updated dashboard configuration 
				getInstance().getDashboardModule().modifyComponent(d.getId(), componentId, componentMap);
			}
		}						
	}

	private String resolveFolderType(String name) {
		if (ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY.equals(name)) {
			return Definition.VIEW_STYLE_WIKI; 
		} else if (ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY.equals(name)) {
			return Definition.VIEW_STYLE_GUESTBOOK; 
		} else if (ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY.equals(name)) {
			return Definition.VIEW_STYLE_BLOG; 
		} else if (ObjectKeys.DASHBOARD_COMPONENT_GALLERY.equals(name)) {
			return Definition.VIEW_STYLE_PHOTO_ALBUM;
		} else if (ObjectKeys.DASHBOARD_COMPONENT_TASK_SUMMARY.equals(name)) {
			return Definition.VIEW_STYLE_TASK;
		} else if (ObjectKeys.DASHBOARD_COMPONENT_CALENDAR_SUMMARY.equals(name)) {
			return Definition.VIEW_STYLE_CALENDAR;
		}
		return Definition.VIEW_STYLE_DEFAULT;
	}

	protected String resolveBinder(Binder binder, String viewType) {
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//	get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);
		Map results = getBinderModule().getBinders(binder, options);
		List<Map> binders = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		for (Map b:binders) {
			String defId = (String)b.get(Constants.COMMAND_DEFINITION_FIELD);
			Definition def = getDefinitionModule().getDefinition(defId);
			String type = DefinitionUtils.getViewType(def.getDefinition());
			if (viewType.equals(type)) return (String)b.get(Constants.DOCID_FIELD);
		}	
		return null;

	}
	//resolve binder type refernces.  Since this is called to create user workspace on zone creation,
	//don't use modules cause not setup yet.
	public static void resolveRelativeBinders(Collection<Binder> binders, Dashboard d) {
		Map components = (Map)d.getProperty(Dashboard.COMPONENTS);
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			Map cMap = (Map)me.getValue();
			if (cMap == null) continue;
			Map data = (Map)cMap.get(Dashboard.DATA);
			if (data.containsKey(ChooseType) && data.get(ChooseType) instanceof String) {
				String viewType = (String)data.get(ChooseType);
				data.remove(ChooseType);
				Long id = null;
				for (Binder b:binders) {
					Definition def = b.getDefaultViewDef();
					String type = DefinitionUtils.getViewType(def.getDefinition());
					if (viewType.equals(type)) {
						id = b.getId();
						break;
					}
				}	

				if (id != null) {
					String savedIds = "";
					if (data.get(DashboardHelper.SearchFormSavedFolderIdList) instanceof String) 
						savedIds = (String)data.get(DashboardHelper.SearchFormSavedFolderIdList);
					Set<String> folderIds = LongIdUtil.getIdsAsStringSet(savedIds);
					folderIds.add(id.toString());
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.addFolderIds(folderIds);
					data.put(DashboardHelper.SearchFormSavedSearchQuery, searchFilter.getFilter().asXML());
					data.put(DashboardHelper.SearchFormSavedFolderIdList, LongIdUtil.getIdsAsString(folderIds));
				} 
			}
		}
  		d.setProperty(Dashboard.COMPONENTS, components);
	}
	
	public static void deleteComponent(ActionRequest request, Binder binder, String componentId, 
			String scope) {
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
			//Get the dashboard component
			String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
			String componentScope = "";
			if (componentId.contains("_")) componentScope = componentId.split("_")[0];
			if (Validator.isNotNull(componentScope)) {
				Dashboard d = getInstance().getDashboardObj(binder, componentScope);	
				//Save the updated dashbord configuration 
				getInstance().getDashboardModule().deleteComponent(d.getId(), dashboardListKey, componentId);
			}
		}
	}

	public static void showHideComponent(ActionRequest request, Binder binder, String componentId, 
			String scope, String action) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getInstance().getProfileModule().getUserProperties(user.getId()).getProperties();
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userProperties, new HashMap(), scope, componentId, false);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");

		if (Validator.isNotNull(dashboardListKey) & ssDashboard.containsKey(dashboardListKey)) {
			Dashboard d = getInstance().getDashboardObj(binder, scope);	
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(Dashboard.ID);
				if (id.equals(componentId)) {
					//We have found the component to be shown or hidden
					if (action.equals("show")) {
						component.put(Dashboard.VISIBLE, new Boolean(true));
					} else if (action.equals("hide")) {
						component.put(Dashboard.VISIBLE, new Boolean(false));
					}
					//Make sure the list also gets saved (in case it was a generated list)
					getInstance().getDashboardModule().setProperty(d.getId(), dashboardListKey, dashboardList);
					break;
				}
			}
		}
	}

	public static void moveComponent(ActionRequest request, Binder binder, String scope, 
			String direction) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getInstance().getProfileModule().getUserProperties(user.getId()).getProperties();
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userProperties, new HashMap(), scope, componentId, false);

		if (Validator.isNotNull(dashboardListKey) && ssDashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
			Dashboard d = getInstance().getDashboardObj(binder, scope);	
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(Dashboard.ID);
				if (id.equals(componentId)) {
					//We have found the component to be moved
					if (direction.equals("up")) {
						if (i > 0) {
							dashboardList.remove(i);
							dashboardList.add(i-1, component);
						    getInstance().getDashboardModule().setProperty(d.getId(), dashboardListKey, dashboardList);
						} else {
							//Move it into the next higher group
							String newListKey = "";
							if (dashboardListKey.equals(Dashboard.NARROW_FIXED)) newListKey = Dashboard.WIDE_TOP;
							if (dashboardListKey.equals(Dashboard.NARROW_VARIABLE)) newListKey = Dashboard.NARROW_FIXED;
							if (dashboardListKey.equals(Dashboard.WIDE_BOTTOM)) newListKey = Dashboard.NARROW_VARIABLE;
							if (!newListKey.equals("")) {
								List newDashboardList = (List) ssDashboard.get(newListKey);
								dashboardList.remove(i);
								newDashboardList.add(component);
								Map updates = new HashMap();
								updates.put(dashboardListKey, dashboardList);
								updates.put(newListKey, newDashboardList);
							    getInstance().getDashboardModule().modifyDashboard(d.getId(), updates);
							}
						}
					} else if (direction.equals("down")) {
						if (i < dashboardList.size()-1) {
							dashboardList.remove(i);
							dashboardList.add(i+1, component);
						    getInstance().getDashboardModule().setProperty(d.getId(), dashboardListKey, dashboardList);
						} else {
							//Move it into the next lower group
							String newListKey = "";
							if (dashboardListKey.equals(Dashboard.WIDE_TOP)) newListKey = Dashboard.NARROW_FIXED;
							if (dashboardListKey.equals(Dashboard.NARROW_FIXED)) newListKey = Dashboard.NARROW_VARIABLE;
							if (dashboardListKey.equals(Dashboard.NARROW_VARIABLE)) newListKey = Dashboard.WIDE_BOTTOM;
							if (!newListKey.equals("")) {
								List newDashboardList = (List) ssDashboard.get(newListKey);
								dashboardList.remove(i);
								newDashboardList.add(0, component);
								Map updates = new HashMap();
								updates.put(dashboardListKey, dashboardList);
								updates.put(newListKey, newDashboardList);
							    getInstance().getDashboardModule().modifyDashboard(d.getId(), updates);
							}
						}
					}
					break;
				}
			}
		}
	}

	public static void saveComponentOrder(String order, Binder binder, String dashboardScope) {
		Dashboard d = getInstance().getDashboardObj(binder, dashboardScope);
		
		//Get the new order as pairs (componentId,dashboard)
		String[] orderPairs = order.split(";");
		Map updates = new HashMap();
		for (int i = 0; i < orderPairs.length; i++) {
			if (orderPairs[i].contains(",")) {
				String id = orderPairs[i].split(",")[0];
				int idOffset = id.lastIndexOf("_dashboard_component_");
				if (idOffset >= 0) {

					id = id.substring(idOffset + "_dashboard_component_".length());
					String orderList = orderPairs[i].split(",")[1];
					//Find the component in its current place by going through each list
			    	List componentListOld;
					List componentListNew = (List) d.getProperty(orderList);
					if (componentListNew == null) {
						componentListNew = new ArrayList();
						d.setProperty(orderList, componentListNew);
					}
					updates.put(orderList, componentListNew);
					
					boolean foundIt = false;
			    	for (int j1 = 0; j1 < ComponentLists.length; j1++) {
						componentListOld = (List) d.getProperty(ComponentLists[j1]);
						for (int j2 = 0; j2 < componentListOld.size(); j2++) {
							Map component = (Map) componentListOld.get(j2);
							if (component.containsKey(Dashboard.ID) && 
									component.get(Dashboard.ID).equals(id)) {
								//Found the component; remove it from this list
								componentListOld.remove(j2);
								updates.put(ComponentLists[j1], componentListOld);
								//Add it to the new place (but only once unless it was just removed from the same list)
								if (!foundIt || ComponentLists[j1].equals(orderList)) componentListNew.add(component);
								foundIt = true;
							}
						}
					}
			    	if (!foundIt) {
			    		//We didn't find the component on any list; just put it on the right list
						String scope = id.substring(0, id.indexOf("_"));
						Map componentListItem = new HashMap();
						componentListItem.put(Dashboard.ID, id);
						componentListItem.put(Dashboard.SCOPE, scope);
						componentListItem.put(Dashboard.VISIBLE, new Boolean(true));
						componentListNew.add(componentListItem);
			    	}
				}
			}
		}
		getInstance().getDashboardModule().modifyDashboard(d.getId(), updates);
	}

	protected Map getDashboard(Binder binder, String scope) {
		//don't create dashboard if doesn't exist
		User user = RequestContextHolder.getRequestContext().getUser();
		Dashboard dashboard = null;
		if (scope.equals(DashboardHelper.Local)) {
			dashboard = getDashboardModule().getUserDashboard(user.getEntityIdentifier(), binder.getId());
		} else if (scope.equals(DashboardHelper.Global)) {
			dashboard = getDashboardModule().getEntityDashboard(user.getEntityIdentifier());
		} else if (scope.equals(DashboardHelper.Binder)) {
			dashboard = getDashboardModule().getEntityDashboard(binder.getEntityIdentifier());
		}
		
		if ((dashboard == null) || (dashboard.getProperties() == null)) {
			return getNewDashboardMap();
		} else {
			if (dashboard.getVersion() == null) {
				fixupDashboard(dashboard);
			}
			//Make a copy of the dashboard so changes won't accidentally bleed through
			return  new HashMap(dashboard.getProperties());
		}
	}
	protected Dashboard getDashboardObj(Binder binder, String scope) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Dashboard dashboard = null;
			
		if (scope.equals(DashboardHelper.Local)) {
			dashboard = getDashboardModule().getUserDashboard(user.getEntityIdentifier(), binder.getId());
			if (dashboard == null) {
				dashboard = getDashboardModule().createUserDashboard(user.getEntityIdentifier(), binder.getId(), getNewDashboardMap());
			}
		} else if (scope.equals(DashboardHelper.Global)) {
			dashboard = getDashboardModule().getEntityDashboard(user.getEntityIdentifier());
			if (dashboard == null) {
				dashboard = getDashboardModule().createEntityDashboard(user.getEntityIdentifier(), getNewDashboardMap());
			}
		} else if (scope.equals(DashboardHelper.Binder)) {
			dashboard = getDashboardModule().getEntityDashboard(binder.getEntityIdentifier());
			if (dashboard == null) {
				dashboard = getDashboardModule().createEntityDashboard(binder.getEntityIdentifier(), getNewDashboardMap());
			}
		}
		if ((dashboard != null) && (dashboard.getVersion() == null)) {
			fixupDashboard(dashboard);
		}

		return dashboard;
	}
	private void fixupDashboard(Dashboard dashboard) {
		//convert all the string[] to strings
		//the arrays are wastefull
		Map components = (Map)dashboard.getProperty(Dashboard.COMPONENTS);
		if (components == null) return;
		//loop through components
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			Map componentMap = (Map)me.getValue();
			if (componentMap == null) continue;
			Map data = (Map)componentMap.get(Dashboard.DATA);
			if (data == null) continue;
			Map fixedData = new HashMap(data);
			//loop through data map only
			for (Iterator iter2=data.entrySet().iterator(); iter2.hasNext();) {
				Map.Entry prop = (Map.Entry)iter2.next();
				String propName = (String)prop.getKey();
				Object propVal = prop.getValue();
				if (propVal instanceof String[]) {
					String[] val = (String[])propVal;
					if (val.length > 0) {
						fixedData.put(propName, val[0]);
					} else {
						fixedData.remove(propName);
					}
				} else if (SearchFormSavedFolderIdList.equals(propName)) {
					//convert from list to string 
					if (propVal instanceof List) {
						List propList = (List)propVal;
						fixedData.put(propName, LongIdUtil.getIdsAsString(propList));
					}
				}
			}
			componentMap.put(Dashboard.DATA, fixedData);
		}
		dashboard.setVersion(Integer.valueOf(1));
		getDashboardModule().setProperty(dashboard.getId(), Dashboard.COMPONENTS, components);

		
	}

	private boolean checkDashboardList(Map ssDashboard, Map dashboard, String listName) {
		boolean changesMade = false;
		if (dashboard == null) return false;
		List components = (List)dashboard.get(listName);
		
		List seenList = new ArrayList();
		for (int i = 0; i < components.size(); i++) {
			String id = (String) ((Map)components.get(i)).get(Dashboard.ID);
			if (seenList.contains(id) || !checkIfComponentExists(id, ssDashboard)) {
				//Remove duplicates or non-existant components
				components.remove(i);
				i--;
				changesMade = true;
			} else {
				seenList.add(id);
			}
		}
		return changesMade;
	}
	
	private boolean checkIfComponentExists(String id, Map ssDashboard) {
		String componentScope = "";
		if (id.contains("_")) componentScope = id.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = null;
			if (componentScope.equals(DashboardHelper.Local)) {
				dashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Global)) {
				dashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
			} else if (componentScope.equals(DashboardHelper.Binder)) {
				dashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
			}
			if (dashboard != null) {
				Map components = (Map) dashboard.get(Dashboard.COMPONENTS);
				if (components.containsKey(id)) return true;
			}
		}
		return false;
	}
	
	private boolean checkIfComponentOnLocalList(String id, Map dashboard) {
		//Find the component in its current place by going through each list
    	List componentList;
    	for (int i1 = 0; i1 < ComponentLists.length; i1++) {
			componentList = (List) dashboard.get(ComponentLists[i1]);
			if (componentList != null) {
				for (int i2 = 0; i2 < componentList.size(); i2++) {
					Map component = (Map) componentList.get(i2);
					if (component.containsKey(Dashboard.ID) && 
							component.get(Dashboard.ID).equals(id)) {
						//Found the component
						return true;
					}
				}
			}
    	}
    	return false;
	}
	
	
	public static boolean checkIfShowingAllComponents(Binder binder) {
		//See if the components are shown or hidden
		UserProperties folderProps = getInstance().getProfileModule().getUserProperties(null, binder.getId());
		Boolean showAllComponents = (Boolean) folderProps.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD_SHOW_ALL);
		if (showAllComponents == null) showAllComponents = Boolean.TRUE;
		return showAllComponents;
	}
	
	public static boolean checkIfContentExists(Map dashboard) {
		boolean dashboardContentExists = false;
		if (dashboard != null) {
			List wt = (List)dashboard.get("wide_top");
			List nf = (List)dashboard.get("narrow_fixed");
			List nv = (List)dashboard.get("narrow_variable");
			List wb = (List)dashboard.get("wide_bottom");
			if (wt.size() > 0 || nf.size() > 0 || nv.size() > 0 || wb.size() > 0) dashboardContentExists = true;
		}
		return dashboardContentExists;
	}

	public static boolean checkIfAnyContentExists(Map dashboard) {
		boolean dashboardContentExists = false;
		if (dashboard != null && dashboard.containsKey(WebKeys.DASHBOARD_COMPONENTS_LIST)) {
			Map dashboardLocal = (Map)dashboard.get("dashboard");
			if (dashboard != null) {
				dashboardContentExists = DashboardHelper.checkIfContentExists(dashboardLocal);
			}
			Map dashboardBinder = (Map)dashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
			if (!dashboardContentExists && dashboardBinder != null) {
				dashboardContentExists = DashboardHelper.checkIfContentExists(dashboardBinder);
			}
			Map dashboardGlobal = (Map)dashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
			if (!dashboardContentExists && dashboardGlobal != null) {
				dashboardContentExists = DashboardHelper.checkIfContentExists(dashboardGlobal);
			}
		}
		return dashboardContentExists;
	}
	
	protected void getRemoteApplicationBean(Map ssDashboard, String id, Map component) {
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData = new HashMap();
    	beans.put(id, idData);
		User user = RequestContextHolder.getRequestContext().getUser();
		idData.put(WebKeys.APPLICATIONS, getProfileModule().getApplications((Collection) null));
	}

}
