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
package org.kablink.teaming.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.kablink.teaming.util.StatusTicket;


public class WebStatusTicket implements StatusTicket {

	private PortletSession session;
	private String id;
	private String status;
	private String state;
	private Boolean doneFlag = false;

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
			return unknownTicket;
		}
		return ticket;
	}
	
	private WebStatusTicket(PortletSession session, String id) {
		this.session = session;
		this.id = id;
	}
	
	public synchronized void done() {
		try {
			session.removeAttribute(id);
		}
		catch(Exception ignore) {}
		doneFlag = true;
	}
	
	public synchronized boolean isDone() {
		return doneFlag;
	}

	public synchronized String getId() {
		return id;
	}

	public synchronized void setStatus(String status) {
		this.status = status;
	}

	public synchronized String getStatus() {
		return status;
	}
	
	public synchronized void setState(String state) {
		this.state = state;
	}

	public synchronized String getState() {
		return state;
	}
	
	private static WebStatusTicket unknownTicket = new WebStatusTicket(null, "__UNKNOWN__") {
			public String getStatus() { return ""; }
			public boolean isDone() {return false;}
	};
	
	private static WebStatusTicket completedTicket = new WebStatusTicket(null, "__COMPLETE__") {
			public String getStatus() { return ""; }
			public boolean isDone() {return true;}
	};
}
