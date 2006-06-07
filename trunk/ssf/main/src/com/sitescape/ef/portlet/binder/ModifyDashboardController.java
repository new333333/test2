package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyDashboardController extends AbstractBinderController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);		
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();
		
		if (formData.containsKey("set_title")) {
			dashboard.put(DashboardHelper.Title, 
					PortletRequestUtils.getStringParameter(request, "title", ""));
			dashboard.put(DashboardHelper.IncludeBinderTitle, 
					PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
		} else if (formData.containsKey("add_wideTop")) {
		} else if (formData.containsKey("add_narrowFixed")) {
		} else if (formData.containsKey("add_narrowVariable")) {
		} else if (formData.containsKey("add_wideBottom")) {
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewBinder(response, binderId, binderType);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);

		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();

		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		Map ssDashboard = new HashMap();
		String dashboardTitle = (String) dashboard.get(DashboardHelper.Title);
		Boolean includeBinderTitle = (Boolean)dashboard.get(DashboardHelper.IncludeBinderTitle);
		String narrowFixedWidth = (String) SPropsUtil.getString("dashboard.size.narrowFixedWidth", "250");
		ssDashboard.put(WebKeys.DASHBOARD_TITLE, dashboardTitle);
		ssDashboard.put(WebKeys.DASHBOARD_INCLUDE_BINDER_TITLE, includeBinderTitle);
		ssDashboard.put(WebKeys.DASHBOARD_NARROW_FIXED_WIDTH, narrowFixedWidth);
		
		//Get the lists of dashboard components that are supported
		String[] components_wide = (String[]) SPropsUtil.getString("dashboard.components.wide", "").split(",");
		String[] components_narrowFixed = (String[]) SPropsUtil.getString("dashboard.components.narrowFixed", "").split(",");
		String[] components_narrowVariable = (String[]) SPropsUtil.getString("dashboard.components.narrowVariable", "").split(",");
		
		List cw = new ArrayList();
		List cnf = new ArrayList();
		List cnv = new ArrayList();
		for (int i = 0; i < components_wide.length; i++) 
			if (!components_wide[i].trim().equals("")) 
			cw.add(components_wide[i].trim());
		for (int i = 0; i < components_narrowFixed.length; i++) 
			if (!components_narrowFixed[i].trim().equals(""))
			cnf.add(components_narrowFixed[i].trim());
		for (int i = 0; i < components_narrowVariable.length; i++) 
			if (!components_narrowVariable[i].trim().equals(""))
			cnv.add(components_narrowVariable[i].trim());
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_NARROW_FIXED, cnf);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_NARROW_VARIABLE, cnv);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENTS_WIDE, cw);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
			
		return new ModelAndView("binder/modify_dashboard", model);
	}
}

