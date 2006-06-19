package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;

public class DashboardHelper {
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
	public final static String Roles = "roles";
	public final static String Data = "data";
	
	//Scopes
	public final static String Local = "local";
	public final static String Binder = "binder";
	public final static String Global = "global";
	
	//Form keys
	public final static String ElementNamePrefix = "data.";

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
			Map userProperties) {
		return getDashboardMap(binder, userFolderProperties, userProperties, DashboardHelper.Local);
	}
	static public Map getDashboardMap(Binder binder, UserProperties userFolderProperties, 
			Map userProperties, String scope) {
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();
		Map dashboard_g = (Map) userProperties.get(ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL);
		if (dashboard_g == null) dashboard_g = DashboardHelper.getNewDashboardMap();
		Map dashboard_b = (Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_DASHBOARD);
		if (dashboard_b == null) dashboard_b = DashboardHelper.getNewDashboardMap();
		Map ssDashboard = new HashMap();
		if (scope.equals(DashboardHelper.Local)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, dashboard);
		} else if (scope.equals(DashboardHelper.Global)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, dashboard_g);
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_MAP, dashboard_b);
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
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, dashboard_g.get(Wide_Top));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, dashboard_g.get(Narrow_Fixed));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, dashboard_g.get(Narrow_Variable));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, dashboard_g.get(Wide_Bottom));
		} else if (scope.equals(DashboardHelper.Binder)) {
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_TOP, dashboard_b.get(Wide_Top));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_FIXED, dashboard_b.get(Narrow_Fixed));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE, dashboard_b.get(Narrow_Variable));
			ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM, dashboard_b.get(Wide_Bottom));
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

		return ssDashboard;
	}

	private static List buildDashboardList(String listName, Map ssDashboard) {
		Map localDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP);
		Map globalDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP);
		Map binderDashboard = (Map) ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP);
		
		//Start with a copy of the local list
		List components = new ArrayList((List)localDashboard.get(listName));
		
		List seenList = new ArrayList();
		for (int i = 0; i < components.size(); i++) {
			String id = (String) ((Map)components.get(i)).get(DashboardHelper.Id);
			if (!seenList.contains(id)) seenList.add(id);
		}
		
		//Then merge in the global and binder lists
		List globalAndBinderComponents = (List)globalDashboard.get(listName);
		globalAndBinderComponents.addAll((List)binderDashboard.get(listName));
		for (int i = 0; i < globalAndBinderComponents.size(); i++) {
			String id = (String) ((Map)globalAndBinderComponents.get(i)).get(DashboardHelper.Id);
			String scope = (String) ((Map)globalAndBinderComponents.get(i)).get(DashboardHelper.Scope);
			if (!seenList.contains(id)) {
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
}
