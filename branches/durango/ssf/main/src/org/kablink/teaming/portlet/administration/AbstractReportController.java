/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portlet.binder.AbstractBinderController;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;


public abstract class AbstractReportController extends  AbstractBinderController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId());
		String binderType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_TYPE, null);	
		if (binderType != null && formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			setupViewBinder(response, binderId, binderType);
		}
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map formData = request.getParameterMap();
		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(this, request, response, model);
		populateModel(request, model);
		return new ModelAndView(chooseView(formData), model);
	}
	
	protected void populateModel(RenderRequest request, Map model)
	{
		Date startDate = null;
		Date endDate = null;
		try {
			MapInputData data = new MapInputData(request.getParameterMap());
			startDate = data.getDateValue(WebKeys.URL_START_DATE);
			endDate = data.getDateValue(WebKeys.URL_END_DATE);
		} catch(Exception e) {
		}
		if(startDate == null) { 
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime();
		}
		if(endDate == null) { endDate = new Date(); }
		model.put(WebKeys.REPORT_START_DATE, startDate);
		model.put(WebKeys.REPORT_END_DATE, endDate);
	}
	
	abstract protected String chooseView(Map formData);
}
