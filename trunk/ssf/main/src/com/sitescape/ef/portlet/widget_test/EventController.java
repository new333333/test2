/*
 * Created on Jun 8, 2005
 */
package com.sitescape.ef.portlet.widget_test;

/**
 * @author billmers
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.web.portlet.SAbstractController;
import javax.servlet.jsp.PageContext;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.domain.Event;
import com.sitescape.util.cal.Duration;

public class EventController extends SAbstractController {
  public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
       throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
  }

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();

	    
      Calendar now = new GregorianCalendar();
      Duration dur = new Duration(1, 30, 0);
      Event ev = new Event(now, dur, Event.WEEKLY);
      ev.setCount(5);
      String foo = new String("event1");
//      Map model = new HashMap();
//      model.put("ss_param_id", foo);
//      ModelUtil.processModel(req,model);

      String path = "widget_test/view_eventtester";
      return new ModelAndView(path);

      
	}
}







