package com.sitescape.ef.module.dashboard;

import java.util.Map;

import javax.portlet.ActionRequest;

import com.sitescape.ef.domain.Binder;

public interface DashboardModule {

	public void getBuddyListBean(Map ssDashboard, String id, Map component);
	public void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component);
	public void getSearchResultsBean(Map ssDashboard, Map model, 
    		String id, Map component);

	public void setTitle(ActionRequest request, Binder binder, String scope);
	public String addComponent(ActionRequest request, Binder binder, String listName, 
			String scope);
	public void saveComponentData(ActionRequest request, Binder binder, String scope);
	public void deleteComponent(ActionRequest request, Binder binder);
	public void showHideComponent(ActionRequest request, Binder binder, String scope, 
			String action);
	public void moveComponent(ActionRequest request, Binder binder, String scope, 
			String direction);
	public Map getDashboard(Binder binder, String scope);
	public void saveDashboards(Binder binder, Map ssDashboard);
	public void saveDashboard(Binder binder, String scope, Map dashboard);
	
}
