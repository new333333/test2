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
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;



/**
 * @author Peter Hurley
 *
 */
public class ReloadOpenerController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		
		String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, binderId);

		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY, "");
		String inIFrameAddEntry = PortletRequestUtils.getStringParameter(request, WebKeys.IN_IFRAME_ADD_ENTRY, "");
		if (!blogReply.equals("")) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ID, "");
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
			String blogReplyCount = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY_COUNT, "");
			model.put(WebKeys.BINDER_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.BINDER_ID));
			model.put(WebKeys.ENTRY_ID, entryId);
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BLOG_REPLY_COUNT, blogReplyCount);
			return new ModelAndView("forum/reload_blog_reply", model);
		} else if (!inIFrameAddEntry.equals("")) {
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ID, "");
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.ENTRY_ID, entryId);
			return new ModelAndView("forum/reload_parent", model);
		} else {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			model.put(WebKeys.ENTRY_ID, entryId);
		}
		
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.ACTION_RELOAD_OPENER_PARENT)) return new ModelAndView("forum/reload_opener_parent", model);
		return new ModelAndView("forum/reload_opener", model);
	}

}
