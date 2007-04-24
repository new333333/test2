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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;


public class SlidingTableRowTag extends BodyTagSupport implements SlidingTableColumnAncestorTag {
	private String _id = "";
	private Boolean _headerRow;
	private List _columns;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			SlidingTableRowAncestorTag slidingTableRowAncestor =
				(SlidingTableRowAncestorTag)findAncestorWithClass(
					this, SlidingTableRowAncestorTag.class);

			if (slidingTableRowAncestor == null) {
				throw new JspTagException();
			}

			//Add the built up row to the table
			if (_headerRow == null) _headerRow = new Boolean(false);
			if (_id == null) _id = "";
			slidingTableRowAncestor.addRow(_id, _columns, _headerRow);

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_columns = null;
			_id = "";
			_headerRow = null;
		}
	}
	
	public void addColumn (String text, String width) {
		if (_columns == null) {
			_columns = new ArrayList();
		}
		if (text == null) text = "";
		if (width == null) width = "";
		Map column = new HashMap();
		column.put("text", text);
		column.put("width", width);
		_columns.add(column);
	}

	public void setId(String id) {
		_id = id;
	}
	public void setHeaderRow(Boolean headerRow) {
		_headerRow = headerRow;
	}
}