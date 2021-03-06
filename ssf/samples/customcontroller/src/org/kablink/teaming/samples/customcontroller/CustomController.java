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
package org.kablink.teaming.samples.customcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;

public class CustomController  extends  SAbstractController {
	// The list of operations that this custom controller supports. There is only one currently, that is, ADD_LOG_MESSAGE.
	final static String OPERATION_ADD_LOG_MESSAGE = "add_log_message";
	
	// The list of parameters used by the ADD_LOG_MESSAGE operation.
	final static String ADD_LOG_MESSAGE__PARAMS_LOG_MESSAGE = "log_message";
	final static String ADD_LOG_MESSAGE__PARAMS_STATUS = "status";
	
	// The list of views used by the ADD_LOG_MESSAGE operation. There is only one currently.
	final static String ADD_LOG_MESSAGE__VIEW = "add_log_message/view";
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if(operation.equals(OPERATION_ADD_LOG_MESSAGE)) {
			action_addLogMessage(request, response);
		}
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if(operation.equals(OPERATION_ADD_LOG_MESSAGE)) {
			return render_addLogMessage(request, response);
		}
		response.getWriter().print("Hey, I don't understand what you're trying to do. Try adding some operation param to the URL like operation=add_log_message");
		return null;
	}
	
	void action_addLogMessage(ActionRequest request, ActionResponse response) {
		String status = "";
		String logMessage = PortletRequestUtils.getStringParameter(request, ADD_LOG_MESSAGE__PARAMS_LOG_MESSAGE, "");
		if(logMessage != null && logMessage.length() > 0) {
			logger.info(logMessage);
			status = "Message written to the log";
		}
		else {
			status = "Message was empty";
		}
		response.setRenderParameter(ADD_LOG_MESSAGE__PARAMS_STATUS, status);
	}
	
	ModelAndView render_addLogMessage(RenderRequest request, RenderResponse response) {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put(ADD_LOG_MESSAGE__PARAMS_STATUS, request.getParameter(ADD_LOG_MESSAGE__PARAMS_STATUS));
		return new ModelAndView(ADD_LOG_MESSAGE__VIEW, model);
	}
}
