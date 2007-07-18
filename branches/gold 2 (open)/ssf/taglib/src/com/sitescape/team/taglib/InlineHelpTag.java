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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class InlineHelpTag extends BodyTagSupport {
	private String tag;
	private String jsp;
	private String _bodyContent;
    
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
			
			if (this.tag == null && this.jsp == null) {
				// Top
				String jsp = "/WEB-INF/jsp/tag_jsps/inline_help/top.jsp";				
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);	
				ServletRequest req = pageContext.getRequest();	
				StringServletResponse res = new StringServletResponse(httpRes);	
				rd.include(req, res);	
				pageContext.getOut().print(res.getString());
	
				// Body	
				pageContext.getOut().print(_bodyContent);
	
				// Bottom	
				jsp = "/WEB-INF/jsp/tag_jsps/inline_help/bottom.jsp";	
				rd = httpReq.getRequestDispatcher(jsp);	
				res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			
			} else {
				String jsp = "/WEB-INF/jsp/tag_jsps/inline_help/tag.jsp";				
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);	
				ServletRequest req = pageContext.getRequest();	
				StringServletResponse res = new StringServletResponse(httpRes);	
				req.setAttribute("tag", this.tag);			
				req.setAttribute("jsp", this.jsp);			
				rd.include(req, res);	
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			tag = null;
			jsp = null;
		}
	}

	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setJsp(String jsp) {
	    this.jsp = jsp;
	}
}


