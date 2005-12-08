package com.sitescape.ef.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.sitescape.ef.web.portlet.SAbstractController;

public class EmployeeSalaryIncrementController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, 
			ActionResponse response) throws Exception {
				
		/*
		System.out.println("*** Content Type: " + request.getResponseContentType());
		
		for(java.util.Enumeration e = request.getResponseContentTypes(); e.hasMoreElements();) {
			String type = (String) e.nextElement();
			System.out.println("$$$ Content Type: " + type);
		}*/

	    // Get the id of the employee
	    Integer id = new Integer(request.getParameter("employee"));
	    // Get the increment amount, which could be a negative number. 
	    Integer increment = new Integer(request.getParameter("increment"));
	    // Call business tier to change the employee's salary. 
	    getEmployeeModule().incrementSalary(id, increment);

	    // Set the action parameter to go to the default view
		response.setRenderParameter("action","employees");

	}
}
