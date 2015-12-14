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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.Map;

import org.kablink.teaming.gwt.client.util.GwtFileLinkAction;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get folder display data'
 * RPC command.
 * 
 * @author drfoster@novell.com
 */
public class FolderDisplayDataRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean				m_folderIsMyFilesStorage;	//
	private boolean				m_folderOwnedByCurrentUser;	//
	private boolean				m_folderSortDescend;		//
	private boolean				m_folderSupportsPinning;	//
	private boolean				m_showHtmlElement;			//
	private boolean				m_showUserList;				//
	private boolean				m_viewPinnedEntries;		//
	private boolean				m_viewSharedFiles;			//
	private GwtFileLinkAction	m_fileLinkAction;			//
	private int					m_folderPageSize;			//
	private Map<String, String>	m_folderColumnWidths;		//
	private String				m_folderSortBy;				//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderDisplayDataRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param folderIsMyFilesStorage
	 * @param folderSortBy
	 * @param folderSortDescend
	 * @param folderPageSize
	 * @param folderColumnWidths
	 * @param folderSupportsPinning
	 * @param showUserList
	 * @param showHtmlElement
	 * @param viewPinnedEntries
	 * @param viewSharedFiles
	 * @param folderOwnedByCurrentUser
	 * @param fileLinkAction
	 */
	public FolderDisplayDataRpcResponseData(boolean folderIsMyFilesStorage, String folderSortBy, boolean folderSortDescend, int folderPageSize, Map<String, String> folderColumnWidths, boolean folderSupportsPinning, boolean showUserList, boolean showHtmlElement, boolean viewPinnedEntries, boolean viewSharedFiles, boolean folderOwnedByCurrentUser, GwtFileLinkAction fileLinkAction) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setFolderIsMyFilesStorage(  folderIsMyFilesStorage  );
		setFolderSortBy(            folderSortBy            );
		setFolderSortDescend(       folderSortDescend       );
		setFolderPageSize(          folderPageSize          );
		setFolderColumnWidths(      folderColumnWidths      );
		setFolderSupportsPinning(   folderSupportsPinning   );
		setShowHtmlElement(         showHtmlElement         );
		setShowUserList(            showUserList            );
		setViewPinnedEntries(       viewPinnedEntries       );
		setViewSharedFiles(         viewSharedFiles         );
		setFolderOwnedByCurrentUser(folderOwnedByCurrentUser);
		setFileLinkAction(          fileLinkAction          );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean             getFolderIsMyFilesStorage()   {return m_folderIsMyFilesStorage;  }
	public boolean             getFolderOwnedByCurrentUser() {return m_folderOwnedByCurrentUser;}
	public boolean             getFolderSortDescend()        {return m_folderSortDescend;       }
	public boolean             getFolderSupportsPinning()    {return m_folderSupportsPinning;   }
	public boolean             getShowHtmlElement()          {return m_showHtmlElement;         }
	public boolean             getShowUserList()             {return m_showUserList;            }
	public boolean             getViewPinnedEntries()        {return m_viewPinnedEntries;       }
	public boolean             getViewSharedFiles()          {return m_viewSharedFiles;         }
	public GwtFileLinkAction   getFileLinkAction()           {return m_fileLinkAction;          }
	public int                 getFolderPageSize()           {return m_folderPageSize;          }
	public Map<String, String> getFolderColumnWidths()       {return m_folderColumnWidths;      }
	public String              getFolderSortBy()             {return m_folderSortBy;            }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFolderIsMyFilesStorage(  boolean             folderIsMyFilesStorage)   {m_folderIsMyFilesStorage   = folderIsMyFilesStorage;  }
	public void setFolderOwnedByCurrentUser(boolean             folderOwnedByCurrentUser) {m_folderOwnedByCurrentUser = folderOwnedByCurrentUser;}
	public void setFolderSortDescend(       boolean             folderSortDescend)        {m_folderSortDescend        = folderSortDescend;       }
	public void setFolderSupportsPinning(   boolean             folderSupportsPinning)    {m_folderSupportsPinning    = folderSupportsPinning;   }
	public void setShowHtmlElement(         boolean             showHtmlElement)          {m_showHtmlElement          = showHtmlElement;         }
	public void setShowUserList(            boolean             showUserList)             {m_showUserList             = showUserList;            }
	public void setViewPinnedEntries(       boolean             viewPinnedEntries)        {m_viewPinnedEntries        = viewPinnedEntries;       }
	public void setViewSharedFiles(         boolean             viewSharedFiles)          {m_viewSharedFiles          = viewSharedFiles;         }
	public void setFileLinkAction(          GwtFileLinkAction   fileLinkAction)           {m_fileLinkAction           = fileLinkAction;          }
	public void setFolderPageSize(          int                 folderPageSize)           {m_folderPageSize           = folderPageSize;          }
	public void setFolderColumnWidths(      Map<String, String> folderColumnWidths)       {m_folderColumnWidths       = folderColumnWidths;      }
	public void setFolderSortBy(            String              folderSortBy)             {m_folderSortBy             = folderSortBy;            }
}
