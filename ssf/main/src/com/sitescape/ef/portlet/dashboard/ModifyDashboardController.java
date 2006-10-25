package com.sitescape.ef.portlet.dashboard;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DashboardPortlet;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyDashboardController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);		
		String dashboardId = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
		DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String scope = DashboardHelper.Portlet;

		if (formData.containsKey("add_wideTop")) {
			componentId = DashboardHelper.addComponent(request, dashboard, DashboardHelper.Wide_Top, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Top);
		} else if (formData.containsKey("add_narrowFixed")) {
			componentId = DashboardHelper.addComponent(request, dashboard, DashboardHelper.Narrow_Fixed, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Fixed);
		} else if (formData.containsKey("add_narrowVariable")) {
			componentId = DashboardHelper.addComponent(request, dashboard, DashboardHelper.Narrow_Variable, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Variable);
		} else if (formData.containsKey("add_wideBottom")) {
			componentId = DashboardHelper.addComponent(request, dashboard, DashboardHelper.Wide_Bottom, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Bottom);
		} else if (operation.equals("_modifyComponentData")) {
			//setup handed in render phase
		} else if (formData.containsKey("_saveConfigData")) {
			DashboardHelper.saveComponentData(request, dashboard);
			response.setRenderParameter(WebKeys.ACTION, "");			
		} else if (formData.containsKey("_deleteComponent")) {
			DashboardHelper.deleteComponent(request, dashboard, componentId);
			response.setRenderParameter(WebKeys.ACTION, "");
 		} else if (operation.equals("_show")) {
			DashboardHelper.showHideComponent(request, dashboard, componentId, "show");
		} else if (operation.equals("_hide")) {
			DashboardHelper.showHideComponent(request, dashboard, componentId, "hide");
		} else if (operation.equals("_moveUp")) {
//			DashboardHelper.moveComponent(request, binder, scope, "up");
//			if (returnView.equals("binder")) setupViewBinder(response, binder);
		} else if (operation.equals("_moveDown")) {
//			DashboardHelper.moveComponent(request, binder, scope, "down");
//			if (returnView.equals("binder")) setupViewBinder(response, binder);
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			response.setRenderParameter(WebKeys.ACTION, "");			
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String dashboardId = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
		DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");

		Map model = new HashMap();
		model.put(WebKeys.DASHBOARD_ID, dashboard.getId());
		
		String dashboardList = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = DashboardHelper.Portlet;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "portlet");

		String cId = "";
		if (operation.equals("_modifyComponentData")) {
			cId = componentId;
		}

		User user = RequestContextHolder.getRequestContext().getUser();
		Map ssDashboard = DashboardHelper.getDashboardMap(dashboard, 
				getProfileModule().getUserProperties(user.getId()).getProperties(), model, cId);
	
		ssDashboard.put(WebKeys.DASHBOARD_LIST, dashboardList);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, returnView);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		String view = "binder/modify_dashboard";
		
		if (formData.containsKey("add_wideTop")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowFixed")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowVariable")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_wideBottom")) {
			view = "binder/modify_dashboard_component";
		} else if (operation.equals("_modifyComponentData")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_saveConfigData")) {
			view = "binder/modify_dashboard_component";
		} else if (operation.equals("_moveUp")) {
		} else if (operation.equals("_moveDown")) {
		}
		return new ModelAndView(view, model);
	}
	
}

