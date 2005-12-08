package com.sitescape.ef.portlet.widget_test;

import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.web.portlet.SAbstractController;

public class TimepickerController extends SAbstractController {
  public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
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
