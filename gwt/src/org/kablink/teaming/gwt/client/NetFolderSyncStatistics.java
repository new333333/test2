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
package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class represents the statistical information regarding synchronization of a net folder.
 * The data here should mirror the data found in the class BinderState.FullSyncStats
 * @author jwootton
 *
 */
public class NetFolderSyncStatistics
	implements IsSerializable, VibeRpcResponseData
{
	/*
	 * The time 'status' value was set or cleared.
	 */
	private Long m_statusDate;
	
	/*
	 * The IPv4 address of the node from which 'status' value was set or cleared.
	 */
	private String m_statusIpv4Address;
	
	/*
	 * The time full sync started on this binder.
	 */
	private Long m_startDate;
	
	/*
	 * The time full sync ended on this binder.
	 */
	private Long m_endDate;
	
	/*
	 * Whether or not the full sync was directory only 
	 */
	private Boolean m_dirOnly;

	/*
	 * Whether or not directory enumeration failed
	 */
	private Boolean m_enumerationFailed;
	
	/*
	 * Count of files encountered as result of enumerating source file system
	 */
	private Integer m_countFiles;
	
	/*
	 * Count of files added
	 */
	private Integer m_countFileAdd;
	
	/*
	 * Count of files expunged
	 */
	private Integer m_countFileExpunge;
	
	/*
	 * Count of files modified
	 */
	private Integer m_countFileModify;
	
	/*
	 * Count of files on which ACLs are set/updated
	 */
	private Integer m_countFileSetAcl;
	
	/*
	 * Count of files on which ownership are explicitly set/updated
	 */
	private Integer m_countFileSetOwnership;
	
	/*
	 * Count of folders encountered as result of enumerating source file system
	 */
	private Integer m_countFolders;
	
	/*
	 * Count of folders added
	 */
	private Integer m_countFolderAdd;
	
	/*
	 * Count of folders expunged
	 */
	private Integer m_countFolderExpunge;
	
	/*
	 * Count of folders on which ACLs are set/updated
	 */
	private Integer m_countFolderSetAcl;
	
	/*
	 * Count of folders on which ownership are explicitly set/updated
	 */
	private Integer m_countFolderSetOwnership;
	
	/*
	 * Count of dangling entries expunged
	 */
	private Integer m_countEntryExpunge;
	
	/*
	 * Count of failure. Note that this does NOT count the number of unique folders
	 * and files failed to process. That is actually hard number to obtain due to
	 * recursive nature of the processing. Instead, this count simply denotes how
	 * many operations failed during the sync without clearing defining what those
	 * operations are and at what granularity. 
	 */
	private Integer m_countFailure;
	
	/*
	 * Count of folders synchronized. This includes both newly created folders and
	 * existing folders that have been synchronized with the source. This number
	 * does not include expunged folders.
	 */
	private Integer m_countFolderProcessed;
	
	/*
	 * Maximum number of folders that were found in the queue at once. In other word,
	 * an indication of how big the queue has grown at any time during the sync.
	 */
	private Integer m_countFolderMaxQueue;
	
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public NetFolderSyncStatistics()
	{
		
	}
	
	/**
	 * 
	 */
	public Integer getCountEntryExpunge() 
	{
		return m_countEntryExpunge;
	}

	/**
	 * 
	 */
	public Integer getCountFailure() 
	{
		return m_countFailure;
	}

	/**
	 * 
	 */
	public Integer getCountFileAdd() 
	{
		return m_countFileAdd;
	}

	/**
	 * 
	 */
	public Integer getCountFiles()
	{
		return m_countFiles;
	}

	/**
	 * 
	 */
	public Integer getCountFileExpunge() 
	{
		return m_countFileExpunge;
	}

	/**
	 * 
	 */
	public Integer getCountFileModify() 
	{
		return m_countFileModify;
	}

	/**
	 * 
	 */
	public Integer getCountFileSetAcl()
	{
		return m_countFileSetAcl;
	}

	/**
	 * 
	 */
	public Integer getCountFileSetOwnership()
	{
		return m_countFileSetOwnership;
	}

	/**
	 * 
	 */
	public Integer getCountFolders() 
	{
		return m_countFolders;
	}

	/**
	 * 
	 */
	public Integer getCountFolderAdd() 
	{
		return m_countFolderAdd;
	}

	/**
	 * 
	 */
	public Integer getCountFolderExpunge() 
	{
		return m_countFolderExpunge;
	}

	/**
	 * 
	 */
	public Integer getCountFolderMaxQueue() 
	{
		return m_countFolderMaxQueue;
	}

	/**
	 * 
	 */
	public Integer getCountFolderProcessed() 
	{
		return m_countFolderProcessed;
	}

	/**
	 * 
	 */
	public Integer getCountFolderSetAcl() 
	{
		return m_countFolderSetAcl;
	}

	/**
	 * 
	 */
	public Integer getCountFolderSetOwnership()
	{
		return m_countFolderSetOwnership;
	}

	/**
	 * 
	 */
	public Boolean getDirOnly()
	{
		return m_dirOnly;
	}

	/**
	 * 
	 */
	public Long getEndDate()
	{
		return m_endDate;
	}

	/**
	 * 
	 */
	public Boolean getEnumerationFailed()
	{
		return m_enumerationFailed;
	}

	/**
	 * 
	 */
	public Long getStartDate()
	{
		return m_startDate;
	}

	/**
	 * 
	 */
	public Long getStatusDate()
	{
		return m_statusDate;
	}

	/**
	 * 
	 */
	public String getStatusIpv4Address()
	{
		return m_statusIpv4Address;
	}

	/**
	 * 
	 */
	public void setCountEntryExpunge( Integer value ) 
	{
		m_countEntryExpunge = value;
	}

	/**
	 * 
	 */
	public void setCountFailure( Integer value ) 
	{
		m_countFailure = value;
	}

	/**
	 * 
	 */
	public void setCountFileAdd( Integer value ) 
	{
		m_countFileAdd = value;
	}

	/**
	 * 
	 */
	public void setCountFiles( Integer value )
	{
		m_countFiles = value;
	}

	/**
	 * 
	 */
	public void setCountFileExpunge( Integer value ) 
	{
		m_countFileExpunge = value;
	}

	/**
	 * 
	 */
	public void setCountFileModify( Integer value ) 
	{
		m_countFileModify = value;
	}

	/**
	 * 
	 */
	public void setCountFileSetAcl( Integer value )
	{
		m_countFileSetAcl = value;
	}

	/**
	 * 
	 */
	public void setCountFileSetOwnership( Integer value )
	{
		m_countFileSetOwnership = value;
	}

	/**
	 * 
	 */
	public void setCountFolders( Integer value ) 
	{
		m_countFolders = value;
	}

	/**
	 * 
	 */
	public void setCountFolderAdd( Integer value ) 
	{
		m_countFolderAdd = value;
	}

	/**
	 * 
	 */
	public void setCountFolderExpunge( Integer value ) 
	{
		m_countFolderExpunge = value;
	}

	/**
	 * 
	 */
	public void setCountFolderMaxQueue( Integer value ) 
	{
		m_countFolderMaxQueue = value;
	}

	/**
	 * 
	 */
	public void setCountFolderProcessed( Integer value ) 
	{
		m_countFolderProcessed = value;
	}

	/**
	 * 
	 */
	public void setCountFolderSetAcl( Integer value ) 
	{
		m_countFolderSetAcl = value;
	}

	/**
	 * 
	 */
	public void setCountFolderSetOwnership( Integer value )
	{
		m_countFolderSetOwnership = value;
	}

	/**
	 * 
	 */
	public void setDirOnly( Boolean value )
	{
		m_dirOnly = value;
	}

	/**
	 * 
	 */
	public void setEndDate( Long value )
	{
		m_endDate = value;
	}

	/**
	 * 
	 */
	public void setEnumerationFailed( Boolean value )
	{
		m_enumerationFailed = value;
	}

	/**
	 * 
	 */
	public void setStartDate( Long value )
	{
		m_startDate = value;
	}

	/**
	 * 
	 */
	public void setStatusDate( Long value )
	{
		m_statusDate = value;
	}

	/**
	 * 
	 */
	public void setStatusIpv4Address( String value )
	{
		m_statusIpv4Address = value;
	}
}
