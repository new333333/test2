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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;

/**
 * Controller to handle ajax requests for the dashboard
 * @author Janet
 *
 */
public class DashboardAjaxController extends SAbstractControllerRetry {
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			String scope = PortletRequestUtils.getStringParameter(request, "_scope");
			if (Validator.isNull(scope)) scope = DashboardHelper.Local;
			String componentId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2);
			if (op.equals(WebKeys.OPERATION_SAVE_DASHBOARD_LAYOUT) && WebHelper.isMethodPost(request)) {
				//Save the order of the dashboard components
				String layout = PortletRequestUtils.getStringParameter(request, "dashboard_layout", "");
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				Binder binder = getBinderModule().getBinder(binderId);
				DashboardHelper.saveComponentOrder(layout, binder, scope);
			} else if (op.equals(WebKeys.OPERATION_SHOW_ALL_DASHBOARD_COMPONENTS)) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				getProfileModule().setUserProperty(null, binderId, 
							ObjectKeys.USER_PROPERTY_DASHBOARD_SHOW_ALL, Boolean.TRUE);
			} else if (op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				getProfileModule().setUserProperty(null, binderId, 
							ObjectKeys.USER_PROPERTY_DASHBOARD_SHOW_ALL, Boolean.FALSE);
			} else if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) && WebHelper.isMethodPost(request)) {
				if (Validator.isNotNull(componentId)) {
					Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
					Binder binder = getBinderModule().getBinder(binderId);
					DashboardHelper.showHideComponent(request, binder, componentId, scope, "hide");
				}
			} else if (op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT) && WebHelper.isMethodPost(request)) {
				if (Validator.isNotNull(componentId)) {
					Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
					Binder binder = getBinderModule().getBinder(binderId);
					DashboardHelper.showHideComponent(request, binder, componentId, scope, "show");
				}				
			} else if (op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT) && WebHelper.isMethodPost(request)) {
				if (Validator.isNotNull(componentId)) {
					Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
					Binder binder = getBinderModule().getBinder(binderId);
					DashboardHelper.deleteComponent(request, binder, componentId, scope);
				}
			}

		}
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);

			//Check for calls from "ss_fetch_url" (which don't output in xml format)
			if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT) ||
					op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_SEARCH_MORE) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_TEAM_MORE) ||
					op.equals(WebKeys.OPERATION_SHOW_ALL_DASHBOARD_COMPONENTS) ||
					op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} 
			if (op.equals(WebKeys.OPERATION_SAVE_DASHBOARD_LAYOUT)) {
				response.setContentType("text/json");
				model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.notLoggedIn");	
				return new ModelAndView("common/json_ajax_return", model);
			}
			response.setContentType("text/xml");			
			return new ModelAndView("forum/ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT)) {
			return ajaxGetDashboardComponent(request, response);
		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_SEARCH_MORE)) {
			return ajaxGetDashboardSearchMore(request, response);			
		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_TEAM_MORE)) {
			return ajaxGetDashboardTeamMore(request, response);
		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) ||
					op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT) ||
					op.equals(WebKeys.OPERATION_SHOW_ALL_DASHBOARD_COMPONENTS) ||
					op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) {
			return new ModelAndView("forum/fetch_url_return");
		} else if (op.equals(WebKeys.OPERATION_SAVE_DASHBOARD_LAYOUT)) {						
			response.setContentType("text/json");
			return new ModelAndView("common/json_ajax_return");			
		} else {
			response.setContentType("text/xml");
			return new ModelAndView("forum/ajax_return");
		}
	}
	private ModelAndView ajaxGetDashboardComponent(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String componentId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2);
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		if (Validator.isNotNull(componentId)) {
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			String scope = PortletRequestUtils.getStringParameter(request, "_scope");
			if (Validator.isNull(scope)) scope = DashboardHelper.Local;
			DashboardHelper.getDashboardMap(binder, getProfileModule().getUserProperties(null).getProperties(), 
					model, scope, componentId, false);
		}		
		return new ModelAndView("definition_elements/view_dashboard_component", model);
	}
	private ModelAndView ajaxGetDashboardSearchMore(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String componentId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2);
		model.put(WebKeys.DIV_ID, PortletRequestUtils.getStringParameter(request, WebKeys.URL_DIV_ID, ""));
		model.put(WebKeys.PAGE_SIZE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_SIZE, "10"));
		model.put(WebKeys.PAGE_NUMBER, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0"));
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
	
		if (Validator.isNotNull(componentId)) {
			String scope = PortletRequestUtils.getStringParameter(request, "_scope");
			if (Validator.isNull(scope)) {
				if (componentId.contains("_")) scope = componentId.split("_")[0];
			}
			if (!DashboardHelper.Portlet.equals(scope)) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				User user = RequestContextHolder.getRequestContext().getUser();
				if (Validator.isNull(scope)) scope = DashboardHelper.Local;
				DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId, false);
			} else {
				String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID);				
				DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
				model.put(WebKeys.DASHBOARD_PORTLET, dashboard);
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(dashboard, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, false);
				
			}
		}
		String view;
		String displayType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DISPLAY_TYPE, "search");
		if (displayType.equals(WebKeys.DISPLAY_STYLE_SEARCH)) view = "dashboard/search_view2";
		else if (displayType.equals(WebKeys.DISPLAY_STYLE_GALLERY)) view = "dashboard/gallery_view2";
		else if (displayType.equals(WebKeys.DISPLAY_STYLE_BLOG)) view = "dashboard/blog_view2";
		else if (displayType.equals(WebKeys.DISPLAY_STYLE_GUESTBOOK)) view = "dashboard/guestbook_view2";
		else if (displayType.equals(WebKeys.DISPLAY_STYLE_TASK)) view = "dashboard/task_view2";
		else if (displayType.equals("comments")) view = "dashboard/comments_view2";
		else view = "dashboard/search_view2";
			return new ModelAndView(view, model);
	}
	
	private ModelAndView ajaxGetDashboardTeamMore(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String componentId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2);
		model.put(WebKeys.DIV_ID, PortletRequestUtils.getStringParameter(request, WebKeys.URL_DIV_ID, ""));
		model.put(WebKeys.PAGE_SIZE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_SIZE, "10"));
		model.put(WebKeys.PAGE_NUMBER, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0"));
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, componentId);
	
		if (Validator.isNotNull(componentId)) {
			String scope = PortletRequestUtils.getStringParameter(request, "_scope", null);
			if (Validator.isNull(scope)) {
				if (componentId.contains("_")) scope = componentId.split("_")[0];
			}
			if (Validator.isNull(scope)) scope = DashboardHelper.Local;
			if (!DashboardHelper.Portlet.equals(scope)) {
				Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				DashboardHelper.getDashboardMap(binder, getProfileModule().getUserProperties(null).getProperties(), 
							model, scope, componentId, false);
			} else {
				String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID);				
				DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
				model.put(WebKeys.DASHBOARD_PORTLET, dashboard);
				DashboardHelper.getDashboardMap(dashboard, 
					getProfileModule().getUserProperties(null).getProperties(),	model, false);
			}
		}
		String view = "dashboard/team_members_list_view";
		return new ModelAndView(view, model);
	}
		
}
