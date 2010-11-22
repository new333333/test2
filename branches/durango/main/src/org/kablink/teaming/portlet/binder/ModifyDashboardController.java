/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.binder;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Peter Hurley
 *
 */
public class ModifyDashboardController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);		
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");
		String scope = "";
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (componentScope.equals("")) componentScope = PortletRequestUtils.getStringParameter(request, "_componentScope", "");
		if (componentScope.equals("")) componentScope = DashboardHelper.Local;
		if (scope.equals("") && !componentScope.equals("")) {
			//The scope wasn't specified, so use the component scope for this operation
			scope = componentScope;
		}
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");

		//The following operations must be done by a form post
		if (WebHelper.isMethodPost(request)) {
			if (formData.containsKey("set_title")) {
				DashboardHelper.setTitle(request, binder, scope);
				if (returnView.equals("binder")) setupViewBinder(response, binder);
			} else if (formData.containsKey("add_wideTop")) {
				componentId = DashboardHelper.addComponent(request, binder, Dashboard.WIDE_TOP, scope);
				response.setRenderParameter("_componentId", componentId);
				response.setRenderParameter("_dashboardList", Dashboard.WIDE_TOP);
			} else if (formData.containsKey("add_narrowFixed")) {
				componentId = DashboardHelper.addComponent(request, binder, Dashboard.NARROW_FIXED, scope);
				response.setRenderParameter("_componentId", componentId);
				response.setRenderParameter("_dashboardList", Dashboard.NARROW_FIXED);
			} else if (formData.containsKey("add_narrowVariable")) {
				componentId = DashboardHelper.addComponent(request, binder, Dashboard.NARROW_VARIABLE, scope);
				response.setRenderParameter("_componentId", componentId);
				response.setRenderParameter("_dashboardList", Dashboard.NARROW_VARIABLE);
			} else if (formData.containsKey("add_wideBottom")) {
				componentId = DashboardHelper.addComponent(request, binder, Dashboard.WIDE_BOTTOM, scope);
				response.setRenderParameter("_componentId", componentId);
				response.setRenderParameter("_dashboardList", Dashboard.WIDE_BOTTOM);
			} else if (formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x")) {
			} else if (formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
				DashboardHelper.saveComponentData(request, binder, scope);
			} else if (formData.containsKey("_deleteComponent")) {
				DashboardHelper.deleteComponent(request, binder, componentId, scope);
				if (returnView.equals("binder")) setupViewBinder(response, binder);
			}
		}
		if (operation.equals("_modifyComponentData")) {
		} else if (operation.equals("_show")) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "show");
			if (returnView.equals("binder")) setupViewBinder(response, binder);
		} else if (operation.equals("_hide")) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "hide");
			if (returnView.equals("binder")) setupViewBinder(response, binder);
		} else if (operation.equals("_moveUp")) {
			DashboardHelper.moveComponent(request, binder, scope, "up");
			if (returnView.equals("binder")) setupViewBinder(response, binder);
		} else if (operation.equals("_moveDown")) {
			DashboardHelper.moveComponent(request, binder, scope, "down");
			if (returnView.equals("binder")) setupViewBinder(response, binder);
		}
				
		if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			if (returnView.equals("binder")) setupViewBinder(response, binder);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String operation = PortletRequestUtils.getStringParameter(request, "_operation", "");

		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.INLINE_NO_IMAGE, "true");

        User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		if (!model.containsKey(WebKeys.SEEN_MAP)) 
			model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(user.getId()));

		String dashboardList = PortletRequestUtils.getStringParameter(request, "_dashboardList", "");
		String componentId = PortletRequestUtils.getStringParameter(request, "_componentId", "");
		String scope = "";
		if (scope.equals("")) scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		if (scope.equals("")) scope = DashboardHelper.Local;
		String componentScope = "";
		if (componentId.contains("_")) componentScope = componentId.split("_")[0];
		if (componentScope.equals("")) componentScope = PortletRequestUtils.getStringParameter(request, "_componentScope", "");
		if (componentScope.equals("")) componentScope = DashboardHelper.Local;
		if (scope.equals("") && !componentScope.equals("")) {
			//The scope wasn't specified, so use the component scope for this operation
			scope = componentScope;
		}
		String returnView = PortletRequestUtils.getStringParameter(request, "_returnView", "binder");

		String cId = "";
		if (operation.equals("_modifyComponentData") || 
				operation.equals("_deleteComponent") ||
				formData.containsKey("_modifyConfigData") || formData.containsKey("_modifyConfigData.x") ||
				formData.containsKey("_saveConfigData") || formData.containsKey("_saveConfigData.x")) {
			cId = componentId;
		}

		Map ssDashboard = DashboardHelper.getDashboardMap(binder, 
				getProfileModule().getUserProperties(user.getId()).getProperties(), model, scope, cId, true);
		
		DashboardHelper.checkDashboardLists(binder, ssDashboard);
		
		ssDashboard.put(WebKeys.DASHBOARD_LIST, dashboardList);
		ssDashboard.put(WebKeys.DASHBOARD_SCOPE, scope);
		ssDashboard.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
		ssDashboard.put(WebKeys.DASHBOARD_RETURN_VIEW, returnView);
		
		model.put(WebKeys.DASHBOARD, ssDashboard);
		String view = "binder/modify_dashboard";
		
		if (op.equals(WebKeys.OPERATION_SET_DASHBOARD_TITLE)) {
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

