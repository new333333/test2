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
 * Show the user's name and presence.
 * 
 * 
 * @author Peter Hurley
 * 
 */
public class ShowUser extends BodyTagSupport {

	private Principal user = null;
	private String titleStyle = "";
    private Boolean showPresence = true;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			// Get a user object from the principal
			User user1 = null;
			if (user != null) {
				ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
				try {
					user1 = profileDao.loadUser(user.getId(), user.getZoneId());
				} catch (Exception e) {
				}
			}

			httpReq.setAttribute(WebKeys.SHOW_USER_USER, user);		
			httpReq.setAttribute(WebKeys.SHOW_USER_TITLE_STYLE, titleStyle);		
			httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_PRESENCE, showPresence);		
			
			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/show_user/show_user.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		} finally {
			user = null;
			showPresence = true;
			titleStyle = "";
		}

		return EVAL_PAGE;
	}

	public void setUser(Principal user) {
		this.user = user;
	}
	public void setShowPresence(Boolean showPresence) {
		this.showPresence = showPresence;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}

}
