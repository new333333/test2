package com.sitescape.ef.portlet.sample;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;

public class EmployeesController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		// To test cross context session sharing: 
		if(1 == 2) {
			String loginName = request.getRemoteUser();
			PortletSession pses = request.getPortletSession();
			pses.setAttribute("set-by-employees", "hello", PortletSession.APPLICATION_SCOPE);
			
			System.out.println("*** EmployeesController login name: " + loginName);
			System.out.println("*** EmployeesController session id: " + 
					request.getPortletSession().getId());
			System.out.println("*** EmployeesController set-by-main-servlet: " + 
					pses.getAttribute("set-by-main-servlet", PortletSession.APPLICATION_SCOPE));
			System.out.println("*** EmployeesController set-by-portlet-adapter: " + pses.getAttribute("set-by-portlet-adapter", PortletSession.APPLICATION_SCOPE));
			System.out.println("*** EmployeesController set-by-download-file: " + pses.getAttribute("set-by-download-file", PortletSession.APPLICATION_SCOPE));
		}

		// test ends:
		
		// Get the list of all employees from the business tier 
		// (via EmployeeModule) and create a ModelAndView datastructure
		// that the Spring's PortalMVC expects. 
		return new ModelAndView("sample/employeesView", "employees", 
				getEmployeeModule().getAllEmployees());
	}

}
