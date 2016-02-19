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
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate the details about what's currently
 * selected.
 *  
 * @author drfoster
 */
public class SelectionDetails implements IsSerializable, VibeRpcResponseData {
	private boolean 					m_hasAdHocEntries;						// true -> Selections include entries from personal storage folders.
	private boolean 					m_hasAdHocFolders;						// true -> Selections include AdHoc Folders.
	private boolean 					m_hasAdHocFoldersWithNestedRemote;		// true -> Selections include AdHoc Folders that contain nested remote (i.e., Mirrored, Net or Cloud) folders.
	private boolean 					m_hasAdHocWorkspaces;					// true -> Selections include AdHoc Workspaces.
	private boolean 					m_hasAdHocWorkspacesWithNestedRemote;	// true -> Selections include AdHoc Workspaces that contain nested remote (i.e., Mirrored, Net or Cloud) folders.
	private boolean 					m_hasCloudFolders;						// true -> Selections include Cloud Folders.
	private boolean 					m_hasMirroredFolders;					// true -> Selections include Vibe Mirrored Folders.
	private boolean 					m_hasNetFolders;						// true -> Selections include Net Folders.
	private boolean 					m_hasRemoteEntries;						// true -> Selections include entries from either Net Folders or Vibe Mirrored folders.
	private ErrorListRpcResponseData	m_purgeConfirmations;					// Set containing information about the items that will be purged.
	private int							m_entryCount;							// Count of entries selected.
	private int							m_folderCount;							// Count of folders selected.
	private int							m_totalCount;							// Total count of everything being tracked.
	private int							m_workspaceCount;						// Count of workspaces selected.
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public SelectionDetails() {
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
	public boolean         hasAdHocBinders()                    {return (m_hasAdHocFolders || m_hasAdHocWorkspaces);                                                       }
	public boolean         hasAdHocEntries()                    {return m_hasAdHocEntries;                                                                                 }
	public boolean         hasAdHocFolders()                    {return m_hasAdHocFolders;                                                                                 }
	public boolean         hasAdHocFoldersWithNestedRemote()    {return m_hasAdHocFoldersWithNestedRemote;                                                                 }
	public boolean         hasAdHocSelections()                 {return (m_hasAdHocEntries || hasAdHocBinders());                                                          }
	public boolean         hasAdHocWorkspaces()                 {return m_hasAdHocWorkspaces;                                                                              }
	public boolean         hasAdHocWorkspacesWithNestedRemote() {return m_hasAdHocWorkspacesWithNestedRemote;                                                              }
	public boolean         hasCloudFolders()                    {return m_hasCloudFolders;                                                                                 }
	public boolean         hasBinders()                         {return (0 < getBinderCount());                                                                            }
	public boolean         hasEntries()                         {return (0 < m_entryCount);                                                                                }
	public boolean         hasFolders()                         {return (0 < m_folderCount);                                                                               }
	public boolean         hasMirroredFolders()                 {return m_hasMirroredFolders;                                                                              }
	public boolean         hasNetFolders()                      {return m_hasNetFolders;                                                                                   }
	public boolean         hasPurgeConfirmations()              {return m_purgeConfirmations.hasErrors();                                                                  }
	public boolean         hasRemoteEntries()                   {return m_hasRemoteEntries;                                                                                }
	public boolean         hasRemoteFolders()                   {return (m_hasCloudFolders || m_hasMirroredFolders | m_hasNetFolders || m_hasAdHocFoldersWithNestedRemote);}
	public boolean         hasRemoteSelections()                {return (hasRemoteEntries() || hasRemoteFolders() || hasRemoteWorkspaces());                               }
	public boolean         hasRemoteWorkspaces()                {return m_hasAdHocWorkspacesWithNestedRemote;                                                              }
	public boolean         hasUnclassified()                    {return (0 < getUnclassifiedCount());                                                                      }
	public boolean         hasWorkspaces()                      {return (0 < m_workspaceCount);                                                                            }
	public int             getEntryCount()                      {return m_entryCount;                                                                                      }
	public int             getBinderCount()                     {return (m_folderCount + m_workspaceCount);                                                                }
	public int             getFolderCount()                     {return m_folderCount;                                                                                     }
	public int             getTotalCount()                      {return m_totalCount;                                                                                      }
	public int             getUnclassifiedCount()               {return (m_totalCount - (m_entryCount + getBinderCount()));                                                }
	public int             getWorkspaceCount()                  {return m_workspaceCount;                                                                                  }
	public List<ErrorInfo> getPurgeConfirmations()              {return m_purgeConfirmations.getErrorList();                                                               }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void addPurgeConfirmation(                 String  confirmation)                       {m_purgeConfirmations.addWarning(confirmation);                            }
	public void incrEntryCount()                                                                  {m_entryCount                        += 1;                                 }
	public void incrFolderCount()                                                                 {m_folderCount                       += 1;                                 }
	public void incrWorkspaceCount()                                                              {m_workspaceCount                    += 1;                                 }
	public void setEntryCount(                        int     entryCount)                         {m_entryCount                         = entryCount;                        }
	public void setFolderCount(                       int     folderCount)                        {m_folderCount                        = folderCount;                       }
	public void setHasAdHocEntries(                   boolean hasAdHocEntries)                    {m_hasAdHocEntries                    = hasAdHocEntries;                   }
	public void setHasAdHocFolders(                   boolean hasAdHocFolders)                    {m_hasAdHocFolders                    = hasAdHocFolders;                   }
	public void setHasAdHocFoldersWithNestedRemote(   boolean hasAdHocFoldersWithNestedRemote)    {m_hasAdHocFoldersWithNestedRemote    = hasAdHocFoldersWithNestedRemote;   }
	public void setHasAdHocWorkspaces(                boolean hasAdHocWorkspaces)                 {m_hasAdHocWorkspaces                 = hasAdHocWorkspaces;                }
	public void setHasAdHocWorkspacesWithNestedRemote(boolean hasAdHocWorkspacesWithNestedRemote) {m_hasAdHocWorkspacesWithNestedRemote = hasAdHocWorkspacesWithNestedRemote;}
	public void setHasCloudFolders(                   boolean hasCloudFolders)                    {m_hasCloudFolders                    = hasCloudFolders;                   }
	public void setHasMirroredFolders(                boolean hasMirroredFolders)                 {m_hasMirroredFolders                 = hasMirroredFolders;                }
	public void setHasNetFolders(                     boolean hasNetFolders)                      {m_hasNetFolders                      = hasNetFolders;                     }
	public void setHasRemoteEntries(                  boolean hasRemoteEntries)                   {m_hasRemoteEntries                   = hasRemoteEntries;                  }
	public void setTotalCount(                        int     totalCount)                         {m_totalCount                         = totalCount;                        }
	public void setWorkspaceCount(                    int     workspaceCount)                     {m_workspaceCount                     = workspaceCount;                    }
}
