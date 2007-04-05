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
import javax.servlet.jsp.tagext.BodyTagSupport;

public class SlidingTableColumnTag extends BodyTagSupport {
	private String _width;
	private String _bodyContent;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString().trim();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			SlidingTableColumnAncestorTag slidingTableColumnAncestor =
				(SlidingTableColumnAncestorTag)findAncestorWithClass(
					this, SlidingTableColumnAncestorTag.class);

			if (slidingTableColumnAncestor == null) {
				throw new JspTagException();
			}

			//Add this column to the current row
			if (_width == null) _width = "";
			slidingTableColumnAncestor.addColumn(_bodyContent, _width);

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
		}
	}
	public void setWidth(String width) {
		_width = width;
	}

}