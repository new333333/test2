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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;


/**
 * @author Peter Hurley
 *
 * This tag is for use in debugging Jsp pages only. It outputs nothing.
 *  It merely provides a convientient place to add a breakpoint so that a bean could be examined.
 * 
 */
public class DebugTag extends BodyTagSupport implements ParamAncestorTag {
    private Object var;
	private List _values;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		try {
			Object test;
			if (this.var != null) test = this.var;
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
			_values = null;
		}
	    
		return EVAL_PAGE;
	}
	
	public void setVar(Object var) {
	    this.var = var;
	}

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new ArrayList();
		}
		if (name.equals(WebKeys.NLT_VALUE)) _values.add(value);
	}

}


