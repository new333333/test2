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
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.util.servlet.StringServletResponse;

/**
 * Displays component to select/choose team members.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class TeamMembers extends BodyTagSupport {
	
	private String formElement = "";

	private Integer instanceCount = 0;
	
	private String binderId = "";
	
	private Boolean appendAll= false;
	
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext
					.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext
					.getResponse();
			
			this.instanceCount++;

			httpReq.setAttribute("binderId", this.binderId);
			httpReq.setAttribute("formElement", this.formElement);
			httpReq.setAttribute("instanceCount", this.instanceCount);
			httpReq.setAttribute("appendAll", this.appendAll);
			
			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/team/team_members.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		} finally {
			formElement = "";
			binderId = "";			
			appendAll = false;
		}

		return EVAL_PAGE;
	}

	public void setBinderId(String binderId) {
		this.binderId = binderId;
	}

	public void setFormElement(String formElement) {
		this.formElement = formElement;
	}

	public void setAppendAll(Boolean appendAll) {
		this.appendAll = appendAll;
	}

}
