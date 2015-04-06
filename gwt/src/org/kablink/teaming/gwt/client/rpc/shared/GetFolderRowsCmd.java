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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.BinderInfo;

/**
 * This class holds all of the information necessary to execute the
 * 'Get Folder Rows' command.
 * 
 * @author drfoster@novell.com
 */
public class GetFolderRowsCmd extends VibeRpcCmd {
	private BinderInfo							m_folderInfo;			//
	private FolderDisplayDataRpcResponseData	m_folderDisplayData;	//
	private int									m_length;				//
	private int									m_start;				//
	private List<FolderColumn>					m_folderColumns;		//
	private String								m_authenticationGuid;	// Use when a Cloud Folder requires authentication.
	private String								m_quickFilter;			//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public GetFolderRowsCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method
	 * 
	 * @param folderInfo
	 * @param folderDisplayData
	 * @param folderColumns
	 * @param start
	 * @param length
	 * @param quickFilter
	 * @param authenticationGuid
	 */
	public GetFolderRowsCmd(BinderInfo folderInfo, FolderDisplayDataRpcResponseData folderDisplayData, List<FolderColumn> folderColumns, int start, int length, String quickFilter, String authenticationGuid) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setFolderInfo(        folderInfo        );
		setFolderDisplayData( folderDisplayData );
		setFolderColumns(     folderColumns     );
		setStart(             start             );
		setLength(            length            );
		setQuickFilter(       quickFilter       );
		setAuthenticationGuid(authenticationGuid);
	}
	
	public GetFolderRowsCmd(BinderInfo folderInfo, FolderDisplayDataRpcResponseData folderDisplayData, List<FolderColumn> folderColumns, int start, int length, String authenticationGuid) {
		// Always use the initial form of the constructor.
		this(folderInfo, folderDisplayData, folderColumns, start, length, null, authenticationGuid);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public BinderInfo                       getFolderInfo()         {return m_folderInfo;        }
	public FolderDisplayDataRpcResponseData getFolderDisplayData()  {return m_folderDisplayData; }
	public int                              getLength()             {return m_length;            }
	public int                              getStart()              {return m_start;             }
	public List<FolderColumn>               getFolderColumns()      {return m_folderColumns;     }
	public String                           getAuthenticationGuid() {return m_authenticationGuid;}
	public String                           getQuickFilter()        {return m_quickFilter;       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFolderInfo(        BinderInfo                       folderInfo)         {m_folderInfo         = folderInfo;        }
	public void setFolderDisplayData( FolderDisplayDataRpcResponseData folderDisplayData)  {m_folderDisplayData  = folderDisplayData; }
	public void setLength(            int                              length)             {m_length             = length;            }
	public void setStart(             int                              start)              {m_start              = start;             }
	public void setFolderColumns(     List<FolderColumn>               folderColumns)      {m_folderColumns      = folderColumns;     }
	public void setAuthenticationGuid(String                           authenticationGuid) {m_authenticationGuid = authenticationGuid;}
	public void setQuickFilter(       String                           quickFilter)        {m_quickFilter        = quickFilter;       }
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.GET_FOLDER_ROWS.ordinal();
	}
}
