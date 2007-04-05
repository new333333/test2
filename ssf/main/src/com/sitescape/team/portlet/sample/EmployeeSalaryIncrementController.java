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
package com.sitescape.team.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.sitescape.team.web.portlet.SAbstractController;

public class EmployeeSalaryIncrementController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, 
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

		// TEST moveEntry
		//getFolderModule().moveEntry(new Long(268), new Long(655), new Long(287));
		//getFolderModule().moveEntry(new Long(287), new Long(602), new Long(268));
	}
}
