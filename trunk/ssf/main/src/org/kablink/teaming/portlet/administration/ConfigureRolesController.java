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
package org.kablink.teaming.portlet.administration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionExistsException;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.WorkAreaHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.Validator;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.ModelAndView;


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
			String roleName = "";
			try {
				roleName = PortletRequestUtils.getStringParameter(request, "roleName").trim();
				if (!roleName.equals(""))
					getAdminModule().addFunction(roleName, operations);
				else
					throw new IllegalArgumentException(NLT.get("errorcode.role.mustHaveName"));
			} catch (FunctionExistsException ns) {
				String[] args = new String[1];
				args[0] = roleName;
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.nameAlreadyExists", args));
			} catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.illegalCharInName"));
			}
			catch (PortletRequestBindingException prbe) {
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.mustHaveName"));
			}
		
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
			String roleName = null;
			try {
				roleName = PortletRequestUtils.getStringParameter(request, "roleName");
			} catch (PortletRequestBindingException prbe) {
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.mustHaveName"));
			}
			if (!Validator.isNull(roleName)) {
				updates.put("name", roleName);
			}
			updates.put("operations", operations);
			try {
				getAdminModule().modifyFunction(functionId, updates);
			} catch (FunctionExistsException ns) {
				String[] args = new String[1];
				args[0] = roleName;
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.nameAlreadyExists", args));
			} catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.illegalCharInName"));
			}
		} else if (formData.containsKey("deleteBtn")) {
			//Get the function id from the form
			Long functionId = PortletRequestUtils.getLongParameter(request, "roleId");
			List result = getAdminModule().deleteFunction(functionId);
			if (result != null) {
				NotSupportedException ns;
				Object o = result.get(result.size() - 1);
				if(o instanceof Function){
					Function function = (Function) o;
					StringBuilder users = new StringBuilder();
					
					for(int i = 0; i < result.size() - 1; i++) {
						Long binderId = ((WorkAreaFunctionMembership)result.get(i)).getWorkAreaId();
						Binder binder = getBinderModule().getBinder(binderId);
						if(binder != null) {
							users.append(binder.getPathName() + ",");
						}
					}
					response.setRenderParameter(WebKeys.ROLE_USERS, users.toString());
					ns =  new NotSupportedException("errorcode.role.inuse", new Object[]{NLT.getDef(function.getName())});
				}
				else {
					ns =  new NotSupportedException("errorcode.role.inuse");
				}
				response.setRenderParameter(WebKeys.EXCEPTION, ns.getLocalizedMessage());
			}
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		model.put(WebKeys.ROLE_USERS, request.getParameter(WebKeys.ROLE_USERS));
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		
		//Set up the role beans
		WorkAreaHelper.buildAccessControlRoleBeans(this, model, false);
		
		return new ModelAndView("administration/configureRoles", model);
		
	}
}
