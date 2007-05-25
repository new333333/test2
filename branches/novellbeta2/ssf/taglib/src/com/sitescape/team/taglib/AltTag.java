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
import java.util.Locale;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;


/**
 * @author Peter Hurley
 *
 */
public class AltTag extends BodyTagSupport implements ParamAncestorTag {
    private String tag;
    private String text;
    private Boolean checkIfTag;
	private List _values;
	private String attName = "ALT";
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		User user = null;
		boolean isAccessible = false;
		if (rc != null) user = rc.getUser();
		if (user != null) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				isAccessible = true;
			}
		}
		if (this.checkIfTag == null) this.checkIfTag = false;
		try {
			if (isAccessible) {
				JspWriter jspOut = pageContext.getOut();
				StringBuffer sb = new StringBuffer();
				if (_values == null) {
					_values = new ArrayList();
				}
				if (tag != null && this.checkIfTag) {
					//This is a request to see if the tag itself is text or a tag
					sb.append(NLT.getDef(this.tag));
				} else if (tag != null && this.text == null) {
					sb.append(NLT.get(this.tag, this._values.toArray()));
				} else if (tag == null && this.text != null) {
					sb.append(this.text);
				} else {
					if (tag != null) sb.append(NLT.get(this.tag, this._values.toArray(), this.text));
				}
				jspOut.print(attName + "=\"" + sb.toString() + "\" ");
			}
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_values = null;
			checkIfTag = null;
			text = null;
			tag = null;
			attName = "ALT";
		}
	    
		return EVAL_PAGE;
	}
	
	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setText(String text) {
	    this.text = text;
	}

	public void setCheckIfTag(Boolean value) {
	    this.checkIfTag = value;
	}

	public void setAttName(String attName) {
	    this.attName = attName;
	}

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new ArrayList();
		}
		if (name.equals(WebKeys.NLT_VALUE)) _values.add(value);
	}
}