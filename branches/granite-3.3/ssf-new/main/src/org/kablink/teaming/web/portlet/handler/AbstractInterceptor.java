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
package org.kablink.teaming.web.portlet.handler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.portlet.ModelAndView;

public class AbstractInterceptor implements HandlerInterceptor {

	public boolean preHandleAction(ActionRequest request, ActionResponse response, Object handler)
	    throws Exception {
		return true;
	}

	public void afterActionCompletion(
			ActionRequest request, ActionResponse response, Object handler, Exception ex)
	    throws Exception {
		
	}

	public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
	    throws Exception {
		return true;
	}

	public void postHandleRender(
			RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		
	}

	public void afterRenderCompletion(
			RenderRequest request, RenderResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

	public void afterEventCompletion(EventRequest arg0, EventResponse arg1,
			Object arg2, Exception arg3) throws Exception {

	}

	public void afterResourceCompletion(ResourceRequest arg0,
			ResourceResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	public void postHandleResource(ResourceRequest arg0, ResourceResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {

	}

	public boolean preHandleEvent(EventRequest arg0, EventResponse arg1,
			Object arg2) throws Exception {
		return true;
	}

	public boolean preHandleResource(ResourceRequest arg0,
			ResourceResponse arg1, Object arg2) throws Exception {
		return true;
	}

}
