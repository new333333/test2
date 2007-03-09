package com.sitescape.team.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.sitescape.team.web.portlet.SAbstractController;

public class EmployeeDeleteController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
	    // Get the id and delete it
	    Integer id = new Integer(request.getParameter("employee"));
	    getEmployeeModule().deleteEmployee(id);

	    // Set the action parameter to go to the default view
		response.setRenderParameter("action","employees");
	}

}
