/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.administration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.WeekendsAndHolidaysConfig;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.StringUtil;
import org.springframework.web.portlet.ModelAndView;

/**
 * Controller that handles the 'Configure Weekends and Holidays'
 * administration tool.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public class ConfigureScheduleController extends  SAbstractController {
	/**
	 * Called when the form is submitted to process the results.
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws Exception
	 */
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		// Is this the result of the Ok push button getting clicked?
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			// Yes!  Extract the holiday information... 
			String[]   holidaysMS = StringUtil.unpack(PortletRequestUtils.getStringParameter(request, WebKeys.URL_HOLIDAYS));
			List<Date> holidays   = new ArrayList<Date>();
			for (int i = 0; i < holidaysMS.length; i += 1) {
				holidays.add(new Date(Long.parseLong(holidaysMS[i])));
			}				
			
			// ...extract the weekend days information... 
			String[]      weekendDayInts = StringUtil.unpack(PortletRequestUtils.getStringParameter(request, WebKeys.URL_WEEKEND_DAYS));
			List<Integer> weekendDays    = new ArrayList<Integer>();
			for (int i = 0; i < weekendDayInts.length; i += 1) {
				weekendDays.add(new Integer(weekendDayInts[i]));
			}

			// ...and update the schedule.
			WeekendsAndHolidaysConfig weekendsAndHolidaysConfig = new WeekendsAndHolidaysConfig();
			weekendsAndHolidaysConfig.setHolidaysFromList(   holidays   );
			weekendsAndHolidaysConfig.setWeekendDaysFromList(weekendDays);			
			getAdminModule().setWeekendsAndHolidaysConfig(weekendsAndHolidaysConfig);
		}
		
		else {
			response.setRenderParameters(formData);
		}
		
	}

	/**
	 * Called as the page is rendering to supply the data need by the
	 * page.
	 * 
	 * @param request
	 * @param response
	 * 
	 * @return
	 * 
	 * throws Exception
	 */
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();

		WeekendsAndHolidaysConfig weekendsAndHolidaysConfig = getAdminModule().getWeekendsAndHolidaysConfig();
		model.put(WebKeys.SCHEDULE_CONFIG, weekendsAndHolidaysConfig);
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());

		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SCHEDULE, model);		
	}
}
