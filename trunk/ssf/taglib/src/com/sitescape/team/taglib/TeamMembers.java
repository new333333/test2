package com.sitescape.team.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.Document;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * Displays component to select/choose team members.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class TeamMembers extends BodyTagSupport {

	private Boolean noTeamMembers = Boolean.TRUE;
	
	private String formNameAllTeamMembers = WebKeys.URL_TEAM_MEMBERS;
	
	private String formNameTeamMemberIds =  WebKeys.URL_TEAM_MEMBER_IDS;
	
	private String label;

	private Integer instanceCount = 0;
	
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
			
			if (this.label == null) {
				this.label = NLT.get("sendMail.team");
			}
			this.instanceCount++;

			httpReq.setAttribute("form_name_all_team_members", this.formNameAllTeamMembers);
			httpReq.setAttribute("form_name_team_member_ids", this.formNameTeamMemberIds);
			httpReq.setAttribute("all_members_label", this.label);
			httpReq.setAttribute("no_team_members", this.noTeamMembers);
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
		}

		return EVAL_PAGE;
	}

	public void setFormNameAllTeamMembers(String formNameAllTeamMembers) {
		this.formNameAllTeamMembers = formNameAllTeamMembers;
	}

	public void setFormNameTeamMemberIds(String formNameTeamMemberIds) {
		this.formNameTeamMemberIds = formNameTeamMemberIds;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNoTeamMembers(Boolean noTeamMembers) {
		this.noTeamMembers = noTeamMembers;
	}
}
