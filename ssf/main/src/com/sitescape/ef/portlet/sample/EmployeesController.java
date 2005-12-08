package com.sitescape.ef.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;

public class EmployeesController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		// Get the list of all employees from the business tier 
		// (via EmployeeModule) and create a ModelAndView datastructure
		// that the Spring's PortalMVC expects. 
		return new ModelAndView("sample/employeesView", "employees", 
				getEmployeeModule().getAllEmployees());
	}

}
