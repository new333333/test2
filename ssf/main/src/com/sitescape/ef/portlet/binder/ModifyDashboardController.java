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
		String scope = PortletRequestUtils.getStringParameter(request, "scope", "");	

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
		} else if (formData.containsKey("_saveConfigData")) {
			saveComponentData(request, binder, scope);
		} else if (formData.containsKey("_deleteComponent")) {
			deleteComponent(request, binder, scope);
		} else if (formData.containsKey("_moveUp")) {
			moveComponent(request, binder, scope, "up");
		} else if (formData.containsKey("_moveDown")) {
			moveComponent(request, binder, scope, "down");
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewBinder(response, binderId, binderType);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		String scope = PortletRequestUtils.getStringParameter(request, "scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;

		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, scope);
		
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		
		//Get the lists of dashboard components that are supported
		String[] components_wide = (String[]) SPropsUtil.getString("dashboard.components.wide", "").split(",");
		String[] components_narrowFixed = (String[]) SPropsUtil.getString("dashboard.components.narrowFixed", "").split(",");
		String[] components_narrowVariable = (String[]) SPropsUtil.getString("dashboard.components.narrowVariable", "").split(",");
		
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
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
			
		return new ModelAndView("binder/modify_dashboard", model);
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
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map dashboard = null;
		if (scope.equals(DashboardHelper.Local)) {
			dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		} else if (scope.equals(DashboardHelper.Global)) {
			dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL);
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
			getProfileModule().setUserProperty(user.getId(), binder.getId(), 
					ObjectKeys.USER_PROPERTY_DASHBOARD_GLOBAL, dashboard);
		} else if (scope.equals(DashboardHelper.Binder)) {
			binder.setProperty(ObjectKeys.BINDER_PROPERTY_DASHBOARD, dashboard);
		}
	}
	
}

