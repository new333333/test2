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
package com.sitescape.team.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;


/**
 * @author Peter Hurley
 *
 */
public class ReloadOpenerController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY, "");
		String inIFrameAddEntry = PortletRequestUtils.getStringParameter(request, WebKeys.IN_IFRAME_ADD_ENTRY, "");
		if (!blogReply.equals("")) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ID, "");
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
			String blogReplyCount = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY_COUNT, "");
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
		
	    return new ModelAndView("forum/reload_opener", model);
	}

}
