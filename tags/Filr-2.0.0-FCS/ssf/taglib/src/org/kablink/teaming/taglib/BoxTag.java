/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kablink.teaming.taglib;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;

/**
 * <a href="BoxTag.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan; ported by Peter Hurley
 * @version $Revision: 1.12 $
 *
 */
public class BoxTag extends BodyTagSupport implements ParamAncestorTag {

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			// Top
			String jsp = _top;
			if (jsp == null || jsp.equals("")) jsp = "/WEB-INF/jsp/box/box_top.jsp";
			if (_style != null && _style.equals("rounded")) jsp = "/WEB-INF/jsp/box/rounded_box_top.jsp";
			
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			if (_params != null) {
				req = new DynamicServletRequest(httpReq, _params);
			}
			else {
				req = pageContext.getRequest();
			}

			StringServletResponse res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			// Body

			pageContext.getOut().print(_bodyContent);

			// Bottom

			jsp = _bottom;
			if (jsp == null || jsp.equals("")) jsp = "/WEB-INF/jsp/box/box_bottom.jsp";
			if (_style != null && _style.equals("rounded")) jsp = "/WEB-INF/jsp/box/rounded_box_bottom.jsp";

			rd = httpReq.getRequestDispatcher(jsp);

			res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			if (_params != null) {
				_params.clear();
			}
			_top = "";
			_bottom = "";
			_style="";
		}
	}

	public void setStyle(String style) {
		_style = style;
	}

	public void setTop(String top) {
		_top = top;
	}

	public void setBottom(String bottom) {
		_bottom = bottom;
	}

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new HashMap();
		}

		String[] values = (String[])_params.get(name);

		if (values == null) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_params.put(name, values);
	}

	private String _top;
	private String _bottom;
	private String _style;
	private String _bodyContent;
	private Map _params;

}