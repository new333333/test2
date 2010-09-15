/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate the entries from an activity stream
 * between the activity stream control and the GWT RPC service methods.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamEntry implements IsSerializable {
	private List<ActivityStreamEntry>	m_comments;					// Comments, up to maximum number configured.
	private String						m_authorAvatarUrl;			//
	private String						m_authorId;					//
	private String						m_authorName;				//
	private String						m_entryDescription;			//
	private String						m_entryId;					//
	private String						m_entryModificationDate;	//
	private String						m_entryTitle;				//
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
	public String getAuthorAvatarUrl()       {return m_authorAvatarUrl;      }
	public String getAuthorId()              {return m_authorId;             }
	public String getAuthorName()            {return m_authorName;           }
	public String getEntryDescription()      {return m_entryDescription;     }	
	public String getEntryId()               {return m_entryId;              }	
	public String getEntryModificationDate() {return m_entryModificationDate;}
	public String getEntryTitle()            {return m_entryTitle;           }	
	public String getParentBinderHover()     {return m_parentBinderHover;    }	
	public String getParentBinderId()        {return m_parentBinderId;       }	
	public String getParentBinderName()      {return m_parentBinderName;     }
	
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
	public void setAuthorAvatarUrl(      String authorAvatarUrl)       {m_authorAvatarUrl       = authorAvatarUrl;      }
	public void setAuthorId(             String authorId)              {m_authorId              = authorId;             }
	public void setAuthorName(           String authorName)            {m_authorName            = authorName;           }
	public void setEntryDescription(     String entryDescription)      {m_entryDescription      = entryDescription;     }	
	public void setEntryId(              String entryId)               {m_entryId               = entryId;              }
	public void setEntryModificationDate(String entryModificationDate) {m_entryModificationDate = entryModificationDate;}
	public void setEntryTitle(           String entryTitle)            {m_entryTitle            = entryTitle;           }
	public void setParentBinderHover(    String parentBinderHover)     {m_parentBinderHover     = parentBinderHover;    }
	public void setParentBinderId(       String parentBinderId)        {m_parentBinderId        = parentBinderId;       }
	public void setParentBinderName(     String parentBinderName)      {m_parentBinderName      = parentBinderName;     }
}
