/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;


public class SlidingTableRowTag extends BodyTagSupport implements SlidingTableColumnAncestorTag {
	private String _id = "";
	private String _style = "";
	private String _oddStyle = "";
	private String _evenStyle = "";
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
			slidingTableRowAncestor.addRow(_id, _columns, _headerRow, _style, _oddStyle, _evenStyle);

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_columns = null;
			_id = "";
			_style = "";
			_oddStyle = "";
			_evenStyle = "";
			_headerRow = null;
		}
	}
	
	public void addColumn (String text, String width, String style) {
		if (_columns == null) {
			_columns = new ArrayList();
		}
		if (text == null) text = "";
		if (_style == null) _style = "";
		if (width == null) width = "";
		Map column = new HashMap();
		column.put("text", text);
		column.put("width", width);
		column.put("style", style);
		_columns.add(column);
	}

	public void setId(String id) {
		_id = id;
	}
	public void setStyle(String style) {
		_style = style;
	}
	public void setOddStyle(String style) {
		_oddStyle = style;
	}
	public void setEvenStyle(String style) {
		_evenStyle = style;
	}
	public void setHeaderRow(Boolean headerRow) {
		_headerRow = headerRow;
	}
}