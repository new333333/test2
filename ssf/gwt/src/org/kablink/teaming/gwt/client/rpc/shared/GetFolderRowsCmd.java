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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;


/**
 * This class holds all of the information necessary to execute the
 * 'Get Folder Rows' command.
 * 
 * @author drfoster@novell.com
 */
public class GetFolderRowsCmd extends VibeRpcCmd {
	private int					m_length;			//
	private int					m_start;			//
	private List<FolderColumn>	m_folderColumns;	//
	private Long				m_folderId;			//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public GetFolderRowsCmd() {
		super();
	}
	
	/**
	 * Constructor method
	 * 
	 * @param folderId
	 * @param folderColumns
	 * @param start
	 * @param length
	 */
	public GetFolderRowsCmd(Long folderId, List<FolderColumn> folderColumns, int start, int length) {
		this();
		
		m_folderId      = folderId;
		m_folderColumns = folderColumns;
		m_start         = start;
		m_length        = length;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int                getLength()        {return m_length;       }
	public int                getStart()         {return m_start;        }
	public List<FolderColumn> getFolderColumns() {return m_folderColumns;}
	public Long               getFolderId()      {return m_folderId;     }
	
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
