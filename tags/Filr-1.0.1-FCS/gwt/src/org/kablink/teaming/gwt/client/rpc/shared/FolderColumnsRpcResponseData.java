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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get folder columns' RPC
 * command.
 * 
 * @author drfoster@novell.com
 */
public class FolderColumnsRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean				m_folderAdmin;			// true -> Logged in user is a folder admin.  false -> They're not.
	private List<FolderColumn>	m_folderColumnsList;	//
	private List<FolderColumn>	m_folderColumnsListAll;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderColumnsRpcResponseData() {
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param folderColumnsList
	 * @param folderColumnsListAll
	 */
	public FolderColumnsRpcResponseData(List<FolderColumn> folderColumnsList, List<FolderColumn> folderColumnsListAll) {
		this();
		
		m_folderColumnsList    = folderColumnsList;
		m_folderColumnsListAll = folderColumnsListAll;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean            isFolderAdmin()       {return m_folderAdmin;         }
	public List<FolderColumn> getFolderColumns()    {return m_folderColumnsList;   }
	public List<FolderColumn> getFolderColumnsAll() {return m_folderColumnsListAll;}

	/**
	 * Set'er methods.
	 * 
	 * @param folderColumnsList
	 */
	public void setFolderAdmin(     boolean            folderAdmin)          {m_folderAdmin          = folderAdmin;         }
	public void setFolderColumns(   List<FolderColumn> folderColumnsList)    {m_folderColumnsList    = folderColumnsList;   }
	public void setFolderColumnsAll(List<FolderColumn> folderColumnsListAll) {m_folderColumnsListAll = folderColumnsListAll;}
}
