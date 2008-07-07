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

import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.presence.PresenceManager;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Roy Klein
 *
 */
public class PresenceInfo extends BodyTagSupport {
    private Principal user = null;
    private Boolean showTitle = false;
	private String titleStyle = "";
    private String zonName=null;
    private String componentId;
    private int userStatus=-1;
    private Boolean showOptionsInline=false;
    private String optionsDivId="";
    
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

			if (this.componentId == null) this.componentId = "";
			if (this.showOptionsInline == null) this.showOptionsInline = false;
			
			//Get a user object from the principal
			User user1 = null;
			if (user != null) {
				ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
				try {
					user1 = profileDao.loadUser(user.getId(), user.getZoneId());
				}
				catch(Exception e) {}
			}

			PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
			
			if (zonName != null) {
				userStatus = presenceService.getPresenceInfo(zonName);
			} else if (user != null && user1 != null) {
				userStatus = presenceService.getPresenceInfo(user1);
			} else {
				userStatus = -99;
			}
			String dudeGif = "sym_s_white_dude.gif"; 
			String altText = NLT.get("presence.none");
			if (userStatus != -99) {
				if (userStatus > 0) {
					if ((userStatus & 16) == 16) {
						dudeGif = "sym_s_yellow_dude.gif";
						altText = NLT.get("presence.away");
					} else {
						dudeGif = "sym_s_green_dude.gif";
						altText = NLT.get("presence.online");
					}
				} else if (userStatus == 0) {
					dudeGif = "sym_s_gray_dude.gif";
					altText = NLT.get("presence.offline");
				}
			}
			// Pass the user status to the jsp
			httpReq.setAttribute(WebKeys.PRESENCE_USER, user1);
			httpReq.setAttribute(WebKeys.PRESENCE_SHOW_TITLE, this.showTitle);
			httpReq.setAttribute(WebKeys.PRESENCE_TITLE_STYLE, this.titleStyle);
			httpReq.setAttribute(WebKeys.PRESENCE_STATUS, new Integer(userStatus));
			// TODO get date in the user's local time zone
			httpReq.setAttribute(WebKeys.PRESENCE_SWEEP_TIME, new Date());
			httpReq.setAttribute(WebKeys.PRESENCE_DUDE, dudeGif);
			httpReq.setAttribute(WebKeys.PRESENCE_TEXT, altText);
			httpReq.setAttribute(WebKeys.PRESENCE_ZON_BRIDGE, "enabled");
			httpReq.setAttribute(WebKeys.PRESENCE_COMPONENT_ID, this.componentId);
			httpReq.setAttribute(WebKeys.PRESENCE_DIV_ID, this.optionsDivId);
			httpReq.setAttribute(WebKeys.PRESENCE_SHOW_OPTIONS_INLINE, this.showOptionsInline);

			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/presence/show_dude.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());
		}
	    catch(Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
	    }
		finally {
			userStatus = -1;
			showTitle = false;
			titleStyle="";
			componentId = "";
			user = null;
			zonName = null;
			this.optionsDivId = "";
		}
	    
		return EVAL_PAGE;
	}

	public void setComponentId(String componentId) {
	    this.componentId = componentId;
	}

	public void setUser(Principal user) {
	    this.user = user;
	}
	public void setShowTitle(Boolean showTitle) {
		this.showTitle = showTitle;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}
	public void setZonName(String zonName) {
	    this.zonName = zonName;
	}
	public void setShowOptionsInline(Boolean showOptionsInline) {
		this.showOptionsInline = showOptionsInline;
	}
	public void setOptionsDivId(String optionsDivId) {
		this.optionsDivId = optionsDivId;
	}

}


