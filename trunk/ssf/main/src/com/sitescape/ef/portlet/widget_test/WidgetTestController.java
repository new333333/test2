package com.sitescape.ef.portlet.widget_test;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

public class WidgetTestController extends SAbstractController {

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
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
		return new ModelAndView(path);
	}

}
