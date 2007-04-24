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

public class EmployeeDeleteController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
	    // Get the id and delete it
	    Integer id = new Integer(request.getParameter("employee"));
	    getEmployeeModule().deleteEmployee(id);

	    // Set the action parameter to go to the default view
		response.setRenderParameter("action","employees");
	}

}
