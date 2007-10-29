package com.sitescape.team.samples.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.portlet.SAbstractController;

/*
 * This sample controller does absolutely nothing useful other than merely
 * providing an empty skeleton.
 */
public class DumbController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {
		// See <module name>Module.java files in individual com.sitescape.team.module.<module name>
		// packages for a list of available methods (API) callable from controller.
		
		System.out.println("DumbController (action)");
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {		
		// See <module name>Module.java files in individual com.sitescape.team.module.<module name>
		// packages for a list of available methods (API) callable from controller.

		System.out.println("DumbController (render)");
		
		return new ModelAndView("sample/dumbView", null);
	}

}
