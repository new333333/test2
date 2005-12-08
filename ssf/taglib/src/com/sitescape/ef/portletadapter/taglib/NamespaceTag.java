package com.sitescape.ef.portletadapter.taglib;

import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.web.WebKeys;

public class NamespaceTag extends TagSupport {

	public int doStartTag() throws JspTagException {
		try {
			ServletRequest req = pageContext.getRequest();

			RenderResponse renderResponse = (RenderResponse)req.getAttribute(
				WebKeys.JAVAX_PORTLET_RESPONSE);

			pageContext.getOut().print(renderResponse.getNamespace());
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

}