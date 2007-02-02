package com.sitescape.ef.portlet.administration;
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

import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
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
		//Add the list of existing functions for this zone
		model.put(WebKeys.FUNCTIONS, getAdminModule().getFunctions());
		
		//Add the list of workAreaOperations that can be added to each function
		Map operations = new HashMap();
		Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
		while (itWorkAreaOperations.hasNext()) {
			String operationName = (String) ((WorkAreaOperation) itWorkAreaOperations.next()).toString();
			operations.put(operationName, NLT.get("workarea_operation." + operationName));
		}
		model.put("ssWorkAreaOperations", operations);
		
		return new ModelAndView("administration/configureRoles", model);
		
	}
}
