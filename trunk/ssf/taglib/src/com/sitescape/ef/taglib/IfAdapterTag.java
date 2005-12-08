package com.sitescape.ef.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.portletadapter.support.PortletAdapterUtil;

public class IfAdapterTag extends TagSupport {

	public int doStartTag() throws JspException {

		if(PortletAdapterUtil.isRunByAdapter((HttpServletRequest) pageContext.getRequest())) {
			// Indicates that the request is being served by the adapter framework.
			return EVAL_BODY_INCLUDE;
		}
		else {
			// Indicates that the request is not served by the adapter framework.
			return SKIP_BODY;
		}
	}
}
