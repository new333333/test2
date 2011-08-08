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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle information about a task folder through a
 * single GWT RPC request.
 *  
 * @author drfoster
 */
public class TaskBundle implements IsSerializable {
	private boolean				m_binderIsMirrored;		//
	private boolean				m_canAddEntry;		    // Based on FolderOperation.addEntry.
	private boolean				m_canContainTasks;		//
	private boolean				m_canModifyTaskLinkage;	// Based on BinderOperation.setProperty.
	private boolean				m_canModifyEntry;		// Based on FolderOperation.modifyEntry.
	private boolean				m_canPurgeEntry;		// Based on FolderOperation.deleteEntry.
	private boolean				m_canTrashEntry;		// Based on FolderOperation.preDeleteEntry.
	private boolean				m_isDebug;				//
	private boolean				m_isFiltered;			//
	private boolean				m_isFromFolder;			//
	private int					m_totalTasks;			//
	private List<TaskListItem>	m_tasks;				//
	private Long				m_binderId;				//
	private String				m_filterTypeParam;		//
	private String				m_modeTypeParam;		//
	private String				m_newTaskUrl;			//
	private TaskLinkage			m_taskLinkage;			//

	/**
	 * Class constructor.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TaskBundle() {
		// Nothing to do.
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean            getBinderIsMirrored()     {return m_binderIsMirrored;    }
	public boolean            getCanAddEntry()          {return m_canAddEntry;         }
	public boolean            getCanContainTasks()      {return m_canContainTasks;     }
	public boolean            getCanModifyTaskLinkage() {return m_canModifyTaskLinkage;}
	public boolean            getCanModifyEntry()       {return m_canModifyEntry;      }
	public boolean            getCanPurgeEntry()        {return m_canPurgeEntry;       }
	public boolean            getCanTrashEntry()        {return m_canTrashEntry;       }
	public boolean            getIsDebug()              {return m_isDebug;             }
	public boolean            getIsFiltered()           {return m_isFiltered;          }
	public boolean            getIsFromFolder()         {return m_isFromFolder;        }
	public int                getTotalTasks()			{return m_totalTasks;          }
	public List<TaskListItem> getTasks()                {return m_tasks;               }
	public Long               getBinderId()             {return m_binderId;            }
	public String             getFilterTypeParam()      {return m_filterTypeParam;     }
	public String             getModeTypeParam()        {return m_modeTypeParam;       }
	public String             getNewTaskUrl()           {return m_newTaskUrl;          }
	public TaskLinkage        getTaskLinkage()          {return m_taskLinkage;         }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBinderIsMirrored(    boolean            binderIsMirrored)     {m_binderIsMirrored     = binderIsMirrored;    }
	public void setCanAddEntry(         boolean            canAddEntry)          {m_canAddEntry          = canAddEntry;         }
	public void setCanContainTasks(     boolean            canContainTasks)      {m_canContainTasks      = canContainTasks;     }
	public void setCanModifyTaskLinkage(boolean            canModifyTaskLinkage) {m_canModifyTaskLinkage = canModifyTaskLinkage;}
	public void setCanModifyEntry(      boolean            canModifyEntry)       {m_canModifyEntry       = canModifyEntry;      }
	public void setCanPurgeEntry(       boolean            canPurgeEntry)        {m_canPurgeEntry        = canPurgeEntry;       }
	public void setCanTrashEntry(       boolean            canTrashEntry)        {m_canTrashEntry        = canTrashEntry;       }
	public void setIsDebug(             boolean            isDebug)              {m_isDebug              = isDebug;             }
	public void setIsFiltered(          boolean            isFiltered)           {m_isFiltered           = isFiltered;          }
	public void setIsFromFolder(        boolean            isFromFolder)         {m_isFromFolder         = isFromFolder;        }
	public void setTotalTasks(          int                totalTasks)           {m_totalTasks           = totalTasks;          }
	public void setTasks(               List<TaskListItem> tasks)                {m_tasks                = tasks;               }
	public void setBinderId(            Long               binderId)             {m_binderId             = binderId;            }
	public void setFilterTypeParam(     String             filterTypeParam)      {m_filterTypeParam      = filterTypeParam;     }
	public void setModeTypeParam(       String             modeTypeParam)        {m_modeTypeParam        = modeTypeParam;       }
	public void setNewTaskUrl(          String             newTaskUrl)           {m_newTaskUrl           = newTaskUrl;          }
	public void setTaskLinkage(         TaskLinkage        taskLinkage)          {m_taskLinkage          = taskLinkage;         }

	/**
	 * Returns true if based on the information in the TaskBundle, the
	 * UI should respect the linkage information and false otherwise.
	 * 
	 * Currently, linkage is ignored for tasks lists that are not
	 * from the folder (i.e., they're an 'Assigned To' list) or that
	 * are filtered.
	 * 
	 * @return
	 */
	public boolean respectLinkage() {
		return (getIsFromFolder() && (!(getIsFiltered())));
	}
}
