/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate the details about the currently selected
 * users.
 *  
 * @author drfoster
 */
public class SelectedUsersDetails implements IsSerializable, VibeRpcResponseData {
	private boolean						m_hasAdHocUserWorkspaces;			// true -> Selections include user Workspaces.
	private boolean						m_hasAdHocUserWorkspacesPurgeOnly;	// true -> Selections include user Workspaces that can only be purged (i.e., LDAP users or their  workspaces contain Mirrored, Net or Cloud folders.)
	private ErrorListRpcResponseData	m_purgeConfirmations;				// Set containing information about the items that will be purged.
	private int							m_totalCount;						// Total count of everything being tracked.
	private int							m_userWorkspaceCount;				// Count of user's with workspaces selected.
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public SelectedUsersDetails() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires
		// ...initialization.
		m_purgeConfirmations = new ErrorListRpcResponseData();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean         hasAdHocUserWorkspaces()          {return m_hasAdHocUserWorkspaces;                }
	public boolean         hasAdHocUserWorkspacesPurgeOnly() {return m_hasAdHocUserWorkspacesPurgeOnly;       }
	public boolean         hasPurgeConfirmations()           {return m_purgeConfirmations.hasErrors();        }
	public boolean         hasPurgeOnlySelections()          {return hasPurgeOnlyWorkspaces();                }
	public boolean         hasPurgeOnlyWorkspaces()          {return m_hasAdHocUserWorkspacesPurgeOnly;       }
	public boolean         hasUnclassified()                 {return (0 < getUnclassifiedCount());            }
	public boolean         hasUserWorkspaces()               {return (0 < m_userWorkspaceCount);              }
	public int             getTotalCount()                   {return m_totalCount;                            }
	public int             getUnclassifiedCount()            {return (m_totalCount - getUserWorkspaceCount());}
	public int             getUserWorkspaceCount()           {return m_userWorkspaceCount;                    }
	public List<ErrorInfo> getPurgeConfirmations()           {return m_purgeConfirmations.getErrorList();     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void addPurgeConfirmation(              String  confirmation)                    {m_purgeConfirmations.addWarning(confirmation);                      }
	public void incrUserWorkspaceCount()                                                    {m_userWorkspaceCount             += 1;                              }
	public void setHasAdHocUserWorkspaces(         boolean hasAdHocUserWorkspaces)          {m_hasAdHocUserWorkspaces          = hasAdHocUserWorkspaces;         }
	public void setHasAdHocUserWorkspacesPurgeOnly(boolean hasAdHocUserWorkspacesPurgeOnly) {m_hasAdHocUserWorkspacesPurgeOnly = hasAdHocUserWorkspacesPurgeOnly;}
	public void setTotalCount(                     int     totalCount)                      {m_totalCount                      = totalCount;                     }
	public void setUserWorkspaceCount(             int     userWorkspaceCount)              {m_userWorkspaceCount              = userWorkspaceCount;             }
}
