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
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String scope = "";
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");

		if (formData.containsKey("set_title")) {
			DashboardHelper.setTitle(request, binder, scope);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("add_wideTop")) {
			componentId = DashboardHelper.addComponent(request, binder, DashboardHelper.Wide_Top, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Top);
		} else if (formData.containsKey("add_narrowFixed")) {
			componentId = DashboardHelper.addComponent(request, binder, DashboardHelper.Narrow_Fixed, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Fixed);
		} else if (formData.containsKey("add_narrowVariable")) {
			componentId = DashboardHelper.addComponent(request, binder, DashboardHelper.Narrow_Variable, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Variable);
		} else if (formData.containsKey("add_wideBottom")) {
			componentId = DashboardHelper.addComponent(request, binder, DashboardHelper.Wide_Bottom, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Bottom);
		} else if (operation.equals("_modifyComponentData")) {
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			DashboardHelper.saveComponentData(request, binder, scope);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (operation.equals("_deleteComponent")) {
			DashboardHelper.deleteComponent(request, binder, componentId, scope);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (operation.equals("_show")) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "show");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (operation.equals("_hide")) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "hide");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (operation.equals("_moveUp")) {
			DashboardHelper.moveComponent(request, binder, scope, "up");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (operation.equals("_moveDown")) {
			DashboardHelper.moveComponent(request, binder, scope, "down");
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
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");

		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		
		String dashboardList = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = "";
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (scope.equals("") && !componentScope.equals("")) {
			//The scope wasn't specified, so use the component scope for this operation
			scope = componentScope;
		}

		String cId = "";
		if (operation.equals("_modifyComponentData") || 
				operation.equals("_deleteComponent")) {
			cId = componentId;
		}

		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, model, scope, cId);

		if (DashboardHelper.checkDashboardLists(ssDashboard)) {
			//The dashboard was fixed up. Go save it
			DashboardHelper.saveDashboards(binder, ssDashboard);
		}
		
		ssDashboard.put(WebKeys.DASHBOARD_LIST, dashboardList);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, returnView);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		String view = "binder/modify_dashboard";
		
		if (op.equals(WebKeys.FORUM_OPERATION_SET_DASHBOARD_TITLE)) {
			view = "binder/modify_dashboard_title";
		} else if (formData.containsKey("add_wideTop")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowFixed")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_narrowVariable")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("add_wideBottom")) {
			view = "binder/modify_dashboard_component";
		} else if (operation.equals("_modifyComponentData")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
			ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, "form");
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			view = "binder/modify_dashboard_component";
		} else if (operation.equals("_deleteComponent")) {
		} else if (operation.equals("_moveUp")) {
		} else if (operation.equals("_moveDown")) {
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
		} else {
		}
		return new ModelAndView(view, model);
	}
	
}

