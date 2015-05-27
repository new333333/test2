/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to model assignment information for tasks,
 * milestones, ...
 * 
 * @author drfoster@novell.com
 */
public class AssignmentInfo extends PrincipalInfo implements IsSerializable {
	private AssigneeType	m_assigneeType;	//
	private String			m_hover;		//
	
	// The following are used for managing group and team assignees for
	// this AssignmentInfo in the user interface.
	private transient List<AssignmentInfo>	m_membership;		//
	private transient int					m_membersShown;		//
	private transient Object				m_membershipPopup;	//
	
	/**
	 * Enumeration used to represent the type of an AssignmentInfo.
	 */
	public enum AssigneeType implements IsSerializable{
		INDIVIDUAL,
		GROUP,
		TEAM,
		PUBLIC,
		PUBLIC_LINK;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isIndividual() {return INDIVIDUAL.equals( this);}
		public boolean isGroup()      {return GROUP.equals(      this);}
		public boolean isTeam()       {return TEAM.equals(       this);}
		public boolean isPublic()     {return PUBLIC.equals(     this);}
		public boolean isPublicLink() {return PUBLIC_LINK.equals(this);}
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean              isAssigneePublic()   {return m_assigneeType.isPublic();}
	public AssigneeType         getAssigneeType()    {return m_assigneeType;           }
	public List<AssignmentInfo> getMembership()      {return m_membership;             }
	public int                  getMembersShown()    {return m_membersShown;           }
	public Object               getMembershipPopup() {return m_membershipPopup;        }
	public String               getHover()           {return m_hover;                  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAssigneeType(   AssigneeType         assigneeType)    {m_assigneeType    = assigneeType;   }
	public void setMembership(     List<AssignmentInfo> membership)      {m_membership      = membership;     }
	public void setMembersShown(   int                  membersShown)    {m_membersShown    = membersShown;   }
	public void setMembershipPopup(Object               membershipPopup) {m_membershipPopup = membershipPopup;}
	public void setHover(          String               hover)           {m_hover           = hover;          }
	
	/**
	 * Constructs an AssignmentInfo from the parameters.
	 * 
	 * @param id
	 * @param title
	 * @param assigneeType
	 * 
	 * @return
	 */
	public static AssignmentInfo construct(Long id, String title, AssigneeType assigneeType) {
		AssignmentInfo reply = new AssignmentInfo();
		
		reply.setId(          id          );
		reply.setTitle(       title       );
		reply.setAssigneeType(assigneeType);
		
		return reply;
	}
	
	public static AssignmentInfo construct(Long id, AssigneeType assigneeType) {
		// Always use the initial form of the method.
		return construct(id, "", assigneeType);
	}

	/**
	 * AssigneeType detectors given a column search key.
	 * 
	 * @param csk
	 * 
	 * @return
	 */
	public static AssigneeType getColumnAssigneeType(String csk) {
		AssigneeType reply;
		if      (isColumnAssignee(     csk)) reply = AssigneeType.INDIVIDUAL;
		else if (isColumnAssigneeGroup(csk)) reply = AssigneeType.GROUP;
		else if (isColumnAssigneeTeam( csk)) reply = AssigneeType.TEAM;
		else                                 reply = null;
		return reply;
	}
	
	public static boolean isColumnAdministrator(String csk) {
		return csk.equals("administrator");
	}
	
	public static boolean isColumnAssigneeInfo(String csk) {
		return
			(isColumnAdministrator(        csk) ||
			 isColumnAssignee(             csk) ||
			 isColumnAssigneeGroup(        csk) ||
			 isColumnAssigneeTeam(         csk) ||
			 isColumnLimitedVisibilityUser(csk));
	}
	
	public static boolean isColumnAssignee(String csk) {
		return
			(csk.equals("attendee")           ||	// Calendars.
			 csk.equals("assignment")         ||	// Tasks.
			 csk.equals("responsible"));			// Milestones.
	}
	
	public static boolean isColumnAssigneeGroup(String csk) {
		return
			(csk.equals("attendee_groups")    ||	// Calendars.
			 csk.equals("assignment_groups")  ||	// Tasks.
			 csk.equals("responsible_groups"));		// Milestones.
	}
	
	public static boolean isColumnAssigneeTeam(String csk) {
		return
			(csk.equals("attendee_teams")     ||	// Calendars.
			 csk.equals("assignment_teams")   ||	// Tasks.
			 csk.equals("responsible_teams"));		// Milestone.
	}
	
	public static boolean isColumnLimitedVisibilityUser(String csk) {
		return csk.equals("limitedVisibilityUser");
	}
}
