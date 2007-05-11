/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;

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
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portlet.binder.AdvancedSearchController;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.search.filter.SearchFiltersBuilder;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeHelper;
import com.sitescape.team.web.tree.FolderConfigHelper;
import com.sitescape.team.web.tree.WorkspaceConfigHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;

public class DashboardHelper extends AbstractAllModulesInjected {
	private static DashboardHelper instance; // A singleton instance

	//Dashboard map keys
	public final static String Title = "title";
	public final static String IncludeBinderTitle = "includeBinderTitle";
	public final static String DisplayStyle = "displayStyle";

	public final static String DisplayStyleDefault = "border";

	//Component Order lists
	public final static String Wide_Top = "wide_top";
	public final static String Narrow_Fixed = "narrow_fixed";
	public final static String Narrow_Variable = "narrow_variable";
	public final static String Wide_Bottom = "wide_bottom";
	public final static String[] ComponentLists = {Wide_Top, Narrow_Fixed, Narrow_Variable, Wide_Bottom};
	 
	
	//Component keys
	public final static String Name = "name";
	public final static String Component_Title = "title";
	public final static String Roles = "roles";
	public final static String Data = "data";
	
	//Component data keys
	public final static String SearchFormSavedSearchQuery = "__savedSearchQuery";
	public final static String SearchFormSavedFolderIdList = "__savedFolderIdList";
	public final static String Users= "users";
	public final static String Groups= "groups";
	public final static String TeamOn= "teamOn";
	public final static String ChooseFirst = "chooseFirst";
	
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
				if ((Boolean)component.get(Dashboard.Visible)) {
					//Set up the bean for this component
					getDashboardBean(binder, ssDashboard, model, (String)component.get(Dashboard.Id), isConfig);
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
    private static void doComponentSetup(Map ssDashboard, Map dashboard, Binder binder, Map model, String id) {
		if (dashboard.containsKey(Dashboard.Components)) {
			Map components = (Map) dashboard.get(Dashboard.Components);
			if (components.containsKey(id)) {
				Map component = (Map) components.get(id);
				String componentName = (String)component.get(Name);
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
					if ((binder != null) && (binder instanceof TemplateBinder)) {
						getInstance().getWorkspaceTreeBean(binder, 
								ssDashboard, model, id, component, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION));	
					} else {
						getInstance().getWorkspaceTreeBean(binder, 
							ssDashboard, model, id, component, null);
					}
				} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY)) {
					getInstance().getWikiHomepageEntryBean(null, ssDashboard, model, id, component, false);
				} else {
					//Set up the search results bean
					getInstance().getSearchResultsBean(binder, ssDashboard, 
							model, id, component, false);
				}
			}
		}
   	
    }
    private static void doComponentConfigSetup(Map ssDashboard, Map dashboard, Binder binder, Map model, String id) {
		if (dashboard.containsKey(Dashboard.Components)) {
			Map components = (Map) dashboard.get(Dashboard.Components);
			if (components.containsKey(id)) {
				Map component = (Map) components.get(id);
				//See if this component needs a bean
				if (component.get(Name).equals(
						ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
					//Set up the buddy list bean
					getInstance().getBuddyListBean(binder, ssDashboard, 
							id, component, true);
				} else if (component.get(Name).equals(
						ObjectKeys.DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST)) {
					//Set up the team members list bean,
					getInstance().getTeamMembersBean(binder, 
							ssDashboard, model, id, component, true);					
				} else if (component.get(Name).equals(
						ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
					//Set up the workspace tree bean,
					getInstance().getWorkspaceTreeBean(binder, 
							ssDashboard, model, id, component, new WorkspaceConfigHelper());
				} else if (component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
					//Set up the search results bean
					getInstance().getSearchResultsBean(binder, ssDashboard, 
							model, id, component, true);
				} else if (component.get(Name).equals(
						ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY)) {
					getInstance().getWorkspaceTreeBean(null, ssDashboard, model, id, component, new FolderConfigHelper());
					getInstance().getWikiHomepageEntryBean(null, ssDashboard, model, id, component, true);
				} else  {
					//Set up the search results bean
					getInstance().getSummaryConfigBean(binder, ssDashboard, 
							model, id, component);
				}
			}
		}
   	
    }


    public static Map getNewDashboardMap() {
    	Map dashboard =  new HashMap();
		dashboard.put(DashboardHelper.Title, "");
		dashboard.put(DashboardHelper.IncludeBinderTitle, new Boolean(false));
		dashboard.put(Dashboard.Components, new HashMap());
		dashboard.put(DashboardHelper.Wide_Top, new ArrayList());
		dashboard.put(DashboardHelper.Narrow_Fixed, new ArrayList());
		dashboard.put(DashboardHelper.Narrow_Variable, new ArrayList());
		dashboard.put(DashboardHelper.Wide_Bottom, new ArrayList());
		
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
		String title = (String) dashboard_b.get(Title);
		Boolean includeBinderTitle = (Boolean) dashboard_b.get(IncludeBinderTitle);
		if (includeBinderTitle==null) includeBinderTitle=Boolean.FALSE;
		Boolean includeBinderTitle_l = (Boolean) dashboard.get(IncludeBinderTitle);
		if (includeBinderTitle_l==null) includeBinderTitle_l=Boolean.FALSE;
		if (includeBinderTitle_l || !dashboard.get(Title).equals("")) {
			title = (String) dashboard.get(Title);
			includeBinderTitle = includeBinderTitle_l;
		}
		if (Validator.isNull(title) && !includeBinderTitle) {
			title = (String) dashboard_g.get(Title);
			includeBinderTitle = (Boolean) dashboard_g.get(IncludeBinderTitle);
		}
		ssDashboard.put(WebKeys.DASHBOARD_TITLE, title);
		ssDashboard.put(WebKeys.DASHBOARD_INCLUDE_BINDER_TITLE, includeBinderTitle);

		//Build the lists of components
		if (scope.equals(DashboardHelper.Local)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, getInstance().buildLocalDashboardList(DashboardHelper.Wide_Top, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, getInstance().buildLocalDashboardList(DashboardHelper.Narrow_Fixed, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, getInstance().buildLocalDashboardList(DashboardHelper.Narrow_Variable, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, getInstance().buildLocalDashboardList(DashboardHelper.Wide_Bottom, ssDashboard));
		} else if (scope.equals(DashboardHelper.Global)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_g.get(DashboardHelper.Wide_Top)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_g.get(DashboardHelper.Narrow_Fixed)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_g.get(DashboardHelper.Narrow_Variable)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_g.get(DashboardHelper.Wide_Bottom)));
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_b.get(DashboardHelper.Wide_Top)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_b.get(DashboardHelper.Narrow_Fixed)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_b.get(DashboardHelper.Narrow_Variable)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_b.get(DashboardHelper.Wide_Bottom)));
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
		if (getInstance().getBinderModule().testAccess(binder, "setProperty")) {
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

		Map ssDashboard = new HashMap();
		model.put(WebKeys.DASHBOARD, ssDashboard);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, DashboardHelper.Portlet);
		Map dashboardProps = new HashMap(dashboard.getProperties());
		ssDashboard.put(WebKeys.DASHBOARD_MAP, dashboardProps);	
		
		Map<String, Map> components = (Map) dashboardProps.get(Dashboard.Components);
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
			String id = (String) ((Map)components.get(i)).get(Dashboard.Id);
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
			String id = (String) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.Id);
			String scope = (String) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.Scope);
			Boolean visible = (Boolean) ((Map)globalAndBinderComponents.get(i)).get(Dashboard.Visible);
			if (!seenList.contains(id) && checkIfComponentExists(id, ssDashboard) && 
					!checkIfComponentOnLocalList(id, localDashboard)) {
				Map newComponent = new HashMap();
				newComponent.put(Dashboard.Id, id);
				newComponent.put(Dashboard.Scope, scope);
				newComponent.put(Dashboard.Visible, visible);
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
	   	Map data = (Map)component.get(DashboardHelper.Data);
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
	    		if (isConfig) {
	    			//keep separate for config
		    		idData.put(WebKeys.SHOW_TEAM_MEMBERS, data.get(TeamOn));
	    		} else if (Boolean.TRUE.equals(data.get(TeamOn))){
	    			//merge into user list if not config
	    			try {
	    				ids.addAll(getBinderModule().getTeamMemberIds(binder, false));
	    			} catch (Exception ex) {};  //skip if don't have access
	    		}
	    	}
			idData.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
 	   	}
	}
	protected void buddyListToXml(Element parent, Map data) {
		Element child = parent.addElement(Users);
		child.setText((String)data.get(Users));
		child = parent.addElement(Groups);
		child.setText((String)data.get(Groups));
		child = parent.addElement(TeamOn);
		if (data.containsKey(TeamOn)) child.setText(data.get(TeamOn).toString());
		else child.setText(Boolean.FALSE.toString());
	}
	
	private Set getIds(Object ids) {
		//handle bad data
		if (ids instanceof String) {
			return FindIdsHelper.getIdsAsLongSet((String)ids);
		} else return new HashSet<Long>();

	}
	protected void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
	    		String id, Map component, DomTreeHelper helper) {
    	Map data = (Map)component.get(DashboardHelper.Data);
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
 
    	if (binder != null) {
    		if (!(binder instanceof TemplateBinder)) {
    			if (binder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
    				if (model.containsKey(WebKeys.WORKSPACE_DOM_TREE)) {	
    					tree = (Document) model.get(WebKeys.WORKSPACE_DOM_TREE);
    				} else {
    					tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, helper),1);
    					idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, binder.getId().toString());
    				}
    			} else if (binder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
    				Folder topFolder = ((Folder)binder).getTopFolder();
    				if (topFolder == null) topFolder = (Folder)binder;
    				Binder workspace = (Binder)topFolder.getParentBinder();
    				tree = getWorkspaceModule().getDomWorkspaceTree(workspace.getId(), new WsDomTreeBuilder(workspace, true, this, helper),1);
    				idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, workspace.getId().toString());
    			}
    		} else {
       			if (binder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
    				if (model.containsKey(WebKeys.WORKSPACE_DOM_TREE)) {	
    					tree = (Document) model.get(WebKeys.WORKSPACE_DOM_TREE);
    				} else {
    					tree = BinderHelper.buildTemplateTreeRoot(this, (TemplateBinder)binder, helper);
    					idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, binder.getId().toString());
    				}
    			} else if (binder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
    				TemplateBinder top = (TemplateBinder)binder;
    				while (!top.isRoot()) {
    					top = (TemplateBinder)top.getParentBinder();
    				}
   					tree = BinderHelper.buildTemplateTreeRoot(this, top, helper);
   					idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, top.getId().toString());
    			}			
    		}
    	} else {
    		Long topId = (Long)data.get(WebKeys.DASHBOARD_WORKSPACE_TOPID);
    		if (topId == null) {
    			Workspace ws = getWorkspaceModule().getWorkspace();
    			tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, helper),1);
    			idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID,ws.getId().toString());
    		} else {
    			Workspace ws = getWorkspaceModule().getWorkspace(topId);
    			tree = getWorkspaceModule().getDomWorkspaceTree(topId, new WsDomTreeBuilder(ws, true, this, helper),1);
    			idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, topId.toString());			
    		}
    			
    	}
		idData.put(WebKeys.DASHBOARD_WORKSPACE_TREE, tree);
    }
	protected void workspaceTreeToXml(Element parent, Map data) {
		Element child = parent.addElement(WebKeys.DASHBOARD_WORKSPACE_TOPID);
   		Long topId = (Long)data.get(WebKeys.DASHBOARD_WORKSPACE_TOPID);
   		if (topId != null)
   		child.setText(topId.toString());
	}

	
	protected void getTeamMembersBean(Binder binder, Map ssDashboard, Map model, String id, Map component, boolean isConfig) {
    	Map data = (Map)component.get(DashboardHelper.Data);
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
    		List users = getBinderModule().getTeamMembers(binder.getId(), true);
    		idData.put(WebKeys.TEAM_MEMBERS, users);
    		idData.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
    	}
	}
	protected void teamMembersToXml(Element parent, Map data) {
		//nothing to store, determined dynamically
		return;
	}
	
    protected void getWikiHomepageEntryBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component, boolean isConfig) {
    	Map data = (Map)component.get(DashboardHelper.Data);
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
		List savedFolderIds = (List)data.get(SearchFormSavedFolderIdList);
		if (savedFolderIds != null && !savedFolderIds.isEmpty()) {
			Binder fBinder = getBinderModule().getBinder(Long.valueOf((String)savedFolderIds.get(0)));				
			idData.put(WebKeys.BINDER, fBinder);
			if (!isConfig) {
				String entryId = (String) fBinder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
				if (entryId != null) {
					Entry entry = getFolderModule().getEntry(fBinder.getId(), Long.valueOf(entryId));
					idData.put(WebKeys.DASHBOARD_WIKI_HOMEPAGE_ENTRY, entry);
				}
			}
		}
		
    }
    //reduce work
    protected void getSummaryConfigBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component) {
    	Map data = (Map)component.get(DashboardHelper.Data);
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
		//Build the jsp bean (sorted by folder title)
		List folderIds = new ArrayList();
		Collection folders=null;
		List savedFolderIds = (List)data.get(SearchFormSavedFolderIdList);
		if (savedFolderIds != null) {
			for (int i = 0; i < savedFolderIds.size(); i++) {
				folderIds.add(Long.valueOf((String)savedFolderIds.get(i)));
			}
			folders = getBinderModule().getBinders(folderIds);		//may have templates		
			idData.put(WebKeys.FOLDER_LIST, folders);
		}
		idData.put(WebKeys.BINDER_ID_LIST, folderIds);

		if (component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
			if ((folders != null) && !folders.isEmpty()) {
				idData.put(WebKeys.BINDER, folders.iterator().next());					
			}
		}
		getWorkspaceTreeBean(null, ssDashboard, model, id, component, new FolderConfigHelper());

    }
    protected void getSearchResultsBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component, boolean isConfig) {
    	Map data = (Map)component.get(DashboardHelper.Data);
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
		
		// Map elementData = BinderHelper.getCommonEntryElements();
		// searchSearchFormData.put(WebKeys.SEARCH_FORM_QUERY_DATA, FilterHelper.buildFilterFormMap(searchQuery,	elementData));
		SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(searchQuery, getDefinitionModule(), getProfileModule());
		searchSearchFormData.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		//Do the search and store the search results in the bean
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.MODIFICATION_DATE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
		if (data.containsKey(WebKeys.SEARCH_FORM_MAX_HITS)) {
			String[] maxHitsStr = (String[])data.get(WebKeys.SEARCH_FORM_MAX_HITS);
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxHitsStr[0]));
		}
		int pageSize = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (data.containsKey(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT)) {
			try {
				String[] resultsCount = (String[])data.get(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT);
				pageSize = Integer.valueOf(resultsCount[0]);
				model.put(WebKeys.PAGE_SIZE, Integer.valueOf(pageSize).toString());
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
		List savedFolderIds = (List)data.get(DashboardHelper.SearchFormSavedFolderIdList);
		if (savedFolderIds == null) savedFolderIds = new ArrayList();
		searchSearchFormData.put(WebKeys.BINDER_ID_LIST, savedFolderIds);

		boolean doSearch = true;		
		if (binder instanceof TemplateBinder) {
			//don't do the search - cannot support links between portlets
			doSearch = false;
			searchSearchFormData.put(WebKeys.SEARCH_FORM_RESULTS, new ArrayList());
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_COUNT, Integer.valueOf(0));
			searchSearchFormData.put(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED, Integer.valueOf(0));
		}  else {
			if (component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY) || 
				component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY) ||
				component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_GALLERY)) {
				if (savedFolderIds.isEmpty()) doSearch = false;
				else {
					//	Limit the search to entries only
					Document searchFilter2 = DocumentHelper.createDocument();
					Element rootElement = searchFilter2.addElement(QueryBuilder.AND_ELEMENT);
					Element field = rootElement.addElement(QueryBuilder.FIELD_ELEMENT);
					field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
					Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
					field = rootElement.addElement(QueryBuilder.FIELD_ELEMENT);
					field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
					child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);
					options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter2);
				}
			}
		}
		if (doSearch) {
			Map retMap = getInstance().getBinderModule().executeSearchQuery(searchQuery, options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			// 	entries = BinderHelper.filterEntryAttachmentResults(entries);
			AdvancedSearchController.checkFileIds(entries);
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
	
			
		//Build the jsp bean (sorted by folder title)
		List folderIds = new ArrayList();
		Collection folders=null;
		if (savedFolderIds != null) {
			for (int i = 0; i < savedFolderIds.size(); i++) {
				folderIds.add(Long.valueOf((String)savedFolderIds.get(i)));
			}
			folders = getBinderModule().getBinders(folderIds);		//may have templates		
			idData.put(WebKeys.FOLDER_LIST, folders);
		}
		idData.put(WebKeys.BINDER_ID_LIST, folderIds);  //longs

		if (component.get(Name).equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
			if ((folders != null) && !folders.isEmpty()) {
				idData.put(WebKeys.BINDER, folders.iterator().next());					
			}
		}
	
    }
    protected void searchResultsToXml(Element parent, Map data) {
		Element child;
		if (data.containsKey(DashboardHelper.SearchFormSavedSearchQuery)) {
			child = parent.addElement("property");
			child.addAttribute("name", DashboardHelper.SearchFormSavedSearchQuery);
			child.setText((String)data.get(DashboardHelper.SearchFormSavedSearchQuery));
		}
		List savedFolderIds = (List)data.get(SearchFormSavedFolderIdList);
		if (savedFolderIds != null && savedFolderIds.size() > 0) {
			child = parent.addElement("property");
			child.addAttribute("name", DashboardHelper.SearchFormSavedFolderIdList);
			StringBuffer buf = new StringBuffer();
			for (int i=0; i<savedFolderIds.size(); ++i) {
				buf.append((String)savedFolderIds.get(i) + " ");
			}
			child.setText(buf.toString());
		}
		if (data.containsKey(WebKeys.SEARCH_FORM_MAX_HITS)) {
			child = parent.addElement("property");
			child.addAttribute("name", WebKeys.SEARCH_FORM_MAX_HITS);
			String[] maxHitsStr = (String[])data.get(WebKeys.SEARCH_FORM_MAX_HITS);
			child.setText(maxHitsStr[0]);
		}
		if (data.containsKey(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT)) {
			child = parent.addElement("property");
			child.addAttribute("name", WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT);
			String[] resultsCount = (String[])data.get(WebKeys.DASHBOARD_SEARCH_RESULTS_COUNT);
			child.setText(resultsCount[0]);
		}
		if (data.containsKey(DashboardHelper.ChooseFirst)) {
			child = parent.addElement("property");
			child.addAttribute("name", DashboardHelper.ChooseFirst);
			Boolean resultsCount = (Boolean)data.get(DashboardHelper.ChooseFirst);
			child.setText(resultsCount.toString());
		}


    }
    
    public static void setTitle(ActionRequest request, Binder binder, String scope) {
		Dashboard dashboard = getInstance().getDashboardObj(binder, scope);
		Map updates = new HashMap();
		updates.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		updates.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		getInstance().getDashboardModule().modifyDashboard(dashboard.getId(), updates);
	}
	
	public static String addComponent(ActionRequest request, Binder binder, 
			String listName, String scope) {
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, "setProperty")) {
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
			component.put(DashboardHelper.Name, componentName);
			component.put(DashboardHelper.Roles, 
					PortletRequestUtils.getStringParameters(request, "roles"));
						
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
		Map components = (Map)dashboard.get(Dashboard.Components);
		Map component = new HashMap();
		component.put(DashboardHelper.Name, name);
		component.put(DashboardHelper.Roles, "");
		components.put(PORTLET_COMPONENT_ID, component);
		doComponentConfigSetup(ssDashboard, dashboard, null, model, PORTLET_COMPONENT_ID);
		return ssDashboard;
	}
	public static void saveComponentData(ActionRequest request, Binder binder, String scope) {
		//Get the dashboard component
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, "setProperty")) {
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
				//Save this value for use when displaying the component
				componentData.put(elementName, formData.get(key));
			}
		}
			
		//Get the component title
		String componentTitle = PortletRequestUtils.getStringParameter(request, 
				DashboardHelper.Component_Title, "");
		String displayStyle = PortletRequestUtils.getStringParameter(request, 
				DashboardHelper.DisplayStyle, DashboardHelper.DisplayStyleDefault);
		
		//Get the component config data map
		Map components = (Map)d.getProperty(Dashboard.Components);
		if (components != null) {
			Map componentMap = (Map) components.get(componentId);
			if (componentMap != null) {
				Map originalComponentData = new HashMap();
				if (componentMap.containsKey(Data)) originalComponentData = (Map) componentMap.get(Data);
				String cName = (String)componentMap.get(DashboardHelper.Name);
				//Get any component specific data
				if (ObjectKeys.DASHBOARD_COMPONENT_SEARCH.equals(cName)) {
					//Get the search query
					// Document query = FilterHelper.getSearchFilter(request);
					Document query = SearchFilterRequestParser.getSearchQuery(request, getDefinitionModule());
					componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query.asXML());
				} else if (componentMap.get(DashboardHelper.Name).
						equals(ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
					if (componentData.containsKey("users")) {
						componentData.put(Users, FindIdsHelper.getIdsAsString((String[])componentData.get("users")));
					}
					if (componentData.containsKey("groups")) {
					componentData.put(Groups, FindIdsHelper.getIdsAsString((String[])componentData.get("groups")));
					}
					componentData.put(TeamOn, 
							Boolean.valueOf(GetterUtil.getBoolean(
									PortletRequestUtils.getStringParameter(request,DashboardHelper.ElementNamePrefix + "teamOn", "false"), false)));

				} else if (ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_GALLERY.equals(cName)) {
					
					//multi-select	
					List folderIds = (List)originalComponentData.get(DashboardHelper.SearchFormSavedFolderIdList);
					if (folderIds == null) folderIds = new ArrayList();
					//add first
					//Get the folderIds out of the formData
					Iterator itFormData = formData.keySet().iterator();
					while (itFormData.hasNext()) {
						String key = (String)itFormData.next();
						if (key.matches("^ss_folder_id_[0-9]+$")) {
							folderIds.add(key.replaceFirst("^ss_folder_id_", ""));
						} else if (key.equals("ss_folder_id")) {
							String id = PortletRequestUtils.getStringParameter(request, "ss_folder_id", null);
							//single select
							if ((id != null) && !folderIds.contains(id)) folderIds.add(id); 
						}

					}
					//	Get the forums to be deleted
					itFormData = formData.entrySet().iterator();
					while (itFormData.hasNext()) {
						Map.Entry me = (Map.Entry) itFormData.next();
						if (((String)me.getKey()).startsWith("del_")) {
							String forumId = ((String)me.getKey()).substring(4);
							folderIds.remove(forumId);
						}
					}
					if (formData.containsKey("chooseFirst")) {
						boolean chooseFirst = GetterUtil.getBoolean(PortletRequestUtils.getStringParameter(request, "chooseFirst", "false"), false);
						if ((binder != null) && (binder instanceof TemplateBinder)) {
							//save - resolve later
							componentData.put(DashboardHelper.ChooseFirst, Boolean.valueOf(chooseFirst));
						} else {
							//resolve binder now
							if (DashboardHelper.Binder.equals(scope) || DashboardHelper.Local.equals(scope)) {
								String id = resolveBinder(binder, cName);
								if (Validator.isNotNull(id)) folderIds.add(id);
							}
						}
					}
					
					if (!folderIds.isEmpty()) {
						Document query = SearchFiltersBuilder.buildFolderListQuery(request, folderIds);
						componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query.asXML());
						componentData.put(DashboardHelper.SearchFormSavedFolderIdList, folderIds);
					} else {
						componentData.remove(DashboardHelper.SearchFormSavedSearchQuery);
						componentData.remove(DashboardHelper.SearchFormSavedFolderIdList);
						
					}
				} else if (ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY.equals(cName) ||
						ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY.equals(cName)) {
					//single select
					List folderIds = new ArrayList();
					if (formData.containsKey("chooseFirst")) {
						boolean chooseFirst = GetterUtil.getBoolean(PortletRequestUtils.getStringParameter(request,"chooseFirst", "false"), false);
						if ((binder != null) && (binder instanceof TemplateBinder)) {
							//save - resolve later
							componentData.put(DashboardHelper.ChooseFirst, Boolean.valueOf(chooseFirst));
						} else {
							//resolve binder now
							if (DashboardHelper.Binder.equals(scope) || DashboardHelper.Local.equals(scope)) {
								String id = resolveBinder(binder, cName);
								if (Validator.isNotNull(id)) folderIds.add(id);
							}
						}
					} else {
						String id = PortletRequestUtils.getStringParameter(request, "ss_folder_id", null);
						if (id != null) folderIds.add(id); 
					}
					if (!folderIds.isEmpty()) {
						Document query = SearchFiltersBuilder.buildFolderListQuery(request, folderIds);
						componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query.asXML());
						componentData.put(DashboardHelper.SearchFormSavedFolderIdList, folderIds);
					} else {
						componentData.remove(DashboardHelper.SearchFormSavedSearchQuery);
						componentData.remove(DashboardHelper.SearchFormSavedFolderIdList);
						
					}
				}
						
				//Save the title and data map
				componentMap.put(DashboardHelper.Component_Title, componentTitle);
				componentMap.put(DashboardHelper.DisplayStyle, displayStyle);
				componentMap.put(DashboardHelper.Data, componentData);
				//Save the updated dashboard configuration 
				getInstance().getDashboardModule().modifyComponent(d.getId(), componentId, componentMap);
			}
		}						
	}
	public static void resolveRelativeBinders(Binder binder, Dashboard d) {
		Map components = (Map)d.getProperty(Dashboard.Components);
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			Map cMap = (Map)me.getValue();
			if (cMap == null) continue;
			Map data = (Map)cMap.get(DashboardHelper.Data);
			if (data.containsKey(DashboardHelper.ChooseFirst)) {
				String cName = (String)cMap.get(DashboardHelper.Name);
				if (cName.equals(ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY) ||
						cName.equals(ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY) ||
						cName.equals(ObjectKeys.DASHBOARD_COMPONENT_GALLERY) ||
						cName.equals(ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY)) {
					Boolean chooseFirst = (Boolean)data.get(DashboardHelper.ChooseFirst);
					if (chooseFirst) {
						String id = getInstance().resolveBinder(binder, cName);
						if (Validator.isNotNull(id)) {
							List<String> folderIds = (List)data.get(DashboardHelper.SearchFormSavedFolderIdList);
							if (folderIds == null) folderIds = new ArrayList();
							folderIds.add(id);
							SearchFilter searchFilter = new SearchFilter();
							
							for (Object fId : folderIds) {
								searchFilter.addFolderId((String)fId);
							}
							
							data.put(DashboardHelper.SearchFormSavedSearchQuery, searchFilter.getFilter().asXML());
							data.put(DashboardHelper.SearchFormSavedFolderIdList, folderIds);
							
						}
					}
				}
				data.remove(DashboardHelper.ChooseFirst);
			}
		}
		getInstance().getDashboardModule().setProperty(d.getId(), Dashboard.Components, components);
	}
	protected String resolveBinder(Binder binder, String name) {
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//	get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);
		Map results = getBinderModule().getBinders(binder, options);
		List<Map> binders = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		for (Map b:binders) {
			String defId = (String)b.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD);
			Definition def = getDefinitionModule().getDefinition(defId);
			String viewType = DefinitionUtils.getViewType(def.getDefinition());
			if (ObjectKeys.DASHBOARD_COMPONENT_WIKI_SUMMARY.equals(name)) {
				if (Definition.VIEW_STYLE_WIKI.equals(viewType)) 
					return (String)b.get(EntityIndexUtils.DOCID_FIELD);
			} else if (ObjectKeys.DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY.equals(name)) {
				if (Definition.VIEW_STYLE_GUESTBOOK.equals(viewType)) 
					return (String)b.get(EntityIndexUtils.DOCID_FIELD);
			} else if (ObjectKeys.DASHBOARD_COMPONENT_BLOG_SUMMARY.equals(name)) {
				if (Definition.VIEW_STYLE_BLOG.equals(viewType)) 
					return (String)b.get(EntityIndexUtils.DOCID_FIELD);
			} else if (ObjectKeys.DASHBOARD_COMPONENT_GALLERY.equals(name)) {
				if (Definition.VIEW_STYLE_PHOTO_ALBUM.equals(viewType)) 
					return (String)b.get(EntityIndexUtils.DOCID_FIELD);
			}
		}	
		return null;

	}
	public static void deleteComponent(ActionRequest request, Binder binder, String componentId, 
			String scope) {
		if (!scope.equals(DashboardHelper.Binder) || getInstance().getBinderModule().testAccess(binder, "setProperty")) {
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
				String id = (String) component.get(Dashboard.Id);
				if (id.equals(componentId)) {
					//We have found the component to be shown or hidden
					if (action.equals("show")) {
						component.put(Dashboard.Visible, new Boolean(true));
					} else if (action.equals("hide")) {
						component.put(Dashboard.Visible, new Boolean(false));
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
				String id = (String) component.get(Dashboard.Id);
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
							if (dashboardListKey.equals(DashboardHelper.Narrow_Fixed)) newListKey = DashboardHelper.Wide_Top;
							if (dashboardListKey.equals(DashboardHelper.Narrow_Variable)) newListKey = DashboardHelper.Narrow_Fixed;
							if (dashboardListKey.equals(DashboardHelper.Wide_Bottom)) newListKey = DashboardHelper.Narrow_Variable;
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
							if (dashboardListKey.equals(DashboardHelper.Wide_Top)) newListKey = DashboardHelper.Narrow_Fixed;
							if (dashboardListKey.equals(DashboardHelper.Narrow_Fixed)) newListKey = DashboardHelper.Narrow_Variable;
							if (dashboardListKey.equals(DashboardHelper.Narrow_Variable)) newListKey = DashboardHelper.Wide_Bottom;
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
							if (component.containsKey(Dashboard.Id) && 
									component.get(Dashboard.Id).equals(id)) {
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
						componentListItem.put(Dashboard.Id, id);
						componentListItem.put(Dashboard.Scope, scope);
						componentListItem.put(Dashboard.Visible, new Boolean(true));
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
		return dashboard;
	}
	public void toXml(Dashboard dashboard, Element parent) {
		Element d = parent.addElement("dashboard");
		d.addAttribute("nextComponentId", String.valueOf(dashboard.getNextComponentId()));
		d.addAttribute("showComponents", String.valueOf(dashboard.isShowComponents()));
		Object val = dashboard.getProperty(DashboardHelper.Title);
		Element p;
		if (val != null) {
			p = d.addElement("property");
			p.addAttribute("name", DashboardHelper.Title);
			p.addCDATA(val.toString());
		}
		val = dashboard.getProperty(DashboardHelper.IncludeBinderTitle);
		if (val != null) {
			p = d.addElement("property");
			p.addAttribute("name", DashboardHelper.IncludeBinderTitle);
			p.addText(val.toString());
		}
		layoutToXml(d, dashboard, DashboardHelper.Wide_Top);
		layoutToXml(d, dashboard, DashboardHelper.Narrow_Fixed);
		layoutToXml(d, dashboard, DashboardHelper.Narrow_Variable);
		layoutToXml(d, dashboard, DashboardHelper.Wide_Bottom);
		Map components = (Map)dashboard.getProperty(Dashboard.Components);
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			Map cMap = (Map)me.getValue();
			if (cMap == null) continue;
			Element component = d.addElement("component");
			component.addAttribute("compenentId", me.getKey().toString());
			componentToXml(component, cMap);
		}
		
	}
	private void layoutToXml(Element parent, Dashboard dashboard, String name) {
		List<Map> contents = (List)dashboard.getProperty(name);
		for (Map cMap:contents) {
			Element layout = parent.addElement("layout");
			layout.addAttribute("name", name);
			for (Iterator iter=cMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				Object val = me.getValue();
				if (val == null) continue;
				Element p=layout.addElement("property");
				p.addAttribute("name", (String)me.getKey());
				p.addText(me.getValue().toString());
				
			}
		}		
	}
	private void componentToXml(Element parent, Map component) {
		String componentName = (String)component.get(Name);
		for (Iterator iter=component.entrySet().iterator(); iter.hasNext();) {
			Map.Entry prop = (Map.Entry)iter.next();
			String propName = prop.getKey().toString();
			
			if (Data.equals(propName)) {
				Element data = parent.addElement(Data);
				Map val = (Map)prop.getValue();
				if ((val != null) && !val.isEmpty()) {
					if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
						getInstance().buddyListToXml(data, val); 
					} else if (componentName.equals(
						ObjectKeys.DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST)) {
						getInstance().teamMembersToXml(data, val); 
					} else if (componentName.equals(
							ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
						getInstance().workspaceTreeToXml(data, val); 
					} else {
						getInstance().searchResultsToXml(data, val);
					}
				}
				
			} else {
				Element property = parent.addElement("property");
				property.addAttribute("name", "propName");
				String val = prop.getValue().toString();
				if (Validator.isNotNull(val)) property.setText(val);
			}
		}
		
	}

	private boolean checkDashboardList(Map ssDashboard, Map dashboard, String listName) {
		boolean changesMade = false;
		if (dashboard == null) return false;
		List components = (List)dashboard.get(listName);
		
		List seenList = new ArrayList();
		for (int i = 0; i < components.size(); i++) {
			String id = (String) ((Map)components.get(i)).get(Dashboard.Id);
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
				Map components = (Map) dashboard.get(Dashboard.Components);
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
					if (component.containsKey(Dashboard.Id) && 
							component.get(Dashboard.Id).equals(id)) {
						//Found the component
						return true;
					}
				}
			}
    	}
    	return false;
	}
	
	public static boolean checkIfShowingAllComponents(Dashboard dashboard) {
		//See if the components are shown or hidden
		Boolean showAllComponents = Boolean.valueOf(dashboard.isShowComponents());
		if (showAllComponents == null) showAllComponents = Boolean.TRUE;
		return showAllComponents;
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
}
