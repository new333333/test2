package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.dashboard.DashboardModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.portlet.forum.SAbstractForumController.TreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;

public class DashboardHelper {
	private static DashboardHelper instance; // A singleton instance

	//Dashboard map keys
	public final static String Title = "title";
	public final static String IncludeBinderTitle = "includeBinderTitle";
	public final static String NextComponent = "nextComponent";
	public final static String Components = "components";
	//Component Order lists
	public final static String Wide_Top = "wide_top";
	public final static String Narrow_Fixed = "narrow_fixed";
	public final static String Narrow_Variable = "narrow_variable";
	public final static String Wide_Bottom = "wide_bottom";
	
	//Component list map keys (Components)
	public final static String Id = "id";
	public final static String Scope = "scope";
	public final static String Visible = "visible";
	
	//Component keys
	public final static String Name = "name";
	public final static String Component_Title = "title";
	public final static String Roles = "roles";
	public final static String Data = "data";
	
	//Component data keys
	public final static String SearchFormSavedSearchQuery = "__savedSearchQuery";
	
	//Scopes
	public final static String Local = "local";
	public final static String Binder = "binder";
	public final static String Global = "global";
	
	//Form keys
	public final static String ElementNamePrefix = "data_";

	protected DashboardModule dashboardModule;
	
	
	public DashboardHelper() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionUtils.class);
		
		instance = this;
	}
    public static DashboardHelper getInstance() {
    	return instance;
    }
	
	protected DashboardModule getDashboardModule() {
		return dashboardModule;
	}
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
    public static void getDashboardBeans(Binder binder, Map ssDashboard, Map model) {
		//Go through each list and build the needed beans
    	String[] listNames = {Wide_Top, Narrow_Fixed, Narrow_Variable, Wide_Bottom};
    	List componentList = new ArrayList();
    	for (int i = 0; i < listNames.length; i++) {
			String scope = (String)ssDashboard.get(WebKeys.DASHBOARD_SCOPE);
			if (scope.equals(DashboardHelper.Local)) {
				componentList = (List) ssDashboard.get(listNames[i]);
			} else if (scope.equals(DashboardHelper.Global)) {
				componentList = (List) ((Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP)).get(listNames[i]);
			} else if (scope.equals(DashboardHelper.Binder)) {
				componentList = (List) ((Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP)).get(listNames[i]);
			}
			for (int j = 0; j < componentList.size(); j++) {
				Map component = (Map) componentList.get(j);
				if ((Boolean)component.get(Visible)) {
					//Set up the bean for this component
					getDashboardBean(binder, ssDashboard, model, (String)component.get(Id));
				}
			}
		}
    }
    
    public static void getDashboardBean(Binder binder, Map ssDashboard, Map model, String id) {
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
			if (dashboard.containsKey(Components)) {
				Map components = (Map) dashboard.get(Components);
				if (components.containsKey(id)) {
					Map component = (Map) components.get(id);
					//See if this component needs a bean
					if (component.get(Name).equals(
							ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
						//Set up the buddy list bean
						getInstance().dashboardModule.getBuddyListBean(ssDashboard, 
								id, component);
					} else if (component.get(Name).equals(
							ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
						//Set up the workspace tree bean
						getInstance().dashboardModule.getWorkspaceTreeBean(binder, 
								ssDashboard, model, id, component);
					} else if (component.get(Name).equals(
							ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
						//Set up the search results bean
						getInstance().dashboardModule.getSearchResultsBean(binder, ssDashboard, 
								model, id, component);
					}
				}
			}
		}
    }
    
    static public Map getNewDashboardMap() {
		Map dashboard = new HashMap();
		dashboard.put(DashboardHelper.Title, "");
		dashboard.put(DashboardHelper.IncludeBinderTitle, new Boolean(false));
		dashboard.put(DashboardHelper.NextComponent, new Integer(1));
		dashboard.put(DashboardHelper.Components, new HashMap());
		dashboard.put(DashboardHelper.Wide_Top, new ArrayList());
		dashboard.put(DashboardHelper.Narrow_Fixed, new ArrayList());
		dashboard.put(DashboardHelper.Narrow_Variable, new ArrayList());
		dashboard.put(DashboardHelper.Wide_Bottom, new ArrayList());
		
		return dashboard;
	}
	
	static public Map getDashboardMap(Binder binder, UserProperties userFolderProperties, 
			Map userProperties, Map model) {
		return getDashboardMap(binder, userFolderProperties, userProperties, model, 
				DashboardHelper.Local);
	}
	static public Map getDashboardMap(Binder binder, UserProperties userFolderProperties, 
			Map userProperties, String scope) {
		return getDashboardMap(binder, userFolderProperties, userProperties, 
				new HashMap(), scope);
	}
	static public Map getDashboardMap(Binder binder, UserProperties userFolderProperties, 
			Map userProperties, Map model, String scope) {
		String componentId = "";
		return getDashboardMap(binder, userFolderProperties, userProperties, 
				model, scope, componentId);
	}
	static public Map getDashboardMap(Binder binder, UserProperties userFolderProperties, 
			Map userProperties, Map model, String scope, String componentId) {
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) {
			dashboard = DashboardHelper.getNewDashboardMap();
		} else {
			dashboard = new HashMap(dashboard);
		}
		Map dashboard_g = (Map) userProperties.get(ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL);
		if (dashboard_g == null) {
			dashboard_g = DashboardHelper.getNewDashboardMap();
		} else {
			dashboard_g = new HashMap(dashboard_g);
		}
		Map dashboard_b = (Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_DASHBOARD);
		if (dashboard_b == null) {
			dashboard_b = DashboardHelper.getNewDashboardMap();
		} else {
			dashboard_b = new HashMap(dashboard_b);
		}
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
		Boolean includeBinderTitle_l = (Boolean) dashboard.get(IncludeBinderTitle);
		if (includeBinderTitle_l || !dashboard.get(Title).equals("")) {
			title = (String) dashboard.get(Title);
			includeBinderTitle = includeBinderTitle_l;
		}
		if (title.equals("") && !includeBinderTitle) {
			title = (String) dashboard_g.get(Title);
			includeBinderTitle = (Boolean) dashboard_g.get(IncludeBinderTitle);
		}
		ssDashboard.put(WebKeys.DASHBOARD_TITLE, title);
		ssDashboard.put(WebKeys.DASHBOARD_INCLUDE_BINDER_TITLE, includeBinderTitle);

		//Build the lists of components
		if (scope.equals(DashboardHelper.Local)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, buildDashboardList(Wide_Top, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, buildDashboardList(Narrow_Fixed, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, buildDashboardList(Narrow_Variable, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, buildDashboardList(Wide_Bottom, ssDashboard));
		} else if (scope.equals(DashboardHelper.Global)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_g.get(Wide_Top)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_g.get(Narrow_Fixed)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_g.get(Narrow_Variable)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_g.get(Wide_Bottom)));
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, new ArrayList((List)dashboard_b.get(Wide_Top)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, new ArrayList((List)dashboard_b.get(Narrow_Fixed)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, new ArrayList((List)dashboard_b.get(Narrow_Variable)));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, new ArrayList((List)dashboard_b.get(Wide_Bottom)));
		}
		
		//Get the lists of dashboard components that are supported
		String[] components_wide = SPropsUtil.getCombinedPropertyList(
				"dashboard.components.wide", ObjectKeys.CUSTOM_PROPERTY_PREFIX);
		String[] components_narrowFixed = SPropsUtil.getCombinedPropertyList(
				"dashboard.components.narrowFixed", ObjectKeys.CUSTOM_PROPERTY_PREFIX);
		String[] components_narrowVariable = SPropsUtil.getCombinedPropertyList(
				"dashboard.components.narrowVariable", ObjectKeys.CUSTOM_PROPERTY_PREFIX);
		
		List cw = new ArrayList();
		List cnf = new ArrayList();
		List cnv = new ArrayList();
		Map componentTitles = new HashMap();
		for (int i = 0; i < components_wide.length; i++) {
			if (!components_wide[i].trim().equals("")) {
				String component = components_wide[i].trim();
				cw.add(component);
				String componentTitle = SPropsUtil.getString("dashboard.title." + component, component);
				componentTitles.put(component, componentTitle);
			}
		}
		for (int i = 0; i < components_narrowFixed.length; i++) {
			if (!components_narrowFixed[i].trim().equals("")) {
				String component = components_narrowFixed[i].trim();
				cnf.add(component);
				String componentTitle = SPropsUtil.getString("dashboard.title." + component, component);
				componentTitles.put(component, componentTitle);
			}
		}
		for (int i = 0; i < components_narrowVariable.length; i++) {
			if (!components_narrowVariable[i].trim().equals("")) {
				String component = components_narrowVariable[i].trim();
				cnv.add(component);
				String componentTitle = SPropsUtil.getString("dashboard.title." + component, component);
				componentTitles.put(component, componentTitle);
			}
		}
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_NARROW_FIXED, cnf);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_NARROW_VARIABLE, cnv);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_WIDE, cw);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_TITLES, componentTitles);

		//Set up the beans
		if (componentId.equals("")) {
			getDashboardBeans(binder, ssDashboard, model);
		} else {
			getDashboardBean(binder, ssDashboard, model, componentId);
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		}
		model.put(WebKeys.DASHBOARD, ssDashboard);
		return ssDashboard;
	}

	private static List buildDashboardList(String listName, Map ssDashboard) {
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
			String id = (String) ((Map)components.get(i)).get(DashboardHelper.Id);
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
			String id = (String) ((Map)globalAndBinderComponents.get(i)).get(DashboardHelper.Id);
			String scope = (String) ((Map)globalAndBinderComponents.get(i)).get(DashboardHelper.Scope);
			if (!seenList.contains(id) && checkIfComponentExists(id, ssDashboard)) {
				seenList.add(id);
				Map newComponent = new HashMap();
				newComponent.put(DashboardHelper.Id, id);
				newComponent.put(DashboardHelper.Scope, scope);
				newComponent.put(DashboardHelper.Visible, true);
				components.add(newComponent);
			}
		}
		
		return components;
	}
	
	public static boolean checkDashboardLists(Map ssDashboard) {
		boolean changesMade = false;
		String[] listNames = {Wide_Top, Narrow_Fixed, Narrow_Variable, Wide_Bottom};
		for (int i = 0; i < listNames.length; i++) {
			boolean localChangesMade = checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP), listNames[i]);
			boolean globalChangesMade = checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP), listNames[i]);
			boolean binderChangesMade = checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP), listNames[i]);
			if (localChangesMade || globalChangesMade || binderChangesMade) changesMade = true;
		}
		return changesMade;
	}
	
	private static boolean checkDashboardList(Map ssDashboard, Map dashboard, String listName) {
		boolean changesMade = false;
		if (dashboard == null) return false;
		List components = (List)dashboard.get(listName);
		
		List seenList = new ArrayList();
		for (int i = 0; i < components.size(); i++) {
			String id = (String) ((Map)components.get(i)).get(DashboardHelper.Id);
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
	
	private static boolean checkIfComponentExists(String id, Map ssDashboard) {
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
				Map components = (Map) dashboard.get(Components);
				if (components.containsKey(id)) return true;
			}
		}
		return false;
	}	
}
