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
package com.sitescape.team.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.sitescape.team.util.NLT;
import com.sitescape.team.util.StatusTicket;

public class WebStatusTicket implements StatusTicket {

	private PortletSession session;
	private String id;
	private String status;

	/**
	 * Allocate a new status ticket and bind it with the user session.
	 * If there is no user session associated with the portlet request, this
	 * returns <code>null</code>.
	 * 
	 * @param id unique id for the ticket
	 * @param request portlet request
	 * @return a status ticket, or <code>null</code> if no user session exists.
	 */
	public static StatusTicket newStatusTicket(String id, PortletRequest request) {
		PortletSession session = request.getPortletSession(false);
		
		if(session == null)
			return null; // unable to allocate a new ticket in this case
	
		WebStatusTicket ticket = new WebStatusTicket(session, id);
		session.setAttribute(id, ticket, PortletSession.APPLICATION_SCOPE);
		return ticket;
	}
	
	/**
	 * Lookup an existing status ticket in the user session.
	 * If there is no user session, or no ticket under the given id,
	 * returns a <code>CompletedWebTicket</code>.
	 * 
	 * @param id unique id for the ticket
	 * @param request portlet request
	 * @return a status ticket
	 */
	public static StatusTicket findStatusTicket(String id, PortletRequest request) {
		PortletSession session = request.getPortletSession(false);
		
		if(session == null)
			return completedTicket;
	
		WebStatusTicket ticket = (WebStatusTicket) session.getAttribute(id, PortletSession.APPLICATION_SCOPE);
		if(ticket == null) {
			return completedTicket;
		}
		return ticket;
	}
	
	private WebStatusTicket(PortletSession session, String id) {
		this.session = session;
		this.id = id;
	}
	
	public void done() {
		session.removeAttribute(id);
	}

	public String getId() {
		return id;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	private static WebStatusTicket completedTicket = new WebStatusTicket(null, "__COMPLETE__") {
			public String getStatus() { return NLT.get("status.complete"); }
	};
}
