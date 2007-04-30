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

public class IfAccessibleTag extends TagSupport {

	public int doStartTag() throws JspException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		User user = null;
		boolean isAccessible = false;
		if (rc != null) user = rc.getUser();
		if (user != null) {
			if (user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				isAccessible = true;
			}
		}
		if (isAccessible) {
			// Indicates that the user is in accessible mode.
			return EVAL_BODY_INCLUDE;
		}
		else {
			// Indicates that the user is not in accessible mode.
			return SKIP_BODY;
		}
	}
}
