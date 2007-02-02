package com.sitescape.team.portlet.widget_test;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.util.Validator;

public class WidgetTestController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = (String)prefs.getValue(WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			url = response.createRenderURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
			prefs.store();
		}
		//Dispatch the the desired jsp
		String action = request.getParameter("action");
		
		String path = "widget_test/widget_test_menu";
		if (action != null) {
			if (action.equals("tree")) {
				path = "widget_test/view_tree";
			} else if (action.equals("date")) {
				path = "widget_test/view_datepicker";
			} else if (action.equals("event")) {
				path = "widget_test/view_eventtester";
			} else if (action.equals("time")) {
				path = "widget_test/view_timepicker";
			} else if (action.equals("htmledit")) {
				path = "widget_test/view_htmleditor";
			}
		}
		
		// Dispatch to the desired operation
		return new ModelAndView(path, model);
	}

}
