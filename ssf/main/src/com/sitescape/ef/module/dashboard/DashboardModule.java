package com.sitescape.ef.module.dashboard;

import java.util.Map;

import com.sitescape.ef.domain.Binder;

public interface DashboardModule {

	public void getBuddyListBean(Map ssDashboard, String id, Map component);
	public void getWorkspaceTreeBean(Binder binder, Map ssDashboard, Map model, 
    		String id, Map component);
	public void getSearchResultsBean(Map ssDashboard, Map model, 
    		String id, Map component);
}
