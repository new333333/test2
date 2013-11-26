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
import java.util.List;
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
import org.dom4j.Document;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Show the user's name and presence.
 * 
 * 
 * @author Peter Hurley
 * 
 */
public class ShowUser extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(ShowUser.class);
	
	private UserPrincipal user = null;
	private String titleStyle = "";
	private String target = "";
    private Boolean showPresence = Boolean.TRUE;
    private Boolean showProfileEntry = Boolean.FALSE;
    private Boolean showInactiveAccounts = Boolean.FALSE;
    private Boolean showHint = Boolean.FALSE;
    private Boolean workspacePreDeleted = Boolean.FALSE;
    private Boolean close = Boolean.FALSE;

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
			ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
			if (this.close == null) this.close = false;
			
			// Get a user object from the principal (do this always if user has limited view of users)
			if ((user != null) && !(user instanceof User) && !(user instanceof Group) || 
					(user != null && Utils.canUserOnlySeeCommonGroupMembers())) {
				try {
					//this will remove the proxy and return a real user or group
					//currently looks like this code is expecting a User
					//get user even if deleted.
					user = profileDao.loadUserPrincipal(user.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
				} catch (Exception e) {
					logger.warn(e);
				}
			}

			if (user != null) {
				httpReq.setAttribute(WebKeys.SHOW_USER_INSTANCE_COUNT, UUID.randomUUID().toString());
				httpReq.setAttribute(WebKeys.SHOW_USER_USER, user);		
				httpReq.setAttribute(WebKeys.SHOW_USER_TITLE_STYLE, titleStyle);
				httpReq.setAttribute(WebKeys.SHOW_USER_TARGET, target);
				httpReq.setAttribute(WebKeys.SHOW_USER_CLOSE, close.toString());
				httpReq.setAttribute(WebKeys.SHOW_USER_IS_GROUP, user instanceof Group);
				httpReq.setAttribute(WebKeys.SHOW_USER_PROFILE_ENTRY, showProfileEntry);
				httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_HINT, showHint);
				httpReq.setAttribute(WebKeys.SHOW_USER_WORKSPACE_PREDELETED, workspacePreDeleted);
				httpReq.setAttribute(WebKeys.SHOW_USER_INACTIVE_ACCOUNTS, showInactiveAccounts);
				if (user != null && (user.isActive() || showInactiveAccounts))
					httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_PRESENCE, showPresence);
				else
					httpReq.setAttribute(WebKeys.SHOW_USER_SHOW_PRESENCE, Boolean.FALSE);
					
				ProfileModule profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
				if (user instanceof Group) {
					if (profileModule != null) {
						Collection<Long> ids = new ArrayList<Long>();
						ids.add(user.getId());
						Set groupUsers = profileModule.getUsersFromPrincipals(ids);
						httpReq.setAttribute(WebKeys.SHOW_USER_GROUP_MEMBERS, groupUsers);
					}
				}
				//See if this user can reference the profiles binder
				Boolean canReferenceProfilesBinder = false;
				try {
					Binder pb = profileModule.getProfileBinder();
					canReferenceProfilesBinder = true;
				} catch(Exception e) {}
				httpReq.setAttribute(WebKeys.SHOW_USER_CAN_ACCESS_PROFILES_BINDER, canReferenceProfilesBinder);
				
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
			showProfileEntry = false;
			showInactiveAccounts = false;
			workspacePreDeleted = false;
			titleStyle = "";
			target = "";
			close = Boolean.FALSE;
		}

		return EVAL_PAGE;
	}

	public void setUser(UserPrincipal user) {
		this.user = user;
	}
	public void setShowPresence(Boolean showPresence) {
		this.showPresence = showPresence;
	}
	public void setShowProfileEntry(Boolean showProfileEntry) {
		this.showProfileEntry = showProfileEntry;
	}
	public void setShowInactiveAccounts(Boolean showInactiveAccounts) {
		this.showInactiveAccounts = showInactiveAccounts;
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
	public void setClose(Boolean close) {
		this.close = close;
	}

}
