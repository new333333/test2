/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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


/**
 * This class holds all of the information necessary to execute the "get folder entries" command.
 * 
 * @author jwootton
 *
 */
public class GetFolderEntriesCmd extends VibeRpcCmd
{
	private String m_zoneUUID;
	private String m_folderId;
	private int m_numEntries;	// The number of entries to read
	private int m_numReplies;	// The number of replies in an entry to get
	private boolean m_getFileAttachments;	// Should we get the file attachments for each entry
	
	/**
	 * For GWT serialization, must have a zero param contructor
	 */
	public GetFolderEntriesCmd()
	{
		super();
	}
	
	/**
	 * 
	 */
	public GetFolderEntriesCmd( String zoneUUID, String folderId, int numEntries, int numReplies )
	{
		this();
		m_zoneUUID = zoneUUID;
		m_folderId = folderId;
		m_numEntries = numEntries;
		m_numReplies = numReplies;
		m_getFileAttachments = false;
	}
	
	/**
	 * 
	 */
	public String getFolderId()
	{
		return m_folderId;
	}
	
	/**
	 * 
	 */
	public boolean getFileAttachmentsValue()
	{
		return m_getFileAttachments;
	}
	
	/**
	 * 
	 */
	public int getNumEntries()
	{
		return m_numEntries;
	}
	
	/**
	 * 
	 */
	public int getNumReplies()
	{
		return m_numReplies;
	}
	
	/**
	 * Returns the command's enumeration value.
	 */
	@Override
	public int getCmdType()
	{
		return VibeRpcCmdType.GET_FOLDER_ENTRIES.ordinal();
	}
	
	/**
	 * 
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}
}
