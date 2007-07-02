/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

public class ConfigureRolesController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("addBtn") && formData.containsKey("roleName")) {
			//Get the list of workAreaOperations to be added to this new role/function
			Set operations = new HashSet();
			Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
			while (itWorkAreaOperations.hasNext()) {
				WorkAreaOperation operation = (WorkAreaOperation) itWorkAreaOperations.next();
				if (formData.containsKey(operation.toString())) {
					operations.add(operation);
				}
			}
			getAdminModule().addFunction(PortletRequestUtils.getStringParameter(request, "roleName"), operations);
		
		} else if (formData.containsKey("modifyBtn") && formData.containsKey("roleId")) {
			//Get the function id from the form
			Long functionId = PortletRequestUtils.getLongParameter(request, "roleId");
			
			//Add the list of workAreaOperations that can be added to each function
			Map updates = new HashMap();
			Set operations = new HashSet();
			Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
			while (itWorkAreaOperations.hasNext()) {
				WorkAreaOperation operation = (WorkAreaOperation) itWorkAreaOperations.next();
				if (formData.containsKey(operation.toString())) {
					operations.add(operation);
				}
			}
			updates.put("operations", operations);
			getAdminModule().modifyFunction(functionId, updates);
		} else if (formData.containsKey("deleteBtn")) {
			//Get the function id from the form
			Long functionId = PortletRequestUtils.getLongParameter(request, "roleId");
			try {
				getAdminModule().deleteFunction(functionId);
			} catch (NotSupportedException ns) {
				response.setRenderParameter(WebKeys.EXCEPTION, ns.getLocalizedMessage());
			}
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		
		//Set up the role beans
		BinderHelper.buildAccessControlRoleBeans(this, model);
		
		return new ModelAndView("administration/configureRoles", model);
		
	}
}
