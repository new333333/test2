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
	
	private String clickRoutine = "";

	private Integer instanceCount = 0;
	
	private String binderId = "";
	
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
			httpReq.setAttribute("clickRoutine", this.clickRoutine);
			httpReq.setAttribute("instanceCount", this.instanceCount);
			
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
			clickRoutine = "";
			binderId = "";			
		}

		return EVAL_PAGE;
	}

	public void setBinderId(String binderId) {
		this.binderId = binderId;
	}

	public void setClickRoutine(String clickRoutine) {
		this.clickRoutine = clickRoutine;
	}

}
