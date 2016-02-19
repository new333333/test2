/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
 * <a href="FormTag.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Peter Hurley
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("unchecked")
public class FormTag extends BodyTagSupport implements ParamAncestorTag {
	private boolean ignore = false;
	private String title = "";
	private String titleTag = "";
	private String formClass = "";
	private String _bodyContent;
	private Map _params;

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			if (ignore) {
				pageContext.getOut().print(_bodyContent);
				return EVAL_PAGE;
			}
			
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			if (this.formClass.equals("")) this.formClass = "ss_form_wrap";
			
			// Top
			String jsp = "/WEB-INF/jsp/tag_jsps/form/top.jsp";
			
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = null;
			if (_params != null) {
				req = new DynamicServletRequest(httpReq, _params);
			}
			else {
				req = pageContext.getRequest();
			}
			StringServletResponse res = new StringServletResponse(httpRes);
			req.setAttribute("ss_title", this.title);
			req.setAttribute("ss_title_tag", this.titleTag);
			req.setAttribute("ss_formClass", this.formClass);
			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			// Bottom
			jsp = "/WEB-INF/jsp/tag_jsps/form/bottom.jsp";
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
			this.title = "";
			this.titleTag = "";
			this.formClass = "";
		}
	}

	public void setTitleTag(String titleTag) {
		this.titleTag = titleTag;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setIgnore(String ignoreString) {
		this.ignore = Boolean.parseBoolean(ignoreString);
	}

	public void setFormClass(String formClass) {
		this.formClass = formClass;
	}

	@Override
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
}
