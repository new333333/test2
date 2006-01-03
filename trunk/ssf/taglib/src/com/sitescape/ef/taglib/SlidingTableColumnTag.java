package com.sitescape.ef.taglib;

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