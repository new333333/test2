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
package com.sitescape.team.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.util.SPropsUtil;

public class IfBrandIsTag extends TagSupport {

	private String brandToCheck;
	
	public int doStartTag() throws JspException {
		String currentBrand = SPropsUtil.getString("branding.prefix", "icecore"); 
		if (brandToCheck.equals(currentBrand)) {
			// Indicates that the current brand matches what was passed in
			return EVAL_BODY_INCLUDE;
		}
		else {
			// Indicates that the current brand does not match what was passed in
			return SKIP_BODY;
		}
	}
	
	
	public void setBrandToCheck(String brandToCheck) {
		this.brandToCheck = brandToCheck;
	}
	
}

