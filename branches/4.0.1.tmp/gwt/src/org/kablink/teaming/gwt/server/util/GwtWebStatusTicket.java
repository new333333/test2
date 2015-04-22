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
package org.kablink.teaming.gwt.server.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.util.StatusTicket;


/**
 * 
 * @author jwootton
 *
 */
public class GwtWebStatusTicket implements StatusTicket
{
	private HttpSession m_session;
	private String m_id;
	private String m_status;
	private String m_state;
	private Boolean m_doneFlag = false;

	private static GwtWebStatusTicket m_unknownTicket = new GwtWebStatusTicket( null, "__UNKNOWN__")
	{
		@Override
		public String getStatus() 
		{ 
			return "";
		}
		
		@Override
		public boolean isDone() 
		{ 
			return true; 
		}
	};

	/**
	 * 
	 */
	private static GwtWebStatusTicket m_completedTicket = new GwtWebStatusTicket( null, "__COMPLETE__" )
	{
		@Override
		public String getStatus() 
		{ 
			return ""; 
		}
		
		@Override
		public boolean isDone() 
		{ 
			return true; 
		}
	};
	
	/**
	 * Allocate a new status ticket and bind it with the user session.
	 * 
	 * @param id unique id for the ticket
	 * @param request portlet request
	 * @return a status ticket, or <code>null</code> if no user session exists.
	 */
	public static StatusTicket newStatusTicket( String id, HttpServletRequest request )
	{
		HttpSession session;
		GwtWebStatusTicket ticket;
		
		session = request.getSession();
		if ( session == null )
			return null; // unable to allocate a new ticket in this case
	
		ticket = new GwtWebStatusTicket( session, id );
		session.setAttribute( id, ticket );
		
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
	public static StatusTicket findStatusTicket( String id, HttpServletRequest request )
	{
		HttpSession session;
		GwtWebStatusTicket ticket;
		
		session = request.getSession( false );
		
		if ( session == null )
			return m_completedTicket;
	
		ticket = (GwtWebStatusTicket) session.getAttribute( id );
		if ( ticket == null )
			return m_unknownTicket;
		
		return ticket;
	}

	/**
	 * 
	 */
	private GwtWebStatusTicket( HttpSession session, String id )
	{
		m_session = session;
		m_id = id;
	}

	/**
	 * 
	 */
	@Override
	public void done()
	{
		try {
			m_session.removeAttribute( m_id );
		}
		catch(Exception ignore) {}
		m_doneFlag = true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isDone()
	{
		return m_doneFlag;
	}

	/**
	 * 
	 */
	@Override
	public String getId()
	{
		return m_id;
	}

	/**
	 * 
	 */
	@Override
	public void setStatus( String status )
	{
		m_status = status;
	}

	/**
	 * 
	 */
	@Override
	public String getStatus()
	{
		return m_status;
	}
	/**
	 * 
	 */
	@Override
	public void setState( String state )
	{
		m_state = state;
	}

	/**
	 * 
	 */
	@Override
	public String getState()
	{
		return m_state;
	}

}
