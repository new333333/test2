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
package com.sitescape.team.portlet.widget_test;

import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.portlet.SAbstractController;

public class TimepickerController extends SAbstractController {
  public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();

		Date initDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		Date initDate2 = cal.getTime();
		request.setAttribute("initDate", initDate);
		request.setAttribute("initDate2", initDate2);
		request.setAttribute("formData", formData);

		String path = "widget_test/view_timepicker";
		return new ModelAndView(path);
	}
}
