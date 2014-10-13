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
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Show the group's contents
 * 
 * 
 * @author Dennis R. Foster
 * 
 */
@SuppressWarnings("serial")
public class ShowGroup extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(ShowGroup.class);
	
	private GroupPrincipal group = null;
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
			ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");

			// Get a group object from the principal
			if ((group != null) && !(group instanceof Group)) {
				try {
					//this will remove the proxy and return a real group
					//currently looks like this code is expecting a Group
					//get user even if deleted.
					group = profileDao.loadGroup(group.getId(), group.getZoneId());
				} catch (Exception e) {
					logger.warn(e);
				}
			}

			if (group != null) {
				httpReq.setAttribute(WebKeys.SHOW_GROUP_INSTANCE_COUNT, UUID.randomUUID().toString());
				httpReq.setAttribute(WebKeys.SHOW_GROUP_GROUP, group);		
				httpReq.setAttribute(WebKeys.SHOW_GROUP_TITLE_STYLE, titleStyle);
				httpReq.setAttribute(WebKeys.SHOW_GROUP_SHOW_PRESENCE, showPresence);
				

				ArrayList<Long> groupIds = new ArrayList<Long>();
				groupIds.add(group.getId());
				Set<Long> groupMemberIds = profileDao.explodeGroups(groupIds, group.getZoneId());
				List<Principal> groupUsers = profileDao.loadPrincipals(groupMemberIds, group.getZoneId(), true);
				httpReq.setAttribute(WebKeys.SHOW_GROUP_GROUP_MEMBERS, groupUsers);
				
				// Output the presence info
				String jsp = "/WEB-INF/jsp/tag_jsps/show_group/show_group.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			group = null;
			showPresence = true;
			titleStyle = "";
		}

		return EVAL_PAGE;
	}

	public void setGroup(GroupPrincipal group) {
		this.group = group;
	}
	public void setShowPresence(Boolean showPresence) {
		this.showPresence = showPresence;
	}
	public void setTitleStyle(String titleStyle) {
	    this.titleStyle = titleStyle;
	}

}
