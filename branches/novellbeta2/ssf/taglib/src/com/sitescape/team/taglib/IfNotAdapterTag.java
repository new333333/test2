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

import javax.servlet.jsp.JspException;

public class IfNotAdapterTag extends IfAdapterTag {

	public int doStartTag() throws JspException {
		if(super.doStartTag() == EVAL_BODY_INCLUDE)
			return SKIP_BODY;
		else
			return EVAL_BODY_INCLUDE;
	}
}
