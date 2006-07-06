package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.FilterHelper;
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
		Binder binder = getBinderModule().getBinder(binderId);
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = "";
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");

		if (formData.containsKey("set_title")) {
			getDashboardModule().setTitle(request, binder, scope);
		} else if (formData.containsKey("add_wideTop")) {
			componentId = getDashboardModule().addComponent(request, binder, DashboardHelper.Wide_Top, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Top);
		} else if (formData.containsKey("add_narrowFixed")) {
			componentId = getDashboardModule().addComponent(request, binder, DashboardHelper.Narrow_Fixed, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Fixed);
		} else if (formData.containsKey("add_narrowVariable")) {
			componentId = getDashboardModule().addComponent(request, binder, DashboardHelper.Narrow_Variable, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Variable);
		} else if (formData.containsKey("add_wideBottom")) {
			componentId = getDashboardModule().addComponent(request, binder, DashboardHelper.Wide_Bottom, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Bottom);
		} else if (formData.containsKey("_modifyComponentData") || formData.containsKey("_modifyComponentData.x")) {
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			getDashboardModule().saveComponentData(request, binder, scope);
		} else if (formData.containsKey("_deleteComponent") || formData.containsKey("_deleteComponent.x")) {
			getDashboardModule().deleteComponent(request, binder);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_show") || formData.containsKey("_show.x")) {
			getDashboardModule().showHideComponent(request, binder, scope, "show");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_hide") || formData.containsKey("_hide.x")) {
			getDashboardModule().showHideComponent(request, binder, scope, "hide");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveUp") || formData.containsKey("_moveUp.x")) {
			getDashboardModule().moveComponent(request, binder, scope, "up");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveDown") || formData.containsKey("_moveDown.x")) {
			getDashboardModule().moveComponent(request, binder, scope, "down");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);

		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		
		String dashboardList = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");

		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, model, scope);

		if (DashboardHelper.checkDashboardLists(ssDashboard)) {
			//The dashboard was fixed up. Go save it
			getDashboardModule().saveDashboards(binder, ssDashboard);
		}
		
		ssDashboard.put(WebKeys.DASHBOARD_LIST, dashboardList);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, returnView);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		String view = "binder/modify_dashboard";
		
		if (formData.containsKey("set_title")) {
		} else if (formData.containsKey("add_wideTop")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowFixed")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowVariable")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_wideBottom")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_modifyComponentData") || formData.containsKey("_modifyComponentData.x")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
			ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, "form");
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_deleteComponent") || formData.containsKey("_deleteComponent.x")) {
		} else if (formData.containsKey("_moveUp") || formData.containsKey("_moveUp.x")) {
		} else if (formData.containsKey("_moveDown") || formData.containsKey("_moveDown.x")) {
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
		} else {
		}
		return new ModelAndView(view, model);
	}
	
}

