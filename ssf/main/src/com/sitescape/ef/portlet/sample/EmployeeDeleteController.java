package com.sitescape.ef.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.sitescape.ef.web.portlet.SAbstractController;

public class EmployeeDeleteController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
	    // Get the id and delete it
	    Integer id = new Integer(request.getParameter("employee"));
	    getEmployeeModule().deleteEmployee(id);

	    // Set the action parameter to go to the default view
		response.setRenderParameter("action","employees");
	}

}
