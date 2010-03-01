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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Peter Hurley
 *
 */
public class MenuTag extends BodyTagSupport implements ParamAncestorTag {
	private String _bodyContent;
	private String title = null;
	private String titleId = "";
	private String titleClass = "";
	private String menuClass = "";
	private String menuWidth = "";
	private String openStyle = "slide_down";     //slide_down, slide_right, immediate, popup
	private String anchor = "";
	private String offsetTop = "8";
	private String offsetLeft = "4";
	private String menuImage = "";
	private Map _params;
    
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

			boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
			RequestContext rc = RequestContextHolder.getRequestContext();
			User user = null;
			Boolean isAccessible = Boolean.FALSE;
			if (rc != null) user = rc.getUser();
			if (user != null) {
				if (user.getDisplayStyle() != null && accessible_simple_ui &&
						user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			//Output the start of the area
			RequestDispatcher rd;
			
			if (isAccessible) rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/top_accessible.jsp");
			else rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/top.jsp");
			
			if (this._params == null) this._params = new HashMap();

			if (this.title != null) _params.put("title", new String[] {this.title});
			if (!_params.containsKey("title")) _params.put("title", "---");
			_params.put("titleId", new String[] {this.titleId});
			_params.put("titleClass", new String[] {this.titleClass});
			_params.put("menuClass", new String[] {this.menuClass});
			_params.put("menuWidth", new String[] {this.menuWidth});
			_params.put("openStyle", new String[] {this.openStyle});
			_params.put("anchor", new String[] {this.anchor});
			_params.put("offsetTop", new String[] {this.offsetTop});
			_params.put("offsetLeft", new String[] {this.offsetLeft});
			_params.put("menuImage", new String [] {this.menuImage});
			_params.put("isAccessible", new String [] {isAccessible.toString()});

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			if (isAccessible) rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/bottom_accessible.jsp");
			else rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/bottom.jsp");
			
			req = new DynamicServletRequest(httpReq, _params);
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.title = null;
			this.titleId = "";
			this.titleClass = "";
			this.menuClass = "";
			this.menuWidth = "";
			this.openStyle = "slide_down";
			this.anchor = "";
			this.offsetTop = "8";
			this.offsetLeft = "4";
			this.menuImage = "";
			if (_params != null) {
				_params.clear();
			}
		}
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

	public void setTitle(String title) {
	    this.title = title;
	}

	public void setTitleId(String titleId) {
	    this.titleId = titleId;
	}

	public void setMenuClass(String menuClass) {
	    this.menuClass = menuClass;
	}

	public void setMenuWidth(String menuWidth) {
	    this.menuWidth = menuWidth; 
	}

	public void setTitleClass(String titleClass) {
	    this.titleClass = titleClass;
	}

	public void setOpenStyle(String openStyle) {
	    this.openStyle = openStyle;
	}

	public void setAnchor(String anchor) {
	    this.anchor = anchor;
	}

	public void setOffsetTop(String offsetTop) {
	    this.offsetTop = offsetTop;
	}

	public void setOffsetLeft(String offsetLeft) {
	    this.offsetLeft = offsetLeft;
	}
	
	public void setMenuImage(String menuImage) {
	    this.menuImage = menuImage;
	}

}


