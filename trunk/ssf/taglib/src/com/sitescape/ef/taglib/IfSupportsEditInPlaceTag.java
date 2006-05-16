package com.sitescape.ef.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.ssfs.util.SsfsUtil;

public class IfSupportsEditInPlaceTag extends TagSupport {

	private String relativeFilePath;
	
	public int doStartTag() throws JspException {

		if(relativeFilePath == null)
			throw new JspException("File path must be specified");
		
		if(SsfsUtil.supportsEditInPlace(relativeFilePath))
			return EVAL_BODY_INCLUDE;
		else
			return SKIP_BODY;
	}
	
	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}
}
