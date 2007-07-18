package com.sitescape.team.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.module.license.LicenseChecker;


public class LicenseOutOfComplianceTag extends BodyTagSupport {
	
	public int doStartTag() throws JspTagException {
		try {
			if(! LicenseChecker.inCompliance()){
				return EVAL_BODY_INCLUDE;
			}
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		
		return EVAL_PAGE;
	}
	
	public int doAfterBody() {
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		return EVAL_BODY_INCLUDE;
	}

}
