package com.sitescape.ef.taglib;

import javax.servlet.jsp.JspException;

public class IfNotAdapterTag extends IfAdapterTag {

	public int doStartTag() throws JspException {
		if(super.doStartTag() == EVAL_BODY_INCLUDE)
			return SKIP_BODY;
		else
			return EVAL_BODY_INCLUDE;
	}
}
