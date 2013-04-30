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
import java.util.Date;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Roy Klein
 *
 */
public class PresenceInfoTag extends BodyTagSupport {
    private Principal user = null;
    private Boolean showTitle = false;
    private Boolean showHint = false;
    private Boolean workspacePreDeleted = false;
	private String titleStyle = "";
	private String target = "";
    private String zonName=null;
    private String componentId;
    private int userStatus=-1;
	private String statusText="";
    private Boolean showOptionsInline=false;
    private String optionsDivId="";
    private Boolean close = false;
    
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
			if (this.close == null) this.close = false;
			
			//Get a user object from the principal
			User user1 = null;
			if (user != null) {
				if (user instanceof User && !Utils.canUserOnlySeeCommonGroupMembers()) {
					user1 = (User) user;
				} else {
					ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
					try {
						user1 = profileDao.loadUser(user.getId(), RequestContextHolder.getRequestContext().getZoneId());
					}
					catch(Exception e) {}
				}
			}

			PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
			String userID = "";
			CustomAttribute attr = null;
			if (user1 != null) {
				attr = user1.getCustomAttribute("presenceID");
				if (attr != null) {
					userID = (String)attr.getValue();
				}
			}

			User userAsking = RequestContextHolder.getRequestContext().getUser();
			String userIDAsking = "";
			if (userAsking != null) {
				attr = userAsking.getCustomAttribute("presenceID");
				if (attr != null) {
					userIDAsking = (String)attr.getValue();
				}
			}

			String imProtocolString = "";
			userStatus = PresenceInfo.STATUS_UNKNOWN;

			if (userID != null && userID.length() > 0 && userIDAsking != null && userIDAsking.length() > 0) {
				imProtocolString = presenceService.getIMProtocolString(userID);

				if (!this.workspacePreDeleted) {
					PresenceInfo pi = presenceService.getPresenceInfo(userIDAsking, userID);
					if (pi != null) {
						userStatus = pi.getStatus();
						statusText = pi.getStatusText();
					}	
				}
			}

			String dudeGif;
			switch (userStatus) {
				case PresenceInfo.STATUS_AVAILABLE:
					dudeGif = "sym_s_green_dude_14.png";
					if (statusText == null) {
						statusText = NLT.get("presence.online");
					}
					break;
				case PresenceInfo.STATUS_AWAY:
					if (statusText == null) {
						statusText = NLT.get("presence.away");
					}
					dudeGif = "sym_s_yellow_dude_14.png";
					break;
				case PresenceInfo.STATUS_IDLE:
					if (statusText == null) {
						statusText = NLT.get("presence.idle");
					}
					dudeGif = "sym_s_yellow_dude_14.png";
					break;
				case PresenceInfo.STATUS_BUSY:
					if (statusText == null) {
						statusText = NLT.get("presence.busy");
					}
					dudeGif = "sym_s_red_dude_14.png";
					break;
				case PresenceInfo.STATUS_OFFLINE:
					if (statusText == null) {
						statusText = NLT.get("presence.offline");
					}
					dudeGif = "sym_s_gray_dude_14.png";
					break;
				default:
					dudeGif = "sym_s_white_dude_14.png";
					statusText = NLT.get("presence.none");
			}

			// Pass the user status to the jsp
			httpReq.setAttribute(WebKeys.PRESENCE_USER, user);
			httpReq.setAttribute(WebKeys.PRESENCE_SHOW_TITLE, this.showTitle);
			httpReq.setAttribute(WebKeys.PRESENCE_SHOW_HINT, this.showHint);
			httpReq.setAttribute(WebKeys.PRESENCE_WORKSPACE_PREDELETED, this.workspacePreDeleted);
			httpReq.setAttribute(WebKeys.PRESENCE_TITLE_STYLE, this.titleStyle);
			httpReq.setAttribute(WebKeys.PRESENCE_TARGET, this.target);
			httpReq.setAttribute(WebKeys.PRESENCE_CLOSE, this.close.toString());
			httpReq.setAttribute(WebKeys.PRESENCE_STATUS, new Integer(userStatus));
			// TODO get date in the user's local time zone
			httpReq.setAttribute(WebKeys.PRESENCE_SWEEP_TIME, new Date());
			httpReq.setAttribute(WebKeys.PRESENCE_DUDE, dudeGif);
			httpReq.setAttribute(WebKeys.PRESENCE_TEXT, statusText);
			httpReq.setAttribute(WebKeys.PRESENCE_ZON_BRIDGE, "enabled");
			httpReq.setAttribute(WebKeys.PRESENCE_COMPONENT_ID, this.componentId);
			httpReq.setAttribute(WebKeys.PRESENCE_DIV_ID, this.optionsDivId);
			httpReq.setAttribute(WebKeys.PRESENCE_SHOW_OPTIONS_INLINE, this.showOptionsInline);
			httpReq.setAttribute(WebKeys.PRESENCE_IM_URL, imProtocolString);

			//See if this user can reference the profiles binder
			ProfileModule profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
			Boolean canReferenceProfilesBinder = false;
			try {
				Binder pb = profileModule.getProfileBinder();
				canReferenceProfilesBinder = true;
			} catch(Exception e) {}
			httpReq.setAttribute(WebKeys.SHOW_USER_CAN_ACCESS_PROFILES_BINDER, canReferenceProfilesBinder);

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
			showHint = false;
			workspacePreDeleted = false;
			titleStyle="";
			target="";
			componentId = "";
			user = null;
			zonName = null;
			this.optionsDivId = "";
			close = false;
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
	public void setShowHint(Boolean showHint) {
		this.showHint = showHint;
	}
	public void setWorkspacePreDeleted(Boolean workspacePreDeleted) {
		this.workspacePreDeleted = workspacePreDeleted;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}
	public void setTarget(String target) {
	    this.target = target;
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
	public void setClose(Boolean close) {
		this.close = close;
	}

}


