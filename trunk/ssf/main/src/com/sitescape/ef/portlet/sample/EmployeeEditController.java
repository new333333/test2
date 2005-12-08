package com.sitescape.ef.portlet.sample;

import com.sitescape.ef.module.sample.Employee;
import com.sitescape.ef.web.portlet.SSimpleFormController;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

public class EmployeeEditController extends SSimpleFormController {

	public void onSubmitAction(ActionRequest request, ActionResponse response,
			Object command,	BindException errors) throws Exception {
		
		// This sample uses the corresponding model class (i.e., Employee) as 
		// a form datastructure. This may or may not be appropriate depending
		// on the usage context and the nature of the model class. In the case
		// where it isn't appropriate, controller developer can invent his own
		// form object (which is web-tier/controller specific) to capture the 
		// form input values, and then somehow map it to the model object. 
		// Or he can ditch the use of form object altogether and choose to 
		// manage the raw input parameters directly. In that case, the controller
		// must not extend SSimpleFormController. For more in-depth knowledge of
		// the UI framework stack, it would be necessary to understand the
		// classes from which SSimpleFormController extends. 
		
		Employee employee = (Employee) command;
	    Integer key;

    	try {
    	    key = new Integer(request.getParameter("employee"));
    	} catch (NumberFormatException ex) {
    	    key = null;
    	}
		
		if (key == null) {
			// We are adding a new employee. 
			getEmployeeModule().addEmployee(employee);
		} else {
			// We are updating an existing employee. 
			getEmployeeModule().updateEmployee(employee);
		}

	    // Set the action parameter to go to the default view
		response.setRenderParameter("action","employees");
	}
	
    protected Object formBackingObject(PortletRequest request)
    		throws Exception {
    	Employee employee;

    	try {
    	    Integer key = new Integer(request.getParameter("employee"));
    	    employee = getEmployeeModule().getEmployee(key);
    	} catch (NumberFormatException ex) {
    		// This happens when the same form is used to create a new employee. 
    		employee = new Employee();
    	}
    	
		return employee;
	}
    
	protected void initBinder(PortletRequest request, PortletRequestDataBinder binder)
			throws Exception {
		binder.setRequiredFields(new String[] {"firstName","lastName"});
		binder.setAllowedFields(new String[] {"firstName","lastName","salary"});
	}

	protected ModelAndView renderInvalidSubmit(RenderRequest request, RenderResponse response)
			throws Exception {
	    BindException errors = getErrorsForNewForm(request);
	    errors.reject("duplicateFormSubmission", "Duplicate form submission");
	    return showForm(request, response, errors);
	}
	
	protected void handleInvalidSubmit(ActionRequest request, ActionResponse response)
			throws Exception {
	}

}
