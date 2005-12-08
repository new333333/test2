package com.sitescape.ef.web.portlet;

import org.springframework.web.portlet.mvc.SimpleFormController;

import com.sitescape.ef.module.sample.EmployeeModule;

public abstract class SSimpleFormController extends SimpleFormController {

	private EmployeeModule employeeModule;
	
	public void setEmployeeModule(EmployeeModule employeeModule) {
		this.employeeModule = employeeModule;
	}
	protected EmployeeModule getEmployeeModule() {
		return employeeModule;
	}
}
