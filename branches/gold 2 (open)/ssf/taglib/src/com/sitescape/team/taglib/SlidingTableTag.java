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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

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
		
		if (_id == null || _id.equals("")) _id = "ss_sTable";
		if (_parentId == null || _parentId.equals("")) _parentId = _id;
		if (_height == null || _height.equals("0")) _height = this.defaultTableHeight;

		// Output the table
		try {
			//Get the jsp to execute
			if (_jsp != null && !_jsp.equals("")) {
				// Output the sliding table using the requested jsp
				RequestDispatcher rd = httpReq.getRequestDispatcher(_jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				req.setAttribute("ss_slidingTableId", _id);
				req.setAttribute("ss_slidingTableParentId", _parentId);
				req.setAttribute("ss_slidingTableRows", _rows);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
				
			} else if (_type == null || _type.equals("") || (displayStyle != null && 
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
			_type = "";
			_jsp = "";
		}
	}

	public void addRow(String id, List columns, Boolean headerRow) {
		if (_rows == null) {
			_rows = new ArrayList();
		}
		if (headerRow == null) headerRow = new Boolean(false);
		if (columns == null) columns = new ArrayList();
		Map row = new HashMap();
		row.put("id", id);
		row.put("headerRow", headerRow);
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
	public void setType(String type) {
		_type = type;
	}
	public void setJsp(String jsp) {
		_jsp = jsp;
	}
}