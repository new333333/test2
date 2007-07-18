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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * @author Hemanth Chokkanathan
 *
 */
public class SkipLinkTag extends BodyTagSupport {
	private String _bodyContent;
	private String tag = "";
	private String id = "";
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			RequestContext rc = RequestContextHolder.getRequestContext();
			User user = null;
			Boolean isAccessible = Boolean.FALSE;
			if (rc != null) user = rc.getUser();
			if (user != null) {
				if (user.getDisplayStyle() != null && user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			//Output the top section skip link
			if (isAccessible == true) {
				httpReq.setAttribute(WebKeys.SKIP_LINK_ID, id);
				httpReq.setAttribute(WebKeys.SKIP_LINK_TAG, tag);
				
				String jsp = "/WEB-INF/jsp/tag_jsps/skiplink/top.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}
			
			 //Body
			pageContext.getOut().print(_bodyContent);
			
			//Output the bottom section skip link
			if (isAccessible == true) {
				String jsp = "/WEB-INF/jsp/tag_jsps/skiplink/bottom.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}
			
			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.tag = "";
			this.id = "";
		}
	}

	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setId(String id) {
	    this.id = id;
	}	
	
}