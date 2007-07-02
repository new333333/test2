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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.ObjectKeys;

public class AppletFileName extends BodyTagSupport {

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		try {
			JspWriter jspOut = pageContext.getOut();
			jspOut.print(ObjectKeys.FILES_FROM_APPLET_FOR_BINDER);
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
	    
		return EVAL_PAGE;
	}
}