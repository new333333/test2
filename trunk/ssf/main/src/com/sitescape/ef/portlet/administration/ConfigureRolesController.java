package com.sitescape.ef.portlet.administration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
public class ConfigureRolesController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("addBtn") && formData.containsKey("roleName")) {
			//Get the list of workAreaOperations to be added to this new role/function
			Function function = new Function();
			function.setName(PortletRequestUtils.getStringParameter(request, "roleName"));
			Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
			while (itWorkAreaOperations.hasNext()) {
				WorkAreaOperation operation = (WorkAreaOperation) itWorkAreaOperations.next();
				if (formData.containsKey(operation.toString())) {
					function.addOperation(operation);
				}
			}
			getAdminModule().addFunction(function);
		
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
		
		} else {
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		//Add the list of existing functions for this zone
		model.put("ssFunctions", getAdminModule().getFunctions());
		
		//Add the list of workAreaOperations that can be added to each function
		Map operations = new HashMap();
		Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
		while (itWorkAreaOperations.hasNext()) {
			String operationName = (String) ((WorkAreaOperation) itWorkAreaOperations.next()).toString();
			operations.put(operationName, NLT.get(ObjectKeys.WORKAREA_OPERATION + "." + operationName));
		}
		model.put("ssWorkAreaOperations", operations);
		
		return new ModelAndView("administration/configureRoles", model);
		
	}
}
