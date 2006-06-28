package com.sitescape.ef.module.dashboard.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
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
    	if (data != null) {
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
    }
    
    public void getSearchResultsBean(Map ssDashboard, Map model, 
    		String id, Map component) {
    	Map data = (Map)component.get(DashboardHelper.Data);
    	if (data != null) {
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
    }
    

}
