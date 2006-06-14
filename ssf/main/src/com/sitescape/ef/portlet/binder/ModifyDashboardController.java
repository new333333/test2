package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		Binder binder = getBinderModule().getBinder(binderId);
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = "";
		if (componentId.contains("_")) scope = componentId.split("_")[0];
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "");

		if (formData.containsKey("set_title")) {
			setTitle(request, binder, scope);
		} else if (formData.containsKey("add_wideTop")) {
			addComponent(request, binder, DashboardHelper.Wide_Top, scope);
		} else if (formData.containsKey("add_narrowFixed")) {
			addComponent(request, binder, DashboardHelper.Narrow_Fixed, scope);
		} else if (formData.containsKey("add_narrowVariable")) {
			addComponent(request, binder, DashboardHelper.Narrow_Variable, scope);
		} else if (formData.containsKey("add_wideBottom")) {
			addComponent(request, binder, DashboardHelper.Wide_Bottom, scope);
		} else if (formData.containsKey("_modifyComponentData") || formData.containsKey("_modifyComponentData.x")) {
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			saveComponentData(request, binder, scope);
		} else if (formData.containsKey("_deleteComponent") || formData.containsKey("_deleteComponent.x")) {
			deleteComponent(request, binder, scope);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveUp") || formData.containsKey("_moveUp.x")) {
			moveComponent(request, binder, scope, "up");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveDown") || formData.containsKey("_moveDown.x")) {
			moveComponent(request, binder, scope, "down");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
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
		String dashboardList = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = componentId.split("_")[0];
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "");

		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, userProperties, scope);
		
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		
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
		ssDashboard.put(WebKeys.DASHBOARD_LIST, dashboardList);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, "binder");
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		String view = "binder/modify_dashboard";
		
		if (formData.containsKey("set_title")) {
		} else if (formData.containsKey("add_wideTop")) {
		} else if (formData.containsKey("add_narrowFixed")) {
		} else if (formData.containsKey("add_narrowVariable")) {
		} else if (formData.containsKey("add_wideBottom")) {
		} else if (formData.containsKey("_modifyComponentData") || formData.containsKey("_modifyComponentData.x")) {
			view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
			ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, "form");
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			if (returnView.equals("binder")) view = "binder/modify_dashboard_component";
		} else if (formData.containsKey("_deleteComponent") || formData.containsKey("_deleteComponent.x")) {
		} else if (formData.containsKey("_moveUp") || formData.containsKey("_moveUp.x")) {
		} else if (formData.containsKey("_moveDown") || formData.containsKey("_moveDown.x")) {
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
		} else {
		}
		return new ModelAndView(view, model);
	}
	
	private void setTitle(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getDashboard(binder, scope);
		
		dashboard.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		dashboard.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		saveDashboard(binder, scope, dashboard);
	}
	
	private void addComponent(ActionRequest request, Binder binder, String listName, String scope) {
		Map dashboard = getDashboard(binder, scope);
		
		//Get the name of the component to be added
		String componentName = PortletRequestUtils.getStringParameter(request, "name", "");
		if (!componentName.equals("")) {
			Map component = new HashMap();
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			component.put(DashboardHelper.Name, componentName);
			component.put(DashboardHelper.Roles, 
					PortletRequestUtils.getStringParameters(request, "roles"));
			int nextComponent = (Integer) dashboard.get(DashboardHelper.NextComponent);
			String id = scope + "_" + String.valueOf(nextComponent);
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
	}
	
	private void saveComponentData(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getDashboard(binder, scope);

		Map formData = request.getParameterMap();
		Map componentData = new HashMap();
		Iterator itKeys = formData.keySet().iterator();
		while (itKeys.hasNext()) {
			String key = (String) itKeys.next();
			if (key.startsWith(DashboardHelper.ElementNamePrefix)) {
				String elementName = key.substring(DashboardHelper.ElementNamePrefix.length());
				//Save this value for use when displaying the component
				componentData.put(elementName, PortletRequestUtils.getStringParameters(request, key));
			}
		}
		
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		if (!dashboardListKey.equals("") && dashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) dashboard.get(dashboardListKey);
			Iterator itDashboardList = dashboardList.iterator();
			while (itDashboardList.hasNext()) {
				Map component = (Map) itDashboardList.next();
				String id = (String) component.get(DashboardHelper.Id);
				if (id.equals(componentId)) {
					//Get the component config data map
					Map components = (Map)dashboard.get(DashboardHelper.Components);
					if (components != null) {
						Map componentMap = (Map) components.get(id);
						if (componentMap != null) {
							//Save the data map
							componentMap.put(DashboardHelper.Data, componentData);
						}						
					}
				}
			}
		}
		//Save the updated dashbord configuration 
		saveDashboard(binder, scope, dashboard);
	}

	private void deleteComponent(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getDashboard(binder, scope);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		if (!dashboardListKey.equals("") && dashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) dashboard.get(dashboardListKey);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(DashboardHelper.Id);
				if (id.equals(componentId)) {
					//We have found the component to be deleted
					dashboardList.remove(i);
					break;
				}
			}
		}
		//Save the updated dashbord configuration 
		saveDashboard(binder, scope, dashboard);
	}

	private void moveComponent(ActionRequest request, Binder binder, String scope, String direction) {
		Map dashboard = getDashboard(binder, scope);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		if (!dashboardListKey.equals("") && dashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) dashboard.get(dashboardListKey);
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
					break;
				}
			}
		}
		//Save the updated dashbord configuration 
		saveDashboard(binder, scope, dashboard);
	}

	private Map getDashboard(Binder binder, String scope) {
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
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();
		return dashboard;
	}
	
	private void saveDashboard(Binder binder, String scope, Map dashboard) {
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

