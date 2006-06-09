package com.sitescape.ef.web.util;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
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
	//Order lists
	public final static String Wide_Top = "wide_top";
	public final static String Narrow_Fixed = "narrow_fixed";
	public final static String Narrow_Variable = "narrow_variable";
	public final static String Wide_Bottom = "wide_bottom";
	
	//Order map keys
	public final static String Id = "id";
	public final static String Scope = "scope";
	public final static String Visible = "visible";
	
	//Component map keys
	public final static String Name = "name";
	public final static String Roles = "roles";

	static public Map getNewDashboardMap() {
		Map dashboard = new HashMap();
		dashboard.put(DashboardHelper.Title, "");
		dashboard.put(DashboardHelper.IncludeBinderTitle, new Boolean(true));
		dashboard.put(DashboardHelper.NextComponent, new Integer(1));
		dashboard.put(DashboardHelper.Components, new HashMap());
		dashboard.put(DashboardHelper.Wide_Top, new HashMap());
		dashboard.put(DashboardHelper.Narrow_Fixed, new HashMap());
		dashboard.put(DashboardHelper.Narrow_Variable, new HashMap());
		dashboard.put(DashboardHelper.Wide_Bottom, new HashMap());
		
		return dashboard;
	}
	
}
