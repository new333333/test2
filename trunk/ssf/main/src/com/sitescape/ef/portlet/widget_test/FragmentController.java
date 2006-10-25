package com.sitescape.ef.portlet.widget_test;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;

public class FragmentController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {
		response.setRenderParameters(request.getParameterMap());
		//There is no action. Just go to the render phase
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		String path = "widget_test/view_fragment";
		String operation = request.getParameter("operation");
		
		if (operation != null) {
			if (operation.equals("viewFragment")) {
				path = "widget_test/view_fragment2";
			}
		}
		
		// Dispatch to the desired operation
		return new ModelAndView(path);
	}

}
