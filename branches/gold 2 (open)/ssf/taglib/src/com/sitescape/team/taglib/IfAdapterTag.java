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

import com.sitescape.team.portletadapter.support.PortletAdapterUtil;

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
