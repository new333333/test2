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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;

/**
 * <a href="BoxTag.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan; ported by Peter Hurley
 * @version $Revision: 1.12 $
 *
 */
public class SlidingTableTag extends BodyTagSupport implements SlidingTableRowAncestorTag {
	private List _rows;
	private String _id;
	private String _parentId;
	private String _folderId;
	private String _height;
	private String _tableStyle;
	private String _type;
	private String _jsp;
	private String displayStyle;
	
	private static String defaultTableHeight = "400";

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
		User user = RequestContextHolder.getRequestContext().getUser();
		displayStyle = user.getDisplayStyle();
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		
		if (_id == null || _id.equals("")) _id = "ss_sTable";
		if (_parentId == null || _parentId.equals("")) _parentId = _id;
		if (_height == null || _height.equals("0")) _height = this.defaultTableHeight;
		if (_tableStyle == null) _tableStyle = "";

		// Output the table
		try {
			//Get the jsp to execute
			if (_jsp != null && !_jsp.equals("") && !_jsp.contains("./") && !_jsp.contains(".\\")) {
				// Output the sliding table using the requested jsp
				RequestDispatcher rd = httpReq.getRequestDispatcher(_jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				req.setAttribute("ss_slidingTableId", _id);
				req.setAttribute("ss_slidingTableParentId", _parentId);
				req.setAttribute("ss_slidingTableRows", _rows);
				req.setAttribute("ss_slidingTableStyle", _tableStyle);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
				
			} else if (_type == null || _type.equals("") || _type.equals("fixed") || 
					(displayStyle != null && accessible_simple_ui &&
					displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE))) {
				String jspStart = "/WEB-INF/jsp/tag_jsps/sliding_table/table_start.jsp";
				String jspEnd = "/WEB-INF/jsp/tag_jsps/sliding_table/table_end.jsp";
				String jspRow = "/WEB-INF/jsp/tag_jsps/sliding_table/table_row.jsp";
				
				// Output the table top
				RequestDispatcher rd = httpReq.getRequestDispatcher(jspStart);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				req.setAttribute("ss_slidingTableId", _id);
				req.setAttribute("ss_slidingTableParentId", _parentId);
				req.setAttribute("ss_slidingTableStyle", _tableStyle);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
				
				// Output the rows
				Iterator itRows = _rows.iterator();
				while (itRows.hasNext()) {
					//Get the next row to output
					Map row = (Map) itRows.next();

					rd = httpReq.getRequestDispatcher(jspRow);
					req = new DynamicServletRequest(httpReq);
					res = new StringServletResponse(httpRes);
					req.setAttribute("ss_slidingTableRowId", row.get("id"));
					req.setAttribute("ss_slidingTableStyle", _tableStyle);
					req.setAttribute("ss_slidingTableRowStyle", row.get("style"));
					req.setAttribute("ss_slidingTableRowOddStyle", row.get("oddStyle"));
					req.setAttribute("ss_slidingTableRowEvenStyle", row.get("evenStyle"));
					req.setAttribute("ss_slidingTableHeaderRow", row.get("headerRow"));
					req.setAttribute("ss_slidingTableRowColumns", row.get("columns"));
					rd.include(req, res);
					pageContext.getOut().print(res.getString());
				}

				// Output the table bottom
				rd = httpReq.getRequestDispatcher(jspEnd);
				req = pageContext.getRequest();
				res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());

			} else if (_type != null && _type.equals("sliding")) {
				String jspStart = "/WEB-INF/jsp/tag_jsps/sliding_table/sliding_table.jsp";
				
				// Output the sliding table
				RequestDispatcher rd = httpReq.getRequestDispatcher(jspStart);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				req.setAttribute("ss_slidingTableId", _id);
				req.setAttribute("ss_slidingTableParentId", _parentId);
				req.setAttribute("ss_slidingTableRows", _rows);
				req.setAttribute("ss_slidingTableFolderId", _folderId);
				req.setAttribute("ss_slidingTableStyle", _tableStyle);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
				
			} else if (_type != null && _type.equals("sliding_scrolled")) {
				String jspStart = "/WEB-INF/jsp/tag_jsps/sliding_table/sliding_scrolled_table.jsp";
				
				// Output the sliding table
				RequestDispatcher rd = httpReq.getRequestDispatcher(jspStart);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				req.setAttribute("ss_slidingTableId", _id);
				req.setAttribute("ss_slidingTableParentId", _parentId);
				req.setAttribute("ss_slidingTableRows", _rows);
				req.setAttribute("ss_slidingTableFolderId", _folderId);
				req.setAttribute("ss_slidingTableScrollHeight", _height);
				req.setAttribute("ss_slidingTableStyle", _tableStyle);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			if (_rows != null) {
				_rows.clear();
			}
			_id = "";
			_parentId = ""; 
			_height = "";
			_tableStyle = "";
			_type = "";
			_jsp = "";
		}
	}

	public void addRow(String id, List columns, Boolean headerRow, String style, String oddStyle, String evenStyle) {
		if (_rows == null) {
			_rows = new ArrayList();
		}
		if (headerRow == null) headerRow = new Boolean(false);
		if (columns == null) columns = new ArrayList();
		Map row = new HashMap();
		row.put("id", id);
		row.put("headerRow", headerRow);
		row.put("style", style);
		row.put("oddStyle", oddStyle);
		row.put("evenStyle", evenStyle);
		row.put("columns", columns);

		_rows.add(row);
	}
	
	public void setId(String id) {
		_id = id;
	}
	public void setParentId(String parentId) {
		_parentId = parentId;
	}
	public void setFolderId(String folderId) {
		_folderId = folderId;
	}
	public void setHeight(String height) {
		_height = height;
	}
	public void setTableStyle(String tableStyle) {
		_tableStyle = tableStyle;
	}
	public void setType(String type) {
		_type = type;
	}
	public void setJsp(String jsp) {
		_jsp = jsp;
	}
}