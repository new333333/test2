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
import com.bradrydzewski.gwt.calendar.client.DateUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to represent an appointment in a calendar for passing
 * through a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("serial")
public class CalendarAppointment extends Appointment implements IsSerializable {
	private List<AssignmentInfo>	m_vibeAttendees;			//
	private List<AssignmentInfo>	m_vibeAttendeeGroups;		//
	private List<AssignmentInfo>	m_vibeAttendeeTeams;		//
	
	private boolean					m_canModify;				//
	private boolean					m_canPurge;					//
	private boolean					m_canTrash;					//
	private boolean					m_isTask;					//
	private boolean					m_seen;						//
	private EntityId				m_entityId;					//
	private Long					m_creatorId;				//
	private String					m_descriptionHtml;			//
	
	private CalendarRecurrence		m_serverRecurrence;			//
	private int						m_clientRecurrenceIndex;	// Client side only.  (-1) if not involved in a recurrence.  Otherwise, 0 based index into the recurrences.
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public CalendarAppointment() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		setClientRecurrenceIndex(-1);
	}

	/**
	 * Constructor method.
	 * 
	 * @param isTask
	 */
	public CalendarAppointment(boolean isTask) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setIsTask(isTask);
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
	
	public boolean            canModify()                 {return m_canModify;                                                         }
	public boolean            canPurge()                  {return m_canPurge;                                                          }
	public boolean            canTrash()                  {return m_canTrash;                                                          }
	public boolean            isTask()                    {return m_isTask;                                                            }
	public boolean            isSeen()                    {return m_seen;                                                              }
	public boolean            isServerRecurrent()         {return ((null != m_serverRecurrence) && m_serverRecurrence.isRecurrent());  }
	public boolean            isClientRecurrentInstance() {return (0 <= m_clientRecurrenceIndex);                                      }
	public CalendarRecurrence getServerRecurrence()       {return m_serverRecurrence;                                                  }
	public EntityId           getEntityId()               {return m_entityId;                                                          }
	public int                getClientRecurrenceIndex()  {return m_clientRecurrenceIndex;                                             }
	public Long               getCreatorId()              {return m_creatorId;                                                         }
	public Long               getFolderId()               {return ((null == m_entityId) ? null : m_entityId.getBinderId());            }
	public Long               getEntryId()                {return ((null == m_entityId) ? null : m_entityId.getEntityId());            }
	public String             getDescriptionHtml()        {return m_descriptionHtml;                                                   }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setVibeAttendees(     List<AssignmentInfo> vibeAttendees)      {m_vibeAttendees      = vibeAttendees;     }
	public void setVibeAttendeeGroups(List<AssignmentInfo> vibeAttendeeGroups) {m_vibeAttendeeGroups = vibeAttendeeGroups;}
	public void setVibeAttendeeTeams( List<AssignmentInfo> vibeAttendeeTeams)  {m_vibeAttendeeTeams  = vibeAttendeeTeams; }
	
	public void setCanModify(            boolean            canModify)             {m_canModify             = canModify;            }
	public void setCanPurge(             boolean            canPurge)              {m_canPurge              = canPurge;             }
	public void setCanTrash(             boolean            canTrash)              {m_canTrash              = canTrash;             }
	public void setIsTask(               boolean            isTask)                {m_isTask                = isTask;               }
	public void setSeen(                 boolean            seen)                  {m_seen                  = seen;                 }
	public void setServerRecurrence(     CalendarRecurrence serverRecurrence)      {m_serverRecurrence      = serverRecurrence;     }
	public void setClientRecurrenceIndex(int                clientRecurrenceIndex) {m_clientRecurrenceIndex = clientRecurrenceIndex;}
	public void setEntityId(             EntityId           entityId)              {m_entityId              = entityId;             }
	public void setCreatorId(            Long               creatorId)             {m_creatorId             = creatorId;            }
	public void setDescriptionHtml(      String             descriptionHtml)       {m_descriptionHtml       = descriptionHtml;      }

	/**
	 * Returns the EntityId of the appointment in a List<EntityId>.
	 * 
	 * @return
	 */
	public List<EntityId> getEntityIdAsList() {
		List<EntityId> reply = new ArrayList<EntityId>();
		if (null != m_entityId) {
			reply.add(m_entityId);
		}
		return reply;
	}
	
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

	/**
	 * Clones this calendar appointment.
	 * 
	 * @return
	 */
	public CalendarAppointment cloneAppointment() {
		// Allocate an appointment to return...
		CalendarAppointment reply = new CalendarAppointment();

		// ...clone the base Appointment information...
        reply.setId(                     getId()         );
		reply.setAllDay(                 isAllDay()      );
        reply.setAttendees(              getAttendees()  );
		reply.setCreatedBy(              getCreatedBy()  );
		reply.setDescription(            getDescription());
		reply.setEnd(  DateUtils.newDate(getEnd())       );
		reply.setLocation(               getLocation()   );
		reply.setStart(DateUtils.newDate(getStart())     );
		reply.setTitle(                  getTitle()      );
		reply.setStyle(                  getStyle()      );
        reply.setCustomStyle(            getCustomStyle());
		
		// ...clone our extended CalendarAppointment information...
		reply.setVibeAttendees(        getVibeAttendees()        );
		reply.setVibeAttendeeGroups(   getVibeAttendeeGroups()   );
		reply.setVibeAttendeeTeams(    getVibeAttendeeTeams()    );
		reply.setCanModify(            canModify()               );
		reply.setCanPurge(             canPurge()                );
		reply.setCanTrash(             canTrash()                );
		reply.setIsTask(               isTask()                  );
		reply.setSeen(                 isSeen()                  );
		reply.setServerRecurrence(     getServerRecurrence()     );
		reply.setClientRecurrenceIndex(getClientRecurrenceIndex());
		reply.setEntityId(             getEntityId()             );
		reply.setCreatorId(            getCreatorId()            );

		// ...and return the clone.
		return reply;
	}
}
