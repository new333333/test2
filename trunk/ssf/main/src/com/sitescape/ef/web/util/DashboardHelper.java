package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;

import org.dom4j.Document;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;

public class DashboardHelper {
	private static DashboardHelper instance; // A singleton instance

	//Dashboard map keys
	public final static String Title = "title";
	public final static String IncludeBinderTitle = "includeBinderTitle";
	public final static String DisplayStyle = "displayStyle";
	public final static String ControlStyle = "controlStyle";
	public final static String NextComponent = "nextComponent";
	public final static String Components = "components";

	public final static String DisplayStyleDefault = "shadow";
	public final static String ControlStyleDefault = "hover";

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

	protected BinderModule binderModule;
	protected FolderModule folderModule;
	protected DefinitionModule definitionModule;
	protected ProfileModule profileModule;
	protected WorkspaceModule workspaceModule;
	
	public DashboardHelper() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionHelper.class);
		
		instance = this;
	}
    public static DashboardHelper getInstance() {
    	return instance;
    }
	
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected FolderModule getFolderModule() {
		return folderModule;
	}
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
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
						getInstance().getBuddyListBean(ssDashboard, 
								id, component);
					} else if (component.get(Name).equals(
							ObjectKeys.DASHBOARD_COMPONENT_WORKSPACE_TREE)) {
						//Set up the workspace tree bean
						getInstance().getWorkspaceTreeBean(binder, 
								ssDashboard, model, id, component);
					} else if (component.get(Name).equals(
							ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
						//Set up the search results bean
						getInstance().getSearchResultsBean(binder, ssDashboard, 
								model, id, component);
					}
				}
			}
		}
    }
    
    public Map getNewDashboardMap() {
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
			dashboard = getInstance().getNewDashboardMap();
		} else {
			dashboard = new HashMap(dashboard);
		}
		Map dashboard_g = (Map) userProperties.get(ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL);
		if (dashboard_g == null) {
			dashboard_g = getInstance().getNewDashboardMap();
		} else {
			dashboard_g = new HashMap(dashboard_g);
		}
		Map dashboard_b = (Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_DASHBOARD);
		if (dashboard_b == null) {
			dashboard_b = getInstance().getNewDashboardMap();
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
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, getInstance().buildDashboardList(Wide_Top, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, getInstance().buildDashboardList(Narrow_Fixed, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, getInstance().buildDashboardList(Narrow_Variable, ssDashboard));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, getInstance().buildDashboardList(Wide_Bottom, ssDashboard));
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
		
		//Check the access rights of the user
		try {
			getInstance().getBinderModule().checkModifyBinderAllowed(binder);
			ssDashboard.put(WebKeys.DASHBOARD_SHARED_MODIFICATION_ALLOWED, new Boolean(true));
		} catch(AccessControlException e) {
			ssDashboard.put(WebKeys.DASHBOARD_SHARED_MODIFICATION_ALLOWED, new Boolean(false));			
		};
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		return ssDashboard;
	}

	private List buildDashboardList(String listName, Map ssDashboard) {
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
			boolean localChangesMade = getInstance().checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP), listNames[i]);
			boolean globalChangesMade = getInstance().checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP), listNames[i]);
			boolean binderChangesMade = getInstance().checkDashboardList(ssDashboard, 
					(Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP), listNames[i]);
			if (localChangesMade || globalChangesMade || binderChangesMade) changesMade = true;
		}
		return changesMade;
	}
	

	protected void getBuddyListBean(Map ssDashboard, String id, Map component) {
	   	Map data = (Map)component.get(DashboardHelper.Data);
	   	if (data != null) {
	    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
	    	if (beans == null) {
	    		beans = new HashMap();
	    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
	    	}
	    	Map idData = new HashMap();
	    	beans.put(id, idData);
			Set ids = new HashSet();		
	    	if (data.containsKey("users")) {
		    	String[] users = (String[])data.get("users");
		    	for (int i = 0; i < users.length; i++) {
		    		ids.add(new Long(users[i].trim()));
		    	}
	    	}
			//Get the configured list of principals to show
			idData.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
		
			Set gids = new HashSet();		
	    	if (data.containsKey("groups")) {
		    	String[] groups = (String[])data.get("groups");
		    	for (int i = 0; i < groups.length; i++) {
		    		gids.add(new Long(groups[i].trim()));
		    	}
	    	}
			idData.put(WebKeys.GROUPS, getProfileModule().getGroups(gids));
	   	}
	}
	protected void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
	    		String id, Map component) {
		if (!model.containsKey(WebKeys.DEFINITION_ENTRY)) model.put(WebKeys.DEFINITION_ENTRY, binder);
    	Map data = (Map)component.get(DashboardHelper.Data);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData = new HashMap();
    	beans.put(id, idData);

    	Document tree = null;
    	if (binder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
			if (model.containsKey(WebKeys.WORKSPACE_DOM_TREE)) {
				tree = (Document) model.get(WebKeys.WORKSPACE_DOM_TREE);
			} else {
				tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsTreeBuilder((Workspace)binder, true, getBinderModule()),1);
				idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, binder.getId().toString());
			}
		} else if (binder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.folder)) {
			Folder topFolder = ((Folder)binder).getTopFolder();
			if (topFolder == null) topFolder = (Folder)binder;
			Binder workspace = (Binder)topFolder.getParentBinder();
			tree = getWorkspaceModule().getDomWorkspaceTree(workspace.getId(), new WsTreeBuilder((Workspace)workspace, true, getBinderModule()),1);
			idData.put(WebKeys.DASHBOARD_WORKSPACE_TOPID, workspace.getId().toString());
			
		}
		idData.put(WebKeys.DASHBOARD_WORKSPACE_TREE, tree);
    }	   
    public void getSearchResultsBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component) {
    	Map data = (Map)component.get(DashboardHelper.Data);
    	if (data == null) data = new HashMap();
    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
    	if (beans == null) {
    		beans = new HashMap();
    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
    	}
    	Map idData = new HashMap();
    	beans.put(id, idData);

		Map searchSearchFormData = new HashMap();
		searchSearchFormData.put("searchFormTermCount", new Integer(0));
		idData.put(WebKeys.SEARCH_FORM_DATA, searchSearchFormData);
		
		Document searchQuery = null;
		if (data.containsKey(DashboardHelper.SearchFormSavedSearchQuery)) 
				searchQuery = (Document)data.get(DashboardHelper.SearchFormSavedSearchQuery);

		Map elementData = getFolderModule().getCommonEntryElements();
		searchSearchFormData.put(WebKeys.SEARCH_FORM_QUERY_DATA, 
				FilterHelper.buildFilterFormMap(searchQuery,
						elementData));
		
		//Do the search and store the search results in the bean
		List entries = getBinderModule().executeSearchQuery(binder, searchQuery);
        searchSearchFormData.put(WebKeys.SEARCH_FORM_RESULTS, entries);
    }
    
	public static void setTitle(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getInstance().getDashboard(binder, scope);
		
		dashboard.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		dashboard.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		getInstance().saveDashboard(binder, scope, dashboard);
	}
	
	public static String addComponent(ActionRequest request, Binder binder, 
			String listName, String scope) {
		Map dashboard = getInstance().getDashboard(binder, scope);
		String id = "";
		
		//Get the name of the component to be added
		String componentName = PortletRequestUtils.getStringParameter(request, "name", "");
		if (!componentName.equals("")) {
			Map component = new HashMap();
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			component.put(DashboardHelper.Name, componentName);
			component.put(DashboardHelper.Roles, 
					PortletRequestUtils.getStringParameters(request, "roles"));
			int nextComponent = (Integer) dashboard.get(DashboardHelper.NextComponent);
			id = scope + "_" + String.valueOf(nextComponent);
			components.put(id, component);
			
			//Add this new component to the list
			List componentList = (List) dashboard.get(listName);
			Map componentListItem = new HashMap();
			componentListItem.put(DashboardHelper.Id, id);
			componentListItem.put(DashboardHelper.Scope, scope);
			componentListItem.put(DashboardHelper.Visible, true);
			componentList.add(componentListItem);
			
			//Increment the next component id
			dashboard.put(DashboardHelper.NextComponent, new Integer(++nextComponent));
			
			getInstance().saveDashboard(binder, scope, dashboard);
		}
		return id;
	}
	
	public static void saveComponentData(ActionRequest request, Binder binder, String scope) {
		//Get the dashboard component
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getInstance().getDashboard(binder, componentScope);

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
			String controlStyle = PortletRequestUtils.getStringParameter(request, 
					DashboardHelper.ControlStyle, DashboardHelper.ControlStyleDefault);
			
			//Get the component config data map
			Map components = (Map)dashboard.get(DashboardHelper.Components);
			if (components != null) {
				Map componentMap = (Map) components.get(componentId);
				if (componentMap != null) {
					//Get any component specific data
					if (componentMap.get(DashboardHelper.Name).
							equals(ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
						//Get the search query
						try {
							Document query = FilterHelper.getSearchFilter(request);
							componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query);
						} catch(Exception ex) {}
					} else if (componentMap.get(DashboardHelper.Name).
							equals(ObjectKeys.DASHBOARD_COMPONENT_BUDDY_LIST)) {
						if (componentData.containsKey("users")) {
							String ids[] = (String[])componentData.get("users");
							Set userIds = new HashSet();
							if (ids != null) {
								for (int i = 0; i < ids.length; i++) {
									String[] uIds = ids[i].split(" ");
									for (int j = 0; j < uIds.length; j++) {
										if (uIds[j].length() > 0) userIds.add(uIds[j].trim());
									}
								}								
							}
							componentData.put("users", userIds.toArray(new String[userIds.size()]));
						}
						if (componentData.containsKey("groups")) {
							String ids[] = (String[])componentData.get("groups");
							Set groupIds = new HashSet();
							if (ids != null) {
								for (int i = 0; i < ids.length; i++) {
									String[] uIds = ids[i].split(" ");
									for (int j = 0; j < uIds.length; j++) {
										if (uIds[j].length() > 0) groupIds.add(uIds[j].trim());
									}
								}
								
							}
							componentData.put("groups", groupIds.toArray(new String[groupIds.size()]));
						}
					}

					
					//Save the title and data map
					componentMap.put(DashboardHelper.Component_Title, componentTitle);
					componentMap.put(DashboardHelper.DisplayStyle, displayStyle);
					componentMap.put(DashboardHelper.ControlStyle, controlStyle);
					componentMap.put(DashboardHelper.Data, componentData);
				}						
			}
			//Save the updated dashboard configuration 
			getInstance().saveDashboard(binder, componentScope, dashboard);
		}
	}

	public static void deleteComponent(ActionRequest request, Binder binder, String componentId, 
			String scope) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getInstance().getDashboard(binder, componentScope);	
			if (!dashboardListKey.equals("") && dashboard.containsKey(dashboardListKey)) {
				List dashboardList = (List) dashboard.get(dashboardListKey);
				for (int i = 0; i < dashboardList.size(); i++) {
					Map component = (Map) dashboardList.get(i);
					String id = (String) component.get(DashboardHelper.Id);
					if (id.equals(componentId)) {
						//We have found the component to be deleted
						dashboardList.remove(i);
					}
				}
			}
			//Delete the component itself
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			if (components != null && components.containsKey(componentId)) {
				components.remove(componentId);
			}
			//Save the updated dashbord configuration 
			getInstance().saveDashboard(binder, componentScope, dashboard);
		}
	}

	public static void showHideComponent(ActionRequest request, Binder binder, String componentId, 
			String scope, String action) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getInstance().getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getInstance().getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, new HashMap(), scope, componentId);
		Map dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_MAP);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");

		if (!dashboardListKey.equals("") && ssDashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(DashboardHelper.Id);
				if (id.equals(componentId)) {
					//We have found the component to be shown or hidden
					if (action.equals("show")) {
						component.put(DashboardHelper.Visible, true);
					} else if (action.equals("hide")) {
						component.put(DashboardHelper.Visible, false);
					}
					//Make sure the list also gets saved (in case it was a generated list)
					dashboard.put(dashboardListKey, dashboardList);
					break;
				}
			}
		}
		//Save the updated dashbord configuration 
		getInstance().saveDashboard(binder, scope, dashboard);
	}

	public static void moveComponent(ActionRequest request, Binder binder, String scope, 
			String direction) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getInstance().getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getInstance().getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, new HashMap(), scope, componentId);

		Map dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_MAP);

		if (!dashboardListKey.equals("") && ssDashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(DashboardHelper.Id);
				if (id.equals(componentId)) {
					//We have found the component to be moved
					if (direction.equals("up")) {
						if (i > 0) {
							dashboardList.remove(i);
							dashboardList.add(i-1, component);
						}
					} else if (direction.equals("down")) {
						if (i < dashboardList.size()-1) {
							dashboardList.remove(i);
							dashboardList.add(i+1, component);
						}
					}
					//Write the list back to the dashboard
					//  If the list is "local", this is the combined list.
					dashboard.put(dashboardListKey, dashboardList);
					break;
				}
			}
		}
		//Save the updated dashbord configuration 
		getInstance().saveDashboard(binder, scope, dashboard);
	}

	public Map getDashboard(Binder binder, String scope) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map dashboard = null;
		if (scope.equals(DashboardHelper.Local)) {
			UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
			dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		} else if (scope.equals(DashboardHelper.Global)) {
			Map userProperties = getProfileModule().getUserProperties(user.getId()).getProperties();
			dashboard = (Map) userProperties.get(ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL);
		} else if (scope.equals(DashboardHelper.Binder)) {
			dashboard = (Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_DASHBOARD);
		}
		if (dashboard == null) {
			dashboard = getNewDashboardMap();
		} else {
			//Make a copy of the dashboard so changes won't accidentally bleed through
			dashboard = new HashMap(dashboard);
		}
		return dashboard;
	}
	
	public static void saveDashboards(Binder binder, Map ssDashboard) {
		//Save the updated dashbord configurations
		String scope = DashboardHelper.Local;
		getInstance().saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP));
		scope = DashboardHelper.Global;
		getInstance().saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP));
		scope = DashboardHelper.Binder;
		getInstance().saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP));
	}
	
	public void saveDashboard(Binder binder, String scope, Map dashboard) {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		//Save the updated dashbord configuration 
		if (scope.equals(DashboardHelper.Local)) {
			getProfileModule().setUserProperty(user.getId(), binder.getId(), 
					ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
		} else if (scope.equals(DashboardHelper.Global)) {
			getProfileModule().setUserProperty(user.getId(),  
					ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL, dashboard);
		} else if (scope.equals(DashboardHelper.Binder)) {
			getBinderModule().setProperty(binder.getId(), 
					ObjectKeys.BINDER_PROPERTY_DASHBOARD, dashboard);
		}
	}
	private boolean checkDashboardList(Map ssDashboard, Map dashboard, String listName) {
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
				Map components = (Map) dashboard.get(Components);
				if (components.containsKey(id)) return true;
			}
		}
		return false;
	}
	
}
