package com.sitescape.team.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

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
}
