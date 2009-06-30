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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Show the user's name and presence.
 * 
 * 
 * @author Peter Hurley
 * 
 */
public class ShowTeam extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(ShowTeam.class);
	
	private Binder team = null;
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
			BinderModule binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");

			if ((team != null) && (team instanceof Binder)) {
				httpReq.setAttribute(WebKeys.SHOW_TEAM_INSTANCE_COUNT, UUID.randomUUID().toString());
				httpReq.setAttribute(WebKeys.SHOW_TEAM_TEAM, team);		
				httpReq.setAttribute(WebKeys.SHOW_TEAM_TITLE_STYLE, titleStyle);
				httpReq.setAttribute(WebKeys.SHOW_TEAM_SHOW_PRESENCE, showPresence);
				

				Set groupUsers = binderModule.getTeamMembers(team, false);
				httpReq.setAttribute(WebKeys.SHOW_TEAM_TEAM_MEMBERS, groupUsers);
				
				// Output the presence info
				String jsp = "/WEB-INF/jsp/tag_jsps/show_team/show_team.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			team = null;
			showPresence = true;
			titleStyle = "";
		}

		return EVAL_PAGE;
	}

	public void setTeam(Binder team) {
		this.team = team;
	}
	public void setShowPresence(Boolean showPresence) {
		this.showPresence = showPresence;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}

}
