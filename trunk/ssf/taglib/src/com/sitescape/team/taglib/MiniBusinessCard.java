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

import org.dom4j.Document;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * Display mini version of user business card.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class MiniBusinessCard extends BodyTagSupport {

	private Principal user = null;

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

			// Get a user object from the principal
			User user1 = null;
			if (user != null) {
				ProfileDao profileDao = (ProfileDao) SpringContextUtil
						.getBean("profileDao");
				try {
					user1 = profileDao.loadUser(user.getId(), user.getZoneId());
				} catch (Exception e) {
				}
			}

			Document profileDef = user1.getEntryDef().getDefinition();
			httpReq.setAttribute(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
			httpReq.setAttribute(WebKeys.PROFILE_CONFIG_ELEMENT, profileDef
					.getRootElement().selectSingleNode(
							"//item[@name='profileEntryMiniBusinessCard']"));
			httpReq.setAttribute(WebKeys.PROFILE_CONFIG_JSP_STYLE, "view");
			httpReq.setAttribute(WebKeys.PROFILE_CONFIG_ENTRY, user);		
			

			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/business_card/view_mini_business_card.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			user = null;
		}

		return EVAL_PAGE;
	}

	public void setUser(Principal user) {
		this.user = user;
	}

}
