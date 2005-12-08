package com.sitescape.ef.portletadapter.taglib;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.web.WebKeys;

public class DefineObjectsTag2 extends TagSupport {

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