/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
/*
 * Created on Jun 8, 2005
 */
package org.kablink.teaming.portlet.widget_test;

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

import org.kablink.teaming.domain.Event;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.util.cal.Duration;
import org.springframework.web.portlet.ModelAndView;
import javax.servlet.jsp.PageContext;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class EventController extends SAbstractController {
  public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
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







