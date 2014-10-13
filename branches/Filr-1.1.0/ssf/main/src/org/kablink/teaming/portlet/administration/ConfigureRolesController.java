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
import java.util.ArrayList;
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionExistsException;
import org.kablink.teaming.security.function.RemoteAddrCondition;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WorkAreaHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.ModelAndView;


public class ConfigureRolesController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Binder topBinder = getWorkspaceModule().getTopWorkspace();
		if ((formData.containsKey("addBtn") && formData.containsKey("roleName")) && WebHelper.isMethodPost(request)) {
			//Get the list of workAreaOperations to be added to this new role/function
			Set operations = new HashSet();
			Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
			while (itWorkAreaOperations.hasNext()) {
				WorkAreaOperation operation = (WorkAreaOperation) itWorkAreaOperations.next();
				if (formData.containsKey(operation.toString())) {
					operations.add(operation);
				}
			}
			//Build the list of any additional conditions
			List<ConditionalClause> conditions = new ArrayList<ConditionalClause>();
			//Currently, we only allow one condition to be selected
			String roleCondition = PortletRequestUtils.getStringParameter(request, "roleCondition", "");
			if (!roleCondition.equals("")) {
				try {
					Long roleConditionId = Long.valueOf(roleCondition);
					Condition condition = getAdminModule().getFunctionCondition(roleConditionId);
					if (condition != null) {
						//Since only one condition can be added, make it a "MUST"
						ConditionalClause cc = new ConditionalClause(condition, ConditionalClause.Meet.MUST);
						conditions.add(cc);
					}
				} catch(Exception e) {}
			}
			String roleName = "";
			String roleScope = ObjectKeys.ROLE_TYPE_BINDER;
			try {
				roleName = PortletRequestUtils.getStringParameter(request, "roleName").trim();
				roleScope = PortletRequestUtils.getStringParameter(request, "roleScope").trim();
				if (!roleName.equals(""))
					getAdminModule().addFunction(roleName, operations, roleScope, conditions);
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
		
		} else if (formData.containsKey("modifyBtn") && formData.containsKey("roleId") && WebHelper.isMethodPost(request)) {
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
			//Build the list of any additional conditions
			List<ConditionalClause> conditions = new ArrayList<ConditionalClause>();
			//Currently, we only allow one condition to be selected
			String roleCondition = PortletRequestUtils.getStringParameter(request, "roleCondition", "");
			if (!roleCondition.equals("")) {
				try {
					Long roleConditionId = Long.valueOf(roleCondition);
					Condition condition = getAdminModule().getFunctionCondition(roleConditionId);
					if (condition != null) {
						//Since only one condition can be added, make it a "MUST"
						ConditionalClause cc = new ConditionalClause(condition, ConditionalClause.Meet.MUST);
						conditions.add(cc);
					}
				} catch(Exception e) {}
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
			updates.put("conditionalClauses", conditions);
			try {
				getAdminModule().modifyFunction(functionId, updates);
			} catch (FunctionExistsException ns) {
				String[] args = new String[1];
				args[0] = roleName;
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.nameAlreadyExists", args));
			} catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, NLT.get("errorcode.role.illegalCharInName"));
			}
		} else if (formData.containsKey("deleteBtn") && WebHelper.isMethodPost(request)) {
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
		} else if (formData.containsKey("addCondition") && WebHelper.isMethodPost(request)) {
			//Define a new condition
			String title = PortletRequestUtils.getStringParameter(request, "title", "???").trim();
			String description = PortletRequestUtils.getStringParameter(request, "description", "", false).trim();
			List<ConditionalClause> conditions = new ArrayList<ConditionalClause>();
			List<String> includeAddressExpressions = new ArrayList<String>();
			List<String> excludeAddressExpressions = new ArrayList<String>();
			Iterator itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String) itFormData.next();
				if (key.startsWith("ipAddressCondition")) {
					String conditionId = key.substring("ipAddressCondition".length());
					if (formData.containsKey("ipAddressAccessCondition"+conditionId)) {
						String expression = PortletRequestUtils.getStringParameter(request, "ipAddressCondition"+conditionId, "");
						String allowDeny = PortletRequestUtils.getStringParameter(request, "ipAddressAccessCondition"+conditionId, "");
						if (!expression.equals("")) {
							if (allowDeny.equals("deny")) {
								excludeAddressExpressions.add(expression);
							} else {
								includeAddressExpressions.add(expression);
							}
						}
					}
				}
			}
			String[] includeExp = new String[includeAddressExpressions.size()];
			String[] excludeExp = new String[excludeAddressExpressions.size()];			
			RemoteAddrCondition rac = new RemoteAddrCondition(title, includeAddressExpressions.toArray(includeExp), 
					excludeAddressExpressions.toArray(excludeExp));
			rac.setDescription(new Description(description));
			getAdminModule().addFunctionCondition(rac);
			
		} else if (formData.containsKey("modifyCondition") && WebHelper.isMethodPost(request)) {
			//Define a new condition
			Long id = PortletRequestUtils.getRequiredLongParameter(request, "id");
			String title = PortletRequestUtils.getStringParameter(request, "title", "???").trim();
			String description = PortletRequestUtils.getStringParameter(request, "description", "", false).trim();
			List<ConditionalClause> conditions = new ArrayList<ConditionalClause>();
			List<String> includeAddressExpressions = new ArrayList<String>();
			List<String> excludeAddressExpressions = new ArrayList<String>();
			Iterator itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String) itFormData.next();
				if (key.startsWith("ipAddressCondition")) {
					String conditionId = key.substring("ipAddressCondition".length());
					if (formData.containsKey("ipAddressAccessCondition"+conditionId)) {
						String expression = PortletRequestUtils.getStringParameter(request, "ipAddressCondition"+conditionId, "");
						String allowDeny = PortletRequestUtils.getStringParameter(request, "ipAddressAccessCondition"+conditionId, "");
						if (!expression.equals("")) {
							if (allowDeny.equals("deny")) {
								excludeAddressExpressions.add(expression);
							} else {
								includeAddressExpressions.add(expression);
							}
						}
					}
				}
			}
			String[] includeExp = new String[includeAddressExpressions.size()];
			String[] excludeExp = new String[excludeAddressExpressions.size()];
			Condition cond = getAdminModule().getFunctionCondition(id);
			if (cond instanceof RemoteAddrCondition) {
				RemoteAddrCondition rac = (RemoteAddrCondition) cond;
				rac.setTitle(title);
				rac.setDescription(new Description(description));
				rac.setIncludeAddressExpressions(includeAddressExpressions.toArray(includeExp));
				rac.setExcludeAddressExpressions(excludeAddressExpressions.toArray(excludeExp));
				getAdminModule().modifyFunctionCondition(rac);
			}
			
		} else if (formData.containsKey("deleteCondition") && WebHelper.isMethodPost(request)) {
			//Delete a condition
			Long id = PortletRequestUtils.getLongParameter(request, "conditionIdToBeDeleted");
			if (id != null) {
				try {
					getAdminModule().deleteFunctionCondition(id);
				} catch(DataIntegrityViolationException e) {
					//There must be a role that is still using this condition
					response.setRenderParameter(WebKeys.ERROR_MESSAGE, NLT.get("error.deletingCondition"));
				}
			}
			
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Map model = new HashMap();
		model.put(WebKeys.ERROR_MESSAGE, request.getParameter(WebKeys.ERROR_MESSAGE));
		
		if (op.equals("defineConditions") || formData.containsKey("addCondition") || 
				formData.containsKey("modifyCondition") || formData.containsKey("deleteCondition")) {
			WorkAreaHelper.buildRoleConditionBeans(this, model);
			return new ModelAndView("administration/configureRoleCondition", model);
		} else {
			model.put(WebKeys.ROLE_USERS, request.getParameter(WebKeys.ROLE_USERS));
			model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
			//Set up the role beans
			WorkAreaHelper.buildAccessControlRoleBeans(this, model, false);
			return new ModelAndView("administration/configureRoles", model);
		}
		
	}
}
