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
import java.util.List;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.Attendee;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to represent an appointment in a calendar for passing
 * through a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("serial")
public class CalendarAppointment extends Appointment implements IsSerializable {
	private List<AssignmentInfo>	m_vibeAttendees;		//
	private List<AssignmentInfo>	m_vibeAttendeeGroups;	//
	private List<AssignmentInfo>	m_vibeAttendeeTeams;	//
	private Long					m_folderId;				//
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public CalendarAppointment() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int getAttendeeCount()           {                              return getAttendees().size();      }
	public int getVibeAttendeesCount()      {validateVibeAttendees();      return m_vibeAttendees.size();     }
	public int getVibeAttendeeGroupsCount() {validateVibeAttendeeGroups(); return m_vibeAttendeeGroups.size();}
	public int getVibeAttendeeTeamsCount()  {validateVibeAttendeeTeams();  return m_vibeAttendeeTeams.size(); }
	
	public List<AssignmentInfo> getVibeAttendees()      {return m_vibeAttendees;     }
	public List<AssignmentInfo> getVibeAttendeeGroups() {return m_vibeAttendeeGroups;}
	public List<AssignmentInfo> getVibeAttendeeTeams()  {return m_vibeAttendeeTeams; }
	public Long                 getFolderId()           {return m_folderId;          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setVibeAttendees(     List<AssignmentInfo> vibeAttendees)      {m_vibeAttendees      = vibeAttendees;     }
	public void setVibeAttendeeGroups(List<AssignmentInfo> vibeAttendeeGroups) {m_vibeAttendeeGroups = vibeAttendeeGroups;}
	public void setVibeAttendeeTeams( List<AssignmentInfo> vibeAttendeeTeams)  {m_vibeAttendeeTeams  = vibeAttendeeTeams; }
	public void setFolderId(          Long                 folderId)           {m_folderId           = folderId;          }

	/**
	 * Add'er methods
	 * 
	 * @param
	 */
	public void addAttendee(         Attendee       attendee)          {getAttendees().add(attendee);                                             }
	public void addVibeAttendee(     AssignmentInfo vibeAttendee)      {validateVibeAttendees();      m_vibeAttendees.add(vibeAttendee);          }
	public void addVibeAttendeeGroup(AssignmentInfo vibeAttendeeGroup) {validateVibeAttendeeGroups(); m_vibeAttendeeGroups.add(vibeAttendeeGroup);}
	public void addVibeAttendeeTeam( AssignmentInfo vibeAttendeeTeam)  {validateVibeAttendeeTeams();  m_vibeAttendeeTeams.add(vibeAttendeeTeam);  }

	/*
	 * List validation methods.
	 */
	private void validateVibeAttendees()      {if (null == m_vibeAttendees)      m_vibeAttendees      = new ArrayList<AssignmentInfo>();}
	private void validateVibeAttendeeGroups() {if (null == m_vibeAttendeeGroups) m_vibeAttendeeGroups = new ArrayList<AssignmentInfo>();}
	private void validateVibeAttendeeTeams()  {if (null == m_vibeAttendeeTeams)  m_vibeAttendeeTeams  = new ArrayList<AssignmentInfo>();}
}
