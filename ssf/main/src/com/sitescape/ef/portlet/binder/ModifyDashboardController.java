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
			setTitle(request, binder, scope);
		} else if (formData.containsKey("add_wideTop")) {
			componentId = addComponent(request, binder, DashboardHelper.Wide_Top, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Top);
		} else if (formData.containsKey("add_narrowFixed")) {
			componentId = addComponent(request, binder, DashboardHelper.Narrow_Fixed, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Fixed);
		} else if (formData.containsKey("add_narrowVariable")) {
			componentId = addComponent(request, binder, DashboardHelper.Narrow_Variable, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Narrow_Variable);
		} else if (formData.containsKey("add_wideBottom")) {
			componentId = addComponent(request, binder, DashboardHelper.Wide_Bottom, scope);
			response.setRenderParameter("_componentId", componentId);
			response.setRenderParameter("_dashboardList", DashboardHelper.Wide_Bottom);
		} else if (formData.containsKey("_modifyComponentData") || formData.containsKey("_modifyComponentData.x")) {
		} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
		} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			saveComponentData(request, binder, scope);
		} else if (formData.containsKey("_deleteComponent") || formData.containsKey("_deleteComponent.x")) {
			deleteComponent(request, binder);
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_show") || formData.containsKey("_show.x")) {
			showHideComponent(request, binder, scope, "show");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_hide") || formData.containsKey("_hide.x")) {
			showHideComponent(request, binder, scope, "hide");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveUp") || formData.containsKey("_moveUp.x")) {
			moveComponent(request, binder, scope, "up");
			if (returnView.equals("binder")) setupViewBinder(response, binderId, binderType);
		} else if (formData.containsKey("_moveDown") || formData.containsKey("_moveDown.x")) {
			moveComponent(request, binder, scope, "down");
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
			saveDashboards(binder, ssDashboard);
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
	
	private void setTitle(ActionRequest request, Binder binder, String scope) {
		Map dashboard = getDashboard(binder, scope);
		
		dashboard.put(DashboardHelper.Title, 
				PortletRequestUtils.getStringParameter(request, "title", ""));
		dashboard.put(DashboardHelper.IncludeBinderTitle, 
				PortletRequestUtils.getBooleanParameter(request, "includeBinderTitle", false));
		
		saveDashboard(binder, scope, dashboard);
	}
	
	private String addComponent(ActionRequest request, Binder binder, String listName, String scope) {
		Map dashboard = getDashboard(binder, scope);
		String id = "";
		
		//Get the name of the component to be added
		String componentName = PortletRequestUtils.getStringParameter(request, "name", "");
		if (!componentName.equals("")) {
			Map component = new HashMap();
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			component.put(DashboardHelper.Name, componentName);
			component.put(DashboardHelper.Roles, 
					PortletRequestUtils.getStringParameters(request, "roles"));
			int nextComponent = (Integer) dashboard.get(DashboardHelper.NextComponent);
			id = scope + "_" + String.valueOf(nextComponent);
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
		return id;
	}
	
	private void saveComponentData(ActionRequest request, Binder binder, String scope) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getDashboard(binder, componentScope);

			//Get the generic data elements that start with the ElementNamePrefix
			Map formData = request.getParameterMap();
			Map componentData = new HashMap();
			Iterator itKeys = formData.keySet().iterator();
			while (itKeys.hasNext()) {
				String key = (String) itKeys.next();
				if (key.startsWith(DashboardHelper.ElementNamePrefix)) {
					String elementName = key.substring(DashboardHelper.ElementNamePrefix.length());
					//Save this value for use when displaying the component
					componentData.put(elementName, formData.get(key));
				}
			}
			
			//Get the component title
			String componentTitle = PortletRequestUtils.getStringParameter(request, DashboardHelper.Component_Title, "");
			
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
								//Get any component specific data
								if (componentMap.get(DashboardHelper.Name).
										equals(ObjectKeys.DASHBOARD_COMPONENT_SEARCH)) {
									//Get the search query
									try {
										Document query = FilterHelper.getSearchFilter(request);
										componentData.put(DashboardHelper.SearchFormSavedSearchQuery, query);
									} catch(Exception ex) {}
								}
								
								//Save the title and data map
								componentMap.put(DashboardHelper.Component_Title, componentTitle);
								componentMap.put(DashboardHelper.Data, componentData);
							}						
						}
					}
				}
			}
			//Save the updated dashboard configuration 
			saveDashboard(binder, componentScope, dashboard);
		}
	}

	private void deleteComponent(ActionRequest request, Binder binder) {
		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (!componentScope.equals("")) {
			Map dashboard = getDashboard(binder, componentScope);	
			if (!dashboardListKey.equals("") && dashboard.containsKey(dashboardListKey)) {
				List dashboardList = (List) dashboard.get(dashboardListKey);
				for (int i = 0; i < dashboardList.size(); i++) {
					Map component = (Map) dashboardList.get(i);
					String id = (String) component.get(DashboardHelper.Id);
					if (id.equals(componentId)) {
						//We have found the component to be deleted
						dashboardList.remove(i);
					}
				}
			}
			//Delete the component itself
			Map components = (Map) dashboard.get(DashboardHelper.Components);
			if (components != null && components.containsKey(componentId)) {
				components.remove(componentId);
			}
			//Save the updated dashbord configuration 
			saveDashboard(binder, componentScope, dashboard);
		}
	}

	private void showHideComponent(ActionRequest request, Binder binder, String scope, String action) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, scope);

		Map dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_MAP);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		if (!dashboardListKey.equals("") && ssDashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String id = (String) component.get(DashboardHelper.Id);
				if (id.equals(componentId)) {
					//We have found the component to be shown or hidden
					if (action.equals("show")) {
						component.put(DashboardHelper.Visible, true);
					} else if (action.equals("hide")) {
						component.put(DashboardHelper.Visible, false);
					}
					//Make sure the list also gets saved (in case it was a generated list)
					dashboard.put(dashboardListKey, dashboardList);
					break;
				}
			}
		}
		//Save the updated dashbord configuration 
		saveDashboard(binder, scope, dashboard);
	}

	private void moveComponent(ActionRequest request, Binder binder, String scope, String direction) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binder.getId());
		Map ssDashboard = DashboardHelper.getDashboardMap(binder, userFolderProperties, userProperties, scope);

		Map dashboard = (Map)ssDashboard.get(WebKeys.DASHBOARD_MAP);

		//Get the dashboard component
		String dashboardListKey = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");

		if (!dashboardListKey.equals("") && ssDashboard.containsKey(dashboardListKey)) {
			List dashboardList = (List) ssDashboard.get(dashboardListKey);
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
					//Write the list back to the dashboard
					//  If the list is "local", this is the combined list.
					dashboard.put(dashboardListKey, dashboardList);
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
		if (dashboard == null) {
			dashboard = DashboardHelper.getNewDashboardMap();
		} else {
			//Make a copy of the dashboard so changes won't accidentally bleed through
			dashboard = new HashMap(dashboard);
		}
		return dashboard;
	}
	
	private void saveDashboards(Binder binder, Map ssDashboard) {
		//Save the updated dashbord configurations
		String scope = DashboardHelper.Local;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_LOCAL_MAP));
		scope = DashboardHelper.Global;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_GLOBAL_MAP));
		scope = DashboardHelper.Binder;
		saveDashboard(binder, scope, (Map)ssDashboard.get(WebKeys.DASHBOARD_BINDER_MAP));
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

