package com.sitescape.ef.taglib;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.portletadapter.support.KeyNames;

public class IfAdapterTag extends TagSupport {

	public int doStartTag() throws JspException {
		
		ServletRequest req = pageContext.getRequest();

		ServletContext ctx = (ServletContext) req.getAttribute(KeyNames.CTX);
    	
		if(ctx == null) {
			// Indicates that the request is not served by the adapter framework.
			return SKIP_BODY;
		}
		else {
			// Indicates that the request is being served by the adapter framework.
			return EVAL_BODY_INCLUDE;
		}
	}
}
