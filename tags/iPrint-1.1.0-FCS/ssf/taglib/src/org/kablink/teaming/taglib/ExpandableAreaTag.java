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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Peter Hurley
 *
 */
public class ExpandableAreaTag extends BodyTagSupport implements ParamAncestorTag {
	private String _bodyContent;
	private String title = "";
	private String titleClass = "ss_bold";
	private String toggleClass = "";
	private String action = "";
	private Boolean initOpen = false;
	private Map _values;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (_values == null) {
				_values = new HashMap();
			}
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/expandable_area/top.jsp");

			ServletRequest req = new DynamicServletRequest(httpReq);
			req.setAttribute("title", this.title);
			if (this._values.containsKey("title")) req.setAttribute("title", this._values.get("title"));
			req.setAttribute("titleClass", this.titleClass);
			req.setAttribute("toggleClass", this.toggleClass);
			req.setAttribute("initOpen", this.initOpen);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/expandable_area/bottom.jsp");
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("title", this.title);
			req.setAttribute("titleClass", this.titleClass);
			req.setAttribute("toggleClass", this.toggleClass);
			req.setAttribute("initOpen", this.initOpen);

			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.title = "";
			this.titleClass = "ss_bold";
			this.toggleClass = "";
			this.action = "";
			this.initOpen = false;
			this._values = null;
		}
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public void setTitleClass(String titleClass) {
	    this.titleClass = titleClass;
	}

	public void setToggleClass(String toggleClass) {
	    this.toggleClass = toggleClass;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public void setInitOpen(Boolean initOpen) {
	    this.initOpen = initOpen;
	}

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new HashMap();
		}
		_values.put(name, value);
	}

}


