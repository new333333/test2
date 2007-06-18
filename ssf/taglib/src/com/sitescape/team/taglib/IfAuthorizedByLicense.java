package com.sitescape.team.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.license.LicenseChecker;

public class IfAuthorizedByLicense extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private String featureName;
	
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	
	public int doStartTag() throws JspException {

		if(LicenseChecker.isAuthorizedByLicense(featureName)) {
			return EVAL_BODY_INCLUDE;
		}
		else {
			return SKIP_BODY;
		}
	}
}
