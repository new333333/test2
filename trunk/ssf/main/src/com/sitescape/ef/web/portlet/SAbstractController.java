package com.sitescape.ef.web.portlet;

import org.springframework.web.portlet.mvc.AbstractController;

import com.sitescape.ef.module.sample.EmployeeModule;

public abstract class SAbstractController extends AbstractController {

	private EmployeeModule employeeModule;
	
	public void setEmployeeModule(EmployeeModule employeeModule) {
		this.employeeModule = employeeModule;
	}
	
	protected EmployeeModule getEmployeeModule() {
		return employeeModule;
	}
}
