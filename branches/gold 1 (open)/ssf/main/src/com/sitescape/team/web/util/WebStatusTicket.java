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
