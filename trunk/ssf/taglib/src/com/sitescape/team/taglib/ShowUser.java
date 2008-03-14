/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserPrincipal;
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

	private UserPrincipal user = null;
	private String titleStyle = "";
    private Boolean showPresence = Boolean.TRUE;

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
			if ((user != null) && !(user instanceof User) && !(user instanceof Group)) {
				ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
				try {
					//this will remove the proxy and return a real user or group
					//currently looks like this code is expecting a User
					//get user even if deleted.
					user = profileDao.loadUserPrincipal(user.getId(), user.getZoneId(), false);
				} catch (Exception e) {
				}
			}

			if ((user != null) && (user instanceof User || user instanceof Group)) {
				httpReq.setAttribute(WebKeys.SHOW_USER_USER, user);		
				httpReq.setAttribute(WebKeys.SHOW_USER_TITLE_STYLE, titleStyle);	
				if (user != null && user.isActive())
					httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_PRESENCE, showPresence);
				else
					httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_PRESENCE, Boolean.FALSE);
					
				
				// Output the presence info
				String jsp = "/WEB-INF/jsp/tag_jsps/show_user/show_user.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			user = null;
			showPresence = true;
			titleStyle = "";
		}

		return EVAL_PAGE;
	}

	public void setUser(UserPrincipal user) {
		this.user = user;
	}
	public void setShowPresence(Boolean showPresence) {
		this.showPresence = showPresence;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}

}
