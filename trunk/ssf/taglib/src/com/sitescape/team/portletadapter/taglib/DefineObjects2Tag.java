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
package com.sitescape.team.portletadapter.taglib;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.web.WebKeys;

public class DefineObjects2Tag extends TagSupport {

	public int doStartTag() {
		ServletRequest req = pageContext.getRequest();

		PortletConfig portletConfig =
			(PortletConfig)req.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);

		if (portletConfig != null) {
			pageContext.setAttribute("portletConfig", portletConfig);
		}

		RenderRequest renderRequest =
			(RenderRequest)req.getAttribute(WebKeys.JAVAX_PORTLET_REQUEST);

		if (renderRequest != null) {
			pageContext.setAttribute("renderRequest", renderRequest);
		}

		RenderResponse renderResponse =
			(RenderResponse)req.getAttribute(WebKeys.JAVAX_PORTLET_RESPONSE);

		if (renderResponse != null) {
			pageContext.setAttribute("renderResponse", renderResponse);
		}

		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

}