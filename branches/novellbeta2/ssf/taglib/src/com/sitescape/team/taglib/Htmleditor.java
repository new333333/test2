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
/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the HTML editor tag
 * 
 */
package com.sitescape.team.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspTagException;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author billmers
 *;
 */

// This is a stub so far; it is just a 
// gutted version of datepicker.

public class Htmleditor extends BodyTagSupport {
	private String id;
	private String name;
	private String initText;
	private String height = "";
	private String color = "";
	private String contextPath;
	private String _bodyContent;
        
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		BodyContent bc = getBodyContent();
		_bodyContent = "";
		if (bc != null) _bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
		    User user = RequestContextHolder.getRequestContext().getUser();
		    String languageCode = user.getLocale().getLanguage();
		    if (languageCode == null || languageCode.equals("")) languageCode = "en";
		    if (languageCode.startsWith("en")) languageCode = "en";
	
		    if (height == null) {
		    	height = "";
		    }
	
		    if (initText == null) {
		    	initText = "";
		    }
	
			//Output the html editor
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/htmlarea/htmlarea.jsp");
	
			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("element_id", this.id);
			req.setAttribute("element_name", this.name);
			req.setAttribute("init_text", this.initText);
			req.setAttribute("element_height", this.height);
			req.setAttribute("element_color", this.color);
			req.setAttribute("language", languageCode);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			if (this._bodyContent != null) pageContext.getOut().print(_bodyContent);

			// Bottom
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/htmlarea/htmlarea_bottom.jsp");
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());
	
			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			this.height = "";
			this.color = "";
			this.initText = "";
			this.id = "";
			this.name = "";
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInitText(String initText) {
		this.initText = initText;
    }

	public void setHeight(String height) {
		this.height = height;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
