/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle information about a Principal through GWT RPC
 * requests.
 *  
 * @author drfoster
 */
public class PrincipalInfo implements IsSerializable {
	private boolean			m_userDisabled;			// true -> The principal is disabled.  false -> It's not.
	private boolean			m_userExternal;			// true -> The principal is external.  false -> They're internal.
	private boolean			m_userHasWS;			// true -> The principal's workspace exists.
	private boolean			m_userPerson;			// true -> The principal is person.  false -> It's not (could be a system user such as File Synchronization Agent, ...)
	private boolean			m_userWSInTrash;		// true -> The principal's workspace exists and is in the trash.
	private GwtPresenceInfo m_presence;				// Only used for individual assignees.
	private int             m_members = (-1);		// Only used for group and team assignees.
	private Long            m_id;					//
	private Long			m_presenceUserWSId;		// Only used for individual assignees.
	private String			m_avatarUrl;			//
	private String			m_emailAddress;			//
	private String			m_presenceDude;			// Used for all assignees.
	private String          m_title;				//
	private String			m_viewProfileEntryUrl;	//

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public PrincipalInfo() {
		// Nothing to do.
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean         isUserDisabled()         {return m_userDisabled;       }
	public boolean         isUserExternal()         {return m_userExternal;       }
	public boolean         isUserHasWS()            {return m_userHasWS;          }
	public boolean         isUserPerson()           {return m_userPerson;         }
	public boolean         isUserWSInTrash()        {return m_userWSInTrash;      }
	public GwtPresenceInfo getPresence()            {return m_presence;           }
	public int             getMembers()             {return m_members;            }
	public Long            getId()                  {return m_id;                 }
	public Long            getPresenceUserWSId()    {return m_presenceUserWSId;   }
	public String          getAvatarUrl()           {return m_avatarUrl;          }
	public String          getEmailAddress()        {return m_emailAddress;       }
	public String          getPresenceDude()        {return m_presenceDude;       }
	public String          getTitle()               {return m_title;              }
	public String          getViewProfileEntryUrl() {return m_viewProfileEntryUrl;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUserDisabled(       boolean         userDisabled)        {m_userDisabled        = userDisabled;       }
	public void setUserExternal(       boolean         userExternal)        {m_userExternal        = userExternal;       }
	public void setUserHasWS(          boolean         userHasWS)           {m_userHasWS           = userHasWS;          }
	public void setUserPerson(         boolean         userPerson)          {m_userPerson          = userPerson;         }
	public void setUserWSInTrash(      boolean         userWSInTrash)       {m_userWSInTrash       = userWSInTrash;      }
	public void setPresence(           GwtPresenceInfo presence)            {m_presence            = presence;           }
	public void setMembers(            int             members)             {m_members             = members;            }
	public void setId(                 Long            id)                  {m_id                  = id;                 }
	public void setPresenceUserWSId(   Long            presenceUserWSId)    {m_presenceUserWSId    = presenceUserWSId;   }
	public void setAvatarUrl(          String          avatarurl)           {m_avatarUrl           = avatarurl;          }
	public void setEmailAddress(       String          emailAddress)        {m_emailAddress        = emailAddress;       }
	public void setPresenceDude(       String          presenceDude)        {m_presenceDude        = presenceDude;       }
	public void setTitle(              String          title)               {m_title               = title;              }
	public void setViewProfileEntryUrl(String          viewProfileEntryUrl) {m_viewProfileEntryUrl = viewProfileEntryUrl;}
	
	/**
	 * Constructs an PrincipalInfo from the parameters.
	 * 
	 * @param id
	 * @param title
	 * 
	 * @return
	 */
	public static PrincipalInfo construct(Long id, String title) {
		PrincipalInfo reply = new PrincipalInfo();
		
		reply.setId(   id   );
		reply.setTitle(title);
		
		return reply;
	}
	
	public static PrincipalInfo construct(Long id) {
		// Always use the initial form of the method.
		return construct(id, "");
	}		
}
