package com.sitescape.ef.module.dashboard.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;

import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.dashboard.DashboardModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

public class DashboardModuleImpl extends CommonDependencyInjection implements DashboardModule {

	protected FolderModule folderModule;
	protected BinderModule binderModule;
	protected DefinitionModule definitionModule;
	protected ProfileModule profileModule;
	protected WorkspaceModule workspaceModule;
	
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
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

    public void getBuddyListBean(Map ssDashboard, String id, Map component) {
    	Map data = (Map)component.get(DashboardHelper.Data);
    	if (data != null) {
	    	Map beans = (Map) ssDashboard.get(WebKeys.DASHBOARD_BEAN_MAP);
	    	if (beans == null) {
	    		beans = new HashMap();
	    		ssDashboard.put(WebKeys.DASHBOARD_BEAN_MAP, beans);
	    	}
	    	Map idData = new HashMap();
	    	beans.put(id, idData);
	    	String[] users = new String[0];
	    	if (data.containsKey("users")) users = (String[])data.get("users");
	    	if (users.length > 0) users = users[0].split(" ");
	    	String[] groups = new String[0];
	    	if (data.containsKey("groups")) groups = (String[])data.get("groups");
	    	if (groups.length > 0) groups = groups[0].split(" ");
	
			Set ids = new HashSet();		
			for (int i = 0; i < users.length; i++) {
				if (!users[i].trim().equals("")) ids.add(new Long(users[i].trim()));
			}
			//Get the configured list of principals to show
			idData.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
			
			Set gids = new HashSet();		
			for (int i = 0; i < groups.length; i++) {
				if (!groups[i].trim().equals("")) gids.add(new Long(groups[i].trim()));
			}
			idData.put(WebKeys.GROUPS, getProfileModule().getGroups(gids));
    	}
    }
    
    public void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
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
    
    public void getSearchResultsBean(Map ssDashboard, Map model, 
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
						(Map) model.get(WebKeys.PUBLIC_ENTRY_DEFINITIONS),
						elementData));
		
		//Do the search and store the search results in the bean
		List entries = getBinderModule().executeSearchQuery(searchQuery);
        searchSearchFormData.put(WebKeys.SEARCH_FORM_RESULTS, entries);
    }
    
	public void setTitle(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getDashboard(binder, scope);
		
		dashboard.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		dashboard.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		saveDashboard(binder, scope, dashboard);
	}
	
	public String addComponent(ActionRequest request, Binder binder, 
			String listName, String scope) {
		Map dashboard = getDashboard(binder, scope);
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
			
			saveDashboard(binder, scope, dashboard);
		}
		return id;
	}
	
	public void saveComponentData(ActionRequest request, Binder binder, String scope) {
		//Get the dashboard component
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getDashboard(binder, componentScope);

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
			String componentTitle = PortletRequestUtils.getStringParameter(request, DashboardHelper.Component_Title, "");
			
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
					}
					
					//Save the title and data map
					componentMap.put(DashboardHelper.Component_Title, componentTitle);
					componentMap.put(DashboardHelper.Data, componentData);
				}						
			}
			//Save the updated dashboard configuration 
			saveDashboard(binder, componentScope, dashboard);
		}
	}

	public void deleteComponent(ActionRequest request, Binder binder) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getDashboard(binder, componentScope);	
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
			saveDashboard(binder, componentScope, dashboard);
		}
	}

	public void showHideComponent(ActionRequest request, Binder binder, String componentId, 
			String scope, String action) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, scope);

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
		saveDashboard(binder, scope, dashboard);
	}

	public void moveComponent(ActionRequest request, Binder binder, String scope, 
			String direction) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, userProperties, scope);

		Map dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_MAP);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

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
		saveDashboard(binder, scope, dashboard);
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
			dashboard = DashboardHelper.getNewDashboardMap();
		} else {
			//Make a copy of the dashboard so changes won't accidentally bleed through
			dashboard = new HashMap(dashboard);
		}
		return dashboard;
	}
	
	public void saveDashboards(Binder binder, Map ssDashboard) {
		//Save the updated dashbord configurations
		String scope = DashboardHelper.Local;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP));
		scope = DashboardHelper.Global;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP));
		scope = DashboardHelper.Binder;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP));
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

}
