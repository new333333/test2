/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate the entries from an activity stream
 * between the activity stream control and the GWT RPC service methods.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamEntry implements IsSerializable {
	private boolean						m_entryFile;				//
	private GwtPresenceInfo             m_authorPresence;           //
	private List<ActivityStreamEntry>	m_comments;					// Comments, up to maximum number configured.
	private String						m_authorAvatarUrl;			//
	private String						m_authorId;					//
	private String						m_authorLogin;				//
	private String						m_authorName;				//
	private String						m_authorWSId;				//
	private int                         m_entryComments;			//
	private String						m_entryDescription;			//
	private int							m_entryDescriptionFormat;	// One of the FORMAT_* values form org.kablink.teaming.domain.Description.
	private String						m_entryDocNum;				//
	private String						m_entryFileIcon;			//
	private String						m_entryId;					//
	private String						m_entryModificationDate;	//
	private boolean						m_entrySeen;				//
	private String						m_entryTitle;				//
	private String						m_entryTopEntryId;			//
	private String						m_entryType;				//
	private String						m_parentBinderHover;		//
	private String						m_parentBinderId;			//
	private String						m_parentBinderName;			//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ActivityStreamEntry() {
		// Nothing to do.
	}

	/**
	 * Returns a count of the activity stream entries being tracked as
	 * comments.
	 * 
	 * @return
	 */
	public int getCommentCount() {
		return getComments().size();
	}

	/**
	 * Returns a List<ActivityStreamEntry>'s for the comments being
	 * tracked for this activity stream entry.
	 * 
	 * @return
	 */
	public List<ActivityStreamEntry> getComments() {
		if (null == m_comments) {
			m_comments = new ArrayList<ActivityStreamEntry>();
		}
		return m_comments;
	}
	

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean         isEntryFile()               {return m_entryFile;             }
	public GwtPresenceInfo getAuthorPresence()         {return m_authorPresence;        }
	public String          getAuthorAvatarUrl()        {return m_authorAvatarUrl;       }
	public String          getAuthorId()               {return m_authorId;              }
	public String          getAuthorLogin()            {return m_authorLogin;           }
	public String          getAuthorName()             {return m_authorName;            }
	public String          getAuthorWorkspaceId()      {return m_authorWSId;            }
	public int             getEntryComments()          {return m_entryComments;         }	
	public String          getEntryDescription()       {return m_entryDescription;      }	
	public int             getEntryDescriptionFormat() {return m_entryDescriptionFormat;}	
	public String          getEntryDocNum()            {return m_entryDocNum;           }
	public String          getEntryFileIcon()          {return m_entryFileIcon;         }
	public String          getEntryId()                {return m_entryId;               }	
	public String          getEntryModificationDate()  {return m_entryModificationDate; }
	public boolean         getEntrySeen()              {return m_entrySeen;             }	
	public String          getEntryTitle()             {return m_entryTitle;            }	
	public String          getEntryTopEntryId()        {return m_entryTopEntryId;       }	
	public String          getEntryType()              {return m_entryType;             }	
	public String          getParentBinderHover()      {return m_parentBinderHover;     }	
	public String          getParentBinderId()         {return m_parentBinderId;        }	
	public String          getParentBinderName()       {return m_parentBinderName;      }
	
	/**
	 * Stores a new List<ActivityStreamEntry> as the comments on this
	 * activity stream entry.
	 * 
	 * @param comments
	 */
	public void setComments(List<ActivityStreamEntry> comments) {
		m_comments = ((null == comments) ? new ArrayList<ActivityStreamEntry>() : comments);
	}
	
	/**
	 * Set'er methods.
	 * 
	 * @return
	 */
	public void setEntryFile(             boolean         entryFile)              {m_entryFile              = entryFile;             }
	public void setAuthorPresence(        GwtPresenceInfo authorPresence)         {m_authorPresence         = authorPresence;        }
	public void setAuthorAvatarUrl(       String          authorAvatarUrl)        {m_authorAvatarUrl        = authorAvatarUrl;       }
	public void setAuthorId(              String          authorId)               {m_authorId               = authorId;              }
	public void setAuthorLogin(           String          authorLogin)            {m_authorLogin            = authorLogin;           }
	public void setAuthorName(            String          authorName)             {m_authorName             = authorName;            }
	public void setAuthorWorkspaceId(     String          authorWSId)             {m_authorWSId             = authorWSId;            }
	public void setEntryComments(         int             entryComments)          {m_entryComments          = entryComments;         }	
	public void setEntryDescription(      String          entryDescription)       {m_entryDescription       = entryDescription;      }	
	public void setEntryDescriptionFormat(int             entryDescriptionFormat) {m_entryDescriptionFormat = entryDescriptionFormat;}	
	public void setEntryDocNum(           String          entryDocNum)            {m_entryDocNum            = entryDocNum;           }
	public void setEntryFileIcon(         String          entryFileIcon)          {m_entryFileIcon          = entryFileIcon;         }
	public void setEntryId(               String          entryId)                {m_entryId                = entryId;               }
	public void setEntryModificationDate( String          entryModificationDate)  {m_entryModificationDate  = entryModificationDate; }
	public void setEntrySeen(             boolean         entrySeen)              {m_entrySeen              = entrySeen;             }
	public void setEntryTitle(            String          entryTitle)             {m_entryTitle             = entryTitle;            }
	public void setEntryTopEntryId(       String          entryTopEntryId)        {m_entryTopEntryId        = entryTopEntryId;       }
	public void setEntryType(             String          entryType)              {m_entryType              = entryType;             }
	public void setParentBinderHover(     String          parentBinderHover)      {m_parentBinderHover      = parentBinderHover;     }
	public void setParentBinderId(        String          parentBinderId)         {m_parentBinderId         = parentBinderId;        }
	public void setParentBinderName(      String          parentBinderName)       {m_parentBinderName       = parentBinderName;      }
}
