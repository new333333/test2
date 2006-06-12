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

		if (formData.containsKey("set_title")) {
			setTitle(request, binderId);
		} else if (formData.containsKey("add_wideTop")) {
			addComponent(request, binderId, DashboardHelper.Wide_Top);
		} else if (formData.containsKey("add_narrowFixed")) {
			addComponent(request, binderId, DashboardHelper.Narrow_Fixed);
		} else if (formData.containsKey("add_narrowVariable")) {
			addComponent(request, binderId, DashboardHelper.Narrow_Variable);
		} else if (formData.containsKey("add_wideBottom")) {
			addComponent(request, binderId, DashboardHelper.Wide_Bottom);
		} else if (formData.containsKey("_saveConfigData")) {
			saveComponentData(request, binderId);
		} else if (formData.containsKey("_deleteComponent")) {
			deleteComponent(request, binderId);
		} else if (formData.containsKey("_moveUp")) {
			moveComponent(request, binderId, "up");
		} else if (formData.containsKey("_moveDown")) {
			moveComponent(request, binderId, "down");
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
		Map ssDashboard = DashboardHelper.getDashboardMap(dashboard);
		
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
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
			
		return new ModelAndView("binder/modify_dashboard", model);
	}
	
	private void setTitle(ActionRequest request, Long binderId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();
		
		dashboard.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		dashboard.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		getProfileModule().setUserProperty(user.getId(), binderId, 
				ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
	}
	
	private void addComponent(ActionRequest request, Long binderId, String listName) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();
		
		//Get the name of the component to be added
		String componentName = PortletRequestUtils.getStringParameter(request, "name", "");
		if (!componentName.equals("")) {
			Map component = new HashMap();
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			component.put(DashboardHelper.Name, componentName);
			component.put(DashboardHelper.Roles, 
					PortletRequestUtils.getStringParameters(request, "roles"));
			int nextComponent = (Integer) dashboard.get(DashboardHelper.NextComponent);
			components.put(String.valueOf(nextComponent), component);
			
			//Add this new component to the list
			List componentList = (List) dashboard.get(listName);
			Map componentListItem = new HashMap();
			componentListItem.put(DashboardHelper.Id, String.valueOf(nextComponent));
			componentListItem.put(DashboardHelper.Scope, DashboardHelper.Local);
			componentListItem.put(DashboardHelper.Visible, true);
			componentList.add(componentListItem);
			
			//Increment the next component id
			dashboard.put(DashboardHelper.NextComponent, new Integer(++nextComponent));
			
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
		}
	}
	
	private void saveComponentData(ActionRequest request, Long binderId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();

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
		getProfileModule().setUserProperty(user.getId(), binderId, 
				ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
	}

	private void deleteComponent(ActionRequest request, Long binderId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();

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
		getProfileModule().setUserProperty(user.getId(), binderId, 
				ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
	}

	private void moveComponent(ActionRequest request, Long binderId, String direction) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map dashboard = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DASHBOARD);
		if (dashboard == null) dashboard = DashboardHelper.getNewDashboardMap();

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
		getProfileModule().setUserProperty(user.getId(), binderId, 
				ObjectKeys.USER_PROPERTY_DASHBOARD, dashboard);
	}
}

