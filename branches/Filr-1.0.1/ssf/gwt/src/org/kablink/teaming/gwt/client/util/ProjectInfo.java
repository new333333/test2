/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to represent project information for a given binder.
 * 
 * @author jwootton@novell.com
 *
 */
public class ProjectInfo implements IsSerializable, VibeRpcResponseData
{
	/**
	 * Possible values for the project's status 
	 */
	public enum ProjectStatus implements IsSerializable
	{
		CANCELLED,
		CLOSED,
		OPEN,
		
		UNKNOWN
	}

	private String m_binderId;
	private ProjectStatus m_status;
	private ArrayList<PrincipalInfo> m_managers;
	private String m_dueDate;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProjectInfo()
	{
		m_status = ProjectStatus.UNKNOWN;
		m_managers = null;
		m_dueDate = null;
	}

	/**
	 * 
	 */
	public void addManager( PrincipalInfo manager )
	{
		if ( m_managers == null )
			m_managers = new ArrayList<PrincipalInfo>();
		
		m_managers.add( manager );
	}
	
	/**
	 * 
	 */
	public String getBinderId()
	{
		return m_binderId;
	}
	
	/**
	 * 
	 */
	public String getDueDate()
	{
		return m_dueDate;
	}
	
	/**
	 * 
	 */
	public ArrayList<PrincipalInfo> getManagers()
	{
		return m_managers;
	}
	
	/**
	 * 
	 */
	public String getStatusStr()
	{
		String status;
		
		switch ( m_status )
		{
		case CANCELLED:
			status = GwtTeaming.getMessages().projectStatusCancelled();
			break;
		
		case CLOSED:
			status = GwtTeaming.getMessages().projectStatusClosed();
			break;
			
		case OPEN:
			status = GwtTeaming.getMessages().projectStatusOpen();
			break;
			
		case UNKNOWN:
		default:
			status = GwtTeaming.getMessages().projectStatusUnknown();
			break;
		}
		
		return status;
	}
	
	/**
	 *
	 */
	public void setBinderId( String binderId )
	{
		m_binderId = binderId;
	}

	/**
	 * 
	 */
	public void setDueDate( String dueDate )
	{
		m_dueDate = dueDate;
	}
	
	/**
	 * 
	 */
	public void setManagers( ArrayList<PrincipalInfo> managers )
	{
		m_managers = managers;
	}
	
	/**
	 * 
	 */
	public void setStatus( ProjectStatus status )
	{
		m_status = status;
	}
	
	/**
	 * 
	 */
	public void setStatus( String status )
	{
		m_status = ProjectStatus.UNKNOWN;
		
		if ( status != null )
		{
			if ( status.equalsIgnoreCase( "open" ) )
				m_status = ProjectStatus.OPEN;
			else if ( status.equalsIgnoreCase( "cancelled" ) )
				m_status = ProjectStatus.CANCELLED;
			else if ( status.equalsIgnoreCase( "closed" ) )
				m_status = ProjectStatus.CLOSED;
		}
	}
}
